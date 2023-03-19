package hello.numblemybox.integration;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import hello.numblemybox.mybox.dto.FileResponse;

class FileAcceptanceTest extends AcceptanceTemplate {

	/**
	 * @Fact 파일 한 개를 업로드하면 하나의 파일의 정보를 조회할 수 있고, 파일 두 개를 업로드하면 세 개의 파일 정보를 조회할 수 있다.
	 * @When 파일 한 개를 업로드하면
	 * @Then 스토리지 안에 파일을 조회할 수 있다.
	 * @When 파일 두 개를 업로드하면
	 * @Then 스토리지 안에 파일을 조회할 수 있다.
	 */
	@Test
	@Order(1)
	void 파일을_업로드하고_조회한다() {
		파일_업로드_요청(그냥_문장);

		var 파일_조회_요청1 = 파일_조회_요청(그냥_문장);
		파일_조회_요청1.jsonPath("$.name").isEqualTo(그냥_문장);

		파일_업로드_요청(끝맺음_문장, 인사_문장);

		var 파일_조회_요청2 = 파일_조회_요청();

		파일_조회_요청2.jsonPath("$.size()").isEqualTo(3);
	}

	/**
	 * @Fact 사용자는 업로드한 파일을 다운로드할 수 있다.
	 * @When 사용자가 파일을 업로드하면
	 * @Then 파일을 다운로드할 수 있다.
	 */
	@Test
	@Order(3)
	void 업로드한_파일을_다운로드한다() throws IOException {
		// when
		파일_업로드_요청(인사_문장);

		// then
		var 파일_조회_요청 = 파일_조회_요청(인사_문장);
		var response = getValue(파일_조회_요청.returnResult().getResponseBody(), FileResponse.class);
		var 파일_다운로드_요청 = 파일_다운로드_요청(response.getId());
		var responseBody = 파일_다운로드_요청.returnResult().getResponseBody();

		assertNotNull(responseBody);

		var 파일_내용 = getString(responseBody);

		assertThat(파일_내용).isEqualTo(
			getString(Files.readAllBytes(프로덕션_업로드_사진_경로.resolve(인사_문장)))
		);
	}
}
