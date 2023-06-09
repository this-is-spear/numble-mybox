package hello.numblemybox.documentation;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;

import hello.numblemybox.mybox.domain.ObjectType;
import hello.numblemybox.mybox.dto.FileResponse;
import hello.numblemybox.mybox.dto.FolderResponse;
import hello.numblemybox.mybox.dto.LoadedFileResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FolderMyBoxDocument extends DocumentTemplate {

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
		when(folderQueryService.findFolder(사용자_정보, folderId))
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
		when(folderQueryService.findRootFolder(사용자_정보))
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
		when(folderQueryService.findFoldersInParent(사용자_정보, folderId))
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

		when(folderQueryService.findFoldersInRoot(사용자_정보))
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
		when(folderQueryService.findFilesInParent(사용자_정보, folderId))
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

		when(folderQueryService.findFilesInRoot(사용자_정보))
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
	void updateName() {
		var folderId = "123DFV24AS";
		var foldername = "foldername";

		webTestClient.patch()
			.uri(uriBuilder -> uriBuilder
				.path("/mybox/folders/{folderId}")
				.queryParam("foldername", foldername)
				.build(folderId))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("folder/update")
			);
	}

	@Test
	void downloadFolder() throws IOException {
		var folderId = "123DFV24AS";

		when(folderCommandService.downloadFolder(사용자_정보, folderId)).thenReturn(
			Mono.just(new LoadedFileResponse("test.txt",
				new ByteArrayInputStream(Files.readAllBytes(업로드할_사진의_경로.resolve("test.zip"))),
				"application/zip")
			));

		webTestClient.post()
			.uri(uriBuilder -> uriBuilder
				.path("/mybox/folders/{folderId}/download")
				.build(folderId))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("folder/download")
			);
	}
}
