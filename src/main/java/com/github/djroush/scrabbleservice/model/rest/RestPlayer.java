package com.github.djroush.scrabbleservice.model.rest;

public class RestPlayer {
	private String id;
	private String name;
	private int score;
	private int skipTurnCount;
	private boolean isForfeited;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getSkipTurnCount() {
		return skipTurnCount;
	}
	public void setSkipTurnCount(int skipTurnCount) {
		this.skipTurnCount = skipTurnCount;
	}
	public boolean getIsForfeited() {
		return isForfeited;
	}
	public void setIsForfeited(boolean isForfeited) {
		this.isForfeited = isForfeited;
	}

	
}
