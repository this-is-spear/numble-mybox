package hello.numblemybox.mybox.exception;

public final class InvalidFilenameException extends MyFileException {

	private InvalidFilenameException(String message) {
		super(message);
	}

	public static InvalidFilenameException tooLong() {
		throw new InvalidFilenameException("20 글자를 넘길 수 없습니다.");
	}

	public static InvalidFilenameException tooShort() {
		throw new InvalidFilenameException("파일 이름이 너무 짧거나 비어 있습니다.");
	}

	public static InvalidFilenameException alreadyFilename() {
		throw new InvalidFilenameException("저장소에 이미 같은 이름의 파일이 있습니다.");
	}
}
