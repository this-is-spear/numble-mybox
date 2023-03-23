package hello.numblemybox.member.domain;

import reactor.core.publisher.Mono;

public interface MemberRepository {
	<S extends Member> Mono<S> insert(S entity);

	Mono<Member> findByUsername(String username);
}
