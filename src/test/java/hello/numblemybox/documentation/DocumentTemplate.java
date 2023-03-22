package hello.numblemybox.documentation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import hello.numblemybox.member.ui.MemberController;
import hello.numblemybox.member.application.MemberService;
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
}
