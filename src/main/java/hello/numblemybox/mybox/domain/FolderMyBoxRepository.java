package hello.numblemybox.mybox.domain;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FolderMyBoxRepository {

	/**
	 * 식별자를 이용해 MyObject 를 찾습니다.
	 *
	 * @param id 식별자
	 * @return MyObject
	 */
	Mono<MyFolder> findById(String id);

	/**
	 * MyObject 의 타입과 사용자 명을 이용해 MyObject 를 찾습니다.
	 *
	 * @param type     MyObject 의 타입
	 * @param username MyObject 의 주인
	 * @return MyObject
	 */
	Mono<MyFolder> findByTypeAndUsername(ObjectType type, String username);

	/**
	 * MyObject 를 저장합니다. MyObject 는 폴더거나 파일입니다.
	 *
	 * @param entity 식별자가 없는 MyObject
	 * @return 식별자가 있는 MyObject
	 */
	Mono<MyFolder> save(MyFolder entity);

	/**
	 * 전체 폴더를 조회합니다.
	 *
	 * @return 전체 폴더 리스트
	 */
	Flux<MyFolder> findAll();

	/**
	 * 상위 폴더 정보를 이용해 하위 폴더 정보를 조회한다.
	 *
	 * @param parentId 상위 폴더의 식별자
	 * @return 하위 폴더 리스트
	 */
	Flux<MyFolder> findByParentId(String parentId);
}
