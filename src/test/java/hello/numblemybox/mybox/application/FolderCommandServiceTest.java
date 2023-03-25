package hello.numblemybox.mybox.application;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static reactor.test.StepVerifier.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.zeroturnaround.zip.ZipUtil;

import hello.numblemybox.fake.FakeFileMyBoxRepository;
import hello.numblemybox.fake.FakeFolderMongoRepository;
import hello.numblemybox.fake.FakeMemberRepository;
import hello.numblemybox.member.domain.Member;
import hello.numblemybox.member.domain.MemberRepository;
import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.domain.ObjectType;
import hello.numblemybox.mybox.exception.InvalidFilenameException;
import hello.numblemybox.mybox.exception.InvalidFoldernameException;
import hello.numblemybox.mybox.infra.LocalMyBoxStorage;

class FolderCommandServiceTest {
	private FolderCommandService folderCommandService;
	private FolderMyBoxRepository folderMyBoxRepository;
	private FileMyBoxRepository fileMyBoxRepository;
	private MyBoxStorage myBoxStorage;
	private MemberRepository memberRepository;
	private UserInfo 사용자_정보;

	@BeforeEach
	void setUp() {
		folderMyBoxRepository = new FakeFolderMongoRepository();
		fileMyBoxRepository = new FakeFileMyBoxRepository();
		memberRepository = new FakeMemberRepository();
		myBoxStorage = new LocalMyBoxStorage();
		folderCommandService = new FolderCommandService(folderMyBoxRepository, fileMyBoxRepository, myBoxStorage);
		var 사용자 = memberRepository.insert(Member.createMember("rjsckdd12@gmail.com", "1234")).block();
		사용자_정보 = new UserInfo(사용자.getId(), 사용자.getUsername(), 사용자.getCapacity());
	}

	@Test
	@DisplayName("폴더를 생성한다.")
	void createFolder() {
		var 폴더_이름 = "newfolder";
		var root = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", 사용자_정보.id()));
		var id = Objects.requireNonNull(root.block()).getId();
		create(folderCommandService.createFolder(사용자_정보, id, 폴더_이름))
			.verifyComplete();
	}

	@Test
	@DisplayName("폴더 이름을 수정한다.")
	void updateFoldername() {
		// given
		var 폴더_이름 = "newfolder";
		var 루트_폴더 = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", 사용자_정보.id())).block();
		var 루트_폴더_식별자 = 루트_폴더.getId();
		var 새로_생성한_폴더 = folderMyBoxRepository.save(new MyFolder(null, 폴더_이름, 사용자_정보.id(), ObjectType.FOLDER, 루트_폴더_식별자))
			.block();
		var 생성한_폴더_식별자 = 새로_생성한_폴더.getId();

		// when
		String 수정할_이름 = "update name";
		create(folderCommandService.updateFolder(사용자_정보, 생성한_폴더_식별자, 수정할_이름))
			.verifyComplete();

		// then
		var myFolder = folderMyBoxRepository.findById(생성한_폴더_식별자).block();
		assertThat(myFolder.getName()).isEqualTo(수정할_이름);
	}

