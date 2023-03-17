package hello.numblemybox.mybox.ui;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import hello.numblemybox.mybox.application.FileCommandService;
import hello.numblemybox.mybox.application.FileQueryService;
import hello.numblemybox.mybox.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 컨트롤러와 관련된 문서는 <a href="https://github.com/this-is-spear/numble-mybox/issues/2">링크</a>에서 확인할 수 있습니다.
 */
@RestController
@RequestMapping("mybox")
@RequiredArgsConstructor
public class FileController {

	private final FileCommandService fileCommandService;
	private final FileQueryService fileQueryService;

	@PostMapping(
		value = "/upload",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public Mono<Void> uploadFiles(
		@RequestPart("files") Flux<FilePart> partFlux
	) {
		return partFlux.publish(fileCommandService::upload).then();
	}

	@PostMapping(
		value = "/{id}/download",
		produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
	)
	public Mono<ResponseEntity<InputStreamResource>> downloadFile(@PathVariable String id) {
		return fileCommandService.downloadFileById(id)
			.map(fileResponse -> ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
					String.format("attachment; filename=\"%s\"", fileResponse.filename()))
				.header(HttpHeaders.CONTENT_TYPE, fileResponse.extension())
				.body(new InputStreamResource(fileResponse.inputStream()))
			);
	}

	@GetMapping(
		value = "/files/{filename:.+}",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Mono<FileResponse> getFile(@PathVariable String filename) {
		return fileQueryService.getFile(filename);
	}

	@GetMapping(
		value = "/files",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Flux<FileResponse> getFiles() {
		return fileQueryService.getFiles();
	}
}
