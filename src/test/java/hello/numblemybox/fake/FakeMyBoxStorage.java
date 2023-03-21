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
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.http.codec.multipart.FilePart;

import hello.numblemybox.mybox.application.MyBoxStorage;
import hello.numblemybox.stubs.FilePartStub;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class FakeMyBoxStorage implements MyBoxStorage {

	private static final int CAPACITY = 1024 * 1024 * 10;

	@Override
	public Mono<String> getPath() {
		return Mono.just(업로드할_사진의_경로.toString());
	}

	@Override
	public Mono<File> getFile(String filename) {
		return Mono.just(업로드할_사진의_경로.resolve(filename).toFile());
	}

	@Override
	public Mono<Void> uploadFile(Mono<FilePart> partMono) {
		return partMono.flatMap(
			filePart -> filePart.transferTo(업로드할_사진의_경로.resolve(filePart.filename()))
		).then();
	}

	@Override
	public Mono<InputStream> downloadFile(Mono<String> filename) {
		return filename
			.publishOn(Schedulers.boundedElastic())
			.map(name -> {
					try {
						var channel = AsynchronousFileChannel.open(업로드할_사진의_경로.resolve(name));
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
						throw new RuntimeException(e);
					}
				}
			);
	}

	@Test
	void uploadFile() throws IOException {
		var filePart = new FilePartStub(테스트할_사진의_경로.resolve(업로드할_사진));
		uploadFile(Mono.just(filePart)).subscribe();
		assertThat(Files.exists(업로드할_사진의_경로.resolve(업로드할_사진))).isTrue();
		Files.deleteIfExists(업로드할_사진의_경로.resolve(업로드할_사진));
	}
}
