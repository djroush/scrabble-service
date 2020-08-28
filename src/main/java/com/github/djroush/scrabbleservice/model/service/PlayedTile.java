package com.github.djroush.scrabbleservice.model.service;

//FIXME: rename to Tile
public class PlayedTile {
	private char letter;
	private boolean isBlank;

	public char getLetter() {
		return letter;
	}
	public void setLetter(char letter) {
		this.letter = letter;
	}
	public boolean isBlank() {
		return isBlank;
	}
	public void setBlank(boolean isBlank) {
		this.isBlank = isBlank;
	}
}
