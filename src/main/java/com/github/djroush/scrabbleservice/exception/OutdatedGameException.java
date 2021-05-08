package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Cannot take an action on an obsolete version of this game")
public class OutdatedGameException extends RuntimeException {
	private static final long serialVersionUID = -8540968600999418990L;

	public OutdatedGameException() {
		super("Cannot take an action on an obsolete version of this game");
	}
}
