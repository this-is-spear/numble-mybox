package hello.numblemybox.mybox.dto;

import java.io.InputStream;

public record LoadedFileResponse(
	String filename,
	InputStream inputStream
) {
}
