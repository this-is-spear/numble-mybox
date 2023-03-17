package hello.numblemybox.mybox.dto;

public record FileResponse(
	String id,
	String name,
	String extension,
	Long size
) {
}
