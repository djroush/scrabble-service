package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Cannot take an action in a game that is not started or already completed")
public class GameNotActiveOrEndgameException extends RuntimeException {
	private static final long serialVersionUID = 2031547607713354757L;

	public GameNotActiveOrEndgameException() {
		super("Cannot take an action in a game that is not started or already completed");
	}

}
