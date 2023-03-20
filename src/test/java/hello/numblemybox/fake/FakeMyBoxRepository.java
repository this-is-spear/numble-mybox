package hello.numblemybox.fake;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class FakeMyBoxRepository implements FileMyBoxRepository {

	private final Map<String, MyFile> map = new HashMap<>();

	@Override
	public Mono<MyFile> insert(MyFile entity) {
		String id = UUID.randomUUID().toString();
		MyFile storedMyFile = new MyFile(id, entity.getFilename(), entity.getUsername(),
			entity.getPath(), entity.getSize(), entity.getExtension());
		map.put(id, storedMyFile);
		return Mono.just(storedMyFile);
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
		if (map.containsKey(id)) {
			return Mono.just(map.get(id));
		}
		return Mono.empty();
	}
}
