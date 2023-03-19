package hello.numblemybox.documentation;

import static hello.numblemybox.stubs.FileStubs.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

import hello.numblemybox.mybox.application.FolderCommandService;
import hello.numblemybox.mybox.application.FolderQueryService;
import hello.numblemybox.mybox.domain.ObjectType;
import hello.numblemybox.mybox.dto.FileResponse;
import hello.numblemybox.mybox.dto.FolderResponse;
import hello.numblemybox.mybox.dto.MyObjectResponse;
import hello.numblemybox.mybox.ui.FolderController;
import reactor.core.publisher.Mono;

@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = FolderController.class)
public class FolderDocument {
	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private FolderCommandService folderCommandService;

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
	void findFolder() {
		var children = new ArrayList<MyObjectResponse>();
		children.add(new FolderResponse("929G1242D1", "첫 번째 폴더", ObjectType.FOLDER, new ArrayList<>()));
		children.add(new FileResponse("193DF1367D", "image.txt", ObjectType.FILE, "txt", 1234L));
		var folderResponse = new FolderResponse("13DFSDKI132SD", "root", ObjectType.FOLDER, children);

		String folderId = "13DFSDKI132SD";
		Mockito.when(folderQueryService.findFolder(folderId))
			.thenReturn(Mono.just(folderResponse));

		webTestClient.get()
			.uri("/mybox/folders/{folderId}", folderId)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("folder/find")
			);
	}

	@Test
	void findRootFolder() {
		var children = new ArrayList<MyObjectResponse>();
		children.add(new FolderResponse("13SDF1343D1", "첫 번째 폴더", ObjectType.FOLDER, new ArrayList<>()));
		children.add(new FolderResponse("13SDF1342S2", "두 번째 폴더", ObjectType.FOLDER, new ArrayList<>()));
		children.add(new FileResponse("13SDF1343D3", "image.txt", ObjectType.FILE, "txt", 1234L));
		var folderResponse = new FolderResponse("13DFSDKI132SD", "root", ObjectType.FOLDER, children);

		Mockito.when(folderQueryService.findRootFolder())
			.thenReturn(Mono.just(folderResponse));

		webTestClient.get()
			.uri("/mybox/folders")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("folder/findRoot")
			);
	}

	@Test
	void uploadFileInFolder() {
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("image1.png", getFileOne(인사_문장))
			.header("Content-disposition", "form-data; name=\"files\"; filename=\"image1.png\"")
			.contentType(MediaType.TEXT_PLAIN);
		builder.part("image2.jpg", getFileOne(끝맺음_문장))
			.header("Content-disposition", "form-data; name=\"files\"; filename=\"image2.jpg\"")
			.contentType(MediaType.TEXT_PLAIN);

		this.webTestClient.post().uri("/mybox/folders/{parentId}/upload", "SD134DFSVC3")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.bodyValue(builder.build())
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("folder/upload")
			);
	}
}
