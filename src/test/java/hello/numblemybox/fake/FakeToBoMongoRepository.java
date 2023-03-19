package hello.numblemybox.fake;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.domain.MyObject;
import hello.numblemybox.mybox.domain.ObjectType;
import reactor.core.publisher.Mono;

public class FakeToBoMongoRepository implements FolderMyBoxRepository {

	private Map<String, MyObject> map = new HashMap<>();

	@Override
	public Mono<MyObject> findById(String id) {
		return Mono.justOrEmpty(map.values().stream()
			.filter(myObject -> myObject.getId().equals(id)
				&& myObject.getType().equals(ObjectType.FOLDER))
			.findFirst());
	}

	@Override
	public Mono<MyObject> findByTypeAndUsername(ObjectType type, String username) {
		return Mono.justOrEmpty(map.values().stream()
			.filter(myObject -> myObject.getType().equals(ObjectType.ROOT))
			.filter(myObject -> myObject.getUsername().equals(username))
			.findFirst());
	}

	@Override
	public <S extends MyObject> Mono<S> insert(S entity) {
		var id = UUID.randomUUID().toString();
		if (entity instanceof MyFolder myFolder) {
			var folder = new MyFolder(id, myFolder.getName(), myFolder.getUsername(), myFolder.getType(),
				myFolder.getChildren(), myFolder.getFiles());
			map.put(id, folder);
			return (Mono<S>)Mono.just(folder);
		} else if (entity instanceof MyFile myFile) {
			var file = new MyFile(id, myFile.getName(), myFile.getUsername(), myFile.getPath(), myFile.getSize(),
				myFile.getExtension());
			map.put(id, file);
			return (Mono<S>)Mono.just(file);
		} else {
			throw new RuntimeException();
		}
	}
}
