package hello.numblemybox.member.infra;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import hello.numblemybox.member.domain.Member;
import hello.numblemybox.member.domain.MemberRepository;
import reactor.core.publisher.Mono;

@Repository
public interface MemberMongoRepository extends ReactiveMongoRepository<Member, String>, MemberRepository {

	@Override
	<S extends Member> Mono<S> insert(S entity);

	@Override
	Mono<Member> findByUsername(String username);
}
