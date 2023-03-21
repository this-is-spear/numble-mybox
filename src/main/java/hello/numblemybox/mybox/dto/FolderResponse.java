package hello.numblemybox.mybox.dto;

import hello.numblemybox.mybox.domain.ObjectType;

public record FolderResponse(
	String id,
	String name,
	ObjectType type
) {
}
