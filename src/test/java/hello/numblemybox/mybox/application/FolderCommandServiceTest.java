package hello.numblemybox.mybox.application;

import static reactor.test.StepVerifier.*;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hello.numblemybox.fake.FakeFileMyBoxRepository;
import hello.numblemybox.fake.FakeFolderMongoRepository;
import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.domain.ObjectType;
import reactor.core.publisher.Flux;

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
		var root = folderMyBoxRepository.insert(MyFolder.createRootFolder(null, "name", ADMIN));
		var id = Objects.requireNonNull(root.block()).getId();
		create(folderCommandService.createFolder(id, 폴더_이름))
			.verifyComplete();

		create(root).expectNextMatches(
			rootFolder -> rootFolder.getChildren().stream().filter(myObject -> myObject.getType().equals(
				ObjectType.FOLDER)).anyMatch(myObject -> myObject.getName().equals(폴더_이름))
		).verifyComplete();
	}

	@Test
	@DisplayName("폴더를 생성할 때 같은 이름의 폴더를 생성할 수 없다.")
	void createFolder_notDuplicateFilename() {
		var 폴더_이름 = "newfolder";
		var root = folderMyBoxRepository.insert(MyFolder.createRootFolder(null, "name", ADMIN));
		var id = Objects.requireNonNull(root.block()).getId();
		create(folderCommandService.createFolder(id, 폴더_이름))
			.verifyComplete();

		create(folderCommandService.createFolder(id, 폴더_이름))
			.verifyError(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("파일 메타데이터를 저장한다.")
	void addFileInFolder() {
		var 첫_번째_파일 = new MyFile(null, "image.png", ADMIN, "./src/...", 1024 * 1024 * 5L, "png");
		var 두_번째_파일 = new MyFile(null, "text.txt", ADMIN, "./src/...", 1024 * 1024 * 5L, "txt");
		var root = folderMyBoxRepository.insert(MyFolder.createRootFolder(null, "name", ADMIN));
		var id = Objects.requireNonNull(root.block()).getId();
		create(folderCommandService.addFileInFolder(id, Flux.just(첫_번째_파일, 두_번째_파일)))
			.verifyComplete();

		create(folderMyBoxRepository.findById(id))
			.expectNextMatches(myFolder -> myFolder.getFiles().size() == 2)
			.verifyComplete();
	}

	@Test
	@DisplayName("파일 메타데이터 이름이 중복이면 예외가 발생한다.")
	void addFileInFolder_notDuplicateName() {
		var 첫_번째_파일 = new MyFile(null, "image.png", ADMIN, "./src/...", 1024 * 1024 * 5L, "png");
		var 두_번째_파일 = new MyFile(null, "image.png", ADMIN, "./src/...", 1024 * 1024 * 5L, "png");
		var root = folderMyBoxRepository.insert(MyFolder.createRootFolder(null, "name", ADMIN));
		var id = Objects.requireNonNull(root.block()).getId();
		create(folderCommandService.addFileInFolder(id, Flux.just(첫_번째_파일, 두_번째_파일)))
			.verifyError(IllegalArgumentException.class);
	}
}
