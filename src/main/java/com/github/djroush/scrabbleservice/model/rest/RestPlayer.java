package com.github.djroush.scrabbleservice.model.rest;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RestPlayer {
	private String name;
	private int score;
	private int skipTurnCount;
	private boolean isForfeited;
}
