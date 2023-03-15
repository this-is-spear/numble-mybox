package hello.numblemybox.mybox.infra;

import java.io.File;

import org.springframework.http.codec.multipart.FilePart;

import hello.numblemybox.mybox.application.MyBoxStorage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class LocalMyBoxStorage implements MyBoxStorage {
	@Override
	public Mono<Void> uploadFiles(Flux<FilePart> partFlux) {
		return null;
	}

	@Override
	public Mono<File> getFile(String filename) {
		return null;
	}
}
