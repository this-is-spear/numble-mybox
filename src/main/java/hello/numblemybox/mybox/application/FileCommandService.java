package hello.numblemybox.mybox.application;

import java.util.Objects;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.dto.LoadedFileResponse;
import hello.numblemybox.mybox.exception.InvalidFilenameException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 서비스와 관련된 문서는 <a href="https://github.com/this-is-spear/numble-mybox/issues/2">링크</a>에서 확인할 수 있습니다.
 */
@Service
@RequiredArgsConstructor
public class FileCommandService {
	private static final String ADMIN = "rjsckdd12@gmail.com";
	private final MyBoxStorage myBoxStorage;
	private final FileMyBoxRepository myBoxRepository;
	private final FolderCommandService folderCommandService;

	/**
	 * 1. 파일의 메타데이터를 식별한다.
	 * 2. 파일 시스템에 업로드한다.
	 * 3. 파일 정보를 저장한다.
	 *
	 * @param filePart 업로드하려는 파일 데이터
	 */
	public Mono<Void> upload(Flux<FilePart> filePart) {
		return filePart
			.publishOn(Schedulers.boundedElastic())
			.flatMap(
				file -> {
					var findFile = myBoxRepository.findByName(file.filename()).flatMap(myFile -> {
						if (myFile != null) {
							return Mono.error(InvalidFilenameException.alreadyFilename());
						}
						return Mono.empty();
					}).then();

					var fileMono = myBoxStorage.getPath().flatMap(
						path -> Mono.just(
								new MyFile(null, file.filename(), ADMIN, path,
									file.headers().getContentLength(), getExtension(file)))
							.flatMap(myBoxRepository::save).then()
					);

					var uploadFile = myBoxStorage.uploadFile(Mono.just(file)).then();
					return Mono.when(findFile, fileMono, uploadFile);
				}
			)
			.then();
	}

	private String getExtension(FilePart file) {
		return Objects.requireNonNull(file.headers().getContentType()).toString();
	}

	public Mono<LoadedFileResponse> downloadFileById(String id) {
		var fileMono = myBoxRepository.findById(id);
		var filename = fileMono.map(MyFile::getFilename);
		var inputStreamMono = myBoxStorage
			.downloadFile(filename);

		return Mono.zip(fileMono, inputStreamMono)
			.map(objects -> new LoadedFileResponse(
				objects.getT1().getFilename(),
				objects.getT2(),
				objects.getT1().getExtension())
			);
	}

	public Mono<Void> upload(String folderId, Flux<FilePart> filePart) {
		return filePart
			.flatMap(file -> {
				var fileMono = myBoxStorage.getPath().flatMap(path -> Mono.just(
							new MyFile(null, file.filename(), ADMIN, path, file.headers().getContentLength(),
								getExtension(file)))
						.flatMap(myFile -> folderCommandService.addFileInFolder(folderId, Mono.just(myFile))))
					.then();
				var uploadFile = myBoxStorage.uploadFile(Mono.just(file)).then();
				return Mono.when(fileMono, uploadFile);
			}).then();
	}
}
