package hello.numblemybox.mybox.application;

import org.springframework.stereotype.Service;

import hello.numblemybox.mybox.dto.FileResponse;
import reactor.core.publisher.Mono;

@Service
public class FileQueryService {

	public Mono<FileResponse> getFile(String filename) {
		return null;
	}
}
