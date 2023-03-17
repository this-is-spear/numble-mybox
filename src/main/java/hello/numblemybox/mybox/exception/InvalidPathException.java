package hello.numblemybox.mybox.exception;

public final class InvalidPathException extends MyFileException {
	private InvalidPathException(String message) {
		super(message);
	}

	public static InvalidPathException nullOrEmpty() {
		throw new InvalidPathException("경로는 비어있을 수 없습니다.");
	}
}
