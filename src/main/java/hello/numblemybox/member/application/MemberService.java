package hello.numblemybox.member.application;

import org.springframework.stereotype.Service;

import hello.numblemybox.member.dto.MemberRequest;
import hello.numblemybox.member.dto.UserInfo;
import reactor.core.publisher.Mono;

@Service
public class MemberService {
	public Mono<UserInfo> login(MemberRequest memberRequest) {
		return null;
	}

	public Mono<Void> register(MemberRequest registerRequest) {
		return null;
	}
}
