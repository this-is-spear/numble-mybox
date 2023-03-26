package hello.numblemybox.mybox.compress;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

class FolderCompressionTemplateTest {
	private FolderMyBoxRepository folderMyBoxRepository;
	private FileMyBoxRepository fileMyBoxRepository;
	private FolderCompressionTemplate folderCompressionTemplate;
	private MemberRepository memberRepository;
	private UserInfo 사용자_정보;

	@BeforeEach
	void setUp() {
		folderMyBoxRepository = new FakeFolderMongoRepository();
		fileMyBoxRepository = new FakeFileMyBoxRepository();
		folderCompressionTemplate = new LocalFolderCompressionTemplate(folderMyBoxRepository, fileMyBoxRepository);
		memberRepository = new FakeMemberRepository();
		var 사용자 = memberRepository.insert(Member.createMember("rjsckdd12@gmail.com", "1234")).block();
		사용자_정보 = new UserInfo(사용자.getId(), 사용자.getUsername(), 사용자.getCapacity());
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

		var path = Paths.get("./src/main/resources/tmp/");
		folderCompressionTemplate.compressFolderInLocal(루트_폴더, path).block();

		var 알집 = new File(path.resolve(루트_폴더.getId() + ".zip").toString());

		assertAll(
			() -> assertThat(알집.exists()).isTrue(),
			() -> assertThat(ZipUtil.containsEntry(알집, "/test0.txt")).isTrue(),
			() -> assertThat(ZipUtil.containsEntry(알집, "/name/test1.txt")).isTrue(),
			() -> assertThat(ZipUtil.containsEntry(알집, "/name/test2.txt")).isTrue()
		);

		Files.deleteIfExists(알집.toPath());
	}

}
