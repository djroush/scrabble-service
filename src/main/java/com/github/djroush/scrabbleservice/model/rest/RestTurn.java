package com.github.djroush.scrabbleservice.model.rest;

import java.util.List;

import com.github.djroush.scrabbleservice.model.service.TurnAction;
import com.github.djroush.scrabbleservice.model.service.TurnState;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RestTurn {
	private TurnAction action;
	public TurnState state;
	private int playerIndex;
	private int loseTurnPlayerIndex;
	private int points;
	private int[] newTileIndexes;
	private List<String> wordsPlayed;
}