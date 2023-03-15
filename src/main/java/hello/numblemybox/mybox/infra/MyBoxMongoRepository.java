package hello.numblemybox.mybox.infra;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import hello.numblemybox.mybox.domain.MyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MyBoxMongoRepository extends ReactiveMongoRepository<MyFile, Long>, MyBoxRepository {

	@Override
	Mono<MyFile> insert(MyFile entity);

	@Override
	Mono<MyFile> findByFilename(String filename);

	@Override
	Flux<MyFile> findAll();
}