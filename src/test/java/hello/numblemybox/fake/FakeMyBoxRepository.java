package hello.numblemybox.fake;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import hello.numblemybox.mybox.domain.MyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class FakeMyBoxRepository implements MyBoxRepository {

	private final Map<String, MyFile> map = new HashMap<>();

	@Override
	public Mono<MyFile> insert(MyFile entity) {
		String id = UUID.randomUUID().toString();
		MyFile storedMyFile = new MyFile(id, entity.getFilename(), entity.getUsername(), entity.getSize(),
			entity.getExtension());
		map.put(id, storedMyFile);
		return Mono.just(storedMyFile);
	}

	@Override
	public Mono<MyFile> findByFilename(String filename) {
		Optional<MyFile> first = map.values().stream()
			.filter(myFile -> Objects.equals(filename, myFile.getFilename()))
			.findFirst();

		return first.map(Mono::just).orElseGet(Mono::empty);
	}

	@Override
	public Flux<MyFile> findAll() {
		return Mono.just(map.values()).flatMapIterable(myFiles -> myFiles);
	}
}
