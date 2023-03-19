package hello.numblemybox.mybox.exception;

public class DuplicateObjectException extends MyFolderException {
	public DuplicateObjectException() {
		super("같인 아이템을 담을 수 없습니다.");
	}
}
