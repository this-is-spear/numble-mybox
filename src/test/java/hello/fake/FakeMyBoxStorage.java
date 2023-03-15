package hello.fake;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.springframework.http.codec.multipart.FilePart;

import hello.numblemybox.mybox.application.MyBoxStorage;
import hello.numblemybox.stubs.FilePartStub;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FakeMyBoxStorage implements MyBoxStorage {

	@Override
	public Mono<Void> uploadFiles(Flux<FilePart> partFlux) {
		return partFlux
			.flatMap(filePart -> filePart.transferTo(업로드할_사진의_경로.resolve(filePart.filename())))
			.then();
	}

	@Override
	public Mono<File> getFile(String filename) {
		return Mono.just(업로드할_사진의_경로.resolve(filename).toFile());
	}

	@Test
	void uploadFile() throws IOException {
		var filePart = new FilePartStub(테스트할_사진의_경로.resolve(업로드할_사진));
		uploadFiles(Flux.just(filePart)).subscribe();
		assertThat(Files.exists(업로드할_사진의_경로.resolve(업로드할_사진))).isTrue();
		Files.delete(업로드할_사진의_경로.resolve(업로드할_사진));
	}
}
