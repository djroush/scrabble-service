package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidActionException extends RuntimeException {
	private static final long serialVersionUID = 2854839980234703982L;

	public InvalidActionException(String msg) {
		super(msg);
	}
}

