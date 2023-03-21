package hello.numblemybox.documentation;

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
	void findFolderMetadata() {
		var children = new ArrayList<MyObjectResponse>();
		children.add(new FolderResponse("929G1242D1", "첫 번째 폴더", ObjectType.FOLDER));
		children.add(new FileResponse("193DF1367D", "image.txt", ObjectType.FILE, "txt", 1234L, "/Users/.."));
		var folderResponse = new FolderResponse("13DFSDKI132SD", "root", ObjectType.FOLDER);

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
	void findRootFolderMetadata() {
		var folderResponse = new FolderResponse("13DFSDKI132SD", "root", ObjectType.FOLDER);
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
}
