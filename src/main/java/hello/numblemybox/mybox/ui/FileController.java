package hello.numblemybox.mybox.ui;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
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

@RestController
@RequestMapping("mybox")
@RequiredArgsConstructor
public class FileController {

	private final FileCommandService fileCommandService;
	private final FileQueryService fileQueryService;

	@PostMapping(
		value = "/local/upload",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public Mono<Void> uploadFiles(
		@RequestPart("files") Flux<FilePart> partFlux
	) {
		return partFlux.log()
			.publish(fileCommandService::upload)
			.log()
			.then();
	}

	@GetMapping(
		value = "/local/files/{filename:.+}",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Mono<FileResponse> getFile(@PathVariable String filename) {
		return fileQueryService.getFile(filename);
	}

	@GetMapping(
		value = "/local/files",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Flux<FileResponse> getFiles() {
		return fileQueryService.getFiles();
	}
}
