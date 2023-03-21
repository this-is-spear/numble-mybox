package hello.numblemybox.mybox.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import hello.numblemybox.mybox.exception.InvalidExtensionException;
import hello.numblemybox.mybox.exception.InvalidFilenameException;
import hello.numblemybox.mybox.exception.InvalidPathException;
import hello.numblemybox.mybox.exception.InvalidSizeException;
import hello.numblemybox.stubs.FileStubs;

class MyFileTest {

	private static final String 파일_식별자 = "id";
	private static final String 파일_이름 = "filename.jpg";
	private static final String 저장된_경로 = FileStubs.업로드할_사진의_경로.toString();
	private static final String 파일_주인_식별자 = "rjsckdd12@gmail.com";
	private static final long 파일_크기 = 123L;
	private static final String 파일_확장자 = "image/jpeg";

	@Test
	@DisplayName("식별자, 파일의 이름, 파일 소유자, 크기, 파일 확장자 정보를 가진다.")
	void myFile_create() {
		var file = assertDoesNotThrow(
			() -> new MyFile(파일_식별자, 파일_이름, 파일_주인_식별자, 저장된_경로, 파일_크기, 파일_확장자)
		);

		assertAll(
			() -> assertThat(file.getId()).isEqualTo(파일_식별자),
			() -> assertThat(file.getFilename()).isEqualTo(파일_이름),
			() -> assertThat(file.getUsername()).isEqualTo(파일_주인_식별자),
			() -> assertThat(file.getSize()).isEqualTo(파일_크기),
			() -> assertThat(file.getExtension()).isEqualTo(파일_확장자)
		);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"s", "2"})
	@DisplayName("파일 이름은 2 글자 이하이거나 비어있으면 예외가 발생한다.")
	void myFile_nameIsNotEmpty(String 짧은_파일_이름) {
		assertThatThrownBy(
			() -> new MyFile(파일_식별자, 짧은_파일_이름, 파일_주인_식별자, 저장된_경로, 파일_크기, 파일_확장자)
		).isInstanceOf(InvalidFilenameException.class)
			.hasMessage("파일 이름이 너무 짧거나 비어 있습니다.");
	}

	@ParameterizedTest
	@ValueSource(strings = {"asdljasdfdsdfsafsfsk1asd32kdf.jpg", "sdjkasdfl3sadfsdfsfsl23nfddsf.png"})
	@DisplayName("파일 이름 크기가 20 글자보다 크면 예외가 발생한다.")
	void myFile_nameShouldBeRule(String 큰_길이_파일_이름) {
		assertThatThrownBy(
			() -> new MyFile(파일_식별자, 큰_길이_파일_이름, 파일_주인_식별자, 저장된_경로, 파일_크기, 파일_확장자)
		).isInstanceOf(InvalidFilenameException.class)
			.hasMessage("20 글자를 넘길 수 없습니다.");
	}

	@ParameterizedTest
	@ValueSource(longs = {30_000_000L, 40_000_000L})
	@DisplayName("크기는 20MB 이상이면 예외가 발생한다.")
	void myFile_sizeNotOver20MB(Long 너무_큰_파일_크기) {
		assertThatThrownBy(
			() -> new MyFile(파일_식별자, 파일_이름, 파일_주인_식별자, 저장된_경로, 너무_큰_파일_크기, 파일_확장자)
		).isInstanceOf(InvalidSizeException.class)
			.hasMessage("파일 크기가 20M 넘을 수 없습니다.");
	}

	@ParameterizedTest
	@ValueSource(strings = {"exe", "sh"})
	@DisplayName("확장자 종류는 exe,sh 이면 예외가 발생한다.")
	void myFile_extensionShouldBeRule(String 유효하지_않은_확장자) {
		assertThatThrownBy(
			() -> new MyFile(파일_식별자, 파일_이름, 파일_주인_식별자, 저장된_경로, 파일_크기, 유효하지_않은_확장자)
		).isInstanceOf(InvalidExtensionException.class)
			.hasMessage("유효하지 않은 확장자입니다.");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("경로는 null이거나 비어있으면 예외가 발생한다.")
	void myFile_extensionNotNullAndEmpty(String 비어있는_경로) {
		assertThatThrownBy(
			() -> new MyFile(파일_식별자, 파일_이름, 파일_주인_식별자, 비어있는_경로, 파일_크기, 파일_확장자)
		).isInstanceOf(InvalidPathException.class)
			.hasMessage("경로는 비어있을 수 없습니다.");
	}
}
