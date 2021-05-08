package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="The tiles played must be in aligned horizontally or vertically without spaces in between")
public class IncorrectTileAlignmentException extends RuntimeException {
	private static final long serialVersionUID = -7029556600533156428L;

	public IncorrectTileAlignmentException() {
		super("The tiles played must be in aligned horizontally or vertically without spaces in between");
	}
}
