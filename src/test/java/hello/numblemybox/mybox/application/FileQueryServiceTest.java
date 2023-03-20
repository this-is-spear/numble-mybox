package hello.numblemybox.mybox.application;

import static hello.numblemybox.stubs.FileStubs.*;
import static reactor.test.StepVerifier.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hello.numblemybox.fake.FakeMyBoxRepository;
import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.ObjectType;
import hello.numblemybox.mybox.dto.FileResponse;

class FileQueryServiceTest {

	private static final FileResponse 이미지_파일_응답 = new FileResponse(
		이미지_파일.getId(),
		이미지_파일.getFilename(),
		ObjectType.FILE,
		이미지_파일.getExtension(),
		이미지_파일.getSize()
	);

	private FileQueryService fileQueryService;
	private FileMyBoxRepository myBoxRepository;

	@BeforeEach
	void setUp() {
		myBoxRepository = new FakeMyBoxRepository();
		myBoxRepository.insert(이미지_파일).subscribe();
		myBoxRepository.insert(텍스트_파일).subscribe();
		fileQueryService = new FileQueryService(myBoxRepository);
	}

	@Test
	@DisplayName("파일을 조회한다.")
	void getFile() {
		var 이미지_파일_이름 = 이미지_파일.getFilename();
		create(fileQueryService.getFile(이미지_파일_이름))
			.expectNextCount(1)
			.verifyComplete();
	}

	@Test
	@DisplayName("전체 파일을 조회한다.")
	void getFiles() {
		create(fileQueryService.getFiles())
			.expectNextCount(2)
			.verifyComplete();
	}
}
