package hello.numblemybox.mybox.domain;

import java.util.Arrays;
import java.util.List;

import hello.numblemybox.mybox.exception.InvalidExtensionException;
import hello.numblemybox.mybox.exception.InvalidPathException;
import hello.numblemybox.mybox.exception.InvalidSizeException;
import lombok.Getter;
import lombok.ToString;

/**
 * 파일과 관련된 문서는 <a href="https://github.com/this-is-spear/numble-mybox/issues/2">링크</a>에서 확인할 수 있습니다.
 */
@Getter
public final class MyFile extends MyObject {
	private static final long MAXIMUM_SIZE = 20_000_000L;

	private static final List<String> LIMITED_EXTENSION = Arrays.asList("sh", "exe");
	private final String path;
	@ToString.Include
	private Long size;
	@ToString.Include
	private final String extension;

	public MyFile(String id, String objectName, String username, String path, Long size,
		String extension) {
		super(id, objectName, username, ObjectType.FILE);
		ensureSize(size);
		ensureExtension(extension);
		ensurePath(path);
		this.path = path;
		this.size = size;
		this.extension = extension;
	}


	private void ensurePath(String path) {
		if (path == null || path.isBlank()) {
			throw InvalidPathException.nullOrEmpty();
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

	public String getFilename() {
		return this.getName();
	}
}
