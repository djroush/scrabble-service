package com.github.djroush.scrabbleservice.service;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.exception.GameAlreadyStartedException;
import com.github.djroush.scrabbleservice.exception.GameFullException;
import com.github.djroush.scrabbleservice.exception.GameNotActiveException;
import com.github.djroush.scrabbleservice.exception.IncorrectPlayerCountException;
import com.github.djroush.scrabbleservice.exception.TurnOutofOrderException;
import com.github.djroush.scrabbleservice.exception.UnknownGameException;
import com.github.djroush.scrabbleservice.exception.UnknownPlayerException;
import com.github.djroush.scrabbleservice.model.rest.Square;
import com.github.djroush.scrabbleservice.model.service.Board;
import com.github.djroush.scrabbleservice.model.service.Game;
import com.github.djroush.scrabbleservice.model.service.GameState;
import com.github.djroush.scrabbleservice.model.service.PlayedTile;
import com.github.djroush.scrabbleservice.model.service.Player;
import com.github.djroush.scrabbleservice.model.service.Rack;
import com.github.djroush.scrabbleservice.model.service.Tile;
import com.github.djroush.scrabbleservice.model.service.TileBag;
import com.github.djroush.scrabbleservice.model.service.Turn;
import com.github.djroush.scrabbleservice.repository.GameRepository;

@Service
public class GameService {
	private static final int MIN_PLAYERS = 2;
	private static final int MAX_PLAYERS = 4;

	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private BoardService boardService;
	@Autowired
	private TileBagService tileService;
	@Autowired 
	private TurnService turnService;
	
	@Autowired
	private GameRepository gameRepository;
	
	public Game newGame(String playerName) {
		final Game game = new Game();
		game.setVersion(0);
		game.setTileBag(new TileBag());
		game.setBoard(new Board());
//		final String gameId = UUID.randomUUID().toString();
		final String gameId = "6AME1";
		
		game.setId(gameId);
		game.setState(GameState.PENDING);
		addPlayer(game, playerName);
		
		insert(game);
		return game;
	}
	
	//This method exists so that a method using gameRepository is not coupled to a public API method 
	public Game refreshGame(String gameId, String playerId) {
		//If an error exists or a browser is closed this can reopen
		final Game game = find(gameId);
		filterPlayer(game, playerId);
		return game;
	}

	// START PENDING GAME ACTIONS
	
	public Game addPlayer(String gameId, String playerName) {
		Game game = find(gameId);
		game = addPlayer(game, playerName);
		return game;
	}
	
	private Game addPlayer(Game game, String playerName) {
		verifyPending(game);
		
		List<Player> players = game.getPlayers();
		int playerCount = players.size();
		if (playerCount >= GameService.MAX_PLAYERS) {
			throw new GameFullException();
		}
		
		//String playerId = UUID.randomUUID().toString()
		String playerId = String.valueOf(players.size()+1);
		game.setPlayerId(playerId);
		Player player = new Player();
		player.setId(playerId);
		player.setName(playerName);
		player.setRack(new Rack());
		players.add(player);
		game.setPlayers(players);
		
		update(game);
		return game;
	}
	
	public Game removePlayer(String gameId, String playerId) {
		final Game game = find(gameId);
		verifyPending(game);
		
		Player player = findPlayer(game, playerId);
		game.getPlayers().remove(player);

		update(game);
		return game;
	}
	
	public Game start(String gameId, String playerId) {
		final Game game = find(gameId);
		verifyPending(game);
		final int numberOfPlayers = game.getPlayers().size();
		if (numberOfPlayers < MIN_PLAYERS) {
			throw new IncorrectPlayerCountException();
		}
		//TODO: grab tiles for all the players
		TileBag tileBag = game.getTileBag();
		
		List<Player> players = game.getPlayers();
		game.setTurnIterator(players.listIterator());
		players
			.forEach(player -> tileService.fillRack(tileBag, player.getRack()));
		game.setState(GameState.ACTIVE);
		
		game.setPlayerCurrentlyUp(upNext(game));
		update(game);
		return game;
	}
	
