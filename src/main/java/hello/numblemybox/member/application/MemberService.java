package hello.numblemybox.member.application;

import java.util.Objects;

import org.springframework.stereotype.Service;

import hello.numblemybox.member.domain.Member;
import hello.numblemybox.member.domain.MemberRepository;
import hello.numblemybox.member.dto.MemberRequest;
import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.member.exception.InvalidMemberException;
import hello.numblemybox.member.exception.InvalidUsernameException;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFolder;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MemberService {
	private static final String ROOT_FOLDER_NAME = "ROOT";
	private final MemberRepository memberRepository;
	private final FolderMyBoxRepository folderMyBoxRepository;

	public Mono<UserInfo> login(MemberRequest memberRequest) {
		return memberRepository.findByUsername(memberRequest.username()).map(member -> {
			if (!Objects.equals(member.getPassword(), memberRequest.password())) {
				throw InvalidMemberException.invalidUser();
			}

			return new UserInfo(member.getId(), member.getUsername(), member.getCapacity());
		}).switchIfEmpty(Mono.error(InvalidMemberException.invalidUser()));
	}

	public Mono<Void> register(MemberRequest registerRequest) {
		var ensureUsername = memberRepository.findByUsername(registerRequest.username())
			.onErrorMap((error) -> {
				throw InvalidUsernameException.alreadyUsername();
			}).map(member -> {
				throw InvalidUsernameException.alreadyUsername();
			}).then();

		var insertMember = memberRepository.insert(getMember(registerRequest))
			.flatMap(member -> folderMyBoxRepository.save(getRoot(member)))
			.then();

		return Mono.when(ensureUsername, insertMember);
	}

	private Member getMember(MemberRequest registerRequest) {
		return Member.createMember(registerRequest.username(), registerRequest.password());
	}

	private MyFolder getRoot(Member member) {
		return MyFolder.createRootFolder(null, ROOT_FOLDER_NAME, member.getId());
	}
}
