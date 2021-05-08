package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="The opening turn must play a tile on the center square of the board")
public class TilesNotCenteredException extends RuntimeException {
	private static final long serialVersionUID = 5570860101909976210L;

	public TilesNotCenteredException() {
		super("The opening turn must play a tile on the center square of the board");
	}
}
