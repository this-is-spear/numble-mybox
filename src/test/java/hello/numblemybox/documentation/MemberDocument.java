package hello.numblemybox.documentation;

import static hello.numblemybox.fake.FakeSessionMutator.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;

import hello.numblemybox.member.dto.MemberRequest;
import hello.numblemybox.member.dto.UserInfo;
import reactor.core.publisher.Mono;

class MemberDocument extends DocumentTemplate {
	private static final String SESSION_KEY = "LOGIN_MEMBER";
	public static final UserInfo 사용자 = new UserInfo("123KD3", "rjsckdd12@gmail.com", 1024 * 1024 * 1024 * 3L);

	@Test
	void register() {
		var 가입하려는_사용자 = new MemberRequest("rjsckdd12@gmail.com", "1234");
		webTestClient.post().uri("/members/register")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(가입하려는_사용자)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("members/register")
			);
	}

	@Test
	void login() {
		var 로그인하려는_사용자 = new MemberRequest("rjsckdd12@gmail.com", "1234");

		Mockito.when(memberService.login(로그인하려는_사용자)).thenReturn(Mono.just(사용자));

		webTestClient.post().uri("/members/login")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(로그인하려는_사용자)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("members/login")
			);
	}

	@Test
	void logout() {
		webTestClient.post().uri("/members/logout")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("members/logout")
			);
	}

	@Test
	void me() {
		webTestClient.mutateWith(sessionMutator(sessionBuilder().put(SESSION_KEY, 사용자).build()))
			.get().uri("/members/me")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(
				WebTestClientRestDocumentation.document("members/me")
			);
	}
}
