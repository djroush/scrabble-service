package com.github.djroush.scrabbleservice.model;

import java.util.ArrayList;
import java.util.List;

public class TileBag {

	private List<Tile> bag = new ArrayList<Tile>(100);
	
	public TileBag() {
		for (int i = 0; i < 9; i++) bag.add(Tile.A); 
		for (int i = 0; i < 2; i++) bag.add(Tile.B);
		for (int i = 0; i < 2; i++) bag.add(Tile.C);
		for (int i = 0; i < 4; i++) bag.add(Tile.D);
		for (int i = 0; i <12; i++) bag.add(Tile.E);
		for (int i = 0; i < 2; i++) bag.add(Tile.F);
		for (int i = 0; i < 3; i++) bag.add(Tile.G);
		for (int i = 0; i < 2; i++) bag.add(Tile.H);
		for (int i = 0; i < 9; i++) bag.add(Tile.I);
		for (int i = 0; i < 1; i++) bag.add(Tile.J);
		for (int i = 0; i < 1; i++) bag.add(Tile.K);
		for (int i = 0; i < 4; i++) bag.add(Tile.L);
		for (int i = 0; i < 2; i++) bag.add(Tile.M);
		for (int i = 0; i < 6; i++) bag.add(Tile.N);
		for (int i = 0; i < 8; i++) bag.add(Tile.O);
		for (int i = 0; i < 2; i++) bag.add(Tile.P);
		for (int i = 0; i < 1; i++) bag.add(Tile.Q);
		for (int i = 0; i < 6; i++) bag.add(Tile.R);
		for (int i = 0; i < 4; i++) bag.add(Tile.S);
		for (int i = 0; i < 6; i++) bag.add(Tile.T);
		for (int i = 0; i < 4; i++) bag.add(Tile.U);
		for (int i = 0; i < 2; i++) bag.add(Tile.V);
		for (int i = 0; i < 2; i++) bag.add(Tile.W);
		for (int i = 0; i < 1; i++) bag.add(Tile.X);
		for (int i = 0; i < 2; i++) bag.add(Tile.Y);
		for (int i = 0; i < 1; i++) bag.add(Tile.Z);
		for (int i = 0; i < 2; i++) bag.add(Tile.BLANK);
	}
	
	public List<Tile> getBag() {
		return bag;
	}
	public void setBag(List<Tile> bag) {
		this.bag = bag;
	}	
}