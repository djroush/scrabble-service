package com.github.djroush.scrabbleservice.model.rest;

public class RestTile {
	private String letter;
	private boolean isBlank;
	
	public String getLetter() {
		return letter;
	}
	public void setLetter(String letter) {
		this.letter = letter;
	}
	public boolean isBlank() {
		return isBlank;
	}
	public void setBlank(boolean isBlank) {
		this.isBlank = isBlank;
	}
	
}
