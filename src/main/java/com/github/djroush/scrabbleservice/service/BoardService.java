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
import com.github.djroush.scrabbleservice.model.Rack;
import com.github.djroush.scrabbleservice.model.rest.Square;

@Service
public class BoardService {

	public void checkMoveValid(Board board, SortedSet<Square> squares) {
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
		if (squares.size() > 1) {
			for(Square square: squares) {
				rowsMatch &= commonRow == square.getRow();
				colsMatch &= commonCol == square.getCol();
			}
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
		final Square firstSquare = squares.first();
		final Square lastSquare = squares.last();
		int commonRow = firstSquare.getRow();
		int commonCol = firstSquare.getCol();
		
		boolean isConnected = false;
		final Direction direction = getDirection(squares);

		if (direction == Direction.HORIZONTAL) {
			for (Square square: squares)  {
				if (commonRow != 0) {
					isConnected |= isOccupied(board, commonRow - 1, square.getCol());
				}
				if (commonRow != Board.HEIGHT-1) {
					isConnected |= isOccupied(board, commonRow + 1, square.getCol());
				}
			}
			if (firstSquare.getCol() > 0) {
				isConnected |= isOccupied(board, commonRow, firstSquare.getCol()-1);
			}
			if (lastSquare.getCol() > Board.WIDTH-1) {
				isConnected |= isOccupied(board, commonRow, lastSquare.getCol()+1);
			}
		} else /* Direction.VERTICAL */ { 
			for (Square square: squares)  {
				if (commonCol != 0) {
					isConnected |= isOccupied(board, square.getRow(), commonCol -1);
				}
				if (commonCol != Board.WIDTH-1) {
					isConnected |= isOccupied(board, square.getRow(), commonCol +1);
				}
			}
			if (firstSquare.getRow() > 0) {
				isConnected |= isOccupied(board, firstSquare.getRow()-1, commonCol);
			}
			if (lastSquare.getRow() > Board.HEIGHT-1) {
				isConnected |= isOccupied(board, lastSquare.getRow()+1, commonCol);
			}
		}
		return isConnected;
	}
	
	public void playSquares(Board board, SortedSet<Square> squares) {
		for (Square square: squares) {
			board.addSquare(square);
		}
	}

	public List<Set<Square>> getAdjoinedSquares(Board board, SortedSet<Square> squares) {
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
