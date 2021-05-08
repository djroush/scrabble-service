package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="You must have 2-4 players to play a game")
public class IncorrectPlayerCountException extends RuntimeException {
	private static final long serialVersionUID = -2279503605984902461L;

	public IncorrectPlayerCountException() {
		super("You must have 2-4 players to play a game");
	}
}
