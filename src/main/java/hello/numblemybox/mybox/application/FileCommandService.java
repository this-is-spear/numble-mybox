package hello.numblemybox.mybox.application;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FileCommandService {

	public Mono<Void> upload(Flux<FilePart> filePart) {
		return Mono.empty();
	}
}
