package com.github.djroush.scrabbleservice.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.github.djroush.scrabbleservice.model.rest.RestBoard;
import com.github.djroush.scrabbleservice.model.rest.RestGame;
import com.github.djroush.scrabbleservice.model.rest.RestPlayer;
import com.github.djroush.scrabbleservice.model.rest.RestPlayerGame;
import com.github.djroush.scrabbleservice.model.rest.RestRack;
import com.github.djroush.scrabbleservice.model.rest.RestTurn;
import com.github.djroush.scrabbleservice.model.rest.Square;
import com.github.djroush.scrabbleservice.model.service.Game;
import com.github.djroush.scrabbleservice.model.service.GameState;
import com.github.djroush.scrabbleservice.model.service.PlayedTile;
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
	public ResponseEntity<RestPlayerGame> createGame(@NonNull @RequestBody AddPlayerRequest requestBody) {
		String playerName = requestBody.getName();

		Game game = gameService.newGame(playerName);
		String playerId = game.getPlayers().get(game.getPlayers().size()-1).getId();
		RestPlayerGame restPlayerGame =convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);  
	}

	@PostMapping(path = "/{gameId}")
	public ResponseEntity<RestPlayerGame> joinGame(@PathVariable String gameId, @RequestBody AddPlayerRequest requestBody) {
		String playerName = requestBody.getName();
		checkInputParameters(gameId, playerName);

		Game game= gameService.addPlayer(gameId, playerName);
		String playerId = game.getPlayers().get(game.getPlayers().size()-1).getId();
		RestPlayerGame restPlayerGame = convertModels(game, playerId);
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

		RestPlayerGame restPlayerGame = convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);
	}
	
	@PostMapping(path = "/{gameId}/{playerId}/exchange", consumes = "application/json")
	public ResponseEntity<RestPlayerGame> exchange(@PathVariable String gameId, @PathVariable String playerId,
			@RequestBody ExchangeRequest exchangeRequest) throws IOException {
		checkInputParameters(gameId, playerId);
		
		final List<Tile> tiles = exchangeRequest.getTiles().stream()
				.map(t -> t.isBlank() ? Tile.BLANK : Tile.from(t.getLetter().charAt(0)))
				.collect(Collectors.toList());
		Game game = gameService.exchange(gameId, playerId, tiles);
		RestPlayerGame restPlayerGame = convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);
	}

	@PostMapping(path = "/{gameId}/{playerId}/pass", consumes = "application/json")
	public ResponseEntity<RestPlayerGame> passTurn(@PathVariable String gameId, @PathVariable String playerId) {
		checkInputParameters(gameId, playerId);
		
		Game game = gameService.passTurn(gameId, playerId);
		RestPlayerGame restPlayerGame = convertModels(game, playerId);
		return ResponseEntity.ok(restPlayerGame);
	}
	
	@PostMapping(path= "/{gameId}/{playerId}/challenge") 
	public ResponseEntity<RestPlayerGame> challenge(@PathVariable String gameId, @PathVariable String playerId,
			@RequestBody ChallengeRequest challengeRequest) {
		checkInputParameters(gameId, playerId);
		boolean challengeTurn = challengeRequest.isChallengeTurn();
		int version = challengeRequest.getVersion();
		final Game game = gameService.challenge(gameId, playerId, challengeTurn, version);
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
		if (gameId == null || gameId.isBlank() || "".equals(gameId)) {
			throw new InvalidInputException();
		}
	}
	
	private RestPlayerGame convertModels(Game game, String playerId) {
		final RestPlayerGame playerGame = new RestPlayerGame();
		final List<Square> boardSquares = game.getBoard().getSquares();
		
		final PlayedTile[] boardTiles = boardSquares.stream()
				.map(square -> square.getTile())
				.collect(Collectors.toList())
				.toArray(new PlayedTile[0]);
		
		final RestBoard board = new RestBoard();
		board.setSquares(boardTiles);
		playerGame.setBoard(board);
		
		final List<Player> players = game.getPlayers();
		final Optional<Player> currentPlayer = players.stream() 
			.filter(player -> playerId.equals(player.getId()))
			.findFirst();
		
		final Rack rack = currentPlayer.isPresent() ? currentPlayer.get().getRack() : null;
		int playerIndex = currentPlayer.isPresent() ? players.indexOf(currentPlayer.get()) : -1;
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
			restPlayer.setName(player.getName());
			restPlayer.setScore(player.getScore());
			restPlayer.setSkipTurnCount(player.getSkipTurnCount());
			restPlayer.setForfeited(player.isForfeited());
			return restPlayer;
		}).collect(Collectors.toList());
		playerGame.setPlayers(restPlayers);

		final List<Player> gamePlayers = game.getPlayers();
		final RestGame restGame = new RestGame();
		restGame.setId(game.getId());
		restGame.setVersion(game.getVersion());
		restGame.setPlayerId(playerId);
		restGame.setPlayerIndex(playerIndex);
		restGame.setState(game.getState().name());
		restGame.setActivePlayerIndex(game.getActivePlayerIndex());
		restGame.setWinningPlayerIndex(game.getWinningPlayerIndex());
		int lastPlayerToPlayTilesIndex = gamePlayers.indexOf(game.getLastPlayerToPlayTiles());
		restGame.setLastPlayerToPlayTilesIndex(lastPlayerToPlayTilesIndex);
		playerGame.setGame(restGame);

		final Turn gameTurn = game.getLastTurn();
		if (gameTurn != null) {
			final RestTurn restTurn = new RestTurn();
			final TurnAction turnAction = gameTurn.getAction();
			final int gamePlayerIndex = gamePlayers.indexOf(gameTurn.getPlayer());
			final Set<Square> playedSquares = gameTurn.getSquares();
			final int[] newTileIndexes = playedSquares == null ? new int[] {} : 
				playedSquares.stream()
					.mapToInt(square -> square.getRow()*15+square.getCol())
					.toArray();
			
			restTurn.setAction(turnAction);
			restTurn.setState(gameTurn.getTurnState());
			restTurn.setPlayerIndex(gamePlayerIndex);
			restTurn.setNewTileIndexes(newTileIndexes);
			if (turnAction == TurnAction.CHALLENGE_TURN) {
				int loseTurnPlayerIndex = gamePlayers.indexOf(gameTurn.getLostTurnPlayer());
				restTurn.setLoseTurnPlayerIndex(loseTurnPlayerIndex);
				if (loseTurnPlayerIndex != playerIndex) {
					restTurn.setPoints(0);
				}
			} else {
				if (turnAction == TurnAction.PLAY_TILES) {
					restTurn.setWordsPlayed(gameTurn.getWordsPlayed());
				}
				restTurn.setPoints(gameTurn.getScore());
			}
			playerGame.setLastTurn(restTurn);
		}
		
		return playerGame;
	}
}
