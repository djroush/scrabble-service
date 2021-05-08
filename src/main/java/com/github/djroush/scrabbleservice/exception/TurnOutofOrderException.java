package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="A player cannot play tiles until it is their turn")
public class TurnOutofOrderException extends RuntimeException {
	private static final long serialVersionUID = -7511702408308157500L;

	public TurnOutofOrderException() {
		super("A player cannot play tiles until it is their turn");
	}
}
