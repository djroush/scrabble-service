package com.github.djroush.scrabbleservice.model.rest;

import com.github.djroush.scrabbleservice.model.service.Board;
import com.github.djroush.scrabbleservice.model.service.PlayedTile;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Square implements Comparable<Square> {
	private  int row;
	private  int col;
	private PlayedTile tile;

	public Square() {};
	
	public Square(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
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
