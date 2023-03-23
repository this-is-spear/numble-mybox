package hello.numblemybox;

import static hello.numblemybox.AuthenticationConfigurer.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import hello.numblemybox.member.exception.InvalidMemberException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class AuthenticationFilter implements WebFilter {
	private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
	private static final String URI = "/mybox/**";
	private static final PathPattern pattern = new PathPatternParser().parse(URI);

	@Override
	@SuppressWarnings("all")
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		if (!pattern.matches(exchange.getRequest().getPath().pathWithinApplication())) {
			return chain.filter(exchange);
		}

		return exchange.getSession()
			.map(session -> {
				if (session.getAttribute(SESSION_KEY) == null) {
					throw InvalidMemberException.invalidUser();
				}
				return session.getAttribute(SESSION_KEY);
			}).flatMap(
				s -> chain.filter(exchange)
			);
	}
}
