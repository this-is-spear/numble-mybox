package hello.numblemybox.mybox.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import hello.numblemybox.mybox.exception.InvalidFilenameException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Document
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MyObject {
	private static final int MAXIMUM_LENGTH = 20;
	private static final int MINIMUM_LENGTH = 2;
	@Id
	@EqualsAndHashCode.Include
	@ToString.Include
	private final String id;
	@ToString.Include
	private final String objectName;
	private final String username;
	private final ObjectType type;

	public MyObject(String id, String objectName, String username, ObjectType type) {
		ensureFilename(objectName);
		this.id = id;
		this.objectName = objectName;
		this.username = username;
		this.type = type;
	}

	private void ensureFilename(String filename) {
		if (filename == null || filename.isBlank() || filename.length() < MINIMUM_LENGTH) {
			throw InvalidFilenameException.tooShort();
		}

		if (filename.length() > MAXIMUM_LENGTH) {
			throw InvalidFilenameException.tooLong();
		}
	}
}
