package hello.numblemybox.mybox.application;

import static hello.numblemybox.stubs.FileStubs.*;
import static reactor.test.StepVerifier.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hello.fake.FakeMyBoxRepository;
import hello.numblemybox.mybox.domain.MyBoxRepository;
import hello.numblemybox.mybox.dto.FileResponse;

class FileQueryServiceTest {

	private static final FileResponse 이미지_파일_응답 = new FileResponse(이미지_파일.getFilename(), 이미지_파일.getExtension(),
		이미지_파일.getSize());
	private static final FileResponse 텍스트_파일_응답 = new FileResponse(텍스트_파일.getFilename(), 텍스트_파일.getExtension(),
		텍스트_파일.getSize());
	private FileQueryService fileQueryService;
	private MyBoxRepository myBoxRepository;

	@BeforeEach
	void setUp() {
		myBoxRepository = new FakeMyBoxRepository();
		myBoxRepository.insert(이미지_파일);
		myBoxRepository.insert(텍스트_파일);
		fileQueryService = new FileQueryService(myBoxRepository);
	}

	@Test
	@DisplayName("파일을 조회한다.")
	void getFile() {
		String 이미지_파일_이름 = 이미지_파일.getFilename();
		create(fileQueryService.getFile(이미지_파일_이름))
			.expectNext(이미지_파일_응답)
			.verifyComplete();
	}

	@Test
	@DisplayName("전체 파일을 조회한다.")
	void getFiles() {
		create(fileQueryService.getFiles())
			.expectNext(이미지_파일_응답)
			.expectNext(텍스트_파일_응답)
			.verifyComplete();
	}
}
