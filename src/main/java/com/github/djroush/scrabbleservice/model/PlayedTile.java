package com.github.djroush.scrabbleservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PlayedTile {
	private char letter;
	private boolean isBlank;
	private Tile tile;

	public char getLetter() {
		return letter;
	}
	public void setLetter(char letter) {
		this.letter = letter;
		this.tile = Tile.from(letter);
	}
	public boolean isBlank() {
		return isBlank;
	}
	public void setBlank(boolean isBlank) {
		this.isBlank = isBlank;
	}
	@JsonIgnore(true)
	public Tile getTile() {
		return tile;
	}
	public String toString() {
		return tile.name();
	}
}
