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
	private static final MyFolder MY_FOLDER = MyFolder.createFolder("id123", "file.txt", ADMIN);

	@Test
	@DisplayName("파일을 추가할 수 있다.")
	void addMyFile() {
		var myFolder = new MyFolder("id1", "folder name", ADMIN);

		myFolder.addMyObject(MY_FILE);
		assertThat(myFolder.getFiles()).hasSize(1);
	}

	@Test
	@DisplayName("폴더를 추가할 수 있다.")
	void addMyFolder() {
		var myFolder = new MyFolder("id1", "folder name", ADMIN);

		myFolder.addMyObject(MY_FOLDER);
		assertThat(myFolder.getChildren()).hasSize(1);
	}

	@Test
	@DisplayName("같은 폴더를 추가하려하면 DuplicateObjectException 예외가 발생한다.")
	void addMyFolder_notDuplicated() {
		var myFolder = new MyFolder("id1", "folder name", ADMIN);
		myFolder.addMyObject(MY_FOLDER);
		assertThatThrownBy(
			() -> myFolder.addMyObject(MY_FOLDER)
		).isInstanceOf(DuplicateObjectException.class);
	}

	@Test
	@DisplayName("같은 파일을 추가하려하면 DuplicateObjectException 예외가 발생한다.")
	void addMyFile_notDuplicated() {
		var myFolder = new MyFolder("id1", "folder name", ADMIN);
		myFolder.addMyObject(MY_FILE);
		assertThatThrownBy(
			() -> myFolder.addMyObject(MY_FILE)
		).isInstanceOf(DuplicateObjectException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("아이디가 존재하지 않으면 DuplicateObjectException 예외가 발생한다.")
	void addMyObject_idNotNull(String 비어있는_식별자) {
		var myFolder = new MyFolder("id1", "folder name", ADMIN);
		assertThatThrownBy(
			() -> myFolder.addMyObject(new MyFile(비어있는_식별자, "file.txt", ADMIN, "User/..",
				SIZE, MediaType.IMAGE_GIF_VALUE))
		).isInstanceOf(InvalidObjectException.class);
	}

	@Test
	@DisplayName("ID를 삭제할 수 있다.")
	void removeMyObject() {
		var children = new ArrayList<MyFolder>();
		var files = new ArrayList<MyFile>();

		files.add(MY_FILE);
		var myFolder = new MyFolder(null, "folder name", ADMIN, ObjectType.FOLDER, children, files);

		myFolder.removeMyObject(MY_FILE);
		assertThat(myFolder.getChildren()).hasSize(0);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("아이디가 존재하지 않으면 DuplicateObjectException 예외가 발생한다.")
	void removeMyObject_idNotNull(String 비어있는_식별자) {
		var myFolder = new MyFolder("id1", "folder name", ADMIN);
		assertThatThrownBy(
			() -> myFolder.removeMyObject(new MyFile(비어있는_식별자, "file.txt", ADMIN, "User/..",
				SIZE, MediaType.IMAGE_GIF_VALUE))
		).isInstanceOf(InvalidObjectException.class);
	}

}
