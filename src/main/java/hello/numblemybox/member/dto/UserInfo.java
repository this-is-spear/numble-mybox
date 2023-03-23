package hello.numblemybox.member.dto;

public record UserInfo(
	String id,
	String username,
	Long availableCapacity
) {
}
