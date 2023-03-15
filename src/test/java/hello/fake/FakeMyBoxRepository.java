package hello.fake;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
		return Mono.just(map.values().stream()
			.filter(myFile -> Objects.equals(filename, myFile.getFilename()))
			.findFirst().get());
	}

	@Override
	public Flux<MyFile> findAll() {
		return Mono.just(map.values()).flatMapIterable(myFiles -> myFiles);
	}
}
