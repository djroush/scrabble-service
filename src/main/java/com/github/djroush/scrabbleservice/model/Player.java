package com.github.djroush.scrabbleservice.model;

public class Player {
	String id;
	String name;
	Rack rack;
	int score;
	int skipTurnCount;
	boolean isForfeited;
	
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
	public Rack getRack() {
		return rack;
	}
	public void setRack(Rack rack) {
		this.rack = rack;
	}
	public void setSkipTurnCount(int skipTurnCount) {
		this.skipTurnCount = skipTurnCount;
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
	public void setIsForfeited(boolean isForfeited) {
		this.isForfeited = true;
	}
	public boolean getIsForfeited() {
		return isForfeited;
	}
}
