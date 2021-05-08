package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="You cannot join a game that is full")
public class GameFullException extends RuntimeException {
	private static final long serialVersionUID = -3990837820479830315L;

	public GameFullException() {
		super("You cannot join a game that is full");
	}
}
