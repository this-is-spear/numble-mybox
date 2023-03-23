package hello.numblemybox.member.application;

import static reactor.test.StepVerifier.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hello.numblemybox.fake.FakeMemberRepository;
import hello.numblemybox.member.domain.Member;
import hello.numblemybox.member.domain.MemberRepository;
import hello.numblemybox.member.dto.MemberRequest;
import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.member.exception.InvalidMemberException;
import hello.numblemybox.member.exception.InvalidUsernameException;

class MemberServiceTest {

	public static final String ID = "rjsckdd12@gmail.com";
	public static final String PASSWORD = "1234";
	private Member 회원;
	private MemberService memberService;

	@BeforeEach
	void setUp() {
		MemberRepository memberRepository = new FakeMemberRepository();
		memberService = new MemberService(memberRepository);
		회원 = memberRepository.insert(Member.createMember(ID, PASSWORD)).block();
	}

	@Test
	@DisplayName("사용자가 로그인한다.")
	void login() {
		var 로그인_요청 = new MemberRequest(회원.getUsername(), 회원.getPassword());

		create(memberService.login(로그인_요청))
			.expectNext(new UserInfo(회원.getId(), 회원.getUsername(), 회원.getCapacity()))
			.verifyComplete();
	}

	@Test
	@DisplayName("로그인할 때, 사용자가 존재하지 않으면 예외가 발생한다.")
	void login_emptyUser() {
		var 존재하지_않는_회원의_ID = "not fount user";
		var 로그인_요청 = new MemberRequest(존재하지_않는_회원의_ID, 회원.getPassword());

		create(memberService.login(로그인_요청))
			.expectError(InvalidMemberException.class)
			.verify();
	}

	@Test
	@DisplayName("로그인할 때, 비밀번호가 틀리면 예외가 발생한다.")
	void login_invalidPassword() {
		var 틀린_비밀번호 = "incorrect password";
		var 로그인_요청 = new MemberRequest(회원.getUsername(), 틀린_비밀번호);

		create(memberService.login(로그인_요청))
			.expectError(InvalidMemberException.class)
			.verify();
	}

	@Test
	@DisplayName("사용자가 회원가입한다.")
	void register() {
		var 회원가입_요청 = new MemberRequest("this-is-spear@email.com", "1111");

		create(memberService.register(회원가입_요청))
			.verifyComplete();
	}

	@Test
	void register_alreadyUsername() {
		var 이미있는_회원이름 = 회원.getUsername();
		var 회원가입_요청 = new MemberRequest(이미있는_회원이름, "1111");
		create(memberService.register(회원가입_요청))
			.expectError(InvalidUsernameException.class)
			.verify();
	}
}
