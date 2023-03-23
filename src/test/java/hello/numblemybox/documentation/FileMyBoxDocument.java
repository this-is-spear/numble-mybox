package hello.numblemybox.documentation;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;

import hello.numblemybox.mybox.dto.LoadedFileResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FileMyBoxDocument extends DocumentTemplate {

	@Test
	void uploadFile() {
		var parentId = "DK3413KDC2";
		var builder = new MultipartBodyBuilder();
		builder.part("image1.png", getFileOne(인사_문장))
			.header("Content-disposition", "form-data; name=\"files\"; filename=\"image1.png\"")
			.contentType(MediaType.TEXT_PLAIN);
		builder.part("image2.jpg", getFileOne(끝맺음_문장))
			.header("Content-disposition", "form-data; name=\"files\"; filename=\"image2.jpg\"")
			.contentType(MediaType.TEXT_PLAIN);
		when(fileCommandService.upload(any(), any(), any(Flux.class))).thenReturn(Mono.empty());

		this.webTestClient.post().uri("/mybox/folders/{parentId}/upload", parentId)
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.bodyValue(builder.build())
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(WebTestClientRestDocumentation.document("file/upload"));
	}

	@Test
	void download() {
		var fileId = "641440b0f4647553d5c7942t";
		var parentId = "DK3413KDC2";

		when(fileCommandService.downloadFileById(사용자_정보, parentId, fileId)).thenReturn(
			Mono.just(new LoadedFileResponse("test.txt",
				new ByteArrayInputStream("hellloooooo my name is tis".getBytes(StandardCharsets.UTF_8)),
				MediaType.TEXT_PLAIN_VALUE)
			)
		);

		this.webTestClient.post().uri("/mybox/folders/{parentId}/download/{fileId}", parentId, fileId)
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(WebTestClientRestDocumentation.document("file/download"));
	}

	@Test
	void updateFilename() {
		var parentId = "DK3413KDC2";
		var fileId = "DFSN13223432DAFK";
		var filename = "update_filename.txt";
		this.webTestClient.patch().uri(uriBuilder ->
				uriBuilder.path("/mybox/folders/{parentId}/update/{fileId}")
					.queryParam("filename", filename)
					.build(parentId, fileId))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(WebTestClientRestDocumentation.document("file/update"));
	}
}
