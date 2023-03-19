package hello.numblemybox.mybox.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.MediaType;

import hello.numblemybox.mybox.exception.DuplicateObjectException;
import hello.numblemybox.mybox.exception.InvalidObjectException;

class MyFolderTest {

	private static final String ADMIN = "rjsckdd12@gmail.com";
	private static final long SIZE = 1024 * 1024 * 12L;
	private static final MyFile MY_FILE = new MyFile("id123", "file.txt", ADMIN, "/Users/...", SIZE,
		MediaType.IMAGE_GIF_VALUE);

	@Test
	@DisplayName("ID를 추가할 수 있다.")
	void addMyObject() {
		MyFolder myFolder = new MyFolder("id1", "folder name", ADMIN);

		myFolder.addMyObject(MY_FILE);
		assertThat(myFolder.getChildrenId()).hasSize(1);
	}

	@Test
	@DisplayName("같은 아이템을 추가하려하면 DuplicateObjectException 예외가 발생한다.")
	void addMyObject_notDuplicated() {
		MyFolder myFolder = new MyFolder("id1", "folder name", ADMIN);
		myFolder.addMyObject(MY_FILE);
		assertThatThrownBy(
			() -> myFolder.addMyObject(MY_FILE)
		).isInstanceOf(DuplicateObjectException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("아이디가 존재하지 않으면 DuplicateObjectException 예외가 발생한다.")
	void addMyObject_idNotNull(String 비어있는_식별자) {
		MyFolder myFolder = new MyFolder("id1", "folder name", ADMIN);
		assertThatThrownBy(
			() -> myFolder.addMyObject(new MyFile(비어있는_식별자, "file.txt", ADMIN, "User/..",
				SIZE, MediaType.IMAGE_GIF_VALUE))
		).isInstanceOf(InvalidObjectException.class);
	}

	@Test
	@DisplayName("ID를 삭제할 수 있다.")
	void removeMyObject() {
		ArrayList<String> childrenId = new ArrayList<>();
		childrenId.add(MY_FILE.getId());
		MyFolder myFolder = new MyFolder(null, "folder name", ADMIN, childrenId);

		myFolder.removeMyObject(MY_FILE);
		assertThat(myFolder.getChildrenId()).hasSize(0);
	}


	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("아이디가 존재하지 않으면 DuplicateObjectException 예외가 발생한다.")
	void removeMyObject_idNotNull(String 비어있는_식별자) {
		MyFolder myFolder = new MyFolder("id1", "folder name", ADMIN);
		assertThatThrownBy(
			() -> myFolder.removeMyObject(new MyFile(비어있는_식별자, "file.txt", ADMIN, "User/..",
				SIZE, MediaType.IMAGE_GIF_VALUE))
		).isInstanceOf(InvalidObjectException.class);
	}

}
