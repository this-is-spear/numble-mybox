package hello.numblemybox.member.exception;

public final class InvalidCapacityException extends MemberException{
	private InvalidCapacityException(String message) {
		super(message);
	}

	public static InvalidCapacityException invalidCapacity() {
		throw new InvalidCapacityException("허용된 용량이 아닙니다.");
	}
}
