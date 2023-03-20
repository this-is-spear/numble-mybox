package hello.numblemybox.mybox.dto;

import hello.numblemybox.mybox.domain.ObjectType;
import lombok.Getter;

@Getter
public class FileResponse extends MyObjectResponse {
	private final String extension;
	private final Long size;

	public FileResponse(String id, String name, ObjectType type, String extension,
		Long size) {
		super(id, name, type);
		this.extension = extension;
		this.size = size;
	}
}
