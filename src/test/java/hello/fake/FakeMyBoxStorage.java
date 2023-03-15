package hello.fake;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.http.codec.multipart.FilePart;

import hello.numblemybox.mybox.application.MyBoxStorage;
import hello.numblemybox.stubs.FilePartStub;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FakeMyBoxStorage implements MyBoxStorage {

	public static final Path PATH = Paths.get("./src/test/resources/upload");

	@Override
	public Mono<Void> uploadFiles(Flux<FilePart> partFlux) {
		return partFlux
			.flatMap(filePart -> filePart.transferTo(PATH.resolve(filePart.filename())))
			.then();
	}

	@Override
	public Mono<File> getFile(String filename) {
		return Mono.just(PATH.resolve(filename).toFile());
	}

	@Test
	void uploadFile() throws IOException {
		String filename = "ElvisPresley.png";
		FilePartStub filePart = new FilePartStub(
			Paths.get("/Users/keonchanglee/Downloads").resolve(filename));
		uploadFiles(Flux.just(filePart)).subscribe();
		assertThat(Files.exists(PATH.resolve(filename))).isTrue();
		Files.delete(PATH.resolve(filename));
	}
}
