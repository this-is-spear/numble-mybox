package hello.numblemybox.stubs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class FileStubs {

	public static final String 그냥_문장 = "test-text3.txt";
	public static final String 인사_문장 = "test-text1.txt";
	public static final String 끝맺음_문장 = "test-text2.txt";
	public static final Path 테스트할_사진의_경로 = Paths.get("./src/test/resources/test-image");
	public static final Path 업로드할_사진의_경로 = Paths.get("./src/test/resources/upload");
	public static final Path 프로덕션_업로드_사진_경로 = Paths.get("./src/main/resources/upload");
	public static final String 업로드할_사진 = "ElvisPresley.png";

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
