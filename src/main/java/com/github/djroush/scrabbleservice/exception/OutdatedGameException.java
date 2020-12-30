package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OutdatedGameException extends RuntimeException {
	private static final long serialVersionUID = -8540968600999418990L;

	public OutdatedGameException(String message) {
		super(message);
	}
}
