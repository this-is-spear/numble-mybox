package hello.numblemybox.mybox.infra;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import hello.numblemybox.stubs.FilePartStub;
import reactor.core.publisher.Flux;

class LocalMyBoxStorageTest {
	private static final Path LOCAL_PATH = Paths.get("./src/main/resources/upload");
	LocalMyBoxStorage localMyBoxStorage = new LocalMyBoxStorage();

	@Test
	void uploadFile() throws IOException {
		var filePart = new FilePartStub(테스트할_사진의_경로.resolve(업로드할_사진));
		localMyBoxStorage.uploadFiles(Flux.just(filePart)).subscribe();
		assertThat(Files.exists(LOCAL_PATH.resolve(업로드할_사진))).isTrue();
		Files.delete(LOCAL_PATH.resolve(업로드할_사진));
	}
}
