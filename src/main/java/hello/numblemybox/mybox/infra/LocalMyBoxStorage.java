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
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class LocalMyBoxStorage implements MyBoxStorage {
	private static final Path LOCAL_PATH = Paths.get("./src/main/resources/upload");

	@Override
	public String getPath() {
		return LOCAL_PATH.toString();
	}

	@Override
	public Mono<File> getFile(String filename) {
		return Mono.just(LOCAL_PATH.resolve(filename).toFile());
	}

	@Override
	public Mono<Void> uploadFile(FilePart file, String fileId) {
		return Mono.defer(() -> file.transferTo(LOCAL_PATH.resolve(fileId)))
			.subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<InputStream> downloadFile(Mono<String> fileId) {
		return fileId
			.publishOn(Schedulers.boundedElastic()).map(id -> {
				try {
					return Files.newInputStream(LOCAL_PATH.resolve(id));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
	}

	@Override
	@SneakyThrows
	public Mono<Void> deleteFile(String fileId) {
		return Mono.fromCallable(() -> Files.deleteIfExists(LOCAL_PATH.resolve(fileId)))
			.subscribeOn(Schedulers.boundedElastic())
			.then();
	}
}
