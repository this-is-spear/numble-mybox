package hello.numblemybox.documentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

import hello.numblemybox.HelloController;

@AutoConfigureRestDocs
@WebFluxTest(controllers = HelloController.class)
class HelloDocument {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void hello() {
		this.webTestClient.get().uri("/hello")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.consumeWith(
				WebTestClientRestDocumentation.document("hello")
			);
	}
}
