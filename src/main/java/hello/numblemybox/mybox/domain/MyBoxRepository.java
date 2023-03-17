package hello.numblemybox.mybox.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MyBoxRepository {

	Mono<MyFile> insert(MyFile entity);

	Mono<MyFile> findByFilename(String filename);

	Flux<MyFile> findAll();
}
