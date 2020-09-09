package com.github.djroush.scrabbleservice.model.rest;

import com.github.djroush.scrabbleservice.model.service.TurnAction;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RestTurn {
	private TurnAction action;
	private int playerIndex;
	private int loseTurnPlayerIndex;
	private int points;
	private int[] newTileIndexes;
}

