package hello.numblemybox.member.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import hello.numblemybox.member.exception.InvalidCapacityException;
import hello.numblemybox.member.exception.InvalidPasswordException;
import hello.numblemybox.member.exception.InvalidUsernameException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 서비스를 사용하는 사용자의 정보를 저장합니다. 사용자가 사용할 수 있는 스토리지 허용량은 30GB입니다.
 */
@Getter
@Document
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Member {
	private static final Long DEFAULT_CAPACITY = 30 * 1024 * 1024 * 1024L;
	@Id
	@EqualsAndHashCode.Include
	@ToString.Include
	private String id;
	@ToString.Include
	private String username;
	private String password;
	@ToString.Include
	private Long capacity;

	public Member(String id, String username, String password, Long capacity) {
		ensureUsername(username);
		ensurePassword(password);
		ensureCapacity(capacity);
		this.id = id;
		this.username = username;
		this.password = password;
		this.capacity = capacity;
	}

	public Member(String username, String password, Long capacity) {
		this(null, username, password, capacity);
	}

	public static Member createMember(String username, String password) {
		return new Member(null, username, password, DEFAULT_CAPACITY);
	}

	private void ensureUsername(String username) {
		if (username == null || username.isBlank()) {
			throw InvalidUsernameException.notEmpty();
		}
	}

	private void ensurePassword(String password) {
		if (password == null || password.isBlank()) {
			throw InvalidPasswordException.notEmpty();
		}
	}

	private void ensureCapacity(Long capacity) {
		if (capacity == null || capacity < 0) {
			throw InvalidCapacityException.invalidCapacity();
		}
	}
}
