package com.github.djroush.scrabbleservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.exception.IncorrectTileAlignmentException;
import com.github.djroush.scrabbleservice.exception.IncorrectTileCountException;
import com.github.djroush.scrabbleservice.exception.TilesNotCenteredException;
import com.github.djroush.scrabbleservice.exception.TilesNotConnectedException;
import com.github.djroush.scrabbleservice.model.Board;
import com.github.djroush.scrabbleservice.model.Direction;
import com.github.djroush.scrabbleservice.model.PlayedTile;
import com.github.djroush.scrabbleservice.model.Turn;
import com.github.djroush.scrabbleservice.model.rest.Square;

@Service
public class BoardService {
	public boolean attemptMove(Board board, SortedSet<Square> squares) {
		//TODO: check the correct player is making a turn
		//TODO: Need to check if player has correct letters
		//TODO: update the tiles to have multipler of SINGLE_LETTER
		//TODO: replace blank tiles with replaced tile
		return false;
	}
	
	//FIXME: break this into smaller methods
	public void checkMoveValid(Board board, SortedSet<Square> squares) {
		//Check length
		boolean correctLength = squares.size() > 0 && squares.size() < 8;
		if (!correctLength) {
			throw new IncorrectTileCountException();
		}

		//Check tiles aligned 
		final Square firstSquare = squares.first();
		final Square lastSquare = squares.last();
		final int commonRow = firstSquare.getRow();
		final int commonCol = firstSquare.getCol();
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
		int existingCount = 0;
		final Direction direction = getDirection(squares);
		if (direction == Direction.HORIZONTAL) {
			for (int col = commonCol; col < lastSquare.getCol(); col++) {
				Square square= board.getSquare(commonRow, col);
				if (square.getTile() != null) {
					existingCount++;
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

		//Make sure the first turn uses the center tile
		if (isFirstTurn(board)) {
		    boolean touchesStartingSquare = false;
			for (Square square: squares) {
				touchesStartingSquare |= square.getRow() == Board.CENTER && square.getCol() == Board.CENTER;
			}
			if (!touchesStartingSquare) {
				throw new TilesNotCenteredException();
			}
		} else {
			//Check connected (i.e: not disjoint)
			boolean isConnected = false;
			if ( existingCount == 0) {
				if (direction == Direction.HORIZONTAL) {
					for (Square square: squares)  {
						if (commonRow != 0) {
							isConnected |= !isOccupied(board, commonRow - 1, square.getCol());
						}
						if (commonRow != Board.HEIGHT-1) {
							isConnected |= !isOccupied(board, commonRow + 1, square.getCol());
						}
					}
					if (firstSquare.getCol() > 0) {
						isConnected |= !isOccupied(board, commonRow, firstSquare.getCol()-1);
					}
					if (lastSquare.getCol() > Board.WIDTH-1) {
						isConnected |= !isOccupied(board, commonRow, lastSquare.getCol()+1);
					}
				} else /* Direction.VERTICAL */ { 
					for (Square square: squares)  {
						if (commonCol != 0) {
							isConnected |= !isOccupied(board, square.getRow(), commonCol -1);
						}
						if (commonCol != Board.WIDTH-1) {
							isConnected |= !isOccupied(board, square.getRow(), commonCol +1);
						}
					}
					if (firstSquare.getRow() > 0) {
						isConnected |= !isOccupied(board, firstSquare.getRow()-1, commonCol);
					}
					if (lastSquare.getRow() > Board.HEIGHT-1) {
						isConnected |= !isOccupied(board, lastSquare.getRow()+1, commonCol);
					}
				}
			}
		
			if (!isConnected) {
				throw new TilesNotConnectedException();
			}
		}
	}

	//TODO: is this before or after squares are played?  after would simplify this logic
	//Else need to check if squares contains current square in the while loops
	public List<Set<Square>> getAdjoinedSquares(Board board, SortedSet<Square> squares) {
		final List<Set<Square>> adjoinedSquares = new ArrayList<Set<Square>>();
		final Square firstSquare = squares.first();
		final Direction direction = getDirection(squares);
		
		Set<Square> wordSquares = new TreeSet<Square>();
		int row = firstSquare.getRow();
		int col = firstSquare.getCol();
		if (direction == Direction.HORIZONTAL) {
			//check for prefix and suffix on same direction
			while (col > 0 && isOccupied(board, row, col-1)) {
				col -= 1;
			}
			do {
				final Square wordSquare = board.getSquare(row, col);
				wordSquares.add(wordSquare);
				col += 1;
			} while (col < Board.WIDTH && isOccupied(board, row, col));
			
			if (wordSquares.size() >= 2) {
				adjoinedSquares.add(wordSquares);
			}
			
			//check for VERTICAL words
			for (final Square square: squares) {
				wordSquares = new TreeSet<Square>();
				row = firstSquare.getRow();
				col = square.getCol();
				while (row > 0 && isOccupied(board, row-1, col)) {
					row -= 1;
				} 
				do {
					final Square wordSquare = board.getSquare(row, col);
					wordSquares.add(wordSquare);
					row += 1;
				} while (row < Board.HEIGHT && isOccupied(board, row, col));
				if (wordSquares.size() >= 2) {
					adjoinedSquares.add(wordSquares);
				}
			}
			
		} else /* Direction.VERTICAL */ {
			while (row > 0 && isOccupied(board, row-1, col)) {
				row -= 1;
			} 
			do {
				final Square wordSquare = board.getSquare(row, col);
				wordSquares.add(wordSquare);
				row += 1;
			} while (row < Board.HEIGHT && isOccupied(board, row, col));

			if (wordSquares.size() >= 2) {
				adjoinedSquares.add(wordSquares);
			}

			//Check HORIZONTAL
			for (final Square square: squares) {
				row = square.getRow();
				col = square.getCol();
				while (col > 0 && isOccupied(board, row, col-1)) {
					col -= 1;
				}
				do {
					final Square wordSquare = board.getSquare(row, col);
					wordSquares.add(wordSquare);
					col += 1;
				} while (col < Board.WIDTH && isOccupied(board, row, col));

				if (wordSquares.size() >= 2) {
					adjoinedSquares.add(wordSquares);
				}
			}
		}
		
		return adjoinedSquares;
	}
	
	public Direction getDirection(SortedSet<Square> squares) {
		final Direction direction = squares.first().getRow() == squares.last().getRow() ? 
				Direction.HORIZONTAL : Direction.VERTICAL;
		return direction;
	}
	
	public void execute(Board board, SortedSet<Square> squares) {
		for (Square square: squares) {
			board.setSquare(square);
		}
	}
	

	
	public boolean isFirstTurn(Board board) {
		return !isOccupied(board, Board.CENTER, Board.CENTER);
	}
	public boolean isOccupied(Board board, int row, int col) {
		return getTile(board, row, col) != null;
	}
	public PlayedTile getTile(Board board, int row, int col) {
		final Square square = board.getSquare(row, col); 
		return square.getTile();
	}
}