	// END PENDING GAME ACTIONS

	//START ACTIVE GAME ACTIONS
	
	public Game playTurn(String gameId, String playerId, SortedSet<Square> squares) {
		final Game game = find(gameId);
		verifyActive(game);
		final Player player = findPlayer(game, playerId);
		isPlayerTurn(game, player);
		
		int skipTurnCount = player.getSkipTurnCount();
		Turn turn = null;
		if (skipTurnCount > 0) {
			player.setSkipTurnCount(skipTurnCount - 1);
			turn = turnService.skipTurn(player, game.getLastTurn());
		} else {
			final Board board = game.getBoard();
			boardService.checkMoveValid(board, squares);
			boardService.playSquares(board,  squares);
			List<Set<Square>> adjoinedSquaresList = boardService.getAdjoinedSquares(board, squares);
			turn = turnService.playTurn(player, squares, adjoinedSquaresList);
		}
		
		game.setLastTurn(turn);
		upNext(game);
		update(game);

		//TODO: wait for a challenge before drawing new letters, remove ENDGAME state?
		//TODO: figure out where this will go!
		final Rack rack = player.getRack();
		List<Tile> tiles = rack.getTiles();
		
		squares.forEach(square -> {
			final PlayedTile playedTile = square.getTile();
			final Tile tile = Tile.from(playedTile.getLetter());
			tiles.remove(tile);
		});
		
		final TileBag tileBag = game.getTileBag();
		
		tileService.fillRack(tileBag, rack);
		if (rack.getTiles().size() == 0) {
			game.setState(GameState.ENDGAME);
			//do something else here?
		} 
		player.setRack(rack);
		
		return game;
	}

	public Game passTurn(String gameId, String playerId) {
		final Game game = find(gameId);
		verifyActive(game);
		final Player player = findPlayer(game, playerId);
		isPlayerTurn(game, player);
		int skipTurnCount = player.getSkipTurnCount();
		Turn turn = null;
		if (skipTurnCount > 0) {
			player.setSkipTurnCount(skipTurnCount - 1);
			turn = turnService.skipTurn(player, game.getLastTurn());
		} else {
			turn = new Turn();
			turn.setSquares(Collections.emptySortedSet());
			turn.setPlayer(player);
			turn.setWordsPlayed(Collections.emptyList());
			turn.setScore(0);
		}
		
		game.setLastTurn(turn);
		upNext(game);
		update(game);
		filterPlayer(game, playerId);
		return game;
	}

	public Game exchange(String gameId, String playerId, List<Tile> tiles) {
		final Game game = find(gameId);
		verifyActive(game);
		final Player player = findPlayer(game, playerId);
		isPlayerTurn(game, player);
		int skipTurnCount = player.getSkipTurnCount();
		Turn turn = null;
		if (skipTurnCount > 0) {
			player.setSkipTurnCount(skipTurnCount - 1);
			turn = turnService.skipTurn(player, game.getLastTurn());
		} else {
			final Rack rack = player.getRack();
			final TileBag tileBag = game.getTileBag();
			tileService.fillRack(tileBag, rack);
			turn = new Turn();
			turn.setSquares(Collections.emptySortedSet());
			turn.setPlayer(player);
			turn.setWordsPlayed(Collections.emptyList());
			turn.setScore(0);
		}
		game.setLastTurn(turn);
		upNext(game);
		update(game);
		
		//TODO: need to create a turn somewhere
		filterPlayer(game, playerId);
		return game;
	}
	
