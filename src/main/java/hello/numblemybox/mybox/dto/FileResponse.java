package hello.numblemybox.mybox.dto;

import hello.numblemybox.mybox.domain.ObjectType;

public record FileResponse(
	String id,
	String name,
	ObjectType type,
	String extension,
	Long size,
	String path
) {
}
