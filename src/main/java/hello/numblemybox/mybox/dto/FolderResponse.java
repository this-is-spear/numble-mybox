package hello.numblemybox.mybox.dto;

import java.util.List;

public record FolderResponse (
	String folderId,
	String folderName,
	List<ChildResponse> children
) {
}
