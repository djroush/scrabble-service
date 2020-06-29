package com.github.djroush.scrabbleservice.controller;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.djroush.scrabbleservice.model.Game;
import com.github.djroush.scrabbleservice.model.Square;
import com.github.djroush.scrabbleservice.model.Tile;
import com.github.djroush.scrabbleservice.service.GameService;

@RestController
@RequestMapping("/scrabble/game")
public class ScrabbleGameController {
	@Autowired
	private GameService gameService;

	@PostMapping(path = "")
	public ResponseEntity<Game> newGame(@NonNull @RequestParam("player") String playerName) {
		Game game = gameService.newGame(playerName);
		return ResponseEntity.ok(game);  
	}

	@GetMapping(path = "/{gameId}")
	public ResponseEntity<Game> getState(@PathVariable String gameId) {
		Game game = gameService.refreshGame(gameId);
		return ResponseEntity.ok(game);  
	}
	//TODO: make an actual request model here!
	@PostMapping(path = "/{gameId}")
	public ResponseEntity<Game> join(@PathVariable String gameId, @RequestParam("player") String playerName) {
		Game game= gameService.addPlayer(gameId, playerName);
		return ResponseEntity.ok(game);   
	}
	
	@DeleteMapping(path = "/{gameId}/{playerId}")
	public ResponseEntity<?> leave(@PathVariable String gameId, @PathVariable String playerId) {
		Game game = gameService.removePlayer(gameId, playerId);
		return ResponseEntity.ok(game);  
	}

	@PostMapping(path = "/{gameId}/{playerId}/start")
	public ResponseEntity<?> startGame(@PathVariable String gameId, @PathVariable String playerId) {
		Game game = gameService.start(gameId, playerId);
		return ResponseEntity.ok(game);  
	}

	@PostMapping(path = "/{gameId}/{playerId}/await")
	public ResponseEntity<?> await(@PathVariable String gameId, @PathVariable String playerId) {
		//TODO: change the body to make it a Request with skip flag
		Game game = gameService.awaitUpdate(gameId, playerId);

		return ResponseEntity.ok(game);  
	}

	
	//TODO: write played words into a database to aide with computer logic?
	@PostMapping(path = "/{gameId}/{playerId}/play")
	public ResponseEntity<?> playTurn(@PathVariable String gameId, @PathVariable String playerId,
			@RequestBody List<Square> squares) {
		//TODO: change the body to make it a Request with skip flag
		SortedSet<Square> sortedSquares = new TreeSet<Square>(squares);
		Game game = gameService.playTurn(gameId, playerId, sortedSquares);

		return ResponseEntity.ok(game);  
	}

	@PostMapping(path= "/{gameId}/{playerId}/pass") 
	public ResponseEntity<?> passTurn(@PathVariable String gameId, @PathVariable String playerId,
			@RequestBody List<Tile> tiles) {
		gameService.passTurn(gameId, playerId);
		return null;
	}
	
	@PostMapping(path= "/{gameId}/{playerId}/exchange") 
	public ResponseEntity<?> exchange(@PathVariable String gameId, @PathVariable String playerId,
			@RequestBody List<Tile> tiles) {
		SortedSet<Tile> sortedTiles = new TreeSet<Tile>(tiles);
		gameService.exchange(gameId, playerId, sortedTiles, false);
		return null;
	}

	@PostMapping(path= "/{gameId}/{playerId}/challenge") 
	public ResponseEntity<Game> challenge(@PathVariable String gameId, @PathVariable String playerId) {
		final Game game = gameService.challenge(gameId, playerId);
		return ResponseEntity.ok(game); 
	}
	
	//Merge with methods above?
	@PostMapping(path= "/{gameId}/{playerId}/forfeit") 
	public ResponseEntity<?> forfeit(@PathVariable String gameId, @PathVariable String playerId) {
		final Game game = gameService.forfeit(gameId, playerId);
		return ResponseEntity.ok(game);
	}

}
