package hello.numblemybox.mybox.domain;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import hello.numblemybox.mybox.exception.InvalidExtensionException;
import hello.numblemybox.mybox.exception.InvalidFilenameException;
import hello.numblemybox.mybox.exception.InvalidPathException;
import hello.numblemybox.mybox.exception.InvalidSizeException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 파일과 관련된 문서는 <a href="https://github.com/this-is-spear/numble-mybox/issues/2">링크</a>에서 확인할 수 있습니다.
 */
@Getter
@Document("myFile")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class MyFile {
	private static final int MAXIMUM_LENGTH = 20;
	private static final int MINIMUM_LENGTH = 2;
	private static final long MAXIMUM_SIZE = 20_000_000L;

	private static final List<String> LIMITED_EXTENSION = Arrays.asList("sh", "exe");
	@Id
	@EqualsAndHashCode.Include
	@ToString.Include
	private String id;
	@ToString.Include
	private String name;
	private String userId;
	private ObjectType type;
	private String path;
	@ToString.Include
	private Long size;
	@ToString.Include
	private String extension;
	@ToString.Include
	private String parentId;

	public MyFile(String id, String name, String userId, ObjectType type, String path, Long size, String extension,
		String parentId) {
		ensureName(name);
		ensureSize(size);
		ensureExtension(extension);
		ensurePath(path);
		this.id = id;
		this.name = name;
		this.userId = userId;
		this.type = type;
		this.path = path;
		this.size = size;
		this.extension = extension;
		this.parentId = parentId;
	}

	public MyFile(String id, String name, String userId, String path, Long size,
		String extension) {
		this(id, name, userId, ObjectType.FILE, path, size, extension, null);
	}

	public void addParent(String parentId) {
		this.parentId = parentId;
	}

	public String getFilename() {
		return this.getName();
	}

	public MyFile updateFilename(String filename) {
		return renameFilename(filename);
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

	private void ensureName(String filename) {
		if (filename == null || filename.isBlank() || filename.length() < MINIMUM_LENGTH) {
			throw InvalidFilenameException.tooShort();
		}

		if (filename.length() > MAXIMUM_LENGTH) {
			throw InvalidFilenameException.tooLong();
		}

		if (!filename.contains(".")) {
			throw InvalidFilenameException.invalidFilename();
		}
	}

	private MyFile renameFilename(String filename) {
		return new MyFile(this.getId(), filename, this.getUserId(), this.getType(), this.getPath(),
			this.getSize(), this.getExtension(), this.getParentId());
	}
}
