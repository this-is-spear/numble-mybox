package hello.numblemybox.documentation;

import static hello.numblemybox.stubs.FileStubs.*;
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
import hello.numblemybox.mybox.application.FolderCommandService;
import hello.numblemybox.mybox.application.FolderQueryService;
import hello.numblemybox.mybox.domain.ObjectType;
import hello.numblemybox.mybox.dto.FileResponse;
import hello.numblemybox.mybox.dto.FolderResponse;
import hello.numblemybox.mybox.dto.LoadedFileResponse;
import hello.numblemybox.mybox.ui.MyBoxController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = MyBoxController.class)
public class MyBoxDocument {
	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private FolderCommandService folderCommandService;

	@MockBean
	private FileCommandService fileCommandService;

	@MockBean
	private FolderQueryService folderQueryService;

	@Test
	void createFolder() {
		webTestClient.post().uri(uriBuilder ->
				uriBuilder.path("/mybox/folders/{parentId}")
					.queryParam("foldername", "직박구리")
					.build("23KDMFVK134DJC"))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("folder/create")
			);
	}

	@Test
	void findFolderMetadata() {
		var folderResponse = new FolderResponse("13DFSDKI132SD", "root", ObjectType.FOLDER);

		var folderId = "13DFSDKI132SD";
		when(folderQueryService.findFolder(folderId))
			.thenReturn(Mono.just(folderResponse));

		webTestClient.get()
			.uri("/mybox/folders/{folderId}", folderId)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("folder/find/metadata")
			);
	}

	@Test
	void findRootFolderMetadata() {
		var folderResponse = new FolderResponse("13DFSDKI132SD", "root", ObjectType.FOLDER);
		when(folderQueryService.findRootFolder())
			.thenReturn(Mono.just(folderResponse));

		webTestClient.get()
			.uri("/mybox/folders/root")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("folder/findRoot/metadata")
			);
	}

	@Test
	void findFoldersInParent() {
		var 참새_폴더 = new FolderResponse("13DFSDKI132SD", "참새", ObjectType.FOLDER);
		var 제비_폴더 = new FolderResponse("13DFSDKI132SD", "제비", ObjectType.FOLDER);

		var folderId = "13DFSDKI132SD";
		when(folderQueryService.findFoldersInParent(folderId))
			.thenReturn(Flux.just(참새_폴더, 제비_폴더));

		webTestClient.get()
			.uri("/mybox/folders/{folderId}/folders", folderId)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("folder/find/folders")
			);
	}

	@Test
	void findFoldersInRootFolder() {
		var 참새_폴더 = new FolderResponse("13DFSDKI132SD", "참새", ObjectType.FOLDER);
		var 제비_폴더 = new FolderResponse("13DFSDKI132SD", "제비", ObjectType.FOLDER);
		var 족제비_폴더 = new FolderResponse("13DFSDKI132SD", "족제비_폴더", ObjectType.FOLDER);

		when(folderQueryService.findFoldersInRoot())
			.thenReturn(Flux.just(참새_폴더, 제비_폴더, 족제비_폴더));

		webTestClient.get()
			.uri("/mybox/folders/root/folders")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("folder/findRoot/folders")
			);
	}

	@Test
	void findFilesInParent() {
		var 이미지_파일 = new FileResponse("13DFSDKI132SD", "image.png", ObjectType.FILE, "png", 1024 * 1024 * 10L,
			"/Users/...");
		var 텍스트_파일 = new FileResponse("13DFSDKI132SD", "text.txt", ObjectType.FILE, "txt", 1024 * 1024 * 10L,
			"/Users/...");

		var folderId = "13DFSDKI132SD";
		when(folderQueryService.findFilesInParent(folderId))
			.thenReturn(Flux.just(이미지_파일, 텍스트_파일));

		webTestClient.get()
			.uri("/mybox/folders/{folderId}/files", folderId)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("folder/find/files")
			);
	}

	@Test
	void findFilesInRootFolder() {
		var 이미지_파일 = new FileResponse("13DFSDKI132SD", "image.png", ObjectType.FILE, "png", 1024 * 1024 * 10L,
			"/Users/...");
		var 텍스트_파일 = new FileResponse("13DFSDKI132SD", "text.txt", ObjectType.FILE, "txt", 1024 * 1024 * 10L,
			"/Users/...");

		when(folderQueryService.findFilesInRoot())
			.thenReturn(Flux.just(이미지_파일, 텍스트_파일));

		webTestClient.get()
			.uri("/mybox/folders/root/files")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("folder/findRoot/files")
			);
	}

	@Test
	void uploadFileInFolder() {
		var parentId = "DK3413KDC2";
		var builder = new MultipartBodyBuilder();
		builder.part("image1.png", getFileOne(인사_문장))
			.header("Content-disposition", "form-data; name=\"files\"; filename=\"image1.png\"")
			.contentType(MediaType.TEXT_PLAIN);
		builder.part("image2.jpg", getFileOne(끝맺음_문장))
			.header("Content-disposition", "form-data; name=\"files\"; filename=\"image2.jpg\"")
			.contentType(MediaType.TEXT_PLAIN);
		when(fileCommandService.upload(any(), any(Flux.class))).thenReturn(Mono.empty());

		this.webTestClient.post().uri("/mybox/folders/{parentId}/upload", parentId)
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
	void download() {
		var fileId = "641440b0f4647553d5c7942t";
		var parentId = "DK3413KDC2";

		when(fileCommandService.downloadFileById(parentId, fileId)).thenReturn(
			Mono.just(new LoadedFileResponse("test.txt",
				new ByteArrayInputStream("hellloooooo my name is tis".getBytes(StandardCharsets.UTF_8)),
				MediaType.TEXT_PLAIN_VALUE)
			)
		);

		this.webTestClient.post().uri("/mybox/folders/{parentId}/download/{fileId}",parentId, fileId)
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("file/download")
			);
	}
}
