package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Tiles cannot be disjoint, they must be connected to the other tiles")
public class TilesNotConnectedException extends RuntimeException {
	private static final long serialVersionUID = -3937412935365931884L;

    public TilesNotConnectedException() {
	  super("Tiles cannot be disjoint, they must be connected to the other tiles");
    }
}
