package hello.numblemybox.mybox.infra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hello.numblemybox.stubs.FilePartStub;
import reactor.core.publisher.Mono;

@Disabled
class ObjectMyBoxStorageTest {
	ObjectMyBoxStorage objectMyBoxStorage = new ObjectMyBoxStorage();

	@Test
	@DisplayName("오브젝트 스토리지에 파일 업로드 후 다운로드한다.")
	void name() throws IOException {
		var path = Paths.get("./src/test/resources/uplaod/test-text.txt");
		var fileId = "object-storage-test";
		objectMyBoxStorage.uploadFile(Mono.just(new FilePartStub(path)), fileId).subscribe();

		var stream = objectMyBoxStorage.downloadFile(Mono.just(fileId)).block();
		StringBuilder textBuilder = new StringBuilder();
		try (Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
			int c;
			while ((c = reader.read()) != -1) {
				textBuilder.append((char)c);
			}
		}
		System.out.println(textBuilder);

		objectMyBoxStorage.deleteFile(fileId);
	}
}
