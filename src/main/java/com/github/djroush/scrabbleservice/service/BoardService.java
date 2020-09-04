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
import com.github.djroush.scrabbleservice.model.rest.Square;
import com.github.djroush.scrabbleservice.model.service.Board;
import com.github.djroush.scrabbleservice.model.service.Direction;
import com.github.djroush.scrabbleservice.model.service.Rack;

@Service
public class BoardService {
	public List<Set<Square>> playSquares(Board board, SortedSet<Square> squares) {
		checkMoveValid(board, squares);
		for (Square square: squares) {
			board.setSquare(square);
		}
		return getAdjoinedSquares(board, squares);
	}
	
	private void checkMoveValid(Board board, SortedSet<Square> squares) {
		boolean correctLength = isCorrectLength(squares);
		if (!correctLength) {
			throw new IncorrectTileCountException();
		}
		boolean areTilesAligned = areTilesAligned(squares);
		if (!areTilesAligned) {
			throw new IncorrectTileAlignmentException();
		}
		boolean areTilesContiguous = areTilesContiguous(board, squares);
		if (!areTilesContiguous) {
			throw new TilesNotConnectedException();
		}
		if (isFirstTurn(board)) {
		    boolean touchesStartingSquare = touchesStartingSquare(squares);
			if (!touchesStartingSquare) {
				throw new TilesNotCenteredException();
			}
		} else {
			boolean areTilesConnected = areTilesConnected(board, squares);
			if (!areTilesConnected) {
				throw new TilesNotConnectedException();
			}
		}
	}
	
	boolean isCorrectLength(Set<Square> squares) {
		return squares.size() > 0 && squares.size() <= Rack.MAX_TILES;
	}
	boolean areTilesAligned(SortedSet<Square> squares) {
		final Square firstSquare = squares.first();
		final int commonRow = firstSquare.getRow();
		final int commonCol = firstSquare.getCol();
		
		boolean colsMatch = true;
		boolean rowsMatch = true;
		for(Square square: squares) {
			rowsMatch &= commonRow == square.getRow();
			colsMatch &= commonCol == square.getCol();
		}
		return rowsMatch || colsMatch;
	}
	
	boolean touchesStartingSquare(SortedSet<Square> squares) {
		boolean touchesStartingSquare = false;
		for (Square square: squares) {
			touchesStartingSquare |= square.getRow() == Board.CENTER && square.getCol() == Board.CENTER;
		}
		return touchesStartingSquare;
	}
	
	public boolean areTilesContiguous(Board board, SortedSet<Square> squares) {
		final Square firstSquare = squares.first();
		final Square lastSquare = squares.last();
		int commonRow = firstSquare.getRow();
		int commonCol = firstSquare.getCol();
		
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
			return squares.size() + existingCount - 1  == lastSquare.getCol() - firstSquare.getCol();
		} else {  /*Direction.VERTICAL */
			for (int row = commonRow; row < lastSquare.getRow(); row++) {
				Square square = board.getSquare(row, commonCol);
				if (square.getTile() != null) {
					existingCount++;
				}
			}
			return squares.size() + existingCount - 1  == lastSquare.getRow() - firstSquare.getRow();
		}
	}
	
	boolean areTilesConnected(Board board, SortedSet<Square> squares) {
		boolean isConnected = false;

		for (Square square: squares)  {
			int row = square.getRow();
			int col = square.getCol();
			if (col != 0) {
				isConnected |= isOccupied(board, row, col-1);
			}
			if (col != Board.WIDTH-1) {
				isConnected |= isOccupied(board, row, col+1);
			}
			if (row > 0) {
				isConnected |= isOccupied(board, row-1, col);
			}
			if (row < Board.HEIGHT-1) {
				isConnected |= isOccupied(board, row+1, col);
			}
		}
		return isConnected;
	}

	 List<Set<Square>> getAdjoinedSquares(Board board, SortedSet<Square> squares) {
		final List<Set<Square>> adjoinedSquares = new ArrayList<Set<Square>>();
		final Square firstSquare = squares.first();
		final Direction direction = getDirection(squares);
		
		Set<Square> wordSquares = new TreeSet<Square>();
		int row = firstSquare.getRow();
		int col = firstSquare.getCol();
		if (direction == Direction.HORIZONTAL) {
			while (col > 0 && isOccupied(board, row, col-1)) {
				col -= 1;
			}
			do {
				final Square wordSquare = board.getSquare(row, col);
				wordSquares.add(wordSquare);
				col += 1;
			} while (col < Board.WIDTH && isOccupied(board, row, col));
			
			if (wordSquares.size() >= Board.MIN_WORD_LENGTH) {
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
				if (wordSquares.size() >= Board.MIN_WORD_LENGTH) {
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
				wordSquares = new TreeSet<Square>();
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
	private  Direction getDirection(SortedSet<Square> squares) {
		final Direction direction = squares.first().getRow() == squares.last().getRow() ? 
				Direction.HORIZONTAL : Direction.VERTICAL;
		return direction;
	}
	
	boolean isFirstTurn(Board board) {
		return !isOccupied(board, Board.CENTER, Board.CENTER);
	}
	boolean isOccupied(Board board, int row, int col) {
		return board.getSquare(row, col).getTile() != null;
	}
}
