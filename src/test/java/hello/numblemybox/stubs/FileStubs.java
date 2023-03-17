package hello.numblemybox.stubs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import hello.numblemybox.mybox.domain.MyFile;

public final class FileStubs {

	public static final String 강아지_사진 = "test-image1.png";
	public static final String 인사_문장 = "test-text1.txt";
	public static final String 끝맺음_문장 = "test-text2.txt";
	public static final Path 테스트할_사진의_경로 = Paths.get("./src/test/resources/test-image");
	public static final Path 업로드할_사진의_경로 = Paths.get("./src/test/resources/upload");
	public static final Path 프로덕션_업로드_사진_경로 = Paths.get("./src/main/resources/upload");
	public static final MyFile 이미지_파일 = new MyFile(null, "image", "rjsckdd12@gmail.com", 테스트할_사진의_경로.toString(),
		1_000_000L, "png");
	public static final MyFile 텍스트_파일 = new MyFile(null, "text", "rjsckdd12@gmail.com", 테스트할_사진의_경로.toString(), 10_000L,
		"txt");
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
