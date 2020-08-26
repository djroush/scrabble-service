
package com.github.djroush.scrabbleservice.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Game {
	public final static int MAX_PLAYERS = 4;

	private int version;
	private String playerId;
	private String id;
	private GameState state;
	private Turn lastTurn;
	private Board board;
	private TileBag tileBag;
	int consecutiveScorelessTurns = 0;
	int turnNumber = 0;
	int activePlayerCount = 0;
	int activePlayerIndex = 0;

	private List<Player> players = new ArrayList<Player>(MAX_PLAYERS);
	@JsonIgnore
	private ListIterator<Player> turnIterator = players.listIterator();
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
	
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
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
	@JsonIgnore
	public TileBag getTileBag() {
		return tileBag;
	}
	@JsonIgnore
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
	public ListIterator<Player> getTurnIterator() {
		return turnIterator;
	}
	public void setTurnIterator(ListIterator<Player> turnIterator) {
		this.turnIterator = turnIterator;
	}
	public Player getPlayerCurrentlyUp() {
		return playerCurrentlyUp;
	}
	public void setPlayerCurrentlyUp(Player currentlyUp) {
		this.playerCurrentlyUp = currentlyUp;
	}
	public int getTurnNumber() {
		return turnNumber;
	}
	public void setTurnNumber(int turnNumber) {
		this.turnNumber = turnNumber;
	}
	public int getActivePlayerCount() {
		return activePlayerCount;
	}
	public void setActivePlayerCount(int activePlayerCount) {
		this.activePlayerCount = activePlayerCount;
	}
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
}