package hello.numblemybox.mybox.infra;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.assertj.core.api.Assertions.*;
import static reactor.test.StepVerifier.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hello.numblemybox.stubs.FilePartStub;
import reactor.core.publisher.Mono;

class LocalMyBoxStorageTest {
	private static final Path LOCAL_PATH = Paths.get("./src/main/resources/upload");
	LocalMyBoxStorage localMyBoxStorage = new LocalMyBoxStorage();

	@Test
	@DisplayName("파일을 업로드한다.")
	void uploadFile() throws IOException {
		var filePart = new FilePartStub(테스트할_사진의_경로.resolve(업로드할_사진));
		create(localMyBoxStorage.uploadFile(Mono.just(filePart)))
			.verifyComplete();
		assertThat(Files.exists(LOCAL_PATH.resolve(업로드할_사진))).isTrue();
		Files.deleteIfExists(LOCAL_PATH.resolve(업로드할_사진));
	}

	@Test
	@DisplayName("파일을 다운로드한다.")
	void downloadFile() throws IOException {
		Files.deleteIfExists(LOCAL_PATH.resolve(업로드할_사진));
		Files.copy(테스트할_사진의_경로.resolve(업로드할_사진), LOCAL_PATH.resolve(업로드할_사진));

		create(localMyBoxStorage.downloadFile(Mono.just(업로드할_사진)))
			.expectNextCount(1)
			.verifyComplete();
		Files.deleteIfExists(LOCAL_PATH.resolve(업로드할_사진));
	}
}
