package hello.numblemybox.mybox.exception;

public class InvalidObjectException extends RuntimeException {
	public InvalidObjectException() {
		super("ID가 존재하지 않습니다.");
	}
}
