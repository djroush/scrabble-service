package com.github.djroush.scrabbleservice.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.djroush.scrabbleservice.model.rest.Square;

@JsonInclude(Include.NON_DEFAULT)
public class Board {
	public static final int CENTER = 7;
	public static final int WIDTH = 15;
	public static final int HEIGHT = 15;
	
	private final List<Square> squares;
	
	public Board() {
		squares = new ArrayList<Square>(WIDTH*HEIGHT);
		for (int row = 0; row < HEIGHT; row++) {
			for (int col = 0; col < WIDTH; col++) {
				final Square square = new Square(row, col);
				squares.add(square);
			}
		}
	}

	public void addSquare(Square square) {
		int row = square.getRow();
		int col = square.getCol();
		int index=row*WIDTH+col;
		squares.set(index, square);
	}
	public Square getSquare(int row, int col) {
		int index=row*WIDTH+col;
		Square square = squares.get(index);
		return square;
	}
}