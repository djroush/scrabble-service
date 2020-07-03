package com.github.djroush.scrabbleservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import com.github.djroush.scrabbleservice.model.Board;
import com.github.djroush.scrabbleservice.model.PlayedTile;
import com.github.djroush.scrabbleservice.model.rest.Square;

public class BoardServiceTest {

	private final char[][] EMPTY_BOARD = new char[][] {
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '}
	};
	

	private final char[][] VALID_BOARD = new char[][] {
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ','A',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ','T',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ','T',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ','E',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ','B','A','S','I','C',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ','H','I','N','T',' ','A','R',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ','S','O',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ','E','D',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ','E',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ','N',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ','T',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '}
	};
	
	private BoardService boardService = new BoardService();
		
	@Test
	public void testIsFirstTurn() {
		final Board board = boardFrom(EMPTY_BOARD);
		boolean result = boardService.isFirstTurn(board);
		assertTrue(result);
	}

	
	@Test
	public void testNotFirstTurn() {
		final Board board = boardFrom(VALID_BOARD);
		boolean result = boardService.isFirstTurn(board);
		assertFalse(result);
	}
	
	@Test
	public void testAdjoinedLetters1() {
		final Board board = boardFrom(VALID_BOARD);
		
		SortedSet<Square> squares = new TreeSet<Square>();
		squares.add(square(7, 2, 'H'));
		squares.add(square(7, 3, 'I'));
		squares.add(square(7, 4, 'N'));
		
		List<Set<Square>> result = boardService.getAdjoinedSquares(board, squares);
		List<String> words = getWords(result);
		assertEquals(3, words.size());
		assertTrue(words.contains("HINT"));
        assertTrue(words.contains("BI"));
        assertTrue(words.contains("AN"));
	}

	@Test
	public void testLengthValidEmpty() {
		assertFalse(boardService.isCorrectLength(Collections.emptySortedSet()));
	}
	@Test
	public void testLengthValidGood() {
		SortedSet<Square> squares = new TreeSet<Square>();
		squares.add(square(7, 3, 'V'));
		squares.add(square(7, 4, 'A'));
		squares.add(square(7, 5, 'L'));
		squares.add(square(7, 6, 'I'));
		squares.add(square(7, 7, 'D'));
		assertTrue(boardService.isCorrectLength(squares));
	}

	@Test
	public void testLengthValidTooLong() {
		SortedSet<Square> squares = new TreeSet<Square>();
		squares.add(square(7, 3, 'W'));
		squares.add(square(7, 4, 'H'));
		squares.add(square(7, 5, 'O'));
		squares.add(square(7, 6, 'O'));
		squares.add(square(7, 7, 'P'));
		squares.add(square(7, 8, 'S'));
		squares.add(square(7, 9, 'I'));
		squares.add(square(7,10, 'E'));
		assertFalse(boardService.isCorrectLength(squares));
	}
	
	@Test
	public void touchesCenterSquareTrue() {
		SortedSet<Square> squares = new TreeSet<Square>();
		squares.add(square(7, 3, 'P'));
		squares.add(square(7, 4, 'I'));
		squares.add(square(7, 5, 'Q'));
		squares.add(square(7, 6, 'U'));
		squares.add(square(7, 7, 'E'));

		assertTrue(boardService.touchesStartingSquare(squares));
	}
	
	
	@Test
	public void touchesCenterSquareFalse() {
		SortedSet<Square> squares = new TreeSet<Square>();
		squares.add(square( 7, 8, 'R'));
		squares.add(square( 8, 8, 'O'));
		squares.add(square( 9, 8, 'D'));
		squares.add(square(10, 8, 'E'));
		squares.add(square(11, 8, 'N'));
		squares.add(square(12, 8, 'T'));

		assertFalse(boardService.touchesStartingSquare(squares));
	}
	
	@Test
	public void testAdjoinedLetters() {
		final Board board = boardFrom(VALID_BOARD);
		
		SortedSet<Square> squares = new TreeSet<Square>();
		squares.add(square( 7, 8, 'R'));
		squares.add(square( 8, 8, 'O'));
		squares.add(square( 9, 8, 'D'));
		squares.add(square(10, 8, 'E'));
		squares.add(square(11, 8, 'N'));
		squares.add(square(12, 8, 'T'));
		
		List<Set<Square>> result = boardService.getAdjoinedSquares(board, squares);
		List<String> words = getWords(result);
		assertEquals(4, words.size());
		assertTrue(words.contains("AR"));
        assertTrue(words.contains("SO"));
        assertTrue(words.contains("ED"));
        assertTrue(words.contains("RODENT"));
	}

	@Test
	public void tesAreTilesConnectedFalse() {
		Board board = boardFrom(VALID_BOARD);
		
		SortedSet<Square> squares = new TreeSet<Square>();
		squares.add(square( 7, 14, 'N'));
		squares.add(square( 8, 14, 'O'));
		squares.add(square( 9, 14, 'P'));
		squares.add(square(10, 14, 'E'));

		boolean result = boardService.areTilesConnected(board, squares); 
		assertFalse(result);
	}
	
	@Test
	public void tesAreTilesConnectedTrue() {
		Board board = boardFrom(VALID_BOARD);
		
		SortedSet<Square> squares = new TreeSet<Square>();
		squares.add(square( 6, 9, 'W'));
		squares.add(square( 7, 9, 'E'));
		squares.add(square( 8, 9, 'B'));

		boolean result = boardService.areTilesConnected(board, squares); 
		assertTrue(result);
	}
	

	//HELPER METHODS
	private Board boardFrom(char[][] boardLetters) {
		final Board board = new Board();
		for (int row = 0; row < boardLetters.length; row++) {
			char[] rowLetters = boardLetters[row];
			for (int col = 0; col < rowLetters.length; col++) {
				char letter = rowLetters[col];
				Square square = new Square(row, col);
				if (letter != ' ' ) {
					PlayedTile playedTile = new PlayedTile();
					playedTile.setLetter(letter);
					square.setTile(playedTile);
				}
				board.addSquare(square);
			}
		}
		return board;
	}
    private Square square(int row, int col, char letter) {
		PlayedTile playedTile1 = new PlayedTile();
		playedTile1.setLetter('Z');
		return new Square(row,col);
    }
	

	private List<String> getWords(List<Set<Square>> result) {
		final List<String> words = new ArrayList<String>();
		result.forEach(res -> { 
			final String word  = res.stream()
				.mapToInt(square -> square.getTile().getLetter())
				.collect(StringBuilder::new, 
	                    StringBuilder::appendCodePoint, 
	                    StringBuilder::append)
				.toString();
			words.add(word);
		});
		return words;
	}
}
