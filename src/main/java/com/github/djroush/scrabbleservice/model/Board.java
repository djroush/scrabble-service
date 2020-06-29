package com.github.djroush.scrabbleservice.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
	public static final int WIDTH = 15;
	public static final int HEIGHT = 15;

	private List<Square> squares = new ArrayList<Square>(WIDTH*HEIGHT);
	private int playedTiles = 0;

	public boolean isOccupied(int row, int col) {
		int index = row * HEIGHT + col;
		Square square = squares.get(index);
		return square.getTile() != null;
	}
	public void setSquare(Square square) {
		int index = square.getRow() * HEIGHT + square.getCol();
		squares.set(index, square);
	}
	public Square getSquare(int row, int col) {
		int index = row * HEIGHT + col;
		return squares.get(index);
	}
	public int getPlayedTiles() {
		return playedTiles;
	}
	public void setPlayedTiles(int playedTiles) {
		this.playedTiles = playedTiles;
	}
	
}