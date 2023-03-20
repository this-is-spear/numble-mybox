package hello.numblemybox.integration;

import static hello.numblemybox.stubs.FileStubs.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import hello.numblemybox.SpringBootTemplate;
import hello.numblemybox.mybox.infra.FileMyBoxMongoRepository;

class AcceptanceTemplate extends SpringBootTemplate {

	@Autowired
	protected ObjectMapper OBJECT_MAPPER;
	@Autowired
	protected WebTestClient webTestClient;
	@Autowired
	protected FileMyBoxMongoRepository myBoxRepository;

	@BeforeEach
	void setUp() throws IOException {
		deleteFiles();
		myBoxRepository.deleteAll().subscribe();
	}

	@AfterAll
	static void afterAll() throws IOException {
		deleteFiles();
	}

	private static void deleteFiles() throws IOException {
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(그냥_문장));
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(끝맺음_문장));
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(인사_문장));
	}

	protected WebTestClient.BodyContentSpec 파일_다운로드_요청(String id) {
		return webTestClient.post().uri("/mybox/{id}/download", id)
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	protected WebTestClient.BodyContentSpec 파일_조회_요청(String filename) {
		return webTestClient.get().uri("/mybox/files/{filename}", filename)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	protected WebTestClient.BodyContentSpec 파일_조회_요청() {
		return webTestClient.get().uri("/mybox/files")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	protected WebTestClient.ResponseSpec 파일_업로드_요청(String... filenames) {
		final var builder = new MultipartBodyBuilder();
		final var requestPartName = "files";

		for (String filename : filenames) {
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

	protected WebTestClient.ResponseSpec 폴더_안_파일_업로드_요청(String foldername, String filename) {
		final var builder = new MultipartBodyBuilder();
		final var requestPartName = "files";
		builder.part("text", getFileOne(filename))
			.header("Content-disposition",
				String.format("form-data; name=\"%s\"; filename=\"%s\"", requestPartName, 끝맺음_문장))
			.contentType(MediaType.TEXT_PLAIN);
		return webTestClient.post().uri("/mybox/folders/{foldername}/upload", foldername)
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.bodyValue(builder.build())
			.exchange()
			.expectStatus().isOk();
	}

	protected WebTestClient.BodyContentSpec 폴더_생성_요청(String parentId, String foldername) {
		return webTestClient.post()
			.uri(uriBuilder -> uriBuilder.path("/mybox/folders/{parentId}")
				.queryParam("foldername", foldername)
				.build(parentId))
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	protected WebTestClient.BodyContentSpec 루트_폴더_조회_요청() {
		return webTestClient.get()
			.uri("/mybox/folders")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	protected WebTestClient.BodyContentSpec 폴더_조회_요청(String folderId) {
		return webTestClient.get()
			.uri("/mybox/folders/{folderId}", folderId)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	protected String getString(byte[] responseBody) throws IOException {
		var reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(responseBody)));
		return reader.readLine();
	}

	protected <T> T getValue(byte[] data, Class<T> t) throws IOException {
		return OBJECT_MAPPER.readValue(data, t);
	}
}
