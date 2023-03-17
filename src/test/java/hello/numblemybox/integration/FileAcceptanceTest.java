package hello.numblemybox.integration;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import hello.numblemybox.SpringBootTemplate;
import hello.numblemybox.mybox.dto.FileResponse;
import hello.numblemybox.mybox.infra.MyBoxMongoRepository;

class FileAcceptanceTest extends SpringBootTemplate {
	@Autowired
	private ObjectMapper OBJECT_MAPPER;
	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private MyBoxMongoRepository myBoxRepository;

	@BeforeEach
	void setUp() throws IOException {
		deleteFiles();
		myBoxRepository.deleteAll().subscribe();
	}

	@AfterAll
	static void afterAll() throws IOException {
		deleteFiles();
	}

	/**
	 * @Fact 파일 한 개를 업로드하면 하나의 파일의 정보를 조회할 수 있고, 파일 두 개를 업로드하면 세 개의 파일 정보를 조회할 수 있다.
	 * @When 파일 한 개를 업로드하면
	 * @Then 스토리지 안에 파일을 조회할 수 있다.
	 * @When 파일 두 개를 업로드하면
	 * @Then 스토리지 안에 파일을 조회할 수 있다.
	 */
	@Test
	@Order(1)
	void 파일을_업로드하고_조회한다() {

		파일_업로드_요청(그냥_문장);

		var 파일_조회_요청1 = 파일_조회_요청(그냥_문장);
		파일_조회_요청1.jsonPath("$.name").isEqualTo(그냥_문장);

		파일_업로드_요청(끝맺음_문장, 인사_문장);

		var 파일_조회_요청2 = 파일_조회_요청();

		파일_조회_요청2.jsonPath("$.size()").isEqualTo(3);
	}

	/**
	 * @Fact 사용자는 업로드한 파일을 다운로드할 수 있다.
	 * @When 사용자가 파일을 업로드하면
	 * @Then 파일을 다운로드할 수 있다.
	 */
	@Test
	@Order(3)
	void 업로드한_파일을_다운로드한다() throws IOException {
		// when
		파일_업로드_요청(인사_문장);

		// then
		var 파일_조회_요청 = 파일_조회_요청(인사_문장);
		var response = getValue(파일_조회_요청.returnResult().getResponseBody(), FileResponse.class);
		var 파일_다운로드_요청 = 파일_다운로드_요청(response.id());
		var responseBody = 파일_다운로드_요청.returnResult().getResponseBody();

		assertNotNull(responseBody);

		var 파일_내용 = getString(responseBody);

		assertThat(파일_내용).isEqualTo(
			getString(Files.readAllBytes(프로덕션_업로드_사진_경로.resolve(인사_문장)))
		);
	}

	private String getString(byte[] responseBody) throws IOException {
		var reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(responseBody)));
		return reader.readLine();
	}

	private <T> T getValue(byte[] data, Class<T> t) throws IOException {
		return OBJECT_MAPPER.readValue(data, t);
	}

	private WebTestClient.BodyContentSpec 파일_다운로드_요청(String id) {
		return webTestClient.post().uri("/mybox/{id}/download", id)
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
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
			var filename = filenames[i];
			builder.part("image", getFileOne(filename))
				.header("Content-disposition",
					String.format("form-data; name=\"%s\"; filename=\"%s\"", requestPartName, filename))
				.contentType(MediaType.TEXT_PLAIN);
		}

		if (filenames.length == 0) {
			builder.part("text", getFileOne(끝맺음_문장))
				.header("Content-disposition",
					String.format("form-data; name=\"%s\"; filename=\"%s\"", requestPartName, 끝맺음_문장))
				.contentType(MediaType.TEXT_PLAIN);
		}

		return webTestClient.post().uri("/mybox/upload")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.bodyValue(builder.build())
			.exchange()
			.expectStatus().isOk();
	}

	private static void deleteFiles() throws IOException {
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(그냥_문장));
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(끝맺음_문장));
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(인사_문장));
	}
}
