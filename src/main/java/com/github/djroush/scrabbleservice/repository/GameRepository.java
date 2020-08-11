package com.github.djroush.scrabbleservice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.github.djroush.scrabbleservice.model.Game;
import com.github.djroush.scrabbleservice.service.GamePublisher;

@Repository
public class GameRepository {
	@Autowired
	private GamePublisher gamePublisher;
	
	//FIXME: Make this an interface and start with an in memory implementation
	//for now make this work with a single game for testing
	private Game game;
	
	public Game find(String gameId) {
		return game;
	}
	public void insert(Game game) {
		this.game = game;
		gamePublisher.addUpdate(game);
	}
	public void update(Game game) {
		this.game = game;
		gamePublisher.addUpdate(game);
	}

}
