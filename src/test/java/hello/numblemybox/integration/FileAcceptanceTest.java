package hello.numblemybox.integration;

import java.io.File;
import java.util.Objects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;

@Disabled
@WebFluxTest
class FileAcceptanceTest {

	private static final String 강아지_사진 = "test-image1.png";
	private static final String 테니스장_사진 = "test-image2.jpg";
	@Autowired
	private WebTestClient webTestClient;

	/**
	 * @Fact 파일 한 개를 업로드하고 파일의 정보를 조회할 수 있다.
	 * @When 파일 한 개를 업로드하면
	 * @Then 스토리지 안에 파일을 조회할 수 있다.
	 */
	@Test
	void 파일을_업로드하고_조회한다() {
		var 파일_업로드_요청 = 파일_업로드_요청(getFileOne(강아지_사진));
		파일_업로드_요청.expectStatus().isOk();

		var 파일_조회_요청 = 파일_조회_요청();
		파일_조회_요청.jsonPath("$.name").isEqualTo(강아지_사진);
	}

	/**
	 * @Fact 사용자는 파일 두 개를 업로드하고 파일들의 정보를 조회할 수 있다.
	 * @When 파일 두 개를 업로드하면
	 * @Then 스토리지 안에 파일을 조회할 수 있다.
	 */
	@Test
	void 파일을_여러개_업로드하고_조회한다() {
		var 파일_업로드_요청 = 파일_업로드_요청(getFileOne(강아지_사진), getFileOne(테니스장_사진));
		파일_업로드_요청.expectStatus().isOk();

		var 파일_조회_요청 = 파일_조회_요청();
		파일_조회_요청.jsonPath("$.[*].name.length()").isEqualTo(2);
	}

	private WebTestClient.BodyContentSpec 파일_조회_요청() {
		return webTestClient.get().uri("/mybox/files/{filename}", 강아지_사진)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	private WebTestClient.ResponseSpec 파일_업로드_요청(File... files) {
		final var builder = new MultipartBodyBuilder();
		final var requestPartName = "files";

		for (File file : files) {
			builder.part("image", file)
				.header("Content-disposition",
					String.format("form-data; name=\"%s\"; filename=\"%s\"", requestPartName, file.getName()));
		}

		if (files.length == 0) {
			builder.part("image", getFileOne("test-image2.jpg"))
				.header("Content-disposition",
					String.format("form-data; name=\"%s\"; filename=\"%s\"", requestPartName, 강아지_사진));
		}

		return webTestClient.post().uri("/mybox/upload")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.bodyValue(builder.build())
			.exchange();
	}

	private File getFileOne(String filename) {
		return new File(
			String.valueOf(
				Objects.requireNonNull(
					this.getClass()
						.getClassLoader()
						.getResource(String.format("upload/%s", filename))
				).getFile()
			)
		);
	}
}