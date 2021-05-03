package com.github.djroush.scrabbleservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.model.service.Game;
import com.github.djroush.scrabbleservice.model.service.Rack;
import com.github.djroush.scrabbleservice.model.service.Tile;
import com.github.djroush.scrabbleservice.model.service.TileBag;

@Service
public class TileBagService {

	public List<Tile> fillRack(Game game) {
		TileBag tileBag = game.getTileBag();
		Optional<Rack> unfullRack = game.getPlayers().stream()
			.filter(player -> !player.isForfeited())
		    .map(player -> player.getRack())
		    .filter(rack -> rack.getTiles().size() < 7)
		    .findFirst();

		if (unfullRack.isPresent()) {
			Rack rack = unfullRack.get();
			int missingTilesCount = Rack.MAX_TILES - rack.getTiles().size(); 
			List<Tile> newTiles = remove(tileBag, missingTilesCount);
			rack.getTiles().addAll(newTiles);
			return newTiles;
		}
		return Collections.emptyList();
	}
	
	public List<Tile> fillRack(TileBag tileBag, Rack rack) {
		int missingTilesCount = Rack.MAX_TILES - rack.getTiles().size(); 
		List<Tile> newTiles = remove(tileBag, missingTilesCount);
		rack.getTiles().addAll(newTiles);
		return newTiles;
	}
	
	private List<Tile> remove(TileBag tileBag, int tileCount) {
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

	public void returnTiles(TileBag tileBag, List<Tile> tiles) {
		tileBag.getBag().addAll(tiles);
		tiles.clear();
	}
}
