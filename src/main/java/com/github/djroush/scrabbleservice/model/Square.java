package com.github.djroush.scrabbleservice.model;

public class Square implements Comparable<Square> {
	private final int row;
	private final int col;
	private Tile tile;
	private ScoreModifier modifier;

	public Square(ScoreModifier modifier, int row, int col) {
		if (row < 0 || row >= Board.HEIGHT || col < 0 || col >= Board.WIDTH) {
			throw new IllegalArgumentException("Row and column must be values between 0 and 15 (exclusive)");
		}
		this.row = row;
		this.col = col;
		this.modifier = modifier;
	}
	
	public int getRow() {
		return row;
	}
	public int getCol() {
		return col;
	}

	public Tile getTile() {
		return tile;
	}
	
	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public ScoreModifier getModifier() {
		return modifier;
	}

	public void setModifier(ScoreModifier modifier) {
		this.modifier = modifier;
	}

	@Override
	public int compareTo(Square o) {
		int index = row * Board.WIDTH +  col;
		int oindex = o.row * Board.WIDTH + o.col;
		return Integer.compare(index, oindex); 
	}
}
