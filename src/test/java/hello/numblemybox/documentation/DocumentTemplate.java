package hello.numblemybox.documentation;

import static hello.numblemybox.AuthenticationConfigurer.*;
import static hello.numblemybox.fake.FakeSessionMutator.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import hello.numblemybox.member.application.MemberService;
import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.member.ui.MemberController;
import hello.numblemybox.mybox.application.FileCommandService;
import hello.numblemybox.mybox.application.FolderCommandService;
import hello.numblemybox.mybox.application.FolderQueryService;
import hello.numblemybox.mybox.ui.MyBoxController;

@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = {
	MyBoxController.class,
	MemberController.class
})
public class DocumentTemplate {
	protected static final UserInfo 사용자_정보 = new UserInfo("1234SDF43DC", "email@email.com", 30 * 1024 * 1024L);
	@Autowired
	protected WebTestClient webTestClient;

	@MockBean
	protected FolderCommandService folderCommandService;

	@MockBean
	protected FileCommandService fileCommandService;

	@MockBean
	protected FolderQueryService folderQueryService;

	@MockBean
	protected MemberService memberService;

	@BeforeEach
	void setUp() {
		this.webTestClient = webTestClient
			.mutateWith(sessionMutator(sessionBuilder().put(SESSION_KEY, 사용자_정보).build()));
	}
}
