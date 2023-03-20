package hello.numblemybox.fake;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.domain.ObjectType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FakeFolderMongoRepository implements FolderMyBoxRepository {

	private Map<String, MyFolder> map = new HashMap<>();

	@Override
	public Mono<MyFolder> findById(String id) {
		return Mono.justOrEmpty(map.values().stream()
			.filter(myObject -> myObject.getId().equals(id)
				&& myObject.getType().equals(ObjectType.FOLDER) || myObject.getType().equals(ObjectType.ROOT))
			.findFirst());
	}

	@Override
	public Mono<MyFolder> findByTypeAndUsername(ObjectType type, String username) {
		return Mono.justOrEmpty(map.values().stream()
			.filter(myObject -> myObject.getType().equals(ObjectType.ROOT))
			.filter(myObject -> myObject.getUsername().equals(username))
			.findFirst());
	}

	@Override
	public Mono<MyFolder> insert(MyFolder from) {
		if (from.getId() == null) {
			var id = UUID.randomUUID().toString();
			var to = new MyFolder(id, from.getName(), from.getUsername(),
				from.getType(), from.getChildren(), from.getFiles());
			map.put(id, to);
			return Mono.just(to);
		}
		return Mono.just(from);
	}

	@Override
	public Flux<MyFolder> findAll() {
		return Flux.fromIterable(map.values());
	}
}
