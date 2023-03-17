package hello.numblemybox.mybox.infra;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
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
						var channel = AsynchronousFileChannel.open(LOCAL_PATH.resolve(name));
						var buffer = ByteBuffer.allocate(CAPACITY);
						channel.read(buffer, 0, buffer, getHandler(channel));
						return new ByteArrayInputStream(buffer.array());
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			);
	}

	private CompletionHandler<Integer, ByteBuffer> getHandler(AsynchronousFileChannel channel) {
		return new CompletionHandler<>() {
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
		};
	}
}
