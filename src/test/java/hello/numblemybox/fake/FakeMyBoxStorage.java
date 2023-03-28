package hello.numblemybox.fake;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.springframework.http.codec.multipart.FilePart;

import hello.numblemybox.mybox.application.MyBoxStorage;
import hello.numblemybox.stubs.FilePartStub;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class FakeMyBoxStorage implements MyBoxStorage {

	private static final int CAPACITY = 1024 * 1024 * 10;

	@Override
	public String getPath() {
		return 업로드할_사진의_경로.toString();
	}

	@Override
	public Mono<File> getFile(String filename) {
		return Mono.just(업로드할_사진의_경로.resolve(filename).toFile());
	}

	@Override
	public Mono<Void> uploadFile(FilePart file, String fileId) {
		return Mono.defer(
			() -> file.transferTo(업로드할_사진의_경로.resolve(fileId))
		).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<InputStream> downloadFile(Mono<String> fileId) {
		return fileId
			.publishOn(Schedulers.boundedElastic())
			.map(id -> {
				try {
					var channel = AsynchronousFileChannel.open(업로드할_사진의_경로.resolve(id));
					var buffer = ByteBuffer.allocate(CAPACITY);
					channel.read(buffer, 0, buffer, new CompletionHandler<>() {
						@Override
						public void completed(Integer result, ByteBuffer attachment) {
							try {
								if (channel.isOpen()) {
									channel.close();
								}
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}

						@Override
						public void failed(Throwable exc, ByteBuffer attachment) {
							exc.printStackTrace();
						}
					});

					return new ByteArrayInputStream(buffer.array());
				} catch (IOException e) {
					throw new RuntimeException();
				}
			});
	}

	@Override
	public Mono<Void> deleteFile(String fileId) {
		return Mono.fromCallable(() -> Files.deleteIfExists(업로드할_사진의_경로.resolve(fileId)))
			.subscribeOn(Schedulers.boundedElastic())
			.then();
	}

	@Test
	void uploadFile() throws IOException {
		var filePart = new FilePartStub(테스트할_사진의_경로.resolve(업로드할_사진));
		String fileId = "12344";
		StepVerifier.create(uploadFile(filePart, fileId))
			.verifyComplete();
		assertThat(Files.exists(업로드할_사진의_경로.resolve(fileId))).isTrue();
		Files.deleteIfExists(업로드할_사진의_경로.resolve(fileId));
	}
}
