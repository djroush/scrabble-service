package com.github.djroush.scrabbleservice.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.model.rest.Square;
import com.github.djroush.scrabbleservice.model.service.PlayedTile;
import com.github.djroush.scrabbleservice.model.service.Rack;
import com.github.djroush.scrabbleservice.model.service.Tile;

@Service
public class RackService {

	public void replaceTiles(Rack rack, Collection<Square> playedSquares) {
		final List<Tile> tiles = rack.getTiles();
		playedSquares.forEach(square -> {
			final PlayedTile playedTile = square.getTile();
			final Tile tile = playedTile.isBlank() ? Tile.BLANK : Tile.from(playedTile.getLetter());
			tiles.remove(tile);
		});
	}
}
