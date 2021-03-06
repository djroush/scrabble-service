package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Only a player who has joined the game may start it")
public class PlayerCannotStartGameException extends RuntimeException {
	private static final long serialVersionUID = 5432074854592591765L;

	public PlayerCannotStartGameException() {
		super("Only a player who has joined the game may start it");
	}
}
