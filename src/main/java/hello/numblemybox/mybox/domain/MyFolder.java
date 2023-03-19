package hello.numblemybox.mybox.domain;

import java.util.ArrayList;
import java.util.List;

import hello.numblemybox.mybox.exception.DuplicateObjectException;
import hello.numblemybox.mybox.exception.InvalidObjectException;

public final class MyFolder extends MyObject {
	private final List<MyFile> files;
	private final List<MyFolder> children;

	public MyFolder(String id, String name, String username, ObjectType type, List<MyFolder> children,
		List<MyFile> files) {
		super(id, name, username, type);
		this.children = children;
		this.files = new ArrayList<>();
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

	public <T extends MyObject> void addMyObject(T t) {
		final var id = t.getId();
		ensureIdIsNull(id);
		ensureIdIsDuplicated(t);
		addItem(t);
	}

	public <T extends MyObject> void removeMyObject(T t) {
		final var id = t.getId();
		ensureIdIsNull(id);
		removeItem(t);
	}

	private <T extends MyObject> void addItem(T t) {
		if (t instanceof MyFolder myFolder) {
			children.add(myFolder);
		} else if (t instanceof MyFile myFile) {
			files.add(myFile);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private <T extends MyObject> void removeItem(T t) {
		if (t instanceof MyFolder myFolder) {
			children.remove(myFolder);
		} else if (t instanceof MyFile myFile) {
			files.remove(myFile);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private <T extends MyObject> void ensureIdIsDuplicated(T t) {
		if (t instanceof MyFolder myFolder) {
			if (this.children.contains(myFolder)) {
				throw new DuplicateObjectException();
			}
		} else if (t instanceof MyFile myFile) {
			if (this.files.contains(myFile)) {
				throw new DuplicateObjectException();
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void ensureIdIsNull(String id) {
		if (id == null || id.isBlank()) {
			throw new InvalidObjectException();
		}
	}

}
