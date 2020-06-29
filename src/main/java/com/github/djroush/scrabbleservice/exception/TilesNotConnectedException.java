package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TilesNotConnectedException extends RuntimeException {
	private static final long serialVersionUID = -3937412935365931884L;

    public TilesNotConnectedException() {
	  super("Tiles cannot be disjoint, they must be connected to the other tiles");
    }
}
