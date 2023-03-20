package hello.numblemybox.mybox.application;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.domain.ObjectType;
import hello.numblemybox.mybox.dto.FolderResponse;
import hello.numblemybox.mybox.dto.MyObjectResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FolderQueryService {

	private static final String ADMIN = "rjsckdd12@gmail.com";
	private final FolderMyBoxRepository myBoxMongoRepository;

	public Mono<FolderResponse> findFolder(String folderId) {
		return myBoxMongoRepository.findById(folderId)
			.flatMap(myObject -> Mono.just((MyFolder)myObject))
			.flatMap(myFolder -> Mono.just(getFolderResponse(myFolder)));
	}

	public Mono<FolderResponse> findRootFolder() {
		return getRootFolder(ADMIN)
			.flatMap(myFolder ->
				Mono.just(getFolderResponse(myFolder)));
	}

	private FolderResponse getFolderResponse(MyFolder myFolder) {
		return new FolderResponse(myFolder.getId(), myFolder.getName(), myFolder.getType(),
			myFolder.getChildren().stream().map(
				f -> new MyObjectResponse(f.getId(), f.getName(), f.getType())
			).collect(Collectors.toList()));
	}

	private Mono<MyFolder> getRootFolder(String username) {
		return myBoxMongoRepository.findByTypeAndUsername(ObjectType.ROOT, username);
	}
}
