package hello.numblemybox.member.exception;

public final class InvalidUsernameException extends MemberException{
	private InvalidUsernameException(String message) {
		super(message);
	}

	public static InvalidUsernameException notEmpty() {
		throw new InvalidUsernameException("이름은 비어있을 수 없습니다.");
	}

	public static InvalidUsernameException alreadyUsername() {
		return new InvalidUsernameException("이미 존재하는 이름입니다.");
	}
}
