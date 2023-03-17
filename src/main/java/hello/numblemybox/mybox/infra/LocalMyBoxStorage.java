package hello.numblemybox.mybox.infra;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import hello.numblemybox.mybox.application.MyBoxStorage;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class LocalMyBoxStorage implements MyBoxStorage {
	private static final Path LOCAL_PATH = Paths.get("./src/main/resources/upload");
	private static final int CAPACITY = 1024 * 1024 * 20;

	@Override
	public Mono<String> getPath() {
		return Mono.just(LOCAL_PATH.toString());
	}

	@Override
	public Mono<File> getFile(String filename) {
		return Mono.just(LOCAL_PATH.resolve(filename).toFile());
	}

	@Override
	public Mono<Void> uploadFile(Mono<FilePart> file) {
		return file
			.flatMap(filePart -> filePart.transferTo(LOCAL_PATH.resolve(filePart.filename())));
	}

	@Override
	public Mono<InputStream> downloadFile(Mono<String> filename) {
		return filename
			.publishOn(Schedulers.boundedElastic())
			.map(name -> {
					try {
						return Files.newInputStream(LOCAL_PATH.resolve(name));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			);
	}
}
