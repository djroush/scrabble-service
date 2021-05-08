package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="An action has already been played in this turn attempting to perform another action is invalid")
public class InvalidActionException extends RuntimeException {
	private static final long serialVersionUID = 2854839980234703982L;

	public InvalidActionException() {
		super("An action has already been played in this turn attempting to perform another action is invalid");
	}
}

