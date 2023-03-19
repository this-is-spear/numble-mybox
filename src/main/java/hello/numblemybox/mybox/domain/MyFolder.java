package hello.numblemybox.mybox.domain;

import java.util.ArrayList;
import java.util.List;

import hello.numblemybox.mybox.exception.DuplicateObjectException;
import hello.numblemybox.mybox.exception.InvalidObjectException;

public final class MyFolder extends MyObject {

	private final List<String> childrenId;

	public MyFolder(String id, String name, String username, List<String> childrenId) {
		super(id, name, username, ObjectType.FOLDER);
		this.childrenId = childrenId;
	}

	public MyFolder(String id, String name, String username) {
		super(id, name, username, ObjectType.FOLDER);
		this.childrenId = new ArrayList<>();
	}

	public List<String> getChildrenId() {
		return new ArrayList<>(childrenId);
	}

	public <T extends MyObject> void addMyObject(T t) {
		final var id = t.getId();
		ensureIdIsNull(id);
		ensureIdisDuplicated(id);
		childrenId.add(id);
	}

	public <T extends MyObject> void removeMyObject(T t) {
		final var id = t.getId();
		ensureIdIsNull(id);
		childrenId.remove(id);
	}

	private void ensureIdisDuplicated(String id) {
		if (this.childrenId.contains(id)) {
			throw new DuplicateObjectException();
		}
	}

	private void ensureIdIsNull(String id) {
		if (id == null || id.isBlank()) {
			throw new InvalidObjectException();
		}
	}

}
