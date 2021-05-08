package com.github.djroush.scrabbleservice.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class AccessControlWebFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain fc)
			throws IOException, ServletException {
		final HttpServletResponse httpResp = (HttpServletResponse)resp;
		httpResp.addHeader("Access-Control-Allow-Origin", "*");
		httpResp.addHeader("Access-Control-Allow-Headers", "Content-Type, If-None-Match, ETag");
		httpResp.addHeader("Access-Control-Expose-Headers", "Content-Length, ETag");
		httpResp.addHeader("Access-Control-Allow-Methods", "HEAD,GET,POST,PUT,DELETE,OPTIONS");
		httpResp.addHeader("Cache-Control", "no-store");

		fc.doFilter(req, httpResp);
	}

}
