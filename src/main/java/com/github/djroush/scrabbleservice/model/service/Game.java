
package com.github.djroush.scrabbleservice.model.service;

import java.util.ArrayList;
import java.util.List;

public class Game {
	public final static int MAX_PLAYERS = 4;

	private String id;
	private GameState state;
	private Turn lastTurn;
	private Board board;
	private TileBag tileBag;
	private int version = 0;
	private int consecutiveScorelessTurns = 0;
	private int activePlayerIndex = 0;

	private List<Player> players = new ArrayList<Player>(MAX_PLAYERS);
	private Player playerCurrentlyUp = null;
		
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public GameState getState() {
		return state;
	}
	public void setState(GameState state) {
		this.state = state;
	}
	public Board getBoard() {
		return board;
	}
	public void setBoard(Board board) {
		this.board = board;
	}
	public TileBag getTileBag() {
		return tileBag;
	}
	public void setTileBag(TileBag tileBag) {
		this.tileBag = tileBag;
	}
	public Turn getLastTurn() {
		return lastTurn;
	}
	public void setLastTurn(Turn lastTurn) {
		this.lastTurn = lastTurn;
	}
	public int getConsecutiveScorelessTurns() {
		return consecutiveScorelessTurns;
	}
	public void setConsecutiveScorelessTurns(int consecutiveScorelessTurns) {
		this.consecutiveScorelessTurns = consecutiveScorelessTurns;
	}
	public List<Player> getPlayers() {
		return players;
	}
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	public Player getPlayerCurrentlyUp() {
		return playerCurrentlyUp;
	}
	public void setPlayerCurrentlyUp(Player currentlyUp) {
		this.playerCurrentlyUp = currentlyUp;
	}
	public int getActivePlayerIndex() {
		return activePlayerIndex;
	}
	public void setActivePlayerIndex(int activePlayerIndex) {
		this.activePlayerIndex = activePlayerIndex;
	}
}
