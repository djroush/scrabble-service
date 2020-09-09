package com.github.djroush.scrabbleservice.model.service;

import java.util.LinkedList;
import java.util.List;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Rack {
	public static final int MAX_TILES = 7;
	private List<Tile> tiles = new LinkedList<Tile>();
	private List<Tile> previousTiles = new LinkedList<Tile>();
}
