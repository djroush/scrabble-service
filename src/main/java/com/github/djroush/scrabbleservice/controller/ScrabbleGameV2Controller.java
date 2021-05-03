package com.github.djroush.scrabbleservice.controller;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RestController;

import com.github.djroush.scrabbleservice.exception.InvalidInputException;
import com.github.djroush.scrabbleservice.model.rest.AddPlayerRequest;
import com.github.djroush.scrabbleservice.model.rest.ChallengeRequest;
import com.github.djroush.scrabbleservice.model.rest.ExchangeRequest;
import com.github.djroush.scrabbleservice.model.rest.PlayTilesRequest;
import com.github.djroush.scrabbleservice.model.rest.RestGame;
import com.github.djroush.scrabbleservice.model.rest.RestPlayerGame;
import com.github.djroush.scrabbleservice.model.rest.Square;
import com.github.djroush.scrabbleservice.model.service.Game;
import com.github.djroush.scrabbleservice.model.service.GameState;
import com.github.djroush.scrabbleservice.model.service.Tile;
import com.github.djroush.scrabbleservice.service.ConverterService;
import com.github.djroush.scrabbleservice.service.GameService;

@RestController
@RequestMapping("/v2/scrabble/game")
public class ScrabbleGameV2Controller {
	
	@Autowired
	private GameService gameService;
	@Autowired 
	private ConverterService converterService;
	
	@PostMapping(path = "")
	public ResponseEntity<RestPlayerGame> createGame(@NonNull @RequestBody AddPlayerRequest requestBody) {
		String playerName = requestBody.getName();

		Game game = gameService.newGame(playerName);
		String playerId = game.getPlayers().get(game.getPlayers().size()-1).getId();
		RestPlayerGame restPlayerGame = converterService.convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);  
	}

	@PostMapping(path = "/{gameId}")
	public ResponseEntity<RestPlayerGame> joinGame(@PathVariable String gameId, @RequestBody AddPlayerRequest requestBody) {
		String playerName = requestBody.getName();
		checkInputParameters(gameId, playerName);

		Game game= gameService.addPlayer(gameId, playerName);
		String playerId = game.getPlayers().get(game.getPlayers().size()-1).getId();
		RestPlayerGame restPlayerGame = converterService.convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);   
	}
	
	@GetMapping(path = "/{gameId}/{playerId}")
	public ResponseEntity<RestPlayerGame> refreshGame(@PathVariable String gameId, @PathVariable String playerId,  
			@RequestHeader(value="ETag",required=false) String eTag) {
		checkInputParameters(gameId, playerId);
		int previousVersion = -1;
		if (eTag != null) {
			try {
				previousVersion = Integer.parseInt(eTag);
			} catch (NumberFormatException nfe){}
		}
		Game game = gameService.refreshGame(gameId, playerId);
		int currentVersion = game.getVersion();
		if (previousVersion == currentVersion) {
			return ResponseEntity
			.status(HttpStatus.NOT_MODIFIED)
			.header("ETag", String.valueOf(game.getVersion()))
			.build();
		} else {
			RestPlayerGame restPlayerGame = converterService.convertModels(game, playerId);

			return ResponseEntity
			.status(HttpStatus.OK)
			.header("ETag", String.valueOf(game.getVersion()))
			.body(restPlayerGame);
		}
	}

	@DeleteMapping(path = "/{gameId}/{playerId}")
	public ResponseEntity<RestGame> leaveGame(@PathVariable String gameId, @PathVariable String playerId) {
		checkInputParameters(gameId, playerId);
		
		Game game = gameService.removePlayer(gameId, playerId);
		RestGame restGame = converterService.convertSimple(game, playerId);
		return ResponseEntity.ok(restGame);   
	}

	@PostMapping(path = "/{gameId}/{playerId}/start")
	public ResponseEntity<RestGame> startGame(@PathVariable String gameId, @PathVariable String playerId) {
		checkInputParameters(gameId, playerId);
		Game game = gameService.start(gameId, playerId);
		RestGame restGame = converterService.convertSimple(game, playerId);
		
		return ResponseEntity.ok(restGame);   
	}

	@PostMapping(path = "/{gameId}/{playerId}/play", consumes = "application/json")
	public ResponseEntity<RestGame> play(@PathVariable String gameId, @PathVariable String playerId,
			@RequestBody PlayTilesRequest playTilesRequest) {
		checkInputParameters(gameId, playerId);
		
		final List<Square> squares = playTilesRequest.getSquares();
		SortedSet<Square> sortedSquares = new TreeSet<Square>(squares);
		Game game = gameService.playTiles(gameId, playerId, sortedSquares);
		//@Async methods only work when called from outside their own class
		if (game.getState() == GameState.ENDGAME) {
			gameService.endGame(gameId, game.getLastTurn().getPlayer());
		} else {
			gameService.setChallengeTimer(game);
		}

		RestGame restGame = converterService.convertSimple(game, playerId);
		return ResponseEntity.ok(restGame);
	}
	
	@PostMapping(path = "/{gameId}/{playerId}/exchange", consumes = "application/json")
	public ResponseEntity<RestGame> exchange(@PathVariable String gameId, @PathVariable String playerId,
			@RequestBody ExchangeRequest exchangeRequest) throws IOException {
		checkInputParameters(gameId, playerId);
		
		final List<Tile> tiles = exchangeRequest.getTiles().stream()
				.map(t -> t.isBlank() ? Tile.BLANK : Tile.from(t.getLetter().charAt(0)))
				.collect(Collectors.toList());
		Game game = gameService.exchange(gameId, playerId, tiles);
		RestGame restGame = converterService.convertSimple(game, playerId);
		return ResponseEntity.ok(restGame);
	}

	@PostMapping(path = "/{gameId}/{playerId}/pass", consumes = "application/json")
	public ResponseEntity<RestGame> passTurn(@PathVariable String gameId, @PathVariable String playerId) {
		checkInputParameters(gameId, playerId);
		
		Game game = gameService.passTurn(gameId, playerId);
		RestGame restGame = converterService.convertSimple(game, playerId);
		return ResponseEntity.ok(restGame);
	}
	
	@PostMapping(path= "/{gameId}/{playerId}/challenge") 
	public ResponseEntity<RestGame> challenge(@PathVariable String gameId, @PathVariable String playerId,
			@RequestBody ChallengeRequest challengeRequest) {
		checkInputParameters(gameId, playerId);
		boolean challengeTurn = challengeRequest.isChallengeTurn();
		int version = challengeRequest.getVersion();
		final Game game = gameService.challenge(gameId, playerId, challengeTurn, version);
		RestGame restGame = converterService.convertSimple(game, playerId);
		return ResponseEntity.ok(restGame);
	}

	@PostMapping(path= "/{gameId}/{playerId}/forfeit")
	public ResponseEntity<RestGame> forfeit(@PathVariable String gameId, @PathVariable String playerId) {
		checkInputParameters(gameId, playerId);
		final Game game = gameService.forfeit(gameId, playerId);
		RestGame restGame = converterService.convertSimple(game, playerId);
		return ResponseEntity.ok(restGame);
	}
	
	private void checkInputParameters(String gameId, String playerId) {
		if (gameId == null || gameId.isBlank() || "".equals(gameId)) {
			throw new InvalidInputException();
		}
	}
	

}
