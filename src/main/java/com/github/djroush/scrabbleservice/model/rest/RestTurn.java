package com.github.djroush.scrabbleservice.model.rest;

import com.github.djroush.scrabbleservice.model.service.TurnAction;

public class RestTurn {
	private TurnAction action;
	private int playerIndex;
	private int loseTurnPlayerIndex;
	private int points;
	
	public TurnAction getAction() {
		return action;
	}
	public void setAction(TurnAction action) {
		this.action = action;
	}
	public int getPlayerIndex() {
		return playerIndex;
	}
	public void setPlayerIndex(int playerIndex) {
		this.playerIndex = playerIndex;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public int getLoseTurnPlayerIndex() {
		return loseTurnPlayerIndex;
	}
	public void setLoseTurnPlayerIndex(int loseTurnPlayerIndex) {
		this.loseTurnPlayerIndex = loseTurnPlayerIndex;
	}
}

