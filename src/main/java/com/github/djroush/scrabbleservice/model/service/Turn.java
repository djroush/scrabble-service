package com.github.djroush.scrabbleservice.model.service;

import java.util.List;
import java.util.SortedSet;

import com.github.djroush.scrabbleservice.model.rest.Square;

public class Turn {
	private SortedSet<Square> squares;
	private List<String> wordsPlayed;
	private Player player;
	private int score;
	
	public SortedSet<Square> getSquares() {
		return squares;
	}
	public void setSquares(SortedSet<Square> squares) {
		this.squares = squares;
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
