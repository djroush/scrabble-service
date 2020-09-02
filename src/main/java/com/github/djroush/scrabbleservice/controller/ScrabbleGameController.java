package com.github.djroush.scrabbleservice.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.djroush.scrabbleservice.exception.InvalidInputException;
import com.github.djroush.scrabbleservice.exception.InvalidPassTurnException;
import com.github.djroush.scrabbleservice.model.rest.ExchangeRequest;
import com.github.djroush.scrabbleservice.model.rest.PassTurnRequest;
import com.github.djroush.scrabbleservice.model.rest.PlayTilesRequest;
import com.github.djroush.scrabbleservice.model.rest.RestBoard;
import com.github.djroush.scrabbleservice.model.rest.RestGame;
import com.github.djroush.scrabbleservice.model.rest.RestPlayer;
import com.github.djroush.scrabbleservice.model.rest.RestPlayerGame;
import com.github.djroush.scrabbleservice.model.rest.RestRack;
import com.github.djroush.scrabbleservice.model.rest.RestTurn;
import com.github.djroush.scrabbleservice.model.rest.Square;
import com.github.djroush.scrabbleservice.model.service.Game;
import com.github.djroush.scrabbleservice.model.service.Player;
import com.github.djroush.scrabbleservice.model.service.Rack;
import com.github.djroush.scrabbleservice.model.service.Tile;
import com.github.djroush.scrabbleservice.model.service.Turn;
import com.github.djroush.scrabbleservice.model.service.TurnAction;
import com.github.djroush.scrabbleservice.service.GameService;

@RestController
@RequestMapping("/scrabble/game")
public class ScrabbleGameController {
	
	@Autowired
	private GameService gameService;

