package hello.numblemybox.mybox.ui;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import hello.numblemybox.mybox.application.FolderCommandService;
import hello.numblemybox.mybox.application.FolderQueryService;
import hello.numblemybox.mybox.dto.FolderResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mybox/folders")
public class FolderController {

	private final FolderCommandService folderCommandService;
	private final FolderQueryService folderQueryService;

	@PostMapping("{parentId}")
	public Mono<Void> createFolder(
		@PathVariable String parentId,
		@RequestParam String foldername
	) {
		return folderCommandService.createFolder(parentId, foldername);
	}

	@GetMapping(
		value = "{folderId}",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Mono<FolderResponse> findFolder(
		@PathVariable String folderId
	) {
		return folderQueryService.findFolder(folderId);
	}

	@GetMapping(
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Mono<FolderResponse> findRootFolder() {
		return folderQueryService.findRootFolder();
	}
}
