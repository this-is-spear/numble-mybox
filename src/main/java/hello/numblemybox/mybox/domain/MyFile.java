package hello.numblemybox.mybox.domain;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import hello.numblemybox.mybox.exception.InvalidExtensionException;
import hello.numblemybox.mybox.exception.InvalidFilenameException;
import hello.numblemybox.mybox.exception.InvalidPathException;
import hello.numblemybox.mybox.exception.InvalidSizeException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 파일과 관련된 문서는 <a href="https://github.com/this-is-spear/numble-mybox/issues/2">링크</a>에서 확인할 수 있습니다.
 */
@Getter
@Document
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class MyFile {
	private static final long MAXIMUM_SIZE = 2_000_000L;
	private static final int MAXIMUM_LENGTH = 20;
	private static final int MINIMUM_LENGTH = 2;
	private static final List<String> LIMITED_EXTENSION = Arrays.asList("sh", "exe");
	@Id
	@EqualsAndHashCode.Include
	@ToString.Include
	private final String id;
	@ToString.Include
	private final String filename;
	private final String username;
	private final String path;
	@ToString.Include
	private Long size;
	@ToString.Include
	private final String extension;

	public MyFile(String id, String filename, String username, String path, Long size, String extension) {
		ensureFilename(filename);
		ensureSize(size);
		ensureExtension(extension);
		ensurePath(path);

		this.id = id;
		this.filename = filename;
		this.username = username;
		this.path = path;
		this.size = size;
		this.extension = extension;
	}

	private void ensurePath(String path) {
		if (path == null || path.isBlank()) {
			throw InvalidPathException.nullOrEmpty();
		}
	}

	private void ensureFilename(String filename) {
		if (filename == null || filename.isBlank() || filename.length() < MINIMUM_LENGTH) {
			throw InvalidFilenameException.tooShort();
		}

		if (filename.length() > MAXIMUM_LENGTH) {
			throw InvalidFilenameException.tooLong();
		}
	}

	private void ensureSize(Long size) {
		if (size > MAXIMUM_SIZE) {
			throw InvalidSizeException.tooLarge();
		}
	}

	private void ensureExtension(String extension) {
		if (LIMITED_EXTENSION.contains(extension)) {
			throw InvalidExtensionException.invalidExtension();
		}
	}
}
