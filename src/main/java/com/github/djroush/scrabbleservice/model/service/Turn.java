package com.github.djroush.scrabbleservice.model.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
	//Transient turn state to allow modifying these will not change the game's current version
	private TurnState turnState = TurnState.AWAITING_ACTION;
	private Set<String> skippedChallengePlayerIds  = new HashSet<String>();
}