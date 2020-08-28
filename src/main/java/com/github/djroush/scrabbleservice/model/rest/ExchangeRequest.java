package com.github.djroush.scrabbleservice.model.rest;

import java.util.List;

import com.github.djroush.scrabbleservice.model.service.Tile;

public class ExchangeRequest {
	private List<Tile> tiles;
	
	public List<Tile> getTiles() {
		return tiles;
	}
	public void setTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}
}
