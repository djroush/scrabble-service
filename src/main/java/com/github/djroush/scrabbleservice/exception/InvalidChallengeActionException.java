package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="A player can only challenge after tiles have been played")
public class InvalidChallengeActionException extends RuntimeException {
	private static final long serialVersionUID = 2854839980234703982L;

	public InvalidChallengeActionException() {
		super("A player can only challenge after tiles have been played");
	}
}

