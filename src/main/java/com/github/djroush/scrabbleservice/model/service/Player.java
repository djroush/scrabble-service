package com.github.djroush.scrabbleservice.model.service;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Player {
	String id;
	String name;
	Rack rack;
	int score;
	int skipTurnCount;
	boolean isForfeited;
}
