package hello.numblemybox.mybox.dto;

public record FileResponse(
	String name,
	String extension,
	Long size
) {
}
