package com.github.djroush.scrabbleservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.model.service.Rack;
import com.github.djroush.scrabbleservice.model.service.Tile;
import com.github.djroush.scrabbleservice.model.service.TileBag;

@Service
public class TileBagService {

//	public boolean hasTiles(Player player, List<Tile> playedTiles) {
//		boolean tilesExist = true;
//		final List<Tile> tilesList = player.getRack().getTiles();
//		final List<Tile> tilesCopyList = new ArrayList<Tile>(tilesList);
//		
//		for (final Tile playedTile: playedTiles) {
//			tilesExist |= tilesCopyList.remove(playedTile); 
//		}
//  TODO: add logic for blank letters?	
//		return tilesExist;
//	}

	public void fillRack(TileBag tileBag, Rack rack) {
		int missingTilesCount = Rack.MAX_TILES - rack.getTiles().size(); 
		List<Tile> newTiles = remove(tileBag, missingTilesCount);
		rack.getTiles().addAll(newTiles);
	}
	
	public List<Tile> remove(TileBag tileBag, int tileCount) {
		final List<Tile> removedTiles = new ArrayList<Tile>(tileCount);
		final List<Tile> bag = tileBag.getBag();
		 int tilesRemaining = bag.size();
		 int removeCount = tileCount  < tilesRemaining ? tileCount : tilesRemaining;
		 for (int i = 0; i < removeCount; i++) {
			 int randomIndex = (int)Math.floor((tilesRemaining-i)*Math.random());
			 
			 removedTiles.add(bag.remove(randomIndex));
		 }
		 return removedTiles;
	}
	
	public List<Tile> exchange(TileBag tileBag, List<Tile> exchangeTiles) {
		tileBag.getBag().addAll(exchangeTiles);
		return remove(tileBag, exchangeTiles.size());
	}

	public boolean isEmpty(TileBag tileBag) {
		return tileBag.getBag().size() == 0;
	}
}
