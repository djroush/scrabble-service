package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GameNotActiveException extends RuntimeException {
	private static final long serialVersionUID = 2031547607713354757L;

	public GameNotActiveException(String message) {
		super(message);
	}

}
