package hello.numblemybox.fake;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.domain.ObjectType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class FakeFileMyBoxRepository implements FileMyBoxRepository {

	private final Map<String, MyFile> map = new HashMap<>();

	@Override
	public Mono<MyFile> save(MyFile from) {
		if (from.getId() == null) {
			var id = UUID.randomUUID().toString();
			var to = new MyFile(id, from.getFilename(), from.getUserId(), ObjectType.FOLDER,
				from.getPath(), from.getSize(), from.getExtension(), from.getParentId());
			map.put(id, to);
			return Mono.just(to);
		}
		map.put(from.getId(), from);
		return Mono.just(from);
	}

	@Override
	public Mono<MyFile> findByName(String objectName) {
		Optional<MyFile> first = map.values().stream()
			.filter(myFile -> Objects.equals(objectName, myFile.getFilename()))
			.findFirst();

		return first.map(Mono::just).orElseGet(Mono::empty);
	}

	@Override
	public Flux<MyFile> findAll() {
		return Mono.just(map.values()).flatMapIterable(myFiles -> myFiles);
	}

	@Override
	public Mono<MyFile> findById(String id) {
		return Mono.justOrEmpty(Optional.ofNullable(map.get(id)));
	}

	@Override
	public Flux<MyFile> findByParentId(String parentId) {
		return Flux.fromIterable(map.values()
			.stream()
			.filter(myFile -> Objects.equals(myFile.getParentId(), parentId))
			.toList()
		);
	}

	@Override
	public Mono<MyFile> findByParentIdAndName(String parentId, String name) {
		return Mono.justOrEmpty(map.values()
			.stream()
			.filter(myFile -> Objects.equals(myFile.getParentId(), parentId) && Objects.equals(myFile.getName(), name))
			.findFirst());
	}

	@Override
	public Mono<MyFile> findByIdAndParentId(String id, String parentId) {
		return Mono.justOrEmpty(Optional.ofNullable(map.get(id))
			.filter(myFile -> Objects.equals(myFile.getParentId(), parentId)));
	}

	@Override
	public Flux<MyFile> findByUserId(String userId) {
		return Flux.fromIterable(map.values().stream()
			.filter(myFile -> Objects.equals(myFile.getUserId(), userId))
			.toList());
	}
}