	/* return true if one of the words is incorrect, else false if all words are valid */
	public Game challenge(String gameId, String challengingPlayerId) {
		final Game game = find(gameId);
		verifyActiveOrEndgame(game);
		
		final Turn turn = game.getLastTurn();
		final List<String> words = turn.getWordsPlayed();
		boolean wordsValid = true;
		
		for (String word: words) {
			wordsValid |= dictionaryService.searchFor(word);
			if (!wordsValid) {
				break;
			}
		}
		
		if (wordsValid) {
			turnService.reverseLastTurn(game);
		} else {
			Player player = findPlayer(game, challengingPlayerId);
			int skipTurnCount = player.getSkipTurnCount();
			player.setSkipTurnCount(skipTurnCount+1);
//			Turn thisTurn = turnService.createTurn(player);
		}
		
		update(game);
		filterPlayer(game, challengingPlayerId);
		return game;
	}
	public Game forfeit(String gameId, String playerId) {
		final Game game = find(gameId);
		verifyActive(game);

		final Player player = findPlayer(game, playerId);
		player.setIsForfeited(true);
		
		update(game);
		filterPlayer(game, playerId);
		return game;
	}
	
	// END ACTIVE GAME ACTIONS
	
	private void verifyActive(Game game) {
		final GameState state = game.getState();
		if (state != GameState.ACTIVE) {
			throw new GameNotActiveException("Cannot take a turn in a game that is not currently active");
		}
	}
	private void verifyActiveOrEndgame(Game game) {
		final GameState state = game.getState();
		if (state != GameState.ACTIVE && state != GameState.ENDGAME) {
			throw new GameNotActiveException("Cannot make a challenge in a game that is not started or already completed");
		}
	}
	private void verifyPending(Game game) {
		final GameState state = game.getState();
		if (state != GameState.PENDING) {
			throw new GameAlreadyStartedException();
		}
	}
	
	private void isPlayerTurn(Game game, Player player) {
		Player currentlyUp = game.getPlayerCurrentlyUp();
		boolean isPlayerTurn = player == currentlyUp;
		if (!isPlayerTurn) {
			throw new TurnOutofOrderException();
		}
	}
	
	private Player findPlayer(Game game, String playerId) {
		final List<Player> players = game.getPlayers();
		final Optional<Player> foundPlayer = players.stream()
			.filter(player -> player.getId().equals(playerId))
			.findFirst();
		if (foundPlayer.isEmpty()) {
			throw new UnknownPlayerException();
		}
		return foundPlayer.get();
	}

	private Player upNext(Game game) {		
		ListIterator<Player> turnIterator = game.getTurnIterator();
		List<Player> players = game.getPlayers();
		Player playerUpNow = null;
		do { //Reset to beginning of list
			boolean hasNext = turnIterator.hasNext();
			if (!hasNext) {  
				turnIterator = players.listIterator();
			}
			playerUpNow = turnIterator.next();
	    } while (playerUpNow.getIsForfeited());

		game.setPlayerCurrentlyUp(playerUpNow);
		return playerUpNow;
	}

	
	private Game find(String gameId) {
		Game game = gameRepository.find(gameId);
		if (game == null) {
			throw new UnknownGameException();
		}
		return game;
	}

	private Game insert(Game game) {
		gameRepository.insert(game);
		return game;
	}
	
	private Game update(Game game) {
		GameState state = game.getState();
		if (state == GameState.ACTIVE || state == GameState.PENDING) {
			game.setVersion(game.getVersion()+1);
		}
		if (state == GameState.ACTIVE) {
			int turnNumber = game.getTurnNumber();
			game.setTurnNumber(turnNumber+1);
		}
		Turn turn = game.getLastTurn();
		//This will be null on the first turn
		if (turn != null) {
			if (turn.getScore() == 0) {
				int consecutiveScorelessTurns = game.getConsecutiveScorelessTurns();
				game.setConsecutiveScorelessTurns(consecutiveScorelessTurns+1);
				
			}
		}

		gameRepository.update(game);
		return game;
	}
	
	private void filterPlayer(Game game, String playerId) {
//		game.getPlayers().forEach(
//		  player -> {
//			//TODO: this is bad, need to convert models here
//			if (!playerId.equals(player.getId())) {
//				player.setRack(null);
//			}
//		});
	}
	
	}

