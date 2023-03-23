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
	 * @Fact 사용자는 폴더 이름을 수정할 수 있다.
	 * @Given 폴더를 생성한 후
	 * @When 폴더 이름을 수정하면
	 * @Then 수정된 폴더 이름을 확인할 수 있다.
	 */
	@Test
	void 폴더_이름을_수정한다() throws IOException {
		// given
		var 새로운_폴더이름 = "수정하려는_폴더_이름";
		폴더_생성_요청(루트_식별자, 새로운_폴더이름);
		var 두_번째_루트_폴더_조회 = 폴더_리스트_조회_요청(루트_식별자);
		var 새로운_폴더_식별자 = getFolderId(두_번째_루트_폴더_조회, 새로운_폴더이름);

		// when
		var 다른_새로운_이름 = "수정하고_싶은_폴더_이름";
		폴더_이름_수정_요청(새로운_폴더_식별자, 다른_새로운_이름);

		// then
		var 세_번째_루트_폴더_조회 = 폴더_리스트_조회_요청(루트_식별자);
		assertThat(isContainsFoldername(세_번째_루트_폴더_조회, 다른_새로운_이름)).isTrue();
	}

	/**
	 * @Fact 사용자는 폴더 내용을 확인할 수 있다.
	 * @Given 폴더를 생성한 후
	 * @When 폴더를 조회하면
	 * @Then 폴더 안 내용을 확인할 수 있다.
	 */
	@Test
	void 폴더_내용을_확인한다() throws IOException {
		// given
		var 새로운_폴더이름 = "폴더_이름";
		// when
		폴더_생성_요청(루트_식별자, 새로운_폴더이름);
		// then
		var 두_번째_루트_폴더_조회 = 폴더_리스트_조회_요청(루트_식별자);
		assertThat(isContainsFoldername(두_번째_루트_폴더_조회, 새로운_폴더이름)).isTrue();
	}

	/**
	 * @Fact 사용자는 폴더 안 내용에서 파일을 메타데이터를 조회한다.
	 * @When 폴더 안에 파일이 있을 때
	 * @Then 폴더 안 파일을 조회할 수 있다.
	 */
	@Test
	void 루트_폴더_안_파일을_다운로드한다() throws IOException {
		// given
		var 새로운_폴더이름 = "폴더_친구";
		var 파일이름 = 그냥_문장;
		폴더_생성_요청(루트_식별자, 새로운_폴더이름);
		var 새로운_폴더_식별자 = getFolderId(폴더_리스트_조회_요청(루트_식별자), 새로운_폴더이름);

		// when
		폴더_안_파일_업로드_요청(새로운_폴더_식별자, 파일이름);
		var 파일_식별자 = getFileId(파일_리스트_조회_요청(새로운_폴더_식별자), 파일이름);

		// then
		var 파일_다운로드_요청 = 파일_다운로드_요청(새로운_폴더_식별자, 파일_식별자);
		var 응답_바디 = 파일_다운로드_요청.returnResult().getResponseBody();

		assertNotNull(응답_바디);

		var 파일_내용 = getString(응답_바디);

		assertThat(파일_내용).isEqualTo(
			getString(Files.readAllBytes(프로덕션_업로드_사진_경로.resolve(파일이름)))
		);
	}

	/**
	 * @Fact 사용자는 폴더를 생성하고 그 안에 파일을 업로드해서 관리할 수 있다.
	 * @Given 사용자는 폴더를 생성하면
	 * @When 안에 파일을 업로드해서
	 * @Then 관리할 수 있다.
	 */
	@Test
	void 폴더_안_파일을_업로드한다() throws IOException {
		// given
		var 새로운_폴더이름 = "새로운_폴더";
		var 파일이름 = 인사_문장;
		폴더_생성_요청(루트_식별자, 새로운_폴더이름);
		var 첫_번째_루트_폴더_조회 = 폴더_리스트_조회_요청(루트_식별자);
		var 새로운_폴더_식별자 = getFolderId(첫_번째_루트_폴더_조회, 새로운_폴더이름);

		// when
		폴더_안_파일_업로드_요청(새로운_폴더_식별자, 파일이름);

		// then
		var 새로운_폴더_조회 = 파일_리스트_조회_요청(새로운_폴더_식별자);
		assertThat(isContainsFilename(새로운_폴더_조회, 파일이름)).isTrue();
	}

	/**
	 * @Fact 사용자는 폴더를 생성하고 파일을 업로드 한 후, 파일 이름을 수정한다.
	 * @Given 사용자는 폴더를 생성하고
	 * @When 파일을 업로드한 후,
	 * @Then 파일이름을 수정한다.
	 */
	@Test
	void 파일_이름을_수정한다() throws IOException {
		// given
		var 새로운_폴더이름 = "또또또_새로운_폴더";
		var 파일이름 = 끝맺음_문장;

		폴더_생성_요청(루트_식별자, 새로운_폴더이름);
		var 첫_번째_루트_폴더_조회 = 폴더_리스트_조회_요청(루트_식별자);
		var 새로운_폴더_식별자 = getFolderId(첫_번째_루트_폴더_조회, 새로운_폴더이름);

		// when
		폴더_안_파일_업로드_요청(새로운_폴더_식별자, 파일이름);

		var 첫_번째_파일_조회 = 파일_리스트_조회_요청(새로운_폴더_식별자);
		assertThat(isContainsFilename(첫_번째_파일_조회, 파일이름)).isTrue();

		var 파일_식별자 = getFileId(첫_번째_파일_조회, 파일이름);
		var 새로운_파일이름 = "완전_새로운_파일.txt";
		폴더_안_파일이름_수정_요청(새로운_폴더_식별자, 파일_식별자, 새로운_파일이름);

		// then
		var 두_번째_파일_조회 = 파일_리스트_조회_요청(새로운_폴더_식별자);
		assertThat(isContainsFilename(두_번째_파일_조회, 새로운_파일이름)).isTrue();
	}

	private String getFolderId(WebTestClient.BodyContentSpec spec, String foldername) throws IOException {
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
