package hello.numblemybox.mybox.application;

import static reactor.test.StepVerifier.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hello.numblemybox.fake.FakeFileMyBoxRepository;
import hello.numblemybox.fake.FakeFolderMongoRepository;
import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFolder;

class FolderQueryServiceTest {

	private FolderQueryService folderQueryService;
	private FolderMyBoxRepository folderMyBoxRepository;
	private FileMyBoxRepository fileMyBoxRepository;

	@BeforeEach
	void setUp() {
		folderMyBoxRepository = new FakeFolderMongoRepository();
		fileMyBoxRepository = new FakeFileMyBoxRepository();
		folderQueryService = new FolderQueryService(folderMyBoxRepository, fileMyBoxRepository);
	}

	@Test
	void findFolder() {
		var 일반_폴더 = MyFolder.createFolder(null, "folder", "rjsckdd12@gmail.com", "123");
		folderMyBoxRepository.save(일반_폴더)
			.map(myFolder ->
				create(folderQueryService.findFolder(myFolder.getId()))
					.expectNextCount(1)
					.verifyComplete()
			)
			.subscribe();
	}

	@Test
	void findRootFolder() {
		var 루트_폴더 = MyFolder.createRootFolder(null, "root", "rjsckdd12@gmail.com");
		create(folderMyBoxRepository.save(루트_폴더))
			.expectNextCount(1)
			.verifyComplete();

		create(folderQueryService.findRootFolder())
			.expectNextCount(1)
			.verifyComplete();
	}
}
