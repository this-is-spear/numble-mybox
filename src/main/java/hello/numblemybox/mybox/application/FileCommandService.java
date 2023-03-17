package hello.numblemybox.mybox.application;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import hello.numblemybox.mybox.domain.MyBoxRepository;
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
	private final MyBoxRepository myBoxRepository;

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
					var findFile = myBoxRepository.findByFilename(file.filename())
						.flatMap(
							myFile -> {
								if (myFile != null) {
									return Mono.error(InvalidFilenameException.alreadyFilename());
								}
								return Mono.empty();
							}
						).then();

					var fileMono = myBoxStorage.getPath().flatMap(
						path -> Mono.just(
							new MyFile(null, file.filename(), ADMIN, path, file.headers().getContentLength(),
								getExtension(file))).flatMap(myBoxRepository::insert).then()
					);
					var uploadFile = myBoxStorage.uploadFile(Mono.just(file)).then();
					return Mono.when(findFile, fileMono, uploadFile);
				}
			)
			.then();
	}

	private String getExtension(FilePart file) {
		return file.filename().split("\\.")[1];
	}

	public Mono<LoadedFileResponse> downloadFileById(Mono<String> id) {
		return Mono.empty();
	}
}
