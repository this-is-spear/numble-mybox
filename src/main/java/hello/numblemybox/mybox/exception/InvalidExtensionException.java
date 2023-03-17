package hello.numblemybox.mybox.exception;

public final class InvalidExtensionException extends MyFileException {
	private InvalidExtensionException(String message) {
		super(message);
	}

	public static InvalidExtensionException invalidExtension() {
		throw new InvalidExtensionException("유효하지 않은 확장자입니다.");
	}
}
