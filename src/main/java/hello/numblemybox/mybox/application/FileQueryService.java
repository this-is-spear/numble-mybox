package hello.numblemybox.mybox.application;

import org.springframework.stereotype.Service;

import hello.numblemybox.mybox.domain.MyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 서비스와 관련된 문서는 <a href="https://github.com/this-is-spear/numble-mybox/issues/2">링크</a>에서 확인할 수 있습니다.
 */
@Service
@RequiredArgsConstructor
public class FileQueryService {
	private final MyBoxRepository myBoxRepository;

	/**
	 * 파일의 이름을 입력받아 데이터베이스에 저장된 파일 메타데이터를 조회합니다.
	 *
	 * @param filename 파일의 이름
	 * @return 파일의 메타데이터
	 */
	public Mono<FileResponse> getFile(String filename) {
		return myBoxRepository.findByFilename(filename).flatMap(myFile -> Mono.just(
			new FileResponse(myFile.getId(), myFile.getFilename(), myFile.getExtension(), myFile.getSize())
		));
	}

	/**
	 * 파일의 이름을 입력받아 데이터베이스에 저장된 파일 메타데이터를 조회합니다.
	 *
	 * @return 전체 파일의 메타데이터
	 */
	public Flux<FileResponse> getFiles() {
		return myBoxRepository.findAll().flatMap(myFile -> Flux.just(
			new FileResponse(myFile.getId(), myFile.getFilename(), myFile.getExtension(), myFile.getSize())
		));
	}
}
