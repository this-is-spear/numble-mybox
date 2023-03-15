package hello.numblemybox.mybox.exception;

public final class InvalidSizeException extends MyFileException {
	private InvalidSizeException(String message) {
		super(message);
	}

	public static InvalidSizeException tooLarge() {
		throw new InvalidSizeException("파일 크기가 20M 넘을 수 없습니다.");
	}
}
