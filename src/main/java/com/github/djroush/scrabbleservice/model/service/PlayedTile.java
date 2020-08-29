package com.github.djroush.scrabbleservice.model.service;

public class PlayedTile {
	private char letter;
	private boolean isBlank;

	public char getLetter() {
		return letter;
	}
	public void setLetter(char letter) {
		this.letter = letter;
	}
	public boolean getIsBlank() {
		return isBlank;
	}
	public void setIsBlank(boolean isBlank) {
		this.isBlank = isBlank;
	}
}
