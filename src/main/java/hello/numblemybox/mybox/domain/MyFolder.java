package hello.numblemybox.mybox.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import hello.numblemybox.mybox.exception.DuplicateObjectException;
import hello.numblemybox.mybox.exception.InvalidFilenameException;
import hello.numblemybox.mybox.exception.InvalidObjectException;
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
	private String username;
	private ObjectType type;
	@ToString.Include
	private List<MyFile> files;
	@ToString.Include
	private List<MyFolder> children;

	public MyFolder(String id, String name, String username, ObjectType type, List<MyFolder> children,
		List<MyFile> files) {
		ensureName(name);
		this.id = id;
		this.name = name;
		this.username = username;
		this.type = type;
		this.children = children;
		this.files = files;
	}

	public MyFolder(String id, String name, String username) {
		this(id, name, username, ObjectType.FOLDER, new ArrayList<>(), new ArrayList<>());
	}

	public static MyFolder createFolder(String id, String name, String username) {
		return new MyFolder(id, name, username, ObjectType.FOLDER, new ArrayList<>(), new ArrayList<>());
	}

	public static MyFolder createRootFolder(String id, String name, String username) {
		return new MyFolder(id, name, username, ObjectType.ROOT, new ArrayList<>(), new ArrayList<>());
	}

	public List<MyFolder> getChildren() {
		return new ArrayList<>(children);
	}

	public List<MyFile> getFiles() {
		return new ArrayList<>(files);
	}

	public void addMyObject(MyFile myFile) {
		final var id = myFile.getId();
		ensureIdIsNull(id);
		if (this.files.contains(myFile)) {
			throw new DuplicateObjectException();
		}
		files.add(myFile);
	}

	public void addMyObject(MyFolder myFolder) {
		final var id = myFolder.getId();
		ensureIdIsNull(id);
		if (this.children.contains(myFolder)) {
			throw new DuplicateObjectException();
		}
		children.add(myFolder);
	}

	public void removeMyObject(MyFile myFile) {
		final var id = myFile.getId();
		ensureIdIsNull(id);
		files.add(myFile);
	}

	public void removeMyObject(MyFolder myFolder) {
		final var id = myFolder.getId();
		ensureIdIsNull(id);
		children.add(myFolder);
	}

	private void ensureIdIsNull(String id) {
		if (id == null || id.isBlank()) {
			throw new InvalidObjectException();
		}
	}

	private void ensureName(String filename) {
		if (filename == null || filename.isBlank() || filename.length() < MINIMUM_LENGTH) {
			throw InvalidFilenameException.tooShort();
		}

		if (filename.length() > MAXIMUM_LENGTH) {
			throw InvalidFilenameException.tooLong();
		}
	}
}
