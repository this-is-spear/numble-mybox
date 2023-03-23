package hello.numblemybox.mybox.ui;

import static hello.numblemybox.AuthenticationConfigurer.*;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import hello.numblemybox.member.dto.UserInfo;
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
public class MyBoxController {

	private final FileCommandService fileCommandService;
	private final FolderCommandService folderCommandService;
	private final FolderQueryService folderQueryService;

	/**
	 * 폴더 이름을 입력받아 지정한 폴더에 폴더를 저장합니다.
	 *
	 * @param parentId   저장하려는 폴더 식별자
	 * @param foldername 폴더 이름 : 폴더 이름은 중복일 수 없습니다.
	 * @return 반환 값 없음
	 */
	@PostMapping("/{parentId}")
	public Mono<Void> createFolder(
		@SessionAttribute(SESSION_KEY) UserInfo userInfo,
		@PathVariable String parentId,
		@RequestParam String foldername
	) {
		return folderCommandService.createFolder(userInfo, parentId, foldername);
	}

	/**
	 * 폴더 이름을 입력받아 폴더를 수정합니다.
	 *
	 * @param folderId   수정할 폴더의 식별자
	 * @param foldername 폴더 이름
	 * @return 반환 값 없음
	 */
	@PatchMapping("/{folderId}")
	public Mono<Void> updateFolder(
		@SessionAttribute(SESSION_KEY) UserInfo userInfo,
		@PathVariable String folderId,
		@RequestParam String foldername
	) {
		return folderCommandService.updateFolder(userInfo, folderId, foldername);
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
		@SessionAttribute(SESSION_KEY) UserInfo userInfo,
		@PathVariable String folderId
	) {
		return folderQueryService.findFolder(userInfo, folderId);
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
	public Mono<FolderResponse> findRootFolderMetadata(@SessionAttribute(SESSION_KEY) UserInfo userInfo) {
		return folderQueryService.findRootFolder(userInfo);
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
		@SessionAttribute(SESSION_KEY) UserInfo userInfo,
		@PathVariable String folderId
	) {
		return folderQueryService.findFoldersInParent(userInfo, folderId);
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
		@SessionAttribute(SESSION_KEY) UserInfo userInfo,
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
		@SessionAttribute(SESSION_KEY) UserInfo userInfo
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
		@SessionAttribute(SESSION_KEY) UserInfo userInfo
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
		@SessionAttribute(SESSION_KEY) UserInfo userInfo,
		@PathVariable String folderId,
		@RequestPart("files") Flux<FilePart> partFlux
	) {
		return fileCommandService.upload(folderId, partFlux);
	}

	/**
	 * 파일 식별자를 입력해 파일을 다운로드한다.
	 *
	 * @param fileId 파일의 식별자
	 * @return 파일 내부 정보
	 */
	@PostMapping(
		value = "/{folderId}/download/{fileId}",
		produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
	)
	public Mono<ResponseEntity<InputStreamResource>> downloadFile(
		@SessionAttribute(SESSION_KEY) UserInfo userInfo,
		@PathVariable String folderId,
		@PathVariable String fileId
	) {
		return fileCommandService.downloadFileById(folderId, fileId)
			.map(fileResponse -> ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
					String.format("attachment; filename=\"%s\"", fileResponse.filename()))
				.header(HttpHeaders.CONTENT_TYPE, fileResponse.extension())
				.body(new InputStreamResource(fileResponse.inputStream()))
			);
	}

	/**
	 * 파일 이름을 입력받아 파일을 수정합니다.
	 *
	 * @param folderId 파일이 저장된 폴더의 식별자
	 * @param fileId   파일 식별자
	 * @param filename 파일 이름
	 * @return 반환값 없음
	 */
	@PatchMapping("/{folderId}/update/{fileId}")
	public Mono<Void> updateFilename(
		@SessionAttribute(SESSION_KEY) UserInfo userInfo,
		@PathVariable String folderId,
		@PathVariable String fileId,
		@RequestParam String filename
	) {
		return fileCommandService.updateFilename(folderId, fileId, filename);
	}
}
