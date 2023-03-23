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
	private static final String ADMIN = "rjsckdd12@gmail.com";
	private final MyBoxStorage myBoxStorage;
	private final FileMyBoxRepository fileMyBoxRepository;
	private final FolderCommandService folderCommandService;

	private String getExtension(FilePart file) {
		return Objects.requireNonNull(file.headers().getContentType()).toString();
	}

	public Mono<LoadedFileResponse> downloadFileById(UserInfo userInfo, String folderId, String fileId) {
		var fileMono = fileMyBoxRepository.findByIdAndParentId(fileId, folderId)
			.map(myFile -> {
				if (!Objects.equals(myFile.getUserId(), userInfo.id())) {
					throw InvalidMemberException.invalidUser();
				}
				return myFile;
			});
		var filename = fileMono.map(MyFile::getFilename);
		var inputStreamMono = myBoxStorage.downloadFile(filename);
		return Mono.zip(fileMono, inputStreamMono).map(this::getLoadedFileResponse);
	}

	public Mono<Void> upload(UserInfo userInfo, String folderId, Flux<FilePart> filePart) {
		return filePart
			.flatMap(file -> {
				var fileMono = myBoxStorage.getPath().flatMap(path -> getMyFile(file, path, userInfo)
						.flatMap(myFile -> folderCommandService.addFileInFolder(folderId, Mono.just(myFile))))
					.then();
				var uploadFile = myBoxStorage.uploadFile(Mono.just(file)).then();
				return Mono.when(fileMono, uploadFile);
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

	public Mono<Void> updateFilename(String folderId, String fileId, String filename) {
		return fileMyBoxRepository.findByIdAndParentId(fileId, folderId)
			.publishOn(Schedulers.boundedElastic())
			.map(myFile -> myFile.updateFilename(filename))
			.map(myFile -> fileMyBoxRepository.save(myFile).subscribe())
			.then();
	}
}
