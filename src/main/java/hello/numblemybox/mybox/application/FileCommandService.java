package hello.numblemybox.mybox.application;

import java.io.InputStream;
import java.util.Objects;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.member.exception.InvalidMemberException;
import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.dto.LoadedFileResponse;
import hello.numblemybox.mybox.exception.CapacityException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

/**
 * 서비스와 관련된 문서는 <a href="https://github.com/this-is-spear/numble-mybox/issues/2">링크</a>에서 확인할 수 있습니다.
 */
@Service
@RequiredArgsConstructor
public class FileCommandService {
	private final MyBoxStorage myBoxStorage;
	private final FileMyBoxRepository fileMyBoxRepository;
	private final FolderCommandService folderCommandService;

	private String getExtension(FilePart file) {
		return Objects.requireNonNull(file.headers().getContentType()).toString();
	}

	/**
	 * 다운로드할 파일의 데이터를 전송한다.
	 *
	 * @param userInfo 사용자 정보
	 * @param folderId 폴더 식별자
	 * @param fileId 파일 식별자
	 * @return 파일 메타데이터와 파일 데이터
	 */
	public Mono<LoadedFileResponse> downloadFileById(UserInfo userInfo, String folderId, String fileId) {
		var fileMono = fileMyBoxRepository.findByIdAndParentId(fileId, folderId)
			.map(myFile -> ensureMember(userInfo, myFile));
		var inputStreamMono = fileMono.flatMap(myFile -> myBoxStorage.downloadFile(myFile.getId()));
		return Mono.zip(fileMono, inputStreamMono).map(this::getLoadedFileResponse);
	}

	/**
	 * 새로운 스레드로 내부 동작을 진행한다.
	 *
	 * @param userInfo 사용자 정보
	 * @param folderId 폴더 식별자
	 * @param filePart 파일 데이터
	 * @return 반환값 없음
	 */
	public Mono<Void> upload(UserInfo userInfo, String folderId, Flux<FilePart> filePart) {
		return filePart.publishOn(Schedulers.boundedElastic()).flatMap(file -> {
			var ensureCapacity = fileMyBoxRepository.findByUserId(userInfo.id())
				.flatMap(myFile -> Flux.just(myFile.getSize()))
				.reduce(Long::sum)
				.map(total -> {
					if (total + file.headers().getContentLength() > userInfo.availableCapacity()) {
						throw CapacityException.over(userInfo.availableCapacity() - total);
					}
					return Mono.empty();
				}).then();

			var uploadFile = getMyFile(file, myBoxStorage.getPath(), userInfo)
				.flatMap(myFile -> folderCommandService.addFileInFolder(folderId, myFile))
				.flatMap(myFile -> myBoxStorage.uploadFile(file, myFile.getId()))
				.then();
			return Mono.when(ensureCapacity, uploadFile);
		}).then();
	}

	private LoadedFileResponse getLoadedFileResponse(Tuple2<MyFile, InputStream> objects) {
		return new LoadedFileResponse(
			objects.getT1().getFilename(),
			objects.getT2(),
			objects.getT1().getExtension());
	}

	private Mono<MyFile> getMyFile(FilePart file, String path, UserInfo userInfo) {
		return Mono.just(new MyFile(null, file.filename(), userInfo.id(), path, file.headers().getContentLength(),
			getExtension(file)));
	}

	public Mono<Void> updateFilename(UserInfo userInfo, String folderId, String fileId, String filename) {
		return fileMyBoxRepository.findByIdAndParentId(fileId, folderId)
			.publishOn(Schedulers.boundedElastic())
			.map(myFile -> ensureMember(userInfo, myFile))
			.flatMap(myFile -> fileMyBoxRepository.save(myFile.updateFilename(filename)))
			.then();
	}

	private MyFile ensureMember(UserInfo userInfo, MyFile myFile) {
		if (!Objects.equals(myFile.getUserId(), userInfo.id())) {
			throw InvalidMemberException.invalidUser();
		}
		return myFile;
	}
}
