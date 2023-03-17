package hello.numblemybox.mybox.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MyBoxRepository {

	/**
	 * 파일을 저장합니다. 저장할 때, id가 null 이면 자동으로 값을 매핑하빈다.
	 * @param entity 파일 메타데이터
	 * @return 파일 메타데이터
	 */
	Mono<MyFile> insert(MyFile entity);

	/**
	 * 파일의 이름을 이용해 파일을 조회합니다. 파일 이름에 맞는 메타데이터가 없는 경우, null 을 Mono 로 래핑해서 반환합니다.
	 * @param filename 파일 이름
	 * @return 파일 메타데이터
	 */
	Mono<MyFile> findByFilename(String filename);

	/**
	 * 저장된 모든 파일 정보를 조회합니다.
	 * @return 파일 메타데이터 리스트
	 */
	Flux<MyFile> findAll();
}
