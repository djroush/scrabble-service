package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTurnException extends RuntimeException {
	private static final long serialVersionUID = 7744610246678829022L;

	public InvalidTurnException() {
		super("A turn must consist of exactly one action (play tiles, exchange tiles, pass turn, turn skipped");
	}
}
