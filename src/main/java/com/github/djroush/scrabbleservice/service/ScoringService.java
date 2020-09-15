package com.github.djroush.scrabbleservice.service;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.model.rest.Square;
import com.github.djroush.scrabbleservice.model.service.PlayedTile;
import com.github.djroush.scrabbleservice.model.service.Rack;
import com.github.djroush.scrabbleservice.model.service.ScoreModifier;
import com.github.djroush.scrabbleservice.model.service.Tile;

@Service
public class ScoringService {
	private static final int BINGO_BONUS = 50;

	//TODO: add method to determine end game scoring

	public int getTurnScore(SortedSet<Square> playedSquares, List<Set<Square>> squareWordList) {
		int turnScore = 0;
		for (final Set<Square> squareWord: squareWordList) {
			int wordMultiplier = 1;
			int wordScore = 0;
			for (Square square: squareWord) {
				final PlayedTile playedTile = square.getTile();
				final ScoreModifier sm =  playedSquares.contains(square) ? 
					getScoreModifier(square) : ScoreModifier.SINGLE_LETTER; 

				final Tile t = Tile.from(playedTile.getLetter());
				int letterValue = t.getValue();
				switch (sm) {
				case TRIPLE_WORD: wordMultiplier *= 3; break;
				case DOUBLE_WORD: wordMultiplier *= 2; break;
				case TRIPLE_LETTER: letterValue *= 3; break;
				case DOUBLE_LETTER: letterValue *= 2; break;
				case SINGLE_LETTER: letterValue *= 1; break;
				case ZERO_LETTER:  letterValue *= 0; break;
				}
				
				if (!playedTile.isBlank()) {
				    wordScore += letterValue;
				}
			}
			wordScore *= wordMultiplier;
			turnScore += wordScore;
		}
		if (playedSquares.size() == Rack.MAX_TILES) {
			turnScore += BINGO_BONUS;
		}
		return turnScore;		
	}
	
    public ScoreModifier getScoreModifier(Square square) {
    	int row = square.getRow();
    	int col = square.getCol();
		final PlayedTile tile = square.getTile();
		
		 if ((row == col || row + col == 14) && 
			 ((row >= 1 && row <= 4)  || row == 7 || (row >= 10 && row <= 13)))  {
			return ScoreModifier.DOUBLE_WORD;
		} else if (row % 7 == 0 && col % 7 == 0) {
			return ScoreModifier.TRIPLE_WORD;
		} else if (tile != null && tile.isBlank()) {
			return ScoreModifier.ZERO_LETTER;
		} else if ((row % 7 == 0 && col % 8 ==  3) ||
			(row % 8 == 3 && col % 7 ==  0) || 
			(row % 10 == 2 && (col == 6 || col == 8)) ||
		      ((row == 6 || row == 8) && (col % 10 == 2 || col == 6 || col == 8))
		   ) {
			return ScoreModifier.DOUBLE_LETTER;
		} else if (row % 4 == 1 && col % 4 == 1 && !(row % 12 == 1 && col % 12 == 1)) {
			return ScoreModifier.TRIPLE_LETTER;
		}
		return ScoreModifier.SINGLE_LETTER;
    }

}
