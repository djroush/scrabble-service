package com.github.djroush.scrabbleservice.model.rest;

import java.util.List;

public class ExchangeRequest {
	private List<RestTile> tiles;
	
	public List<RestTile> getTiles() {
		return tiles;
	}
	public void setTiles(List<RestTile> tiles) {
		this.tiles = tiles;
	}
}
