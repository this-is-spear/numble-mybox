package hello.numblemybox.fake;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
			.filter(myObject -> myObject.getId().equals(id))
			.findFirst());
	}

	@Override
	public Mono<MyFolder> findByTypeAndUserId(ObjectType type, String userId) {
		return Mono.justOrEmpty(map.values().stream()
			.filter(myObject -> myObject.getType().equals(type))
			.filter(myObject -> myObject.getUserId().equals(userId))
			.findFirst());
	}

	@Override
	public Mono<MyFolder> save(MyFolder from) {
		if (from.getId() == null) {
			var id = UUID.randomUUID().toString();
			var to = new MyFolder(id, from.getName(), from.getUserId(), from.getType(), from.getParentId());
			map.put(id, to);
			return Mono.just(to);
		}
		map.put(from.getId(), from);
		return Mono.just(from);
	}

	@Override
	public Flux<MyFolder> findAll() {
		return Flux.fromIterable(map.values());
	}

	@Override
	public Flux<MyFolder> findByParentId(String parentId) {
		return Flux.fromIterable(map.values()
			.stream()
			.filter(myFolder -> Objects.equals(myFolder.getParentId(), parentId))
			.toList());
	}

	@Override
	public Mono<MyFolder> findByParentIdAndName(String parentId, String foldername) {
		return Mono.justOrEmpty(map.values()
			.stream()
			.filter(myFolder -> Objects.equals(myFolder.getParentId(), parentId)
				&& Objects.equals(myFolder.getName(), foldername))
			.findFirst());
	}
}
