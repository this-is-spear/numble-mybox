package hello.numblemybox.mybox.application;

import org.springframework.stereotype.Service;

import hello.numblemybox.mybox.dto.FolderResponse;
import reactor.core.publisher.Mono;

@Service
public class FolderQueryService {

	public Mono<FolderResponse> findFolder(String folderId) {
		return null;
	}

	public Mono<FolderResponse> findRootFolder() {
		return null;
	}
}
