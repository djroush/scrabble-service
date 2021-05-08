package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Cannot take a turn in a game that is not currently active")
public class GameNotActiveException extends RuntimeException {
	private static final long serialVersionUID = 2031547607713354757L;

	public GameNotActiveException() {
		super("Cannot take a turn in a game that is not currently active");
	}
}
