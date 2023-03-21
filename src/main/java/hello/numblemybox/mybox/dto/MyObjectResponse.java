package hello.numblemybox.mybox.dto;

import hello.numblemybox.mybox.domain.ObjectType;
import lombok.Getter;

@Getter
public class MyObjectResponse {
	private final String id;
	private final String name;
	private final ObjectType type;

	public MyObjectResponse(String id, String name, ObjectType type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}
}
