package hello.numblemybox.mybox.application;

import java.io.File;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MyBoxStorage {
	Mono<Void> uploadFiles(Flux<FilePart> partFlux);

	Mono<File> getFile(String filename);
}
