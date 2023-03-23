package hello.numblemybox.fake;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import hello.numblemybox.member.domain.Member;
import hello.numblemybox.member.domain.MemberRepository;
import reactor.core.publisher.Mono;

public class FakeMemberRepository implements MemberRepository {
	private Map<String, Member> map = new HashMap<>();

	@Override
	public Mono<Member> insert(Member entity) {
		var id = UUID.randomUUID().toString();
		var member = new Member(id, entity.getUsername(), entity.getPassword(), entity.getCapacity());
		map.put(id, member);
		return Mono.just(member);
	}

	@Override
	public Mono<Member> findByUsername(String username) {
		return Mono.justOrEmpty(map.values().stream()
			.filter(member -> member.getUsername().equals(username))
			.findFirst());
	}
}
