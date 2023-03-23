package hello.numblemybox.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public final class InvalidMemberException extends MemberException {

	private InvalidMemberException(String message) {
		super(message);
	}

	public static InvalidMemberException invalidUser() {
		return new InvalidMemberException("유효하지 않은 사용자입니다.");
	}
}
