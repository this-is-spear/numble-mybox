package hello.numblemybox.member.ui;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.server.WebSession;

import hello.numblemybox.member.application.MemberService;
import hello.numblemybox.member.dto.MemberRequest;
import hello.numblemybox.member.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
	private static final String SESSION_KEY = "LOGIN_MEMBER";
	private final MemberService memberService;

	/**
	 * 아이디와 비밀번호를 입력해 로그인합니다. 로그인에 성공하게 되면 세션에 저장합니다.
	 *
	 * @param webSession   세션 정보
	 * @param loginRequest 로그인하기 위한 입력 정보
	 * @return 반환값 없음
	 */
	@PostMapping(
		value = "/login",
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	public Mono<Void> login(
		WebSession webSession,
		@RequestBody MemberRequest loginRequest
	) {
		return memberService.login(loginRequest).mapNotNull(
			user -> webSession.getAttributes().put(SESSION_KEY, user)
		).then();
	}

	/**
	 * 아이디와 비밀번호를 입력해 회원가입 합니다.
	 *
	 * @param registerRequest 회원가입하기 위한 입력 정보
	 * @return 반환값 없음
	 */
	@PostMapping(
		value = "/register",
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	public Mono<Void> register(
		@RequestBody MemberRequest registerRequest
	) {
		return memberService.register(registerRequest);
	}

	/**
	 * 세션에 저장된 사용자의 정보를 확인합니다. 로그인된 사용자만 사용할 수 있습니다.
	 *
	 * @param userInfo 세션에 저장된 사용자의 정보
	 * @return 세션에 저장된 사용자의 정보
	 */
	@GetMapping(
		value = "/me",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Mono<UserInfo> me(@SessionAttribute(SESSION_KEY) UserInfo userInfo) {
		return Mono.just(userInfo);
	}

	/**
	 * 사용자를 로그아웃합니다.
	 *
	 * @param webSession 세션 정보
	 * @return 반환값 없음
	 */
	@PostMapping("/logout")
	public Mono<Void> logout(
		WebSession webSession
	) {
		return webSession.invalidate().then();
	}
}
