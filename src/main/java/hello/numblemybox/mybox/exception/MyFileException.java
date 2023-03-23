package hello.numblemybox.mybox.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public abstract class MyFileException extends RuntimeException {
	protected MyFileException(String message) {
		super(message);
	}
}
