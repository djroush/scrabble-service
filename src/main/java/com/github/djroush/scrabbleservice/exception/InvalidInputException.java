package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidInputException extends RuntimeException {
	private static final long serialVersionUID = -5476315396617074381L;

	public InvalidInputException() {
		super("Received a request with invalid input parameters");
	}
}
