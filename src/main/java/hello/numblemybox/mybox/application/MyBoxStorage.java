package hello.numblemybox.mybox.application;

import java.io.File;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;

public interface MyBoxStorage {

	/**
	 * 데이터베이스에 저장되는 파일의 메타데이터에는 저장되는 위치를 포함해야 합니다. MyBoxStorage 에 저장된 경로를 이용해 데이터를 반환합니다.
	 *
	 * @return 현재 저장되는 파일의 경로를 반환합니다.
	 */
	Mono<String> getPath();

	Mono<File> getFile(String filename);

	Mono<Void> uploadFile(Mono<FilePart> file);
}
