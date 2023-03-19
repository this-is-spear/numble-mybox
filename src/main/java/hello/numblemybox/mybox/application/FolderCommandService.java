package hello.numblemybox.mybox.application;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FolderCommandService {
	public Mono<Void> uploadInFolder(String folderId, Flux<FilePart> partFlux) {
		return null;
	}

	public Mono<Void> createFolder(String parentId, String foldername) {
		return null;
	}
}
