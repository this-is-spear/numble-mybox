package hello.numblemybox.mybox.domain;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface FolderMyBoxRepository {

	/**
	 * 식별자를 이용해 MyObject 를 찾습니다.
	 * @param id 식별자
	 * @return MyObject
	 */
	Mono<MyObject> findById(String id);

	/**
	 * MyObject 의 타입과 사용자 명을 이용해 MyObject 를 찾습니다.
	 * @param type MyObject 의 타입
	 * @param username MyObject 의 주인
	 * @return MyObject
	 */
	Mono<MyObject> findByTypeAndUsername(ObjectType type, String username);

	/**
	 * MyObject 를 저장합니다. MyObject 는 폴더거나 파일입니다.
	 * @param entity 식별자가 없는 MyObject
	 * @return 식별자가 있는 MyObject
	 * @param <S> MyObject 을 상속하는 클래스인 MyFile 과 MyFolder 중 하나의 정보를 포함합니다.
	 */
	<S extends MyObject> Mono<S> insert(S entity);

}
