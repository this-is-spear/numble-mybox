package hello.numblemybox.fake;

import java.util.Map;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClientConfigurer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import com.google.common.collect.ImmutableMap;

import reactor.core.publisher.Mono;

public class FakeSessionMutator implements WebTestClientConfigurer {

	private static Map<String, Object> sessionMap;

	private FakeSessionMutator(final Map<String, Object> sessionMap) {
		FakeSessionMutator.sessionMap = sessionMap;
	}

	public static FakeSessionMutator sessionMutator(final Map<String, Object> sessionMap) {
		return new FakeSessionMutator(sessionMap);
	}

	@Override
	public void afterConfigurerAdded(final WebTestClient.Builder builder,
		final WebHttpHandlerBuilder httpHandlerBuilder,
		final ClientHttpConnector connector) {
		final SessionMutatorFilter sessionMutatorFilter = new SessionMutatorFilter();
		httpHandlerBuilder.filters(filters -> filters.add(0, sessionMutatorFilter));
	}

	public static ImmutableMap.Builder<String, Object> sessionBuilder() {
		return new ImmutableMap.Builder<>();
	}

	private static class SessionMutatorFilter implements WebFilter {
		@Override
		public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain webFilterChain) {
			return exchange.getSession()
				.doOnNext(webSession -> webSession.getAttributes().putAll(sessionMap))
				.then(webFilterChain.filter(exchange));
		}
	}
}
