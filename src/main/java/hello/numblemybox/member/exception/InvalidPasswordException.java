package hello.numblemybox.member.exception;

public final class InvalidPasswordException extends MemberException{
	private InvalidPasswordException(String message) {
		super(message);
	}

	public static InvalidPasswordException notEmpty() {
		throw new InvalidPasswordException("비밀번호는 비어있을 수 없습니다.");
	}

}
