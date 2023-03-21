package hello.numblemybox.integration;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.type.TypeReference;

import hello.numblemybox.mybox.dto.FileResponse;
import hello.numblemybox.mybox.dto.FolderResponse;

class MyBoxAcceptanceTest extends AcceptanceTemplate {

	/**
	 * @Fact 사용자는 폴더 내용을 확인할 수 있다.
	 * @Given 폴더를 생성한 후
	 * @When 폴더를 조회하면
	 * @Then 폴더 안 내용을 확인할 수 있다.
	 */
	@Test
	void 폴더_내용을_확인한다() throws IOException {
		// given
		var 첫_번째_루트_폴더_조회 = 루트_폴더_메타데이터_조회_요청();
		var parentId = getRootId(첫_번째_루트_폴더_조회);
		var foldername = "폴더_이름";

		// when
		폴더_생성_요청(parentId, foldername);
		// then
		var 두_번째_루트_폴더_조회 = 폴더_리스트_조회_요청(parentId);
		assertThat(isContainsFoldername(두_번째_루트_폴더_조회, foldername)).isTrue();
	}

	/**
	 * @Fact 사용자는 폴더 안 내용에서 파일을 메타데이터를 조회한다.
	 * @When 폴더 안에 파일이 있을 때
	 * @Then 폴더 안 파일을 조회할 수 있다.
	 */
	@Test
	void 루트_폴더_안_파일을_다운로드한다() throws IOException {
		// when
		var 첫_번째_루트_폴더_조회 = 루트_폴더_메타데이터_조회_요청();
		var parentId = getRootId(첫_번째_루트_폴더_조회);
		폴더_안_파일_업로드_요청(parentId, 그냥_문장);
		var 파일리스트_조회 = 파일_리스트_조회_요청(parentId);

		// then
		var fileId = getFileId(파일리스트_조회, 그냥_문장);
		var 파일_다운로드_요청 = 파일_다운로드_요청(parentId, fileId);
		var responseBody = 파일_다운로드_요청.returnResult().getResponseBody();

		assertNotNull(responseBody);

		var 파일_내용 = getString(responseBody);

		assertThat(파일_내용).isEqualTo(
			getString(Files.readAllBytes(프로덕션_업로드_사진_경로.resolve(그냥_문장)))
		);
	}

	/**
	 * @Fact 사용자는 폴더를 생성하고 그 안에 파일을 업로드해서 관리할 수 있다.
	 * @When 사용자는 폴더를 생성하면
	 * @Then 그 안에 파일을 업로드해서 관리할 수 있다.
	 */
	@Test
	void 폴더_안_파일을_다운로드한다() throws IOException {
		// given
		var 첫_번째_루트_폴더_조회 = 루트_폴더_메타데이터_조회_요청();
		var parentId = getRootId(첫_번째_루트_폴더_조회);
		var foldername = "폴더_친구";

		// when
		폴더_생성_요청(parentId, foldername);
		var 두_번째_루트_폴더_조회 = 폴더_리스트_조회_요청(parentId);
		var folderId = getFolderId(두_번째_루트_폴더_조회, foldername);

		// then
		폴더_안_파일_업로드_요청(folderId, 그냥_문장);
		var 세_번째_루트_폴더_조회 = 파일_리스트_조회_요청(folderId);
		assertThat(isContainsFilename(세_번째_루트_폴더_조회, 그냥_문장)).isTrue();
	}

	private String getFolderId(WebTestClient.BodyContentSpec spec, String foldername) throws IOException {
		// TODO 응답 데이터에서 파일 이름과 맞는 식별자 조회
		var folderResponses = OBJECT_MAPPER.readValue(spec.returnResult().getResponseBody(),
			new TypeReference<List<FolderResponse>>() {
			});
		return folderResponses.stream()
			.filter(response -> Objects.equals(response.name(), foldername))
			.findFirst()
			.get().id();
	}

	private String getFileId(WebTestClient.BodyContentSpec spec, String filename) throws IOException {
		var fileResponse = OBJECT_MAPPER.readValue(spec.returnResult().getResponseBody(),
			new TypeReference<List<FileResponse>>() {
			});
		return fileResponse.stream()
			.filter(response -> Objects.equals(response.name(), filename))
			.findFirst()
			.get().id();
	}

	private String getRootId(WebTestClient.BodyContentSpec spec) throws IOException {
		return OBJECT_MAPPER.readValue(spec.returnResult().getResponseBody(), FolderResponse.class).id();
	}

	private boolean isContainsFoldername(WebTestClient.BodyContentSpec spec, String foldername) throws IOException {
		var responses = OBJECT_MAPPER.readValue(spec.returnResult().getResponseBody(),
			new TypeReference<List<FolderResponse>>() {
			});
		return responses.stream().anyMatch(response -> Objects.equals(response.name(), foldername));
	}

	private boolean isContainsFilename(WebTestClient.BodyContentSpec spec, String foldername) throws IOException {
		var responses = OBJECT_MAPPER.readValue(spec.returnResult().getResponseBody(),
			new TypeReference<List<FileResponse>>() {
			});
		return responses.stream().anyMatch(response -> Objects.equals(response.name(), foldername));
	}
}
