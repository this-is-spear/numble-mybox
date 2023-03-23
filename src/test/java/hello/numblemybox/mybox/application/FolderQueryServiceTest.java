package hello.numblemybox.mybox.application;

import static reactor.test.StepVerifier.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hello.numblemybox.fake.FakeFileMyBoxRepository;
import hello.numblemybox.fake.FakeFolderMongoRepository;
import hello.numblemybox.fake.FakeMemberRepository;
import hello.numblemybox.member.domain.Member;
import hello.numblemybox.member.domain.MemberRepository;
import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFolder;

class FolderQueryServiceTest {

	private FolderQueryService folderQueryService;
	private FolderMyBoxRepository folderMyBoxRepository;
	private FileMyBoxRepository fileMyBoxRepository;
	private MemberRepository memberRepository;
	public UserInfo 사용자_정보 = new UserInfo("123", "rjsckdd12@gmail.com", 30 * 1024 * 1024L);

	@BeforeEach
	void setUp() {
		folderMyBoxRepository = new FakeFolderMongoRepository();
		fileMyBoxRepository = new FakeFileMyBoxRepository();
		memberRepository = new FakeMemberRepository();
		folderQueryService = new FolderQueryService(folderMyBoxRepository, fileMyBoxRepository);
		var 사용자 = memberRepository.insert(Member.createMember("rjsckdd12@gmail.com", "1234")).block();
		사용자_정보 = new UserInfo(사용자.getId(), 사용자.getUsername(), 사용자.getCapacity());
	}

	@Test
	void findFolder() {
		var 일반_폴더 = MyFolder.createFolder(null, "folder", "rjsckdd12@gmail.com", "123");
		folderMyBoxRepository.save(일반_폴더)
			.map(myFolder ->
				create(folderQueryService.findFolder(사용자_정보, myFolder.getId()))
					.expectNextCount(1)
					.verifyComplete()
			)
			.subscribe();
	}

	@Test
	void findRootFolder() {
		var 루트_폴더 = MyFolder.createRootFolder(null, "root", 사용자_정보.id());
		create(folderMyBoxRepository.save(루트_폴더))
			.expectNextCount(1)
			.verifyComplete();

		create(folderQueryService.findRootFolder(사용자_정보))
			.expectNextCount(1)
			.verifyComplete();
	}
}
