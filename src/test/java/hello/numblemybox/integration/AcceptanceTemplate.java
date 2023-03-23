package hello.numblemybox.integration;

import static hello.numblemybox.fake.FakeSessionMutator.*;
import static hello.numblemybox.stubs.FileStubs.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import hello.numblemybox.SpringBootTemplate;
import hello.numblemybox.member.application.MemberService;
import hello.numblemybox.member.dto.MemberRequest;
import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.dto.FolderResponse;
import hello.numblemybox.mybox.infra.FileMyBoxMongoRepository;
import hello.numblemybox.mybox.infra.FolderMyBoxMongoRepository;

class AcceptanceTemplate extends SpringBootTemplate {
	protected static final MemberRequest 사용자의_정보 = new MemberRequest("email@email.com", "password");
	private static final String SESSION_KEY = "LOGIN_MEMBER";
	private static final UserInfo 사용자 = new UserInfo(null, 사용자의_정보.username(), 1024 * 1024 * 1024 * 3L);
	private static final String SET_COOKIE = "Set-Cookie";
	private static final String ADMIN = "rjsckdd12@gmail.com";
	@Autowired
	protected ObjectMapper OBJECT_MAPPER;
	@Autowired
	protected WebTestClient webTestClient;
	@Autowired
	protected FileMyBoxMongoRepository fileMyBoxMongoRepository;
	@Autowired
	protected FolderMyBoxMongoRepository folderMyBoxRepository;

	@BeforeEach
	void setUp() throws IOException {
		deleteFiles();
		fileMyBoxMongoRepository.deleteAll().subscribe();
		folderMyBoxRepository.deleteAll().subscribe();
		folderMyBoxRepository.save(MyFolder.createRootFolder(null, "ROOT", ADMIN)).subscribe();
	}

	@AfterAll
	static void afterAll() throws IOException {
		deleteFiles();
	}

	protected String getRootId(WebTestClient.BodyContentSpec spec) throws IOException {
		return OBJECT_MAPPER.readValue(spec.returnResult().getResponseBody(), FolderResponse.class).id();
	}

	protected WebTestClient.BodyContentSpec 파일_다운로드_요청(String folderId, String fileId) {
		return webTestClient.post().uri("/mybox/folders/{folderId}/download/{fileId}", folderId, fileId)
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	protected WebTestClient.ResponseSpec 폴더_안_파일_업로드_요청(String parentId, String filename) {
		final var builder = new MultipartBodyBuilder();
		final var requestPartName = "files";
		builder.part("text", getFileOne(filename))
			.header("Content-disposition",
				String.format("form-data; name=\"%s\"; filename=\"%s\"", requestPartName, filename))
			.contentType(MediaType.TEXT_PLAIN);
		return webTestClient.post().uri("/mybox/folders/{parentId}/upload", parentId)
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.bodyValue(builder.build())
			.exchange()
			.expectStatus().isOk();
	}

	protected WebTestClient.ResponseSpec 폴더_안_파일이름_수정_요청(String parentId, String fileId, String filename) {
		return webTestClient.patch().uri(uriBuilder -> uriBuilder.path("/mybox/folders/{parentId}/update/{fileId}")
				.queryParam("filename", filename)
				.build(parentId, fileId))
			.exchange()
			.expectStatus()
			.isOk();
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

	protected WebTestClient.BodyContentSpec 폴더_이름_수정_요청(String parentId, String foldername) {
		return webTestClient.patch()
			.uri(uriBuilder -> uriBuilder.path("/mybox/folders/{parentId}")
				.queryParam("foldername", foldername)
				.build(parentId))
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	protected WebTestClient.BodyContentSpec 루트_폴더_메타데이터_조회_요청() {
		return webTestClient.get()
			.uri("/mybox/folders/root")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	protected WebTestClient.BodyContentSpec 폴더_리스트_조회_요청(String folderId) {
		return webTestClient.get()
			.uri("/mybox/folders/{folderId}/folders", folderId)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	protected WebTestClient.BodyContentSpec 파일_리스트_조회_요청(String folderId) {
		return webTestClient.get()
			.uri("/mybox/folders/{folderId}/files", folderId)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}

	protected String getString(byte[] responseBody) throws IOException {
		var reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(responseBody)));
		return reader.readLine();
	}

	private static void deleteFiles() throws IOException {
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(그냥_문장));
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(끝맺음_문장));
		Files.deleteIfExists(프로덕션_업로드_사진_경로.resolve(인사_문장));
	}

	protected void 회원가입_요청(MemberRequest 사용자의_정보) {
		webTestClient.post().uri("/members/register")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(사용자의_정보)
			.exchange()
			.expectStatus().isOk();
	}

	protected WebTestClient.BodyContentSpec 로그인_요청(MemberRequest 사용자의_정보) {
		return webTestClient.post().uri("/members/login")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(사용자의_정보)
			.exchange()
			.expectHeader()
			.exists(SET_COOKIE)
			.expectStatus().isOk()
			.expectBody();
	}

	protected WebTestClient.BodyContentSpec 사용자_정보조회_요청() {
		return webTestClient.mutateWith(sessionMutator(sessionBuilder().put(SESSION_KEY, 사용자).build()))
			.get().uri("/members/me")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}
}
