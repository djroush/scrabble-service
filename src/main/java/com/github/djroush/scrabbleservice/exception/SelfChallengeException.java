package com.github.djroush.scrabbleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="A player cannot challenge their own turn")
public class SelfChallengeException extends RuntimeException {
	private static final long serialVersionUID = 2854839980234703982L;

	public SelfChallengeException() {
		super("A player cannot challenge their own turn");
	}
}