	@Test
	@DisplayName("루트 폴더는 수정할 수 없다.")
	void updateFoldername_noRoot() {
		// given
		String 수정할_이름 = "update name";
		var 루트_폴더 = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", 사용자_정보.id()));
		var 루트_폴더_식별자 = Objects.requireNonNull(루트_폴더.block()).getId();

		// then & then
		create(folderCommandService.updateFolder(사용자_정보, 루트_폴더_식별자, 수정할_이름))
			.verifyError(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("폴더이름을 수정할 때, 수정하려는 폴더 이름이 비어있으면 예외가 발생한다.")
	void updateFoldername_noEmpty(String 비어있는_이름) {
		// given
		var 폴더_이름 = "newfolder";
		var 루트_폴더 = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", 사용자_정보.id()));
		var 루트_폴더_식별자 = Objects.requireNonNull(루트_폴더.block()).getId();
		var 새로_생성한_폴더 = folderMyBoxRepository.save(new MyFolder(null, 폴더_이름, 사용자_정보.id(), ObjectType.FOLDER, 루트_폴더_식별자))
			.block();
		var 생성한_폴더_식별자 = 새로_생성한_폴더.getId();

		// then & then
		create(folderCommandService.updateFolder(사용자_정보, 생성한_폴더_식별자, 비어있는_이름))
			.verifyError(RuntimeException.class);
	}

	@Test
	@DisplayName("파일 메타데이터를 저장한다.")
	void addFileInFolder() {
		var 두_번째_파일 = new MyFile(null, "text.txt", 사용자_정보.id(), "./src/...", 1024 * 1024 * 5L, "txt");
		var root = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", 사용자_정보.id()));
		var id = Objects.requireNonNull(root.block()).getId();
		create(folderCommandService.addFileInFolder(id, 두_번째_파일))
			.expectNextCount(1)
			.verifyComplete();
	}

	@Test
	@DisplayName("폴더를 생성할 때 같은 이름의 폴더를 생성할 수 없다.")
	void createFolder_notDuplicateFilename() {
		var 폴더_이름 = "newfolder";
		var root = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", 사용자_정보.id()));
		var id = Objects.requireNonNull(root.block()).getId();
		create(folderCommandService.createFolder(사용자_정보, id, 폴더_이름))
			.verifyComplete();

		create(folderCommandService.createFolder(사용자_정보, id, 폴더_이름))
			.verifyError(InvalidFoldernameException.class);
	}

	@Test
	@DisplayName("파일 메타데이터 이름이 중복이면 예외가 발생한다.")
	void addFileInFolder_notDuplicateName() {
		var 첫_번째_파일 = new MyFile(null, "image.png", 사용자_정보.id(), "./src/...", 1024 * 1024 * 5L, "png");
		var 두_번째_파일 = new MyFile(null, "image.png", 사용자_정보.id(), "./src/...", 1024 * 1024 * 5L, "png");
		var root = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", 사용자_정보.id()));
		var id = Objects.requireNonNull(root.block()).getId();
		create(folderCommandService.addFileInFolder(id, 첫_번째_파일))
			.expectNextCount(1)
			.verifyComplete();

		create(folderCommandService.addFileInFolder(id, 두_번째_파일))
			.verifyError(InvalidFilenameException.class);
	}

	@Test
	@DisplayName("폴더를 다운로드한다.")
	void downloadFolder() throws IOException {
		// 루트 폴더 생성
		var 루트_폴더 = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", 사용자_정보.id())).block();
		// 파일 생성
		fileMyBoxRepository.save(
			new MyFile("test-text1.txt", "test0.txt", 사용자_정보.id(), ObjectType.FILE, 업로드할_사진의_경로.toString(), 100L,
				"plain/txt", 루트_폴더.getId())).block();

		// 폴더 생성
		var 하위_폴더 = folderMyBoxRepository.save(MyFolder.createFolder(null, "child", 사용자_정보.id(), 루트_폴더.getId()))
			.block();

		// 파일 생성
		fileMyBoxRepository.save(
			new MyFile("test-text2.txt", "test1.txt", 사용자_정보.id(), ObjectType.FILE, 업로드할_사진의_경로.toString(), 100L,
				"plain/txt", 하위_폴더.getId())).block();
		// 파일 생성
		fileMyBoxRepository.save(
			new MyFile("test-text3.txt", "test2.txt", 사용자_정보.id(), ObjectType.FILE, 업로드할_사진의_경로.toString(), 100L,
				"plain/txt", 하위_폴더.getId())).block();

		var loadedFileResponse = folderCommandService.downloadFolder(사용자_정보, 루트_폴더.getId()).block();

		Path path = Paths.get("./src/main/resources/tmp/" + 루트_폴더.getId() + ".zip");
		File 알집 = new File(path.toString());

		assertAll(
			() -> assertThat(알집.exists()).isTrue(),
			() -> assertThat(ZipUtil.containsEntry(알집, "/test0.txt")).isTrue(),
			() -> assertThat(ZipUtil.containsEntry(알집, "/name/test1.txt")).isTrue(),
			() -> assertThat(ZipUtil.containsEntry(알집, "/name/test2.txt")).isTrue()
		);

		Files.deleteIfExists(path);
	}
}
