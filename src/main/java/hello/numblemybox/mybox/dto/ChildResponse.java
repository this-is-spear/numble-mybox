package hello.numblemybox.mybox.dto;

import hello.numblemybox.mybox.domain.ItemType;

public record ChildResponse(
	String id,
	String name,
	ItemType type
) {
}
