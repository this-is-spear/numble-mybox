package hello.numblemybox.member.exception;

public final class RootFolderException extends MemberException {
	private RootFolderException(String message) {
		super(message);
	}

	public static RootFolderException invalidId() {
		return new RootFolderException("Root Folder ID가 Null일 수 없습니다.");
	}
}
