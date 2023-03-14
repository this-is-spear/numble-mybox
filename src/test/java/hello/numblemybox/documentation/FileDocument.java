package hello.numblemybox.documentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Objects;

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
import hello.numblemybox.mybox.ui.FileController;
import reactor.core.publisher.Mono;

@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = FileController.class)
public class FileDocument {
	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private FileCommandService fileCommandService;

	@Test
	void upload() {
		when(fileCommandService.upload(any())).thenReturn(Mono.empty());

		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("image1", getFileOne("test-image1.png"))
			.header("Content-disposition", "form-data; name=\"files\"; filename=\"file1\"");
		builder.part("image2", getFileOne("test-image2.jpg"))
			.header("Content-disposition", "form-data; name=\"files\"; filename=\"file2\"");

		this.webTestClient.post().uri("/mybox/upload")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.bodyValue(builder.build())
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("upload")
			);
	}

	private File getFileOne(String filename) {
		return new File(
			String.valueOf(
				Objects.requireNonNull(
					this.getClass()
						.getClassLoader()
						.getResource(String.format("upload/%s", filename))
				).getFile()
			)
		);
	}
}
