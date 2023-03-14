package hello.numblemybox.stubs;

import java.io.File;
import java.util.Objects;

public class FileStubs {

	public static final String 강아지_사진 = "test-image1.png";
	public static final String 테니스장_사진 = "test-image2.jpg";

	public static File getFileOne(String filename) {
		return new File(
			String.valueOf(
				Objects.requireNonNull(
					FileStubs.class.getClassLoader().getResource(String.format("upload/%s", filename))
				).getFile()
			)
		);
	}
}
