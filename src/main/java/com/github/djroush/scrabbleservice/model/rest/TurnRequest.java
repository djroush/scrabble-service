package com.github.djroush.scrabbleservice.model.rest;

import java.util.List;

import com.github.djroush.scrabbleservice.model.service.Tile;

public class TurnRequest {
	private List<Square> squares;
	private List<Tile> tiles;
	private boolean passTurn = false;
	private boolean lostTurn = false;
	
	public List<Square> getSquares() {
		return squares;
	}
	public void setSquares(List<Square> squares) {
		this.squares = squares;
	}
	public List<Tile> getTiles() {
		return tiles;
	}
	public void setTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}
	public boolean isPassTurn() {
		return passTurn;
	}
	public void setPassTurn(boolean passTurn) {
		this.passTurn = passTurn;
	}
	public boolean isLostTurn() {
		return lostTurn;
	}
	public void setLostTurn(boolean lostTurn) {
		this.lostTurn = lostTurn;
	}
}
