package hello.numblemybox.mybox.infra;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import hello.numblemybox.mybox.domain.MyObject;
import hello.numblemybox.mybox.domain.ObjectType;
import reactor.core.publisher.Mono;

@Repository
public interface FolderMyBoxMongoRepository extends ReactiveMongoRepository<MyObject, String> {

	@Override
	Mono<MyObject> findById(String id);

	Mono<MyObject> findByTypeAndUsername(ObjectType type, String username);

	@Override
	<S extends MyObject> Mono<S> insert(S entity);
}
