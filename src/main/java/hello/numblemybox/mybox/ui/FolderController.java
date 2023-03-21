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

import hello.numblemybox.mybox.application.FileCommandService;
import hello.numblemybox.mybox.application.FolderCommandService;
import hello.numblemybox.mybox.application.FolderQueryService;
import hello.numblemybox.mybox.dto.FileResponse;
import hello.numblemybox.mybox.dto.FolderResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mybox/folders")
public class FolderController {

	private final FileCommandService fileCommandService;
	private final FolderCommandService folderCommandService;
	private final FolderQueryService folderQueryService;

	/**
	 * 지정한 폴더에 폴더를 저장합니다.
	 *
	 * @param parentId   저장하려는 폴더 식별자
	 * @param foldername 폴더 이름 : 폴더 이름은 중복일 수 없습니다.
	 * @return 반환 값 없음
	 */
	@PostMapping("/{parentId}")
	public Mono<Void> createFolder(
		@PathVariable String parentId,
		@RequestParam String foldername
	) {
		return folderCommandService.createFolder(parentId, foldername);
	}

	/**
	 * 지정한 폴더의 메타데이터를 조회합니다.
	 *
	 * @param folderId 폴더 식별자
	 * @return 폴더 메타데이터
	 */
	@GetMapping(
		value = "/{folderId}",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Mono<FolderResponse> findFolderMetadata(
		@PathVariable String folderId
	) {
		return folderQueryService.findFolder(folderId);
	}

	/**
	 * 로트 폴더 메타데이터를 조회합니다.
	 *
	 * @return 루트 폴더의 메타데이터
	 */
	@GetMapping(
		value = "/root",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Mono<FolderResponse> findRootFolderMetadata() {
		return folderQueryService.findRootFolder();
	}

	/**
	 * 지정한 폴더 내 존재하는 폴더 메타데이터 정보를 조회합니다.
	 *
	 * @param folderId 폴더 식별자
	 * @return 폴더 메타데이터 리스트
	 */
	@GetMapping(
		value = "/{folderId}/folders",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Flux<FolderResponse> findFoldersInParent(
		@PathVariable String folderId
	) {
		return folderQueryService.findFoldersInParent(folderId);
	}

	/**
	 * 지정한 폴더 내 존재하는 파일 메타데이터 정보를 조회합니다.
	 *
	 * @param folderId 지정한 폴더 식별자
	 * @return 파일 메타데이터 리스트
	 */
	@GetMapping(
		value = "/{folderId}/files",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Flux<FileResponse> findFilesInParent(
		@PathVariable String folderId
	) {
		return folderQueryService.findFilesInParent(folderId);
	}

	/**
	 * 루트 내 폴더 메타데이터를 조회합니다.
	 *
	 * @return 폴더 메타데이터 리스트
	 */
	@GetMapping(
		value = "/root/folders",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Flux<FolderResponse> findFoldersInRoot(
	) {
		return folderQueryService.findFoldersInRoot();
	}

	/**
	 * 루트 내 파일 메타데이터를 조회합니다.
	 *
	 * @return 폴더 메타데이터 리스트
	 */
	@GetMapping(
		value = "/root/files",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Flux<FileResponse> findFilesInRoot(
	) {
		return folderQueryService.findFilesInRoot();
	}

	/**
	 * 폴더 내부에 파일을 업로드합니다.
	 *
	 * @param partFlux 파일 정보
	 * @return 반환값 없음
	 */
	@PostMapping(
		value = "/{folderId}/upload",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public Mono<Void> uploadFilesInFolder(
		@PathVariable String folderId,
		@RequestPart("files") Flux<FilePart> partFlux
	) {
		return fileCommandService.upload(folderId, partFlux);
	}
}
