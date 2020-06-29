package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class GameAlreadyStartedException extends RuntimeException {
	private static final long serialVersionUID = 3488157920573831479L;

	public GameAlreadyStartedException() {
		super("Unable to join an in progress game");
	}
}
