package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TurnOutofOrderException extends RuntimeException {
	private static final long serialVersionUID = -7511702408308157500L;

	public TurnOutofOrderException() {
		super("A player cannot play tiles until it is their turn");
	}
}
