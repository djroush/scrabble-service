package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPassTurnException extends RuntimeException {
	private static final long serialVersionUID = -2778819010523084883L;

	public InvalidPassTurnException() {
		super("The request must set passTurn=true for a turn to occur");
	}
}
