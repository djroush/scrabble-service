package com.github.djroush.scrabbleservice.service;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.exception.IncorrectTileAlignmentException;
import com.github.djroush.scrabbleservice.exception.IncorrectTileCountException;
import com.github.djroush.scrabbleservice.exception.TilesNotCenteredException;
import com.github.djroush.scrabbleservice.exception.TilesNotConnectedException;
import com.github.djroush.scrabbleservice.model.Board;
import com.github.djroush.scrabbleservice.model.Direction;
import com.github.djroush.scrabbleservice.model.Game;
import com.github.djroush.scrabbleservice.model.Player;
import com.github.djroush.scrabbleservice.model.ScoreModifier;
import com.github.djroush.scrabbleservice.model.Square;
import com.github.djroush.scrabbleservice.model.Turn;

@Service
public class BoardService {
    public Board initializeBoard() {
    	Board board = new Board();
		for (int row = 0; row < Board.HEIGHT; row++) {
			for (int col = 0; col < Board.WIDTH; col++) {
				ScoreModifier modifier = getModifier(row, col);
				Square square = new Square(modifier, row, col);
				board.setSquare(square);
			}
		}
    	return board;
    }
	
    //TODO: Have a lookup map<type, list<pair(row, col)> to simplify this logic?
    private ScoreModifier getModifier(int row, int col) {
		ScoreModifier modifier = ScoreModifier.NONE;
		if ((row % 7 == 0 && col % 8 ==  3) ||
			(row % 8 == 3 && col % 7 ==  0) || 
			(row % 10 == 2 && (col == 6 || col == 8)) ||
			((row == 6 || row == 8) && (col % 10 == 2 || row + col == 14))
		   ) {
			modifier = ScoreModifier.DOUBLE_LETTER;
		} else if (row % 4 == 1 && col % 4 == 1 && (row % 12 != 1 && col % 12 != 1)) {
			modifier = ScoreModifier.TRIPLE_LETTER;
		} else if ((row == col || row + col == 16) && 
			      ((row >= 1 && row <= 4)  || (row == 7 && col == 7)))  {
			modifier = ScoreModifier.DOUBLE_WORD;
	    //The center tile is handled above by DOUBLE_WORD
		} else if (row % 7 == 0 && col % 7 == 0) {
			modifier = ScoreModifier.TRIPLE_WORD;
		}
		return modifier;
    }
    
	public boolean attemptMove(Board board, SortedSet<Square> squares) {
		//TODO: check the correct player is making a turn
		//TODO: Need to check if player has correct letters
		//TODO: update the tiles to have multipler of NONE
		return false;
	}
	
	public void checkMoveValid(Board board, SortedSet<Square> squares) {
		//Check  length
		boolean correctLength = squares.size() > 0 && squares.size() < 8;
		if (!correctLength) {
			throw new IncorrectTileCountException();
		}
		
		//Make sure the first turn uses the center tile
		if (isFirstTurn(board)) {
		    boolean touchesStartingSquare = false;
			for (Square square: squares) {
				touchesStartingSquare |= square.getRow() == 7 && square.getCol() == 7;
			}
			if (!touchesStartingSquare) {
				throw new TilesNotCenteredException();
			}
		}
		
		final Square firstSquare = squares.first();
		final Square lastSquare = squares.last();
		int commonRow = firstSquare.getRow();
		int commonCol = firstSquare.getCol();
		
		//Check tiles aligned 
		if (squares.size() > 1) {
			boolean colsMatch = true;
			boolean rowsMatch = true;
			for(Square square: squares) {
				rowsMatch &= commonRow == square.getRow();
				colsMatch &= commonCol == square.getCol();
			}
			if (!rowsMatch && !colsMatch) {
				throw new IncorrectTileAlignmentException();
			}
		}
		
		//Check contiguous
		Direction direction = squares.first().getRow() == squares.last().getRow() ? 
				Direction.HORIZONTAL : Direction.VERTICAL;
		
		int existingCount = 0;
		if (direction == Direction.HORIZONTAL) {
			for (int col = commonCol; col < lastSquare.getCol(); col++) {
				Square square = board.getSquare(commonRow, col);
				if (square.getTile() != null) {
					existingCount++;
				} else {
					
				}
				
			}
			if (squares.size() + existingCount - 1  != lastSquare.getCol() - firstSquare.getCol()) {
				throw new TilesNotConnectedException();
			}
		} else {  /*Direction.VERTICAL */
			for (int row = commonRow; row < lastSquare.getRow(); row++) {
				Square square = board.getSquare(row, commonCol);
				if (square.getTile() != null) {
					existingCount++;
				}
			}
			if (squares.size() + existingCount - 1  != lastSquare.getRow() - firstSquare.getRow()) {
				throw new TilesNotConnectedException();
			}
		}

		//Check connected
		boolean isConnected = false;
		if ( existingCount == 0) {
			if (direction == Direction.HORIZONTAL) {
				for (Square square: squares)  {
					if (commonRow != 0) {
						isConnected |= !board.isOccupied(commonRow - 1, square.getCol());
					}
					if (commonRow != Board.HEIGHT-1) {
						isConnected |= !board.isOccupied(commonRow + 1, square.getCol());
					}
				}
				if (firstSquare.getCol() > 0) {
					isConnected |= !board.isOccupied(commonRow, firstSquare.getCol()-1);
				}
				if (lastSquare.getCol() > Board.WIDTH-1) {
					isConnected |= !board.isOccupied(commonRow, lastSquare.getCol()+1);
				}
			} else /* Direction.VERTICAL */ { 
				for (Square square: squares)  {
					if (commonCol != 0) {
						isConnected |= !board.isOccupied(square.getRow(), commonCol -1);
					}
					if (commonCol != Board.WIDTH-1) {
						isConnected |= !board.isOccupied(square.getRow(), commonCol +1);
					}
				}
				if (firstSquare.getRow() > 0) {
					isConnected |= !board.isOccupied(firstSquare.getRow()-1, commonCol);
				}
				if (lastSquare.getRow() > Board.HEIGHT-1) {
					isConnected |= !board.isOccupied(lastSquare.getRow()+1, commonCol);
				}
			}
		}
		
		if (!isConnected) {
			throw new TilesNotConnectedException();
		}
	}

	public Turn executeTurn(Player player, Board board, SortedSet<Square> squares) {
		Turn turn = new Turn();
		
		// TODO Add logic for executeTurn here
		return turn;
	}
	
	public Turn skipTurn(Player player, Turn lastTurn) {
		Turn turn = new Turn();
		turn.setPlayer(player);
		turn.setScore(0);
		return turn;
	}
	
	public void reverseLastTurn(Game game) {
		Turn turn = game.getLastTurn();
		Player player = turn.getPlayer();
		List<Square> playedSquares = turn.getSquares();
		playedSquares.forEach(square -> {
			square.setTile(null);
			square.setModifier(getModifier(square.getRow(), square.getCol()));
		});
		int score = player.getScore();
		int skippedTurnCount  = player.getSkipTurnCount();
		player.setScore(score - turn.getScore());
		player.setSkipTurnCount(skippedTurnCount + 1);
		// TODO Finish coding here
	}

	public boolean isFirstTurn(Board board) {
		
		return board.getPlayedTiles() == 0;
	}
	public void setMultiplier(Set<Square> squares) {
		
	}

}
