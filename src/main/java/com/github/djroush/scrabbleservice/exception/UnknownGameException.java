package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UnknownGameException extends RuntimeException {
	private static final long serialVersionUID = -4730633786592026369L;

	public UnknownGameException() {
		super("Unable to find the specified game");
	}
}
