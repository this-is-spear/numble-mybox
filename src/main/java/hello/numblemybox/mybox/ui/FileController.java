package hello.numblemybox.mybox.ui;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import hello.numblemybox.mybox.application.FileCommandService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("mybox")
@RequiredArgsConstructor
public class FileController {

	private final FileCommandService fileCommandService;

	@PostMapping(
		value = "upload",
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
}
