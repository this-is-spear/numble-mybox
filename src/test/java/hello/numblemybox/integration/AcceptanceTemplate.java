package hello.numblemybox.integration;

import static hello.numblemybox.fake.FakeSessionMutator.*;
import static hello.numblemybox.stubs.FileStubs.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import hello.numblemybox.SpringBootTemplate;
import hello.numblemybox.member.domain.Member;
import hello.numblemybox.member.dto.MemberRequest;
import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.member.infra.MemberMongoRepository;
import hello.numblemybox.mybox.application.MyBoxStorage;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.dto.FolderResponse;
import hello.numblemybox.mybox.infra.FileMyBoxMongoRepository;
import hello.numblemybox.mybox.infra.FolderMyBoxMongoRepository;

class AcceptanceTemplate extends SpringBootTemplate {
	protected String 루트_식별자;
	protected static final MemberRequest 사용자의_정보 = new MemberRequest("email@email.com", "password");
	private static final String SESSION_KEY = "LOGIN_MEMBER";
	private static final String SET_COOKIE = "Set-Cookie";
	@Autowired
	protected ObjectMapper OBJECT_MAPPER;
	@Autowired
	protected WebTestClient webTestClient;
	@Autowired
	protected MemberMongoRepository memberMongoRepository;
	@Autowired
	protected FileMyBoxMongoRepository fileMyBoxMongoRepository;
	@Autowired
	protected FolderMyBoxMongoRepository folderMyBoxRepository;
	@Autowired
	protected MyBoxStorage storage;

	@BeforeEach
	void setUp() {
		fileMyBoxMongoRepository.deleteAll().block();
		folderMyBoxRepository.deleteAll().block();
		var 저장된_사용자 = memberMongoRepository.insert(Member.createMember("alreadyUser@email.com", "1234"))
			.block();
		var 사용자_정보 = new UserInfo(저장된_사용자.getId(), 저장된_사용자.getUsername(), 저장된_사용자.getCapacity());
		루트_식별자 = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "ROOT", 사용자_정보.id())).block().getId();
		webTestClient = webTestClient.mutate()
			.responseTimeout(Duration.ofMillis(10000)).build()
			.mutateWith(sessionMutator(sessionBuilder().put(SESSION_KEY, 사용자_정보).build()));
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
		return webTestClient.get().uri("/members/me")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody();
	}
}
