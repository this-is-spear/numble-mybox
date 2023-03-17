package hello.numblemybox.integration;

import static hello.numblemybox.stubs.FileStubs.*;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;

import hello.numblemybox.SpringBootTemplate;
import hello.numblemybox.mybox.infra.MyBoxMongoRepository;

class FileAcceptanceTest extends SpringBootTemplate {

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private MyBoxMongoRepository myBoxRepository;

	@BeforeEach
	void setUp() {
		myBoxRepository.deleteAll().subscribe();
	}

	/**
	 * @Fact 파일 한 개를 업로드하고 파일의 정보를 조회할 수 있다.
	 * @When 파일 한 개를 업로드하면
	 * @Then 스토리지 안에 파일을 조회할 수 있다.
	 */
	@Test
	@Order(1)
	void 파일을_업로드하고_조회한다() throws IOException {
		var 파일_업로드_요청 = 파일_업로드_요청(인사_문장);
		파일_업로드_요청.expectStatus().isOk();

		var 파일_조회_요청 = 파일_조회_요청(인사_문장);
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(인사_문장));
		파일_조회_요청.jsonPath("$.name").isEqualTo(인사_문장);
	}

	/**
	 * @Fact 사용자는 파일 두 개를 업로드하고 파일들의 정보를 조회할 수 있다.
	 * @When 파일 두 개를 업로드하면
	 * @Then 스토리지 안에 파일을 조회할 수 있다.
	 */
	@Test
	@Order(2)
	void 파일을_여러개_업로드하고_조회한다() throws IOException {
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(끝맺음_문장));
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(인사_문장));
		var 파일_업로드_요청 = 파일_업로드_요청(끝맺음_문장, 인사_문장);
		파일_업로드_요청.expectStatus().isOk();

		var 파일_조회_요청 = 파일_조회_요청();
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(끝맺음_문장));
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(인사_문장));
		파일_조회_요청.jsonPath("$.size()").isEqualTo(2);
	}

	private WebTestClient.BodyContentSpec 파일_조회_요청(String filename) {
		return webTestClient.get().uri("/mybox/files/{filename}", filename)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	private WebTestClient.BodyContentSpec 파일_조회_요청() {
		return webTestClient.get().uri("/mybox/files")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	private WebTestClient.ResponseSpec 파일_업로드_요청(String... filenames) {
		final var builder = new MultipartBodyBuilder();
		final var requestPartName = "files";

		for (int i = 0; i < filenames.length; i++) {
			String filename = filenames[i];
			builder.part("image", getFileOne(filename))
				.header("Content-disposition",
					String.format("form-data; name=\"%s\"; filename=\"%s\"", requestPartName, filename));
		}

		if (filenames.length == 0) {
			builder.part("image", getFileOne(끝맺음_문장))
				.header("Content-disposition",
					String.format("form-data; name=\"%s\"; filename=\"%s\"", requestPartName, 끝맺음_문장));
		}

		return webTestClient.post().uri("/mybox/upload")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.bodyValue(builder.build())
			.exchange();
	}
}
