package hello.numblemybox.mybox.exception;

public final class InvalidFoldernameException extends MyFolderException {

	private InvalidFoldernameException(String message) {
		super(message);
	}

	public static InvalidFoldernameException alreadyFilename() {
		throw new InvalidFoldernameException("저장소에 이미 같은 이름의 폴더가 있습니다.");
	}
}
