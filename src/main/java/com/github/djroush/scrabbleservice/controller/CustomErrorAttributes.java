package com.github.djroush.scrabbleservice.controller;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {
	@Override @SuppressWarnings("deprecation")
	public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
		boolean showErrorInfo = true;
		//TODO: should probably remove the stack trace
		Map<String, Object> errorAttributes = getErrorAttributes(request, showErrorInfo);
		errorAttributes.remove("trace");
		errorAttributes.remove("exception");
		return errorAttributes;
	}
}
