package hello.numblemybox.mybox.application;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import hello.numblemybox.mybox.domain.MyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FileCommandService {
	private static final String ADMIN = "rjsckdd12@gmail.com";
	private final MyBoxStorage myBoxStorage;
	private final MyBoxRepository myBoxRepository;

	/**
	 * 1. 파일의 메타데이터를 식별한다.
	 * 2. 파일 시스템에 업로드한다.
	 * 3. 파일 정보를 저장한다.
	 *
	 * @param filePart 업로드하려는 파일 데이터
	 */
	public Mono<Void> upload(FilePart filePart) {
		var myFile = new MyFile(null, filePart.filename(), ADMIN, filePart.headers().getContentLength(),
			getExtension(filePart));
		myBoxStorage.uploadFiles(Flux.just(filePart)).subscribe();
		myBoxRepository.insert(myFile).subscribe();
		return Mono.empty();
	}

	private String getExtension(FilePart file) {
		return file.filename().split("\\.")[1];
	}
}
