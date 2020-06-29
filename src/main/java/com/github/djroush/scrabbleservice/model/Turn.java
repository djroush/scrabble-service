package com.github.djroush.scrabbleservice.model;

import java.util.List;

public class Turn {
	private List<Square> squares;
	private Direction direction;
	private List<String> wordsPlayed;
	private Player player;
	private int score;
	
	public List<Square> getSquares() {
		return squares;
	}
	public void setSquares(List<Square> squares) {
		this.squares = squares;
	}
	
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public List<String> getWordsPlayed() {
		return wordsPlayed;
	}
	public void setWordsPlayed(List<String> wordsPlayed) {
		this.wordsPlayed = wordsPlayed;
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
}
