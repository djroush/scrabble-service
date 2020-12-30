
package com.github.djroush.scrabbleservice.model.service;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Game {
	public final static int MAX_PLAYERS = 4;

	private String id;
	private GameState state;
	private Turn lastTurn;
	private Board board;
	private TileBag tileBag;
	private int version = 0;
	private int consecutiveScorelessTurns = 0;
	private int activePlayerIndex = -1;

	private List<Player> players = new ArrayList<Player>(MAX_PLAYERS);
	private Player activePlayer = null;
	private Player lastPlayerToPlayTiles = null;
}
