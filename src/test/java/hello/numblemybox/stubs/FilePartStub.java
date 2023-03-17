package hello.numblemybox.stubs;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FilePartStub implements FilePart {
	private static final DataBufferFactory factory = new DefaultDataBufferFactory();
	private final Path path;

	public FilePartStub(Path path) {
		this.path = path;
	}

	@Override
	public String name() {
		return "ElvisPresley.png";
	}

	@Override
	public HttpHeaders headers() {
		MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
		valueMap.add(HttpHeaders.CONTENT_LENGTH, "128");
		valueMap.add("name", "text.txt");
		valueMap.add(HttpHeaders.CONTENT_DISPOSITION, "filename=\"text.txt\"");
		valueMap.add(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE);
		return new HttpHeaders(valueMap);
	}

	@Override
	public Flux<DataBuffer> content() {
		return DataBufferUtils.read(
			new ByteArrayResource("hello!".getBytes(StandardCharsets.UTF_8)), factory, 1024);
	}

	@Override
	public String filename() {
		return "ElvisPresley.png";
	}

	@Override
	public Mono<Void> transferTo(Path dest) {
		return Mono.<Void>create(sink -> {
			try {
				Files.copy(this.path, dest, StandardCopyOption.REPLACE_EXISTING);
				sink.success();
			} catch (Exception ex) {
				sink.error(ex);
			}
		}).then();
	}
}
