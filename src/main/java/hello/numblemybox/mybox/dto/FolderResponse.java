package hello.numblemybox.mybox.dto;

import hello.numblemybox.mybox.domain.ObjectType;
import lombok.Getter;

@Getter
public final class FolderResponse extends MyObjectResponse {

	public FolderResponse(String id, String name, ObjectType type) {
		super(id, name, type);
	}
}
