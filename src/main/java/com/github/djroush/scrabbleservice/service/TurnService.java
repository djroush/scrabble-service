package com.github.djroush.scrabbleservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.model.rest.Square;
import com.github.djroush.scrabbleservice.model.service.Player;
import com.github.djroush.scrabbleservice.model.service.Turn;
import com.github.djroush.scrabbleservice.model.service.TurnAction;

@Service
public class TurnService {
	@Autowired
	private ScoringService scoringService;
	
	public Turn playTurn(Player player, SortedSet<Square> playedSquares, List<Set<Square>> squareWordList) {
		final int turnScore = scoringService.getTurnScore(playedSquares, squareWordList);
		final List<String> playedWords = getPlayedWords(playedSquares, squareWordList);
		final Turn turn = new Turn();
		turn.setPlayer(player);
		turn.setSquares(playedSquares);
		turn.setAction(TurnAction.PLAY_TILES);

		turn.setScore(turnScore); 
		turn.setWordsPlayed(playedWords);
		return turn;
	}
	
	private List<String> getPlayedWords(SortedSet<Square> playedSquares, List<Set<Square>> squareWordList) {
		final List<String> wordList = new ArrayList<String>(squareWordList.size());

		final StringBuilder sb = new StringBuilder();
		for (Set<Square> squareWord: squareWordList) {
			for (Square square: squareWord) {
				char letter = square.getTile().getLetter();
				sb.append(letter);
			}
			String word = sb.toString();
			wordList.add(word);
			sb.delete(0, word.length());
		}
		return wordList;		
	}

	public Turn skipTurn(Player player, Turn lastTurn) {
		final Turn turn = new Turn();
		turn.setPlayer(player);
		turn.setSquares(Collections.emptySortedSet());
		turn.setScore(0);
		turn.setWordsPlayed(Collections.emptyList());
		turn.setAction(TurnAction.PASS_TURN);
		return turn;
	}
	
	public Turn exchangeTiles(Player player) {
		final Turn turn = new Turn();
		turn.setAction(TurnAction.EXCHANGE_TILES);
		turn.setSquares(Collections.emptySortedSet());
		turn.setPlayer(player);
		turn.setWordsPlayed(Collections.emptyList());
		turn.setScore(0);
		return turn;
	}

	public Turn passTurn(Player player) {
		final Turn turn = new Turn();
		turn.setAction(TurnAction.PASS_TURN);
		turn.setSquares(Collections.emptySortedSet());
		turn.setPlayer(player);
		turn.setWordsPlayed(Collections.emptyList());
		turn.setScore(0);
		return turn;
	}

	public Turn forfeitGame(Player player) {
		final Turn turn = new Turn();
		turn.setAction(TurnAction.FORFEIT_GAME);
		turn.setSquares(Collections.emptySortedSet());
		turn.setPlayer(player);
		turn.setWordsPlayed(Collections.emptyList());
		turn.setScore(0);
		return turn;
	}

	public Turn challengeTurn(Turn lastTurn, Player challengingPlayer, Player losingPlayer) {
		final Turn turn = new Turn();
		turn.setAction(TurnAction.CHALLENGE_TURN);
		turn.setChallengeWon(!challengingPlayer.equals(losingPlayer));
		turn.setLostTurnPlayer(losingPlayer);
		turn.setPlayer(challengingPlayer);
		turn.setScore(0);
		turn.setSquares(Collections.emptySortedSet());
		turn.setWordsPlayed(Collections.emptyList());
		return turn;
	}
}
