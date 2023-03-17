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

	/**
	 * 파일 정보를 조회한다. 이름에 맞는 파일 정보가 없으면 예외가 발생합니다.
	 * @param filename 파일 이름
	 * @return 원본 파일
	 */
	Mono<File> getFile(String filename);

	/**
	 * 파일을 저장소에서 업로드합니다.
	 * @param file 원본 파일
	 * @return Void
	 */
	Mono<Void> uploadFile(Mono<FilePart> file);
}
