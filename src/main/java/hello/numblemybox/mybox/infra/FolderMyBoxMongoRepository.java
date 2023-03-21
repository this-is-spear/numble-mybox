package hello.numblemybox.mybox.infra;

import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.domain.ObjectType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FolderMyBoxMongoRepository extends ReactiveMongoRepository<MyFolder, String>, FolderMyBoxRepository {

	@Override
	Mono<MyFolder> findById(Publisher<String> id);

	@Override
	Flux<MyFolder> findAll();

	@Override
	Mono<MyFolder> save(MyFolder entity);

	Mono<MyFolder> findByTypeAndUsername(ObjectType type, String username);

	Flux<MyFolder> findByParentId(String parentId);
}
