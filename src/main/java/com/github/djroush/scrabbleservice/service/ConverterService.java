package com.github.djroush.scrabbleservice.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.model.rest.RestBoard;
import com.github.djroush.scrabbleservice.model.rest.RestGame;
import com.github.djroush.scrabbleservice.model.rest.RestPlayer;
import com.github.djroush.scrabbleservice.model.rest.RestPlayerGame;
import com.github.djroush.scrabbleservice.model.rest.RestRack;
import com.github.djroush.scrabbleservice.model.rest.RestTurn;
import com.github.djroush.scrabbleservice.model.rest.Square;
import com.github.djroush.scrabbleservice.model.service.Game;
import com.github.djroush.scrabbleservice.model.service.PlayedTile;
import com.github.djroush.scrabbleservice.model.service.Player;
import com.github.djroush.scrabbleservice.model.service.Rack;
import com.github.djroush.scrabbleservice.model.service.Turn;
import com.github.djroush.scrabbleservice.model.service.TurnAction;

@Service
public class ConverterService {
	public RestGame convertSimple(Game game, String playerId) {
		final RestGame restGame = new RestGame();
		restGame.setId(game.getId());
		restGame.setPlayerId(playerId);
		restGame.setVersion(game.getVersion());
		restGame.setState(game.getState().name());
		return restGame;
	}
	
	public RestPlayerGame convertModels(Game game, String playerId) {
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
			})
			.sorted((String a, String b) -> a.compareTo(b))
			.collect(Collectors.toList());
		
			
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
