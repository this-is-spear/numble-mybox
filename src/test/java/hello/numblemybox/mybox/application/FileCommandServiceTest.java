package hello.numblemybox.mybox.application;

import static hello.numblemybox.stubs.FileStubs.*;
import static org.assertj.core.api.Assertions.*;
import static reactor.test.StepVerifier.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hello.numblemybox.fake.FakeFileMyBoxRepository;
import hello.numblemybox.fake.FakeMyBoxStorage;
import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.exception.InvalidFilenameException;
import hello.numblemybox.stubs.FilePartStub;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FileCommandServiceTest {

	private static final String ADMIN = "rjsckdd12@gmail.com";
	private FileCommandService fileCommandService;
	private FileMyBoxRepository myBoxRepository;
	private MyBoxStorage myBoxStorage;

	@BeforeEach
	void setUp() {
		myBoxRepository = new FakeFileMyBoxRepository();
		myBoxStorage = new FakeMyBoxStorage();
		fileCommandService = new FileCommandService(myBoxStorage, myBoxRepository);
	}

	@Test
	@DisplayName("이미지를 업로드한다.")
	void upload() throws IOException {
		// given
		Files.deleteIfExists(업로드할_사진의_경로.resolve(업로드할_사진));
		var 사진 = new FilePartStub(테스트할_사진의_경로.resolve(업로드할_사진));

		// when
		create(fileCommandService.upload(Flux.just(사진)))
			.verifyComplete();

		// then
		assertThat(Files.exists(업로드할_사진의_경로.resolve(업로드할_사진))).isTrue();
		Files.deleteIfExists(업로드할_사진의_경로.resolve(업로드할_사진));

		StepVerifier.create(myBoxRepository.findByName(사진.filename()))
			.expectNextMatches(myFile -> Objects.equals(사진.filename(), myFile.getFilename()))
			.verifyComplete();
	}

	@Test
	@DisplayName("업로드하려는 파일과 같은 이름의 파일이 이미 저장되어 있으면 예외가 발생한다.")
	void upload_NotExistFile() throws IOException {
		var 사진 = new FilePartStub(테스트할_사진의_경로.resolve(업로드할_사진));
		myBoxRepository.insert(new MyFile(null, 사진.filename(), ADMIN, 업로드할_사진의_경로.toString(),
				사진.headers().getContentLength(), 사진.filename().split("\\.")[1]))
			.subscribe();

		create(fileCommandService.upload(Flux.just(사진)))
			.expectError(InvalidFilenameException.class)
			.verify();
		Files.deleteIfExists(업로드할_사진의_경로.resolve(업로드할_사진));
	}

	@Test
	@DisplayName("파일의 정보를 조회한다.")
	void getFile() throws IOException {
		// given
		var 사진 = new FilePartStub(테스트할_사진의_경로.resolve(업로드할_사진));
		create(myBoxStorage.uploadFile(Mono.just(사진))).verifyComplete();

		// when & then
		create(myBoxStorage.getFile(업로드할_사진))
			.expectNextMatches(File::isFile)
			.verifyComplete();

		Files.deleteIfExists(업로드할_사진의_경로.resolve(업로드할_사진));
	}

	@Test
	@DisplayName("ID를 입력받아 파일을 다운로드한다.")
	void downloadFileById() throws IOException {
		// given
		Files.deleteIfExists(업로드할_사진의_경로.resolve(업로드할_사진));
		Files.copy(테스트할_사진의_경로.resolve(업로드할_사진), 업로드할_사진의_경로.resolve(업로드할_사진));

		MyFile myFile = new MyFile(null, 업로드할_사진, "rk",
			테스트할_사진의_경로.toString(), (long)1024 * 1024 * 10, "jpg");

		myBoxRepository.insert(myFile)
			.subscribe(entity -> create(fileCommandService.downloadFileById(entity.getId()))
				.expectNextCount(1)
				.verifyComplete());
	}
}
