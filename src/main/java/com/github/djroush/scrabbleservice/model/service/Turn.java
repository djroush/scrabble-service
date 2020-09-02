package com.github.djroush.scrabbleservice.model.service;

import java.util.List;
import java.util.SortedSet;

import com.github.djroush.scrabbleservice.model.rest.Square;

public class Turn {
	private TurnAction action;
	private SortedSet<Square> squares;
	private List<String> wordsPlayed;
	private Player player;
	private Player lostTurnPlayer;
	private int score;
	private boolean challengeWon;
	
	public TurnAction getAction() {
		return action;
	}
	public void setAction(TurnAction action) {
		this.action = action;
	}
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
	public Player getLostTurnPlayer() {
		return lostTurnPlayer;
	}
	public void setLostTurnPlayer(Player lostTurnPlayer) {
		this.lostTurnPlayer = lostTurnPlayer;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public boolean isChallengeWon() {
		return challengeWon;
	}
	public void setChallengeWon(boolean challengeWon) {
		this.challengeWon = challengeWon;
	}
	
}
