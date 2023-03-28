package hello.numblemybox.mybox.application;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Flow;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;

public interface MyBoxStorage {
	Path ZIP_PATH = Paths.get("./src/main/resources/tmp");

	String getPath();

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
	Mono<Void> uploadFile(FilePart file, String fileId);

	/**
	 * 저장소에 있는 파일을 다운로드합니다.
	 *
	 * @param filename 저장된 파일 이름
	 * @return 파일 내부 정보
	 */
	Mono<InputStream> downloadFile(Mono<String> filename);

	Mono<Void> deleteFile(String fileId);
}
