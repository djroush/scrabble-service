package com.github.djroush.scrabbleservice.model;

import java.util.LinkedList;
import java.util.List;

public class Rack {
	public static final int MAX_TILES = 7;
	private List<Tile> tiles = new LinkedList<Tile>();

	public List<Tile> getTiles() {
		return tiles;
	}

	public void setTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}
}
