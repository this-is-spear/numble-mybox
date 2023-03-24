package hello.numblemybox.mybox.exception;

public final class CapacityException extends MyFileException {

	private CapacityException(String message) {
		super(message);
	}

	public static CapacityException over(long availableCapacity) {
		return new CapacityException(String.format("허용된 용량을 초과했습니다. 허용된 용량은 %d 입니다.", availableCapacity));
	}
}
