package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Received a request with invalid input parameters")
public class InvalidInputException extends RuntimeException {
	private static final long serialVersionUID = -5476315396617074381L;

	public InvalidInputException() {
		super("Received a request with invalid input parameters");
	}
}
