package com.github.djroush.scrabbleservice.model.rest;

public class PassTurnRequest {
	private boolean passTurn = false;
	
	public boolean isPassTurn() {
		return passTurn;
	}
	public void setPassTurn(boolean passTurn) {
		this.passTurn = passTurn;
	}
}
