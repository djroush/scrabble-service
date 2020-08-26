package com.github.djroush.scrabbleservice.model.rest;

import com.github.djroush.scrabbleservice.model.service.Board;
import com.github.djroush.scrabbleservice.model.service.PlayedTile;

public class Square implements Comparable<Square> {
	private  int row;
	private  int col;
	private PlayedTile tile;

	public Square() {};
	
	public Square(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	public int getRow() {
		return row;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public int getCol() {
		return col;
	}
	public PlayedTile getTile() {
		return tile;
	}

	public void setTile(PlayedTile tile) {
		this.tile = tile;
	}

	//TODO: do i need to add tile compare here?
	@Override
	public int compareTo(Square o) {
		int index = row*Board.WIDTH + col;
		int oindex = o.row*Board.WIDTH + o.col;
		
		return Integer.compare(index,  oindex);
	}
	
	public String toString() {
		return "(" + row + "," + col + ") = " + tile.getLetter();
	}
}
