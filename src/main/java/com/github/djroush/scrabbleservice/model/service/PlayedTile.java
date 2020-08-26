package com.github.djroush.scrabbleservice.model.service;

//TODO: this is messy, clean it up later probably don't need tile here
public class PlayedTile {
	private char letter;
	private boolean isBlank;

	public char getLetter() {
		return letter;
	}
	public void setLetter(char letter) {
		this.letter = letter;
//		this.tile = Tile.from(letter);
	}
	public boolean isBlank() {
		return isBlank;
	}
	public void setBlank(boolean isBlank) {
		this.isBlank = isBlank;
	}
}
