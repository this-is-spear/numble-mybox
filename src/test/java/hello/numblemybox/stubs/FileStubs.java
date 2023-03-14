package hello.numblemybox.stubs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class FileStubs {

	public static final String 강아지_사진 = "test-image1.png";
	public static final String 테니스장_사진 = "test-image2.jpg";
	public static final String 인사_문장 = "test-text1.txt";
	public static final String 끝맺음_문장 = "test-text2.txt";

	public static byte[] getFileOne(String filename) {
		try {
			return Files.readAllBytes(
				Paths.get(
					Objects.requireNonNull(
						FileStubs.class.getClassLoader().getResource(String.format("upload/%s", filename))
					).getPath()
				)
			);
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
}
