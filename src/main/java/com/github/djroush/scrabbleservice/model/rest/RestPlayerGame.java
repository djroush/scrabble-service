package com.github.djroush.scrabbleservice.model.rest;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RestPlayerGame {
    private RestBoard board;
    private List<RestPlayer> players;
	private RestRack rack;
	private RestGame game;
	private RestTurn lastTurn;
}
