package hello.numblemybox.mybox.application;

import static org.assertj.core.api.Assertions.*;
import static reactor.test.StepVerifier.*;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import hello.numblemybox.fake.FakeFileMyBoxRepository;
import hello.numblemybox.fake.FakeFolderMongoRepository;
import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.domain.ObjectType;
import reactor.core.publisher.Mono;

class FolderCommandServiceTest {

	private static final String ADMIN = "rjsckdd12@gmail.com";
	private FolderCommandService folderCommandService;
	private FolderMyBoxRepository folderMyBoxRepository;
	private FileMyBoxRepository fileMyBoxRepository;

	@BeforeEach
	void setUp() {
		folderMyBoxRepository = new FakeFolderMongoRepository();
		fileMyBoxRepository = new FakeFileMyBoxRepository();
		folderCommandService = new FolderCommandService(folderMyBoxRepository, fileMyBoxRepository);
	}

	@Test
	@DisplayName("폴더를 생성한다.")
	void createFolder() {
		var 폴더_이름 = "newfolder";
		var root = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", ADMIN));
		var id = Objects.requireNonNull(root.block()).getId();
		create(folderCommandService.createFolder(id, 폴더_이름))
			.verifyComplete();
	}

	@Test
	@DisplayName("폴더 이름을 수정한다.")
	void updateFoldername() {
		// given
		var 폴더_이름 = "newfolder";
		var 루트_폴더 = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", ADMIN)).block();
		var 루트_폴더_식별자 = 루트_폴더.getId();
		var 새로_생성한_폴더 = folderMyBoxRepository.save(new MyFolder(null, 폴더_이름, ADMIN, ObjectType.FOLDER, 루트_폴더_식별자))
			.block();
		var 생성한_폴더_식별자 = 새로_생성한_폴더.getId();

		// when
		String 수정할_이름 = "update name";
		create(folderCommandService.updateFolder(생성한_폴더_식별자, 수정할_이름))
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
		var 루트_폴더 = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", ADMIN));
		var 루트_폴더_식별자 = Objects.requireNonNull(루트_폴더.block()).getId();

		// then & then
		create(folderCommandService.updateFolder(루트_폴더_식별자, 수정할_이름))
			.verifyError(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void updateFoldername_noEmpty(String 비어있는_이름) {
		// given
		var 폴더_이름 = "newfolder";
		var 루트_폴더 = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", ADMIN));
		var 루트_폴더_식별자 = Objects.requireNonNull(루트_폴더.block()).getId();
		var 새로_생성한_폴더 = folderMyBoxRepository.save(new MyFolder(null, 폴더_이름, ADMIN, ObjectType.FOLDER, 루트_폴더_식별자))
			.block();
		var 생성한_폴더_식별자 = 새로_생성한_폴더.getId();

		// then & then
		create(folderCommandService.updateFolder(생성한_폴더_식별자, 비어있는_이름))
			.verifyError(RuntimeException.class);
	}

	@Test
	@DisplayName("파일 메타데이터를 저장한다.")
	void addFileInFolder() {
		var 두_번째_파일 = new MyFile(null, "text.txt", ADMIN, "./src/...", 1024 * 1024 * 5L, "txt");
		var root = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", ADMIN));
		var id = Objects.requireNonNull(root.block()).getId();
		create(folderCommandService.addFileInFolder(id, Mono.just(두_번째_파일)))
			.verifyComplete();
	}

	@Test
	@DisplayName("폴더를 생성할 때 같은 이름의 폴더를 생성할 수 없다.")
	void createFolder_notDuplicateFilename() {
		var 폴더_이름 = "newfolder";
		var root = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", ADMIN));
		var id = Objects.requireNonNull(root.block()).getId();
		create(folderCommandService.createFolder(id, 폴더_이름))
			.verifyComplete();

		create(folderCommandService.createFolder(id, 폴더_이름))
			.verifyError(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("파일 메타데이터 이름이 중복이면 예외가 발생한다.")
	void addFileInFolder_notDuplicateName() {
		var 첫_번째_파일 = new MyFile(null, "image.png", ADMIN, "./src/...", 1024 * 1024 * 5L, "png");
		var 두_번째_파일 = new MyFile(null, "image.png", ADMIN, "./src/...", 1024 * 1024 * 5L, "png");
		var root = folderMyBoxRepository.save(MyFolder.createRootFolder(null, "name", ADMIN));
		var id = Objects.requireNonNull(root.block()).getId();
		create(folderCommandService.addFileInFolder(id, Mono.just(첫_번째_파일)))
			.verifyComplete();

		create(folderCommandService.addFileInFolder(id, Mono.just(두_번째_파일)))
			.verifyError(IllegalArgumentException.class);
	}
}