	@PostMapping(path = "")
	//FIXME: read player from body not querystring!
	public ResponseEntity<RestPlayerGame> createGame(@NonNull @RequestParam("player") String playerName) {
		Game game = gameService.newGame(playerName);
		String playerId = game.getPlayers().get(game.getPlayers().size()-1).getId();
		RestPlayerGame restPlayerGame =convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);  
	}

	//TODO: make an actual request model here!
	//FIXME: read player from body not querystring!
	@PostMapping(path = "/{gameId}")
	public ResponseEntity<RestPlayerGame> joinGame(@PathVariable String gameId, @RequestParam("player") String playerName) {
		checkInputParameters(gameId, playerName);
		if ("null".equals(gameId)) {
			throw new InvalidInputException();
		}
		Game game= gameService.addPlayer(gameId, playerName);
		String playerId = game.getPlayers().get(game.getPlayers().size()-1).getId();
		RestPlayerGame restPlayerGame = convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);   
	}
	
	@GetMapping(path = "/{gameId}/{playerId}")
	public ResponseEntity<RestPlayerGame> getGame(@PathVariable String gameId, @PathVariable String playerId,  
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
			RestPlayerGame restPlayerGame = convertModels(game, playerId);

			return ResponseEntity
			.status(HttpStatus.OK)
			.header("ETag", String.valueOf(game.getVersion()))
			.body(restPlayerGame);
		}
	}

	@DeleteMapping(path = "/{gameId}/{playerId}")
	public ResponseEntity<RestPlayerGame> leaveGame(@PathVariable String gameId, @PathVariable String playerId) {
		checkInputParameters(gameId, playerId);
		
		Game game = gameService.removePlayer(gameId, playerId);
		RestPlayerGame restPlayerGame = convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);   
	}

	@PostMapping(path = "/{gameId}/{playerId}/start")
	public ResponseEntity<RestPlayerGame> startGame(@PathVariable String gameId, @PathVariable String playerId) {
		checkInputParameters(gameId, playerId);
		Game game = gameService.start(gameId, playerId);
		RestPlayerGame restPlayerGame = convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);   
	}

	@PostMapping(path = "/{gameId}/{playerId}/play", consumes = "application/json")
	public ResponseEntity<RestPlayerGame> play(@PathVariable String gameId, @PathVariable String playerId,
			@RequestBody PlayTilesRequest playTilesRequest) throws IOException {
		checkInputParameters(gameId, playerId);
		
		final List<Square> squares = playTilesRequest.getSquares();
		SortedSet<Square> sortedSquares = new TreeSet<Square>(squares);
		Game game = gameService.playTurn(gameId, playerId, sortedSquares);
		RestPlayerGame restPlayerGame = convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);
	}
	
	@PostMapping(path = "/{gameId}/{playerId}/exchange", consumes = "application/json")
	public ResponseEntity<RestPlayerGame> exchange(@PathVariable String gameId, @PathVariable String playerId,
			@RequestBody ExchangeRequest turnRequest) throws IOException {
		checkInputParameters(gameId, playerId);
		
		final List<Tile> tiles = turnRequest.getTiles();
		Game game = gameService.exchange(gameId, playerId, tiles);
		RestPlayerGame restPlayerGame = convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);
	}

	//TODO: fix the return types on the models below here split into multiple methods
	@PostMapping(path = "/{gameId}/{playerId}/pass", consumes = "application/json")
	public ResponseEntity<RestPlayerGame> passTurn(@PathVariable String gameId, @PathVariable String playerId,
			@RequestBody PassTurnRequest turnRequest) throws IOException {
		checkInputParameters(gameId, playerId);
		
		boolean isPass = turnRequest.isPassTurn();	
		if (isPass) {
			Game game = gameService.passTurn(gameId, playerId);
			RestPlayerGame restPlayerGame = convertModels(game, playerId);
			return ResponseEntity.ok(restPlayerGame);
		} else {
			throw new InvalidPassTurnException();
		}
	}
	
	@PostMapping(path= "/{gameId}/{playerId}/challenge") 
	public ResponseEntity<RestPlayerGame> challenge(@PathVariable String gameId, @PathVariable String playerId) {
		checkInputParameters(gameId, playerId);
		final Game game = gameService.challenge(gameId, playerId);
		RestPlayerGame restPlayerGame = convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);
	}

	@PostMapping(path= "/{gameId}/{playerId}/forfeit")
	public ResponseEntity<RestPlayerGame> forfeit(@PathVariable String gameId, @PathVariable String playerId) {
		checkInputParameters(gameId, playerId);
		final Game game = gameService.forfeit(gameId, playerId);
		RestPlayerGame restPlayerGame = convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);
	}
	
	private void checkInputParameters(String gameId, String playerId) {
		if (gameId == null || gameId.isBlank() || "null".equals(gameId)) {
			throw new InvalidInputException();
		}
	}
	
	private RestPlayerGame convertModels(Game game, String playerId) {
		final RestPlayerGame playerGame = new RestPlayerGame();
		final List<Square> boardSquares = game.getBoard().getSquares();
		
		final RestBoard board = new RestBoard();
		board.setSquares(boardSquares);
		playerGame.setBoard(board);
		
		final Optional<Player> currentPlayer = game.getPlayers().stream() 
			.filter(player -> playerId.equals(player.getId()))
			.findFirst();
		final Rack rack = currentPlayer.isPresent() ? currentPlayer.get().getRack() : null;
		if (rack != null) {
			RestRack restRack = new RestRack();
			
			List<String> tiles = rack.getTiles().stream().map(tile -> { 
				String name = tile.getName();
				return "BLANK".equals(name) ? " " : name;	
			}).collect(Collectors.toList());
			
			restRack.setTiles(tiles);
			playerGame.setRack(restRack);
		}
		
		final List<RestPlayer> restPlayers = game.getPlayers().stream().
		    map(player -> {
		    final RestPlayer restPlayer = new RestPlayer();
		    //Don't show other people's ids so they can't make calls to the service pretending to be the other user
		    if (playerId.equals(player.getId())) {
		    	restPlayer.setId(player.getId());
		    }
			restPlayer.setName(player.getName());
			restPlayer.setScore(player.getScore());
			restPlayer.setSkipTurnCount(player.getSkipTurnCount());
			restPlayer.setForfeited(player.getIsForfeited());
			return restPlayer;
		}).collect(Collectors.toList());
		playerGame.setPlayers(restPlayers);

		final RestGame restGame = new RestGame();
		restGame.setId(game.getId());
		restGame.setPlayerId(playerId);
		restGame.setVersion(game.getVersion());
		restGame.setState(game.getState().name());
		restGame.setActivePlayerIndex(game.getActivePlayerIndex());
		playerGame.setGame(restGame);

		if (game.getLastTurn() != null) {
			final RestTurn restTurn = new RestTurn();
			final Turn gameTurn = game.getLastTurn();
			final TurnAction turnAction = gameTurn.getAction();
			final List<Player> gamePlayers = game.getPlayers();
			final int playerIndex = gamePlayers.indexOf(gameTurn.getPlayer());
			restTurn.setAction(turnAction);
			restTurn.setPlayerIndex(playerIndex);
			if (turnAction == TurnAction.CHALLENGE_TURN) {
				int loseTurnPlayerIndex = gamePlayers.indexOf(gameTurn.getLostTurnPlayer());
				restTurn.setLoseTurnPlayerIndex(loseTurnPlayerIndex);
				if (loseTurnPlayerIndex != playerIndex) {
					//Someone lost a challenge so their points should be negative
					restTurn.setPoints(-gameTurn.getScore());
				}
			} else {
				restTurn.setPoints(gameTurn.getScore());
			}
			playerGame.setLastTurn(restTurn);
		}
		
		return playerGame;
	}

}
