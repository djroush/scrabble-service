package com.github.djroush.scrabbleservice.model.rest;

public class RestGame {
	public String id;
	public String playerId;
	public int version;
	public String state;
	public int activePlayerIndex;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getActivePlayerIndex() {
		return activePlayerIndex;
	}
	public void setActivePlayerIndex(int activePlayerIndex) {
		this.activePlayerIndex = activePlayerIndex;
	}
	
}
