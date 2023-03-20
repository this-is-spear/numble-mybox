package hello.numblemybox.mybox.dto;

import java.util.List;

import hello.numblemybox.mybox.domain.ObjectType;
import lombok.Getter;

@Getter
public class FolderResponse extends MyObjectResponse {
	private final List<? extends MyObjectResponse> children;

	public FolderResponse(String id, String name, ObjectType type,
		List<? extends MyObjectResponse> children) {
		super(id, name, type);
		this.children = children;
	}
}
