package com.github.djroush.scrabbleservice.model.rest;

import java.util.List;

public class RestPlayerGame {
    private RestBoard board;
    private List<RestPlayer> players;
	private RestRack rack;
	private RestGame game;

	public RestBoard getBoard() {
		return board;
	}
	public void setBoard(RestBoard board) {
		this.board = board;
	}
	public List<RestPlayer> getPlayers() {
		return players;
	}
	public void setPlayers(List<RestPlayer> players) {
		this.players = players;
	}
	public RestRack getRack() {
		return rack;
	}
	public void setRack(RestRack rack) {
		this.rack = rack;
	}
	public RestGame getGame() {
		return game;
	}
	public void setGame(RestGame game) {
		this.game = game;
	}
}
