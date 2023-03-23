package hello.numblemybox.mybox.domain;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import hello.numblemybox.mybox.exception.InvalidFilenameException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Document
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class MyFolder {

	private static final int MAXIMUM_LENGTH = 20;
	private static final int MINIMUM_LENGTH = 2;

	@Id
	@EqualsAndHashCode.Include
	@ToString.Include
	private String id;
	@ToString.Include
	private String name;
	private String userId;
	@ToString.Include
	private ObjectType type;
	@ToString.Include
	private String parentId;

	public MyFolder(String id, String name, String userId, ObjectType type, String parentId) {
		ensureName(name);
		this.id = id;
		this.name = name;
		this.userId = userId;
		this.type = type;
		this.parentId = parentId;
	}

	public static MyFolder createFolder(String id, String name, String userId, String parentId) {
		return new MyFolder(id, name, userId, ObjectType.FOLDER, parentId);
	}

	public static MyFolder createRootFolder(String id, String name, String userId) {
		return new MyFolder(id, name, userId, ObjectType.ROOT, null);
	}

	public void setId(String id) {
		this.id = id;
	}

	private void ensureName(String filename) {
		if (filename == null || filename.isBlank() || filename.length() < MINIMUM_LENGTH) {
			throw InvalidFilenameException.tooShort();
		}

		if (filename.length() > MAXIMUM_LENGTH) {
			throw InvalidFilenameException.tooLong();
		}
	}

	public MyFolder updateName(String foldername) {
		if (Objects.equals(this.getType(), ObjectType.ROOT)) {
			throw new IllegalArgumentException("루트 폴더의 이름은 변경할 수 없습니다.");
		}
		return new MyFolder(this.getId(), foldername, this.getUserId(), this.getType(), this.getParentId());
	}
}
