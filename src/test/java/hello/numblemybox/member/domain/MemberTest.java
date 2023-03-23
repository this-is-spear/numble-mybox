package hello.numblemybox.member.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import hello.numblemybox.member.exception.InvalidCapacityException;
import hello.numblemybox.member.exception.InvalidPasswordException;
import hello.numblemybox.member.exception.InvalidUsernameException;

class MemberTest {
	private static final String ID = "rjsckdd12@gmail.com";
	private static final String PASSWORD = "1234";

	@Test
	@DisplayName("ID와 비밀번호를 입력받아 사용자를 생성한다.")
	void createMember() {
		assertDoesNotThrow(
			() -> Member.createMember(ID, PASSWORD)
		);
	}

	@Test
	@DisplayName("사용자의 기본 용량은 30GB 이다.")
	void createMember_defaultCapacity() {
		Member member = Member.createMember(ID, PASSWORD);
		assertThat(member.getCapacity()).isEqualTo(30 * 1024 * 1024 * 1024L);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("ID가 비어있으면 InvalidUsernameException 예외가 발생한다.")
	void createMember_usernameNotEmpty(String 비어있는_ID) {
		assertThatThrownBy(
			() -> Member.createMember(비어있는_ID, PASSWORD)
		).isInstanceOf(InvalidUsernameException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("비밀번호가 비어있으면 예외가 발생한다.")
	void createMember_passwordNotEmpty(String 비어있는_비밀번호) {
		assertThatThrownBy(
			() -> Member.createMember(ID, 비어있는_비밀번호)
		).isInstanceOf(InvalidPasswordException.class);
	}

	@ParameterizedTest
	@ValueSource(longs = -1L)
	@NullSource
	@DisplayName("용량은 비어있거나 음수이면 예외가 발생한다.")
	void createMember_invalidCapacity(Long 유효하지_않은_용량) {
		assertThatThrownBy(
			() -> new Member(null, ID, PASSWORD, 유효하지_않은_용량)
		).isInstanceOf(InvalidCapacityException.class);
	}
}
