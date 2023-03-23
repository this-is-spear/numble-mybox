package hello.numblemybox.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class InvalidPasswordException extends MemberException {
	private InvalidPasswordException(String message) {
		super(message);
	}

	public static InvalidPasswordException notEmpty() {
		throw new InvalidPasswordException("비밀번호는 비어있을 수 없습니다.");
	}

}
