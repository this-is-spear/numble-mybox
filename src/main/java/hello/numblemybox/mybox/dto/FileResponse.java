package hello.numblemybox.mybox.dto;

import hello.numblemybox.mybox.domain.ObjectType;
import lombok.Getter;

@Getter
public final class FileResponse extends MyObjectResponse {
	private final String extension;
	private final Long size;
	private final String path;

	public FileResponse(String id, String name, ObjectType type, String extension,
		Long size, String path) {
		super(id, name, type);
		this.extension = extension;
		this.size = size;
		this.path = path;
	}
}
