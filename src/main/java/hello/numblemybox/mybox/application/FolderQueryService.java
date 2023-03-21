package hello.numblemybox.mybox.application;

import org.springframework.stereotype.Service;

import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.domain.ObjectType;
import hello.numblemybox.mybox.dto.FileResponse;
import hello.numblemybox.mybox.dto.FolderResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FolderQueryService {

	private static final String ADMIN = "rjsckdd12@gmail.com";
	private final FolderMyBoxRepository folderMyBoxRepository;

	public Mono<FolderResponse> findFolder(String folderId) {
		return folderMyBoxRepository.findById(folderId)
			.flatMap(this::getFolderResponse);
	}

	public Mono<FolderResponse> findRootFolder() {
		return getRootFolder(ADMIN)
			.flatMap(this::getFolderResponse);
	}

	public Flux<FolderResponse> findFoldersInParent(String folderId) {
		return null;
	}

	public Flux<FolderResponse> findFoldersInRoot() {
		return null;
	}

	public Flux<FileResponse> findFilesInParent(String folderId) {
		return null;
	}

	public Flux<FileResponse> findFilesInRoot() {
		return null;
	}

	private Mono<FolderResponse> getFolderResponse(MyFolder myFolder) {
		return Mono.just(new FolderResponse(myFolder.getId(), myFolder.getName(),
			myFolder.getType()));
	}

	private Mono<MyFolder> getRootFolder(String username) {
		return folderMyBoxRepository.findByTypeAndUsername(ObjectType.ROOT, username);
	}
}
