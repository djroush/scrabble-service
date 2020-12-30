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
import com.github.djroush.scrabbleservice.model.service.TurnState;

@Service
public class TurnService {
	@Autowired
	private ScoringService scoringService;
	
	public Turn challengeTurn(Player challengingPlayer, Player losingPlayer) {
		Turn turn = new Turn();
		turn.setAction(TurnAction.CHALLENGE_TURN);
		turn.setTurnState(TurnState.AWAITING_ACTION);
		turn.setChallengeWon(!challengingPlayer.equals(losingPlayer));
		turn.setLostTurnPlayer(losingPlayer);
		turn.setPlayer(challengingPlayer);
		turn.setScore(0);
		turn.setSquares(Collections.emptySortedSet());
		turn.setWordsPlayed(Collections.emptyList());
		return turn;
	}

	
	public Turn playTurn(Player player, SortedSet<Square> playedSquares, List<Set<Square>> squareWordList) {
		Turn turn = new Turn();
		final int turnScore = scoringService.getTurnScore(playedSquares, squareWordList);
		final List<String> playedWords = getPlayedWords(playedSquares, squareWordList);
		turn.setPlayer(player);
		turn.setSquares(playedSquares);
		turn.setAction(TurnAction.PLAY_TILES);
		turn.setTurnState(TurnState.AWAITING_CHALLENGE);

		turn.setScore(turnScore); 
		turn.setWordsPlayed(playedWords);
		return turn;
	}
	
	public Turn skipTurn(Player player) {
		Turn turn = new Turn();
		turn.setPlayer(player);
		turn.setSquares(Collections.emptySortedSet());
		turn.setScore(0);
		turn.setWordsPlayed(Collections.emptyList());
		turn.setAction(TurnAction.PASS_TURN);
		return turn;
	}
	
	public Turn exchangeTiles(Player player) {
		Turn turn = new Turn();
		turn.setAction(TurnAction.EXCHANGE_TILES);
		turn.setSquares(Collections.emptySortedSet());
		turn.setPlayer(player);
		turn.setWordsPlayed(Collections.emptyList());
		turn.setScore(0);
		return turn;
	}

	public Turn passTurn(Player player) {
		Turn turn = new Turn();
		turn.setAction(TurnAction.PASS_TURN);
		turn.setSquares(Collections.emptySortedSet());
		turn.setPlayer(player);
		turn.setWordsPlayed(Collections.emptyList());
		turn.setScore(0);
		return turn;
	}

	public Turn forfeitGame(Player player) {
		Turn turn = new Turn();
		turn.setAction(TurnAction.FORFEIT_GAME);
		turn.setSquares(Collections.emptySortedSet());
		turn.setPlayer(player);
		turn.setWordsPlayed(Collections.emptyList());
		turn.setScore(0);
		return turn;
	}

	public Turn gameStart() {
		Turn turn = new Turn();
		turn.setAction(TurnAction.GAME_STARTED);
		return turn;
	}

	public void forgoChallenge(Turn lastTurn, String forgoingPlayerId) {
		if (lastTurn != null && lastTurn.getPlayer() != null && lastTurn.getPlayer().getId() != forgoingPlayerId) {
			final Set<String> skippedChallengePlayerIds = lastTurn.getSkippedChallengePlayerIds();
			skippedChallengePlayerIds.add(forgoingPlayerId);
		}
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

}
