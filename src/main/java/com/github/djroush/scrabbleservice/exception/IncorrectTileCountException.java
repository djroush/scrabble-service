package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class IncorrectTileCountException extends RuntimeException {
	private static final long serialVersionUID = 5625970660306192945L;

	public IncorrectTileCountException() {
		super("You must play 1-7 tiles per turn or pass");
	}
}
