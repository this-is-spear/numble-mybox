package hello.numblemybox.mybox.exception;

public abstract class MyFileException extends RuntimeException {
	protected MyFileException(String message) {
		super(message);
	}
}
