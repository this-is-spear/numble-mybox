package hello.numblemybox.mybox.application;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

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
	 *
	 * @param filename 파일 이름
	 * @return 원본 파일
	 */
	Mono<File> getFile(String filename);

	/**
	 * 파일을 저장소에서 업로드합니다.
	 *
	 * @param file 원본 파일
	 * @return Void
	 */
	Mono<Void> uploadFile(Mono<FilePart> file, String fileId);

	/**
	 * 저장소에 있는 파일을 다운로드합니다.
	 *
	 * @param filename 저장된 파일 이름
	 * @return 파일 내부 정보
	 */
	Mono<InputStream> downloadFile(Mono<String> filename);

	/**
	 * 경로를 입력받아 파일을 다운로드합니다.
	 *
	 * @param path 다운로드할 파일의 경로
	 * @return 파일 내부 정보
	 */
	Mono<InputStream> downloadFile(Path path);

	Path getZipPath();
}
