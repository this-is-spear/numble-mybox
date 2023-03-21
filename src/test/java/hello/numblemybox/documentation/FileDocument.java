package hello.numblemybox.documentation;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

import hello.numblemybox.mybox.application.FileCommandService;
import hello.numblemybox.mybox.application.FileQueryService;
import hello.numblemybox.mybox.domain.ObjectType;
import hello.numblemybox.mybox.dto.FileResponse;
import hello.numblemybox.mybox.dto.LoadedFileResponse;
import hello.numblemybox.mybox.ui.FileController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = FileController.class)
public class FileDocument {
	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private FileCommandService fileCommandService;

	@MockBean
	private FileQueryService fileQueryService;

	@Test
	void upload() {
		when(fileCommandService.upload(any())).thenReturn(Mono.empty());

		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("image1.png", getFileOne(인사_문장))
			.header("Content-disposition", "form-data; name=\"files\"; filename=\"image1.png\"")
			.contentType(MediaType.TEXT_PLAIN);
		builder.part("image2.jpg", getFileOne(끝맺음_문장))
			.header("Content-disposition", "form-data; name=\"files\"; filename=\"image2.jpg\"")
			.contentType(MediaType.TEXT_PLAIN);

		this.webTestClient.post().uri("/mybox/upload")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.bodyValue(builder.build())
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("file/upload")
			);
	}

	@Test
	void getOne() {
		String filename = "test.txt";
		when(fileQueryService.getFile(filename)).thenReturn(
			Mono.just(new FileResponse("a1s23df", "test", ObjectType.FILE, "txt", 128L, "/Users/...")));

		this.webTestClient.get().uri("/mybox/files/{fileName}", filename)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("file/getOne")
			);
	}

	@Test
	void getAll() {
		when(fileQueryService.getFiles()).thenReturn(
			Flux.just(
				new FileResponse("CMS13fa", "image", ObjectType.FILE, "png", 2_000_000L, "/Users/..."),
				new FileResponse("ADM342KD", "profile", ObjectType.FILE, "jpg", 3_000_000L, "/Users/...")
			)
		);

		this.webTestClient.get().uri("/mybox/files")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("file/getAll")
			);
	}

	@Test
	void download() {
		String id = "641440b0f4647553d5c7942t";

		when(fileCommandService.downloadFileById(any())).thenReturn(
			Mono.just(new LoadedFileResponse("test.txt",
				new ByteArrayInputStream("hellloooooo my name is tis".getBytes(StandardCharsets.UTF_8)),
				MediaType.TEXT_PLAIN_VALUE)
			)
		);

		this.webTestClient.post().uri("/mybox/{id}/download", id)
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("file/download")
			);
	}
}
