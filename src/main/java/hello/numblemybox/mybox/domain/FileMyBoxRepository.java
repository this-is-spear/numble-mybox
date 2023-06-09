package hello.numblemybox.mybox.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileMyBoxRepository {

	/**
	 * 파일 메타데이터를 저장합니다. 저장할 때, id가 null 이면 자동으로 값을 매핑하빈다.
	 *
	 * @param entity 파일 메타데이터
	 * @return 파일 메타데이터
	 */
	Mono<MyFile> save(MyFile entity);

	/**
	 * 파일의 이름을 이용해 파일 메타데이터를 조회합니다. 파일 이름에 맞는 메타데이터가 없는 경우, null 을 Mono 로 래핑해서 반환합니다.
	 *
	 * @param objectName 파일 이름
	 * @return 파일 메타데이터
	 */
	Mono<MyFile> findByName(String objectName);

	/**
	 * 저장된 모든 파일 메타데이터를 조회합니다.
	 *
	 * @return 파일 메타데이터 리스트
	 */
	Flux<MyFile> findAll();

	/**
	 * ID를 입력받아 파일 메타데이터를 조회합니다.
	 *
	 * @param id 파일의 식별자
	 * @return 파일의 메티데이터
	 */
	Mono<MyFile> findById(String id);

	/**
	 * 상위 폴더 정보를 이용해 하위 파일 정보를 조회합니다.
	 *
	 * @param parentId 상위 폴더의 식별자
	 * @return 파일 메타데이터 리스트
	 */
	Flux<MyFile> findByParentId(String parentId);

	/**
	 * 상위 폴더 정보와 파일 이름을 입력해 파일 정보를 조회합니다.
	 *
	 * @param parentId 폴더 식별자
	 * @param name     파일 이름
	 * @return 입력한 정보를 가진 파일
	 */
	Mono<MyFile> findByParentIdAndName(String parentId, String name);

	/**
	 * 폴더 식별자와 파일 식별자를 이용해 파일을 조회합니다.
	 *
	 * @param id       파일 식별자
	 * @param parentId 폴더 식별자
	 * @return 파일 메타데이터
	 */
	Mono<MyFile> findByIdAndParentId(String id, String parentId);

	/**
	 * 사용자의 식별자를 이용해 파일 목록을 조회합니다.
	 *
	 * @param userId 사용자의 식별자
	 * @return 파일 목록
	 */
	Flux<MyFile> findByUserId(String userId);
}
