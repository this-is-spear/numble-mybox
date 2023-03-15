package hello.numblemybox.mybox.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import hello.numblemybox.mybox.exception.InvalidExtensionException;
import hello.numblemybox.mybox.exception.InvalidFilenameException;
import hello.numblemybox.mybox.exception.InvalidSizeException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Document
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class MyFile {
	@Id
	@EqualsAndHashCode.Include
	@ToString.Include
	String id;
	@ToString.Include
	String filename;
	String username;
	@ToString.Include
	Long size;
	@ToString.Include
	String extension;

	public MyFile(String id, String filename, String username, Long size, String extension) {
		ensureFilename(filename);
		ensureSize(size);
		ensureExtension(extension);

		this.id = id;
		this.filename = filename;
		this.username = username;
		this.size = size;
		this.extension = extension;
	}

	private void ensureFilename(String filename) {
		if (filename == null || filename.isBlank() || filename.length() < 2) {
			throw InvalidFilenameException.tooShort();
		}

		if (filename.length() > 10) {
			throw InvalidFilenameException.tooLong();
		}
	}

	private void ensureSize(Long size) {
		if (size > 2_000_000L) {
			throw InvalidSizeException.tooLarge();
		}
	}

	private void ensureExtension(String extension) {
		if (extension.equals("sh") || extension.equals("exe")) {
			throw InvalidExtensionException.invalidExtension();
		}
	}
}
