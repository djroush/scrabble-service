package com.github.djroush.scrabbleservice.repository;

import org.springframework.stereotype.Repository;

import com.github.djroush.scrabbleservice.model.Game;

@Repository
public class GameRepository {
	//FIXME: Make this an interface and start with an in memory implementation
	//for now make this work with a single game for testing
	private Game game;
	
	public Game find(String gameId) {
		return game;
	}

	public void update(Game game) {
		this.game = game;
	}

	public void insert(Game game) {
		this.game = game;
	}
}
