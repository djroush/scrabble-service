package com.github.djroush.scrabbleservice.model;

import java.util.ArrayList;
import java.util.List;

public class TileBag {

	private List<Tile> tileBag = new ArrayList<Tile>(100);
	
	public TileBag() {
		for (int i = 0; i < 9; i++) tileBag.add(Tile.A); 
		for (int i = 0; i < 2; i++) tileBag.add(Tile.B);
		for (int i = 0; i < 2; i++) tileBag.add(Tile.C);
		for (int i = 0; i < 4; i++) tileBag.add(Tile.D);
		for (int i = 0; i <12; i++) tileBag.add(Tile.E);
		for (int i = 0; i < 2; i++) tileBag.add(Tile.F);
		for (int i = 0; i < 3; i++) tileBag.add(Tile.G);
		for (int i = 0; i < 2; i++) tileBag.add(Tile.H);
		for (int i = 0; i < 9; i++) tileBag.add(Tile.I);
		for (int i = 0; i < 1; i++) tileBag.add(Tile.J);
		for (int i = 0; i < 1; i++) tileBag.add(Tile.K);
		for (int i = 0; i < 4; i++) tileBag.add(Tile.L);
		for (int i = 0; i < 2; i++) tileBag.add(Tile.M);
		for (int i = 0; i < 6; i++) tileBag.add(Tile.N);
		for (int i = 0; i < 8; i++) tileBag.add(Tile.O);
		for (int i = 0; i < 2; i++) tileBag.add(Tile.P);
		for (int i = 0; i < 1; i++) tileBag.add(Tile.Q);
		for (int i = 0; i < 6; i++) tileBag.add(Tile.R);
		for (int i = 0; i < 4; i++) tileBag.add(Tile.S);
		for (int i = 0; i < 6; i++) tileBag.add(Tile.T);
		for (int i = 0; i < 4; i++) tileBag.add(Tile.U);
		for (int i = 0; i < 2; i++) tileBag.add(Tile.V);
		for (int i = 0; i < 2; i++) tileBag.add(Tile.W);
		for (int i = 0; i < 1; i++) tileBag.add(Tile.X);
		for (int i = 0; i < 2; i++) tileBag.add(Tile.Y);
		for (int i = 0; i < 1; i++) tileBag.add(Tile.Z);
		for (int i = 0; i < 2; i++) tileBag.add(Tile.BLANK);
	}
	
	public List<Tile> remove(int tileCount) {
		List<Tile> removedTiles = new ArrayList<Tile>(tileCount);
		 int tilesRemaining = tileBag.size();
		 int removeCount = tileCount  < tilesRemaining ? tileCount : tilesRemaining;
		 for (int i = 0; i < removeCount; i++) {
			 int randomIndex = (int)Math.floor(tilesRemaining*Math.random());
			 removedTiles.add(tileBag.remove(randomIndex));
		 }
		 return removedTiles;
	}
	
	public List<Tile> exchange(List<Tile> exchangeTiles) {
		tileBag.addAll(exchangeTiles);
		return remove(exchangeTiles.size());
	}
	
	public boolean isEmpty() {
		return tileBag.isEmpty();
	}
}
