package com.github.djroush.scrabbleservice.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class AccessControlFilter implements WebFilter {
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*");
		exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "Content-Type, If-None-Match, ETag");
		return chain.filter(exchange);
	}
}