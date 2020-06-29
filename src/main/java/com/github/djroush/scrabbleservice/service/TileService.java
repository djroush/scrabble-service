package com.github.djroush.scrabbleservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.model.Player;
import com.github.djroush.scrabbleservice.model.Rack;
import com.github.djroush.scrabbleservice.model.Tile;
import com.github.djroush.scrabbleservice.model.TileBag;

@Service
public class TileService {

	public void fillRack(TileBag tileBag, Rack rack) {
		int missingTilesCount = Rack.MAX_TILES - rack.getTiles().size(); 
		List<Tile> newTiles = tileBag.remove(missingTilesCount);
		rack.getTiles().addAll(newTiles);
	}
	
	public boolean hasTiles(Player player, List<Tile> playedTiles) {
		boolean tilesExist = true;
		final List<Tile> tilesList = player.getRack().getTiles();
		final List<Tile> tilesCopyList = new ArrayList<Tile>(tilesList);
		
		for (final Tile playedTile: playedTiles) {
			tilesExist |= tilesCopyList.remove(playedTile); 
		}
		return tilesExist;
	}

}
