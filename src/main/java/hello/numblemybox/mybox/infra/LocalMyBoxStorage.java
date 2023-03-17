package hello.numblemybox.mybox.infra;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import hello.numblemybox.mybox.application.MyBoxStorage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LocalMyBoxStorage implements MyBoxStorage {
	private static final Path LOCAL_PATH = Paths.get("./src/main/resources/upload");

	@Override
	public Mono<Void> uploadFiles(Flux<FilePart> partFlux) {
		return partFlux
			.flatMap(filePart -> filePart.transferTo(LOCAL_PATH.resolve(filePart.filename())))
			.then();
	}

	@Override
	public Mono<File> getFile(String filename) {
		return Mono.just(LOCAL_PATH.resolve(filename).toFile());
	}

	@Override
	public Mono<Void> uploadFile(Mono<FilePart> file) {
		return file
			.flatMap(filePart -> filePart.transferTo(LOCAL_PATH.resolve(filePart.filename())));
	}
}
