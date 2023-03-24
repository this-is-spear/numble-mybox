package hello.numblemybox.integration;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class MemberAcceptanceTest extends AcceptanceTemplate {

	/**
	 * @Fact 사용자는 회원 가입 후 로그인할 수 있다.
	 * @Given 회원가입하면
	 * @When 로그인해서
	 * @Then 자신의 정보를 조회할 수 있다.
	 */
	@Test
	void 회원가입_후_로그인한다() {
		// given
		회원가입_요청(사용자의_정보);

		// when
		로그인_요청(사용자의_정보);

		// then
		사용자_정보조회_요청().jsonPath("username", 사용자의_정보.username());
	}

	@Test
	void 사용자의_루트_폴더를_조회한다() throws IOException {
		var 루트_폴더_메타데이터_조회 = 루트_폴더_메타데이터_조회_요청();
		var rootId = getRootId(루트_폴더_메타데이터_조회);
		assertThat(rootId).isNotBlank();
	}
}
