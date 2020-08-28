package com.github.djroush.scrabbleservice.model.rest;

import java.util.List;

public class PlayTilesRequest {
	private List<Square> squares;
	
	public List<Square> getSquares() {
		return squares;
	}
	public void setSquares(List<Square> squares) {
		this.squares = squares;
	}
}
