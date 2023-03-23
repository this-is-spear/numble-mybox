package hello.numblemybox.mybox.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public abstract class MyFolderException extends RuntimeException {
	protected MyFolderException(String message) {
		super(message);
	}
}
