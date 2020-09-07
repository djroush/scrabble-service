package com.github.djroush.scrabbleservice.model.service;

import java.util.List;
import java.util.SortedSet;

import com.github.djroush.scrabbleservice.model.rest.Square;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Turn {
	private TurnAction action;
	private SortedSet<Square> squares;
	private List<String> wordsPlayed;
	private Player player;
	private Player lostTurnPlayer;
	private int score;
	private boolean challengeWon;
}
