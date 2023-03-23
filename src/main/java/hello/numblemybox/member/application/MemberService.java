package hello.numblemybox.member.application;

import java.util.Objects;

import org.springframework.stereotype.Service;

import hello.numblemybox.member.domain.Member;
import hello.numblemybox.member.domain.MemberRepository;
import hello.numblemybox.member.dto.MemberRequest;
import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.member.exception.InvalidMemberException;
import hello.numblemybox.member.exception.InvalidUsernameException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;

	public Mono<UserInfo> login(MemberRequest memberRequest) {
		return memberRepository.findByUsername(memberRequest.username()).map(member -> {
			if (!Objects.equals(member.getPassword(), memberRequest.password())) {
				throw InvalidMemberException.invalidUser();
			}

			return new UserInfo(member.getId(), member.getUsername(), member.getCapacity());
		}).switchIfEmpty(Mono.error(InvalidMemberException.invalidUser()));
	}

	public Mono<Void> register(MemberRequest registerRequest) {
		return memberRepository.findByUsername(registerRequest.username())
			.map(member -> {
				throw InvalidUsernameException.alreadyUsername();
			})
			.switchIfEmpty(
				memberRepository.insert(Member.createMember(registerRequest.username(), registerRequest.password())))
			.then();
	}

}
