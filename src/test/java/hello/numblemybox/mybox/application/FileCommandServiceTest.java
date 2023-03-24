package hello.numblemybox.mybox.application;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.assertj.core.api.Assertions.*;
import static reactor.test.StepVerifier.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hello.numblemybox.fake.FakeFileMyBoxRepository;
import hello.numblemybox.fake.FakeFolderMongoRepository;
import hello.numblemybox.fake.FakeMemberRepository;
import hello.numblemybox.fake.FakeMyBoxStorage;
import hello.numblemybox.member.domain.Member;
import hello.numblemybox.member.domain.MemberRepository;
import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.domain.ObjectType;
import hello.numblemybox.mybox.exception.CapacityException;
import hello.numblemybox.stubs.FilePartStub;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FileCommandServiceTest {

	private FileCommandService fileCommandService;
	private FileMyBoxRepository fileMyBoxRepository;
	private FolderMyBoxRepository folderMyBoxRepository;
	private MyBoxStorage myBoxStorage;
	private MemberRepository memberRepository;
	private MyFolder ROOT;
	private UserInfo 사용자_정보;

	@BeforeEach
	void setUp() {
		fileMyBoxRepository = new FakeFileMyBoxRepository();
		folderMyBoxRepository = new FakeFolderMongoRepository();
		memberRepository = new FakeMemberRepository();
		var 사용자 = memberRepository.insert(new Member("rjsckdd12@gmail.com", "1234", 20_000_000L)).block();
		사용자_정보 = new UserInfo(사용자.getId(), 사용자.getUsername(), 사용자.getCapacity());
		ROOT = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "root", 사용자_정보.id())).block();
		myBoxStorage = new FakeMyBoxStorage();
		fileCommandService = new FileCommandService(myBoxStorage, fileMyBoxRepository,
			new FolderCommandService(folderMyBoxRepository, fileMyBoxRepository));
	}

	@Test
	@DisplayName("이미지를 업로드한다.")
	void upload() throws IOException {
		// given
		var 사진 = new FilePartStub(테스트할_사진의_경로.resolve(업로드할_사진));
		// when
		create(fileCommandService.upload(사용자_정보, ROOT.getId(), Flux.just(사진)))
			.verifyComplete();

		MyFile 저장된_파일_메타데이터 = fileMyBoxRepository.findByParentIdAndName(ROOT.getId(), 업로드할_사진).block();

		// then
		assertThat(Files.exists(업로드할_사진의_경로.resolve(저장된_파일_메타데이터.getId()))).isTrue();
		Files.deleteIfExists(업로드할_사진의_경로.resolve(저장된_파일_메타데이터.getId()));

		StepVerifier.create(fileMyBoxRepository.findByName(사진.filename()))
			.expectNextMatches(myFile -> Objects.equals(사진.filename(), myFile.getFilename()))
			.verifyComplete();
	}

	@Test
	@DisplayName("업로드할 때, 사용자가 허용하는 용량보마 많아지면 안된다.")
	void upload_capacityNotOver() {
		// given
		var 사진 = new FilePartStub(테스트할_사진의_경로.resolve(업로드할_사진));

		var 기존에_있는_파일 = new MyFile(null, "file.exe", 사용자_정보.id(), "asdf", 20_000_000L, "jpg");
		기존에_있는_파일.addParent(ROOT.getId());
		create(fileMyBoxRepository.save(기존에_있는_파일))
			.expectNextCount(1)
			.verifyComplete();

		create(fileCommandService.upload(사용자_정보, ROOT.getId(), Flux.just(사진)))
			.verifyError(CapacityException.class);
	}

	// TODO
	@Test
	@DisplayName("파일의 정보를 조회한다.")
	void getFile() throws IOException {
		// given
		String fileId = "1234";
		Files.deleteIfExists(업로드할_사진의_경로.resolve(fileId));
		var 사진 = new FilePartStub(테스트할_사진의_경로.resolve(업로드할_사진));
		create(myBoxStorage.uploadFile(Mono.just(사진), fileId)).verifyComplete();

		// when & then
		create(myBoxStorage.getFile(fileId))
			.expectNextMatches(File::isFile)
			.verifyComplete();

		Files.deleteIfExists(업로드할_사진의_경로.resolve(fileId));
	}

	@Test
	@DisplayName("ID를 입력받아 파일을 다운로드한다.")
	void downloadFileById() throws IOException {
		// given
		var myFile = new MyFile(null, 업로드할_사진, 사용자_정보.id(), ObjectType.FOLDER,
			업로드할_사진의_경로.toString(), (long)1024 * 1024 * 10, "jpg", ROOT.getId());
		var file = fileMyBoxRepository.save(myFile).block();
		Files.copy(테스트할_사진의_경로.resolve(업로드할_사진), 업로드할_사진의_경로.resolve(file.getId()));

		// when & then
		create(fileCommandService.downloadFileById(사용자_정보, ROOT.getId(), file.getId()))
			.expectNextCount(1)
			.verifyComplete();

		Files.deleteIfExists(업로드할_사진의_경로.resolve(file.getId()));
	}

	@Test
	@DisplayName("다운로드할 파일이 없으면 예외가 발생한다.")
	void downloadFileById_notExistInStorage() {
		// given
		var myFile = new MyFile(null, 업로드할_사진, "rk", ObjectType.FOLDER,
			업로드할_사진의_경로.toString(), (long)1024 * 1024 * 10, "jpg", ROOT.getId());

		// when
		var file = fileMyBoxRepository.save(myFile).block();

		// then
		create(fileCommandService.downloadFileById(사용자_정보, ROOT.getId(), file.getId()))
			.verifyError(RuntimeException.class);
	}

	@Test
	@DisplayName("다운로드할 파일의 메타데이터가 없으면 아무 것도 반환하지 않는다.")
	void downloadFileById_notExistInDatabase() throws IOException {
		// given
		var 파일_메타데이터 = fileMyBoxRepository.save(new MyFile(null, 업로드할_사진, 사용자_정보.id(), ObjectType.FOLDER,
			업로드할_사진의_경로.toString(), (long)1024 * 1024 * 10, "jpg", ROOT.getId())).block();

		// when & then
		create(fileCommandService.downloadFileById(사용자_정보, ROOT.getId(), 파일_메타데이터.getId()))
			.verifyError(RuntimeException.class);
	}

	@Test
	@DisplayName("파일 이름을 수정한다.")
	void updateFilename() {
		// given
		var 폴더_식별자 = ROOT.getId();
		var 파일_식별자 = fileMyBoxRepository.save(new MyFile(null, 업로드할_사진, 사용자_정보.id(), ObjectType.FOLDER,
			업로드할_사진의_경로.toString(), (long)1024 * 1024 * 10, "jpg", 폴더_식별자)).block().getId();

		// then & then
		String 새로운_파일_이름 = "newFile.txt";
		create(fileCommandService.updateFilename(사용자_정보, 폴더_식별자, 파일_식별자, 새로운_파일_이름)).verifyComplete();
	}

}
