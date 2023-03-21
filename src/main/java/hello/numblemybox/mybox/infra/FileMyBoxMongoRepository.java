package hello.numblemybox.mybox.infra;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FileMyBoxMongoRepository extends ReactiveMongoRepository<MyFile, String>, FileMyBoxRepository {

	@Override
	Mono<MyFile> save(MyFile entity);

	@Override
	Mono<MyFile> findByName(String name);

	@Override
	Flux<MyFile> findAll();

	@Override
	Mono<Void> deleteAll();

	@Override
	Mono<MyFile> findById(String id);

	Flux<MyFile> findByParentId(String parentId);
}
