package com.github.djroush.scrabbleservice.controller;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.djroush.scrabbleservice.exception.InvalidTurnException;
import com.github.djroush.scrabbleservice.model.Game;
import com.github.djroush.scrabbleservice.model.PlayedTile;
import com.github.djroush.scrabbleservice.model.Tile;
import com.github.djroush.scrabbleservice.model.rest.Square;
import com.github.djroush.scrabbleservice.model.rest.TurnRequest;
import com.github.djroush.scrabbleservice.service.GameService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/scrabble/game")
public class ScrabbleGameController {
	@Autowired
	private GameService gameService;

	@PostMapping(path = "")
	public ResponseEntity<Game> createGame(@NonNull @RequestParam("player") String playerName) {
		Game game = gameService.newGame(playerName);
		
		return ResponseEntity.ok(game);  
	}

	//TODO: make an actual request model here!
	@PostMapping(path = "/{gameId}")
	public ResponseEntity<Game> joinGame(@PathVariable String gameId, @RequestParam("player") String playerName) {
		Game game= gameService.addPlayer(gameId, playerName);
		return ResponseEntity.ok(game);   
	}
	
	@GetMapping(path = "/{gameId}/{playerId}")
	public ResponseEntity<Game> getGame(@PathVariable String gameId) {
		Game game = gameService.refreshGame(gameId);
		return ResponseEntity.ok(game);  
	}
	@PostMapping(path = "/{gameId}/{playerId}/start")
	public ResponseEntity<?> startGame(@PathVariable String gameId, @PathVariable String playerId) {
		Game game = gameService.start(gameId, playerId);
		return ResponseEntity.ok(game);  
	}
	@DeleteMapping(path = "/{gameId}/{playerId}")
	public ResponseEntity<?> leaveGame(@PathVariable String gameId, @PathVariable String playerId) {
		Game game = gameService.removePlayer(gameId, playerId);
		return ResponseEntity.ok(game);  
	}


	@GetMapping(path = "/{gameId}/{playerId}/await")
	public Mono<ResponseEntity<Game>> await(@PathVariable String gameId, @PathVariable String playerId, @RequestHeader("ETag") String eTag) {
		Game game = gameService.refreshGame(gameId);
		int currentVersion = game.getVersion();
		int previousVersion = -1;
		if (eTag != null) {
			try {
				previousVersion = Integer.parseInt(eTag);
			} finally {}
		}
		boolean eTagMissing = eTag == null;
		if (previousVersion < currentVersion || eTagMissing) {
			return Mono.just(ResponseEntity
				.status(HttpStatus.OK)
				.header("ETag", String.valueOf(game.getVersion()))
				.body(game));
		} else if (previousVersion > currentVersion) {
			return Mono.just(ResponseEntity
				.status(HttpStatus.PRECONDITION_FAILED)
				.body(null));
		} else {
			Mono<Game> updatedGame = gameService.awaitUpdate(gameId, currentVersion);
			//Success
			Mono<ResponseEntity<Game>> result = updatedGame
				.map(game1 ->ResponseEntity
					.status(HttpStatus.OK)
					.header("ETag", String.valueOf(game.getVersion()))
					.body(game));
			result.defaultIfEmpty(ResponseEntity
					.status(HttpStatus.NOT_MODIFIED)
					.header("ETag", String.valueOf(game.getVersion()))
					.build());

			
			
//			Game updatedGame = gameService.awaitUpdate(gameId, currentVersion);
//			//Success
//			Mono<ResponseEntity<Game>> result = Mono.just(updatedGame)
//				.map(game1 ->ResponseEntity
//					.status(HttpStatus.OK)
//					.header("ETag", String.valueOf(game.getVersion()))
//					.body(game));
//			result.defaultIfEmpty(ResponseEntity
//					.status(HttpStatus.NOT_MODIFIED)
//					.header("ETag", String.valueOf(game.getVersion()))
//					.build());

//			//Error
//			return ResponseEntity
//				.status(HttpStatus.INTERNAL_SERVER_ERROR)
//				.body(t);
//			};
					
			return result;
		}
	}
	
	@PostMapping(path = "/{gameId}/{playerId}", consumes = "application/json")
	public ResponseEntity<Game> takeTurn(@PathVariable String gameId, @PathVariable String playerId,
			@RequestBody TurnRequest turnRequest) throws IOException {
		
		final List<Square> squares = turnRequest.getSquares();
		final List<Tile> tiles = turnRequest.getTiles();
		boolean isPlay =  squares != null;
		boolean isExchange = tiles != null;
		boolean isPass = turnRequest.isPassTurn();
		boolean isSkipped = turnRequest.isLostTurn();
		
		int count = 0;
		if (isPlay) { count++; }
		if (isExchange) { count++; }
		if (isPass) { count++; }
		if (isSkipped) { count++; }
		
		if (count != 1) {
			throw new InvalidTurnException();
		}
		Game game = null;
		if (isPlay) {
			SortedSet<Square> sortedSquares = new TreeSet<Square>(squares);
			game = gameService.playTurn(gameId, playerId, sortedSquares);
		} else if (isExchange) {
			game = gameService.exchange(gameId, playerId, tiles);
		} else {
			game = gameService.passTurn(gameId, playerId);
		}
		return ResponseEntity.ok(game);
	}

	
	public static void main(String[] args) throws IOException {
		Square square = new Square(7, 5);
		PlayedTile tile = new PlayedTile();
		tile.setLetter('M');
		tile.setBlank(false);
		square.setTile(tile);
		
		ObjectMapper objectMapper = new ObjectMapper(); 
		String json = objectMapper.writeValueAsString(square);
		System.out.println(json);
		
		String json2 = "{\"row\":7,\"col\":5,\"tile\":{\"blank\":false,\"letter\":\"M\"}}";
		Square square2 = objectMapper.readValue(json2, Square.class);
		System.out.println("square2: " + square2);
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
