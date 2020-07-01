package com.github.djroush.scrabbleservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.model.Game;
import com.github.djroush.scrabbleservice.model.PlayedTile;
import com.github.djroush.scrabbleservice.model.Player;
import com.github.djroush.scrabbleservice.model.ScoreModifier;
import com.github.djroush.scrabbleservice.model.Turn;
import com.github.djroush.scrabbleservice.model.rest.Square;

@Service
public class TurnService {
	
	public Turn playTurn(Player player, SortedSet<Square> playedSquares, List<Set<Square>> squareWordList) {
		Turn turn = new Turn();
		turn.setPlayer(player);
		turn.setSquares(playedSquares);
		
		int turnScore = 0;
		final List<String> words = new ArrayList<String>(squareWordList.size());
		for (final Set<Square> squareWord: squareWordList) {
			int wordMultiplier = 1;
			int wordScore = 0;
			final StringBuilder sb = new StringBuilder();
			for (Square square: squareWord) {
				final PlayedTile tile = square.getTile();
				final ScoreModifier sm =  playedSquares.contains(square) ? 
					getScoreModifier(square) : ScoreModifier.SINGLE_LETTER; 
						
				int letterValue = tile.getValue();
				switch (sm) {
				case TRIPLE_WORD: wordMultiplier *= 3; break;
				case DOUBLE_WORD: wordMultiplier *= 2; break;
				case TRIPLE_LETTER: letterValue *= 3; break;
				case DOUBLE_LETTER: letterValue *= 2; break;
				case SINGLE_LETTER: letterValue *= 1; break;
				case ZERO_LETTER:  letterValue *= 0; break;
				}
				
				if (!tile.isBlank()) {
				    wordScore += letterValue;
				}
				sb.append(tile.toString());
			}
			wordScore *= wordMultiplier;
			turnScore += wordScore;
			String word = sb.toString();
			words.add(word);
			sb.delete(0, word.length());
		}
		turn.setScore(turnScore); 
		turn.setWordsPlayed(words);
		return turn;
	}
	
    public ScoreModifier getScoreModifier(Square square) {
    	int row = square.getRow();
    	int col = square.getCol();
		final PlayedTile tile = square.getTile();
		
		if (tile != null && tile.isBlank()) {
			return ScoreModifier.ZERO_LETTER;
		}
		
		if ((row % 7 == 0 && col % 8 ==  3) ||
			(row % 8 == 3 && col % 7 ==  0) || 
			(row % 10 == 2 && (col == 6 || col == 8)) ||
			((row == 6 || row == 8) && (col % 10 == 2 || row + col == 14))
		   ) {
			return ScoreModifier.DOUBLE_LETTER;
		} else if (row % 4 == 1 && col % 4 == 1 && (row % 12 != 1 && col % 12 != 1)) {
			return ScoreModifier.TRIPLE_LETTER;
		} else if ((row == col || row + col == 16) && 
			      ((row >= 1 && row <= 4)  || (row == 7 && col == 7)))  {
			return ScoreModifier.DOUBLE_WORD;
	    //The center tile is handled above by DOUBLE_WORD
		} else if (row % 7 == 0 && col % 7 == 0) {
			return ScoreModifier.TRIPLE_WORD;
		}
		return ScoreModifier.SINGLE_LETTER;
    }
	
	public Turn skipTurn(Player player, Turn lastTurn) {
		final Turn turn = new Turn();
		turn.setPlayer(player);
		turn.setSquares(Collections.emptySortedSet());
		turn.setScore(0);
		turn.setWordsPlayed(Collections.emptyList());
		return turn;
	}
	
	public void reverseLastTurn(Game game) {
		Turn turn = game.getLastTurn();
		Player player = turn.getPlayer();
		SortedSet<Square> playedSquares = turn.getSquares();
		playedSquares.forEach(square -> {
			square.setTile(null);
		});
		int score = player.getScore();
		int skippedTurnCount  = player.getSkipTurnCount();
		player.setScore(score - turn.getScore());
		player.setSkipTurnCount(skippedTurnCount + 1);
		// TODO Finish coding here
	}
	
	

}
