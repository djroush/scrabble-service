package com.github.djroush.scrabbleservice.service;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.SortedSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.exception.GameAlreadyStartedException;
import com.github.djroush.scrabbleservice.exception.GameFullException;
import com.github.djroush.scrabbleservice.exception.GameNotActiveException;
import com.github.djroush.scrabbleservice.exception.IncorrectPlayerCountException;
import com.github.djroush.scrabbleservice.exception.PlayerCannotStartGameException;
import com.github.djroush.scrabbleservice.exception.TurnOutofOrderException;
import com.github.djroush.scrabbleservice.exception.UnknownGameException;
import com.github.djroush.scrabbleservice.exception.UnknownPlayerException;
import com.github.djroush.scrabbleservice.model.Board;
import com.github.djroush.scrabbleservice.model.Game;
import com.github.djroush.scrabbleservice.model.GameState;
import com.github.djroush.scrabbleservice.model.Player;
import com.github.djroush.scrabbleservice.model.Rack;
import com.github.djroush.scrabbleservice.model.Square;
import com.github.djroush.scrabbleservice.model.Tile;
import com.github.djroush.scrabbleservice.model.TileBag;
import com.github.djroush.scrabbleservice.model.Turn;
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
	private TileService tileService;
	@Autowired
	private GameRepository gameRepository;
	
	public Game newGame(String playerName) {
		final Game game = new Game();
		game.setTileBag(new TileBag());
//		final String gameId = UUID.randomUUID().toString();
		final String gameId = "6AME1";
		
		game.setId(gameId);
		game.setState(GameState.PENDING);
		addPlayer(game, playerName);
		
		insert(game);
		return game;
	}
	
	//This method exists so that a method using gameRepository is not coupled to a public API method 
	public Game refreshGame(String gameId) {
		//If an error exists or a browser is closed this can reopen
		final Game game = find(gameId);
		return game;
	}

	// START PENDING GAME ACTIONS
	
	public Game addPlayer(String gameId, String playerName) {
		Game game = find(gameId);
		game = addPlayer(game, playerName);

		update(game);
		return game;
	}
	
	private Game addPlayer(Game game, String playerName) {
		verifyPending(game);
		
		List<Player> players = game.getPlayers();
		int playerCount = players.size();
		if (playerCount >= GameService.MAX_PLAYERS) {
			throw new GameFullException();
		}
		
		Player player = new Player();
		player.setId(String.valueOf(players.size()+1));
//		player.setId(UUID.randomUUID().toString());
		player.setName(playerName);
		player.setRack(new Rack());
		players.add(player);
		game.setTurnIterator(players.listIterator());
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
	
	public Game start(String gameId, String startingPlayerId) {
		final Game game = find(gameId);
		verifyPending(game);
		final int numberOfPlayers = game.getPlayers().size();
		if (numberOfPlayers < MIN_PLAYERS) {
			throw new IncorrectPlayerCountException();
		}
		final Player startingPlayer = findPlayer(game, startingPlayerId);
		if (startingPlayer == null) {
			throw new PlayerCannotStartGameException();
		}
		//TODO: grab tiles for all the players
		TileBag tileBag = game.getTileBag();
		game.getPlayers()
			.forEach(player -> tileService.fillRack(tileBag, player.getRack()));
		game.setState(GameState.ACTIVE);
		update(game);
		return game;
	}
	
	// END PENDING GAME ACTIONS

	//START ACTIVE GAME ACTIONS
	public Game awaitUpdate(String gameId, String playerId) {
		//Put pub sub stuff here?
		
		//TODO players who are awaiting their turn can call this! 
		return null;
	}
	
	public Game playTurn(String gameId, String playerId, SortedSet<Square> squares) {
		final Game game = find(gameId);
		verifyActive(game);
		final Player player = findPlayer(game, playerId);
		isPlayerTurn(game, player);
		int skipTurnCount = player.getSkipTurnCount();
		Turn turn = null;
		if (skipTurnCount > 0 || player.getIsForfeited()) {
			player.setSkipTurnCount(skipTurnCount - 1);
			turn = boardService.skipTurn(player, game.getLastTurn());
		} else {
			final Board board = game.getBoard();
			boardService.checkMoveValid(board, squares);
			turn = boardService.executeTurn(player, board, squares);
			//TODO calculate score, wait for challenge clear before pulling new letters?
			//Move this to a different area and have a two part turn?
			final TileBag tileBag = game.getTileBag();
			final Rack rack = player.getRack();
			tileService.fillRack(tileBag, rack); 
		}
		
		game.setLastTurn(turn);
		
		Player upNext = upNext(game);
		game.setPlayerCurrentlyUp(upNext);

		if (player.getRack().getTiles().size() == 0) {
//			triggerEndGame();
		} 
		
		update(game);
		return game;
	}


	public Game passTurn(String gameId, String playerId) {
		final SortedSet<Tile> NO_TILES = Collections.emptySortedSet();
		final Game game = exchange(gameId, playerId, NO_TILES, true);
		
		//TODO: create a turn here
		
		update(game);
		return game;
	}

	public Game exchange(String gameId, String playerId, SortedSet<Tile> tiles, boolean isPass) {
		Game game = find(gameId);
		verifyActive(game);
		
		//TODO: implement exchange logic,  add BagService

		
		if (!isPass) {
			update(game);
		}
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
			boardService.reverseLastTurn(game);
		} else {
			Player player = findPlayer(game, challengingPlayerId);
			int skipTurnCount = player.getSkipTurnCount();
			player.setSkipTurnCount(skipTurnCount+1);
		}

		update(game);
		return game;
	}
	public Game forfeit(String gameId, String playerId) {
		final Game game = find(gameId);
		verifyActive(game);

		final Player player = findPlayer(game, playerId);
		player.setIsForfeited(true);
		
		update(game);
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
		boolean hasNext = turnIterator.hasNext();
		Player playerUpNow = null;
		do { //Reset to beginning of list
			if (!hasNext) {  
				turnIterator = players.listIterator();
			}
			playerUpNow = turnIterator.next();
	    } while (!playerUpNow.getIsForfeited());

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
		int turnNumber = game.getTurnNumber();
		if (game.getState() == GameState.ACTIVE) {
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
}

