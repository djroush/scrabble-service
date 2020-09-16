package com.github.djroush.scrabbleservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
import com.github.djroush.scrabbleservice.model.service.TurnAction;
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
	private RackService rackService;
	@Autowired
	private TileBagService tileBagService;
	@Autowired 
	private TurnService turnService;
	
	@Autowired
	private GameRepository gameRepository;
	
	public Game newGame(String playerName) {
		final Game game = new Game();
		game.setTileBag(new TileBag());
		game.setBoard(new Board());
		final String gameId = UUID.randomUUID().toString().substring(0,8).toUpperCase();
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
		
		final String playerId = UUID.randomUUID().toString().substring(0,8).toUpperCase();
		Player player = new Player();
		player.setId(playerId);
		player.setName(playerName);
		player.setRack(new Rack());
		if (players.isEmpty()) {
			game.setActivePlayerIndex(-1);
			game.setActivePlayer(null);
		}
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
		final List<Player> players = game.getPlayers();
		if (players.size() < MIN_PLAYERS) {
			throw new IncorrectPlayerCountException();
		}
		final TileBag tileBag = game.getTileBag();

		//Randomize the first player
		Collections.shuffle(players);
		
		players.forEach(player -> tileBagService.fillRack(tileBag, player.getRack()));
		final Player firstPlayer = game.getPlayers().get(0);
		game.setActivePlayerIndex(0);
		game.setActivePlayer(firstPlayer);
		game.setState(GameState.ACTIVE);
		
		update(game);
		return game;
	}
	
	// END PENDING GAME ACTIONS

	//START ACTIVE GAME ACTIONS
	public Game playTiles(String gameId, String playerId, SortedSet<Square> squares) {
		final Game game = find(gameId);
		verifyActive(game);
		final Player player = findPlayer(game, playerId);
		isPlayerTurn(game, player);

		final Rack rack = player.getRack();
		List<Tile> previousTiles = rack.getPreviousTiles();
		previousTiles.clear();
		previousTiles.addAll(rack.getTiles());
		
		
		final Board board = game.getBoard();
		final List<Set<Square>> adjoinedSquaresList = boardService.playSquares(board,  squares);
		final Turn turn = turnService.playTurn(player, squares, adjoinedSquaresList);
		
		final TileBag tileBag = game.getTileBag();
		rackService.replaceTiles(rack, squares);
		
		tileBagService.fillRack(tileBag, rack);
		
		if (rack.getTiles().size() == 0) {
			game.setState(GameState.ENDGAME);
		} 
		player.setRack(rack);
		player.setScore(player.getScore() + turn.getScore());

		game.setLastPlayerToPlayTiles(player);
		game.setLastTurn(turn);
		game.setCanChallenge(true);
		
		updateNextPlayer(game);
		update(game);
		return game;
	}

	public Game passTurn(String gameId, String playerId) {
		final Game game = find(gameId);
		verifyActive(game);
		final Player player = findPlayer(game, playerId);
		isPlayerTurn(game, player);
		Turn turn = turnService.passTurn(player);
		
		game.setLastTurn(turn);
		updateNextPlayer(game);
		update(game);
		
		return game;
	}

	public Game exchange(String gameId, String playerId, List<Tile> tiles) {
		final Game game = find(gameId);
		verifyActive(game);
		final Player player = findPlayer(game, playerId);
		isPlayerTurn(game, player);
		
		final Rack rack = player.getRack();
		final TileBag tileBag = game.getTileBag();
		tiles.forEach(tile -> {
			rack.getTiles().remove(tile);
			tileBag.getBag().add(tile);
		});
		tileBagService.fillRack(tileBag, rack);
		final Turn turn = turnService.exchangeTiles(player);
		
		game.setLastTurn(turn);
		updateNextPlayer(game);
		update(game);
		
		return game;
	}
	
	/* return true if one of the words is incorrect, else false if all words are valid */
	public Game challenge(String gameId, String challengingPlayerId) {
		final Game game = find(gameId);
		verifyActiveOrEndgame(game);

		final Turn lastTurn = game.getLastTurn();
		final Player challengeTurnPlayer = findPlayer(game, challengingPlayerId);
		final Player playTilesPlayer = lastTurn.getPlayer();
				
		final List<String> words = lastTurn.getWordsPlayed();
		boolean wordsValid = true;
		for (String word: words) {
			wordsValid &= dictionaryService.searchFor(word);
			if (!wordsValid) {
				break;
			}
		}

		Player losingPlayer = null;
		boolean challengeWon = !wordsValid;
		if (challengeWon) {
			losingPlayer = playTilesPlayer;
			game.setState(GameState.ACTIVE);

			
		//Revert previous turn
			//Remove tiles from board
			List<Square> squares = game.getBoard().getSquares();
			List<Tile> tiles =  new LinkedList<Tile>();
			SortedSet<Square> playedSquares = lastTurn.getSquares();
			playedSquares.forEach(square -> {
				int index = square.getRow()*15 + square.getCol();
				Square boardSquare = squares.get(index);
				final PlayedTile playedTile = square.getTile();
				final Tile tile = playedTile.isBlank() ? Tile.BLANK : Tile.from(playedTile.getLetter()); 
				tiles.add(tile);
				boardSquare.setTile(null);
				squares.set(index, boardSquare);
			});

			//Determine the tiles drawn from the bag and return them
			final Rack rack = losingPlayer.getRack();
			List<Tile> drawnTiles = new ArrayList<>(tiles);
			drawnTiles.addAll(rack.getTiles());
			
			rack.getPreviousTiles()
				.forEach(tile -> drawnTiles.remove(tile));
			tileBagService.returnTiles(game.getTileBag(), drawnTiles);

			//Reset the players rack to the previous state 
			rack.getTiles().clear();
			rack.getTiles().addAll(rack.getPreviousTiles());
			rack.getPreviousTiles().clear();
			
			int score = playTilesPlayer.getScore() - lastTurn.getScore();
			playTilesPlayer.setScore(score);
		} else {
			losingPlayer = challengeTurnPlayer;
		}
		int skippedTurnCount = losingPlayer.getSkipTurnCount() + 1;
		if (losingPlayer.equals(game.getActivePlayer())) {
			updateNextPlayer(game);
		} else {
			losingPlayer.setSkipTurnCount(skippedTurnCount);
		}
		 
		final Turn thisTurn = turnService.challengeTurn(lastTurn, challengeTurnPlayer, losingPlayer);
		
		game.setCanChallenge(false);
		game.setLastTurn(thisTurn);
		update(game);
		
		return game;
	}
	public Game forfeit(String gameId, String playerId) {
		final Game game = find(gameId);
		verifyActive(game);

		final Player player = findPlayer(game, playerId);
		player.setForfeited(true);
		if (player.equals(game.getActivePlayer())) {
			updateNextPlayer(game);
		}

		final TileBag tileBag = game.getTileBag();
		final List<Tile> tiles = player.getRack().getTiles();
		tileBagService.returnTiles(tileBag, tiles);
		
		final Turn turn = turnService.forfeitGame(player);

		long activePlayers = game.getPlayers().stream()
			.filter(gamePlayer -> !gamePlayer.isForfeited()).count();
		if (activePlayers <= 1L) {
			game.setState(GameState.ABORTED);
		}

		game.setLastTurn(turn);
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
		Player activePlayer = game.getActivePlayer();
		boolean isPlayerTurn = player == activePlayer;
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

	private void updateNextPlayer(Game game) {		
		List<Player> players = game.getPlayers();
		int activePlayerIndex = game.getActivePlayerIndex();
		
		Player player = null;
		do { //Reset to beginning of list
			activePlayerIndex += 1;
			activePlayerIndex %= players.size();
			player = players.get(activePlayerIndex);
			int skipTurnCount = player.getSkipTurnCount();
			if (skipTurnCount > 0) {
				player.setSkipTurnCount(skipTurnCount-1);
				continue;
			}
			if (!player.isForfeited()) {
				break;
			}
	    } while (true);

		game.setActivePlayerIndex(activePlayerIndex);
		game.setActivePlayer(player);
	}

	@Async
	public void endGame(String gameId, Player finalTurnPlayer) {
		try {
			Thread.sleep(12_000L);
		} catch (InterruptedException ie) {
			//TODO: log an error
		}
		final Game game = find(gameId);
		if (game.getState() == GameState.ENDGAME && game.getTileBag().getBag().isEmpty()) {
			game.setState(GameState.FINISHED);
			int finalTurnPlayerScore = finalTurnPlayer.getScore();
			for (Player player: game.getPlayers()) {
				if (!player.equals(finalTurnPlayer)) {
					int playerScore = player.getScore();
					for (Tile tile: player.getRack().getTiles()) {
						playerScore -= tile.getValue();
						finalTurnPlayerScore += tile.getValue();
					}
					player.setScore(playerScore);
				}
			}
			finalTurnPlayer.setScore(finalTurnPlayerScore);
			update(game);
		}
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
		game.setVersion(game.getVersion()+1);
		Turn turn = game.getLastTurn();
		//This will be null on the first turn
		if (turn != null) {
			if (turn.getScore() == 0 && (turn.getAction() != TurnAction.PLAY_TILES)) {
				//TODO: This needs to be adjusted for passes as well, probably need a second consecutiveScorelessTurns counter 
//				int consecutiveScorelessTurns = game.getConsecutiveScorelessTurns();
//				game.setConsecutiveScorelessTurns(consecutiveScorelessTurns+1);
//				if (consecutiveScorelessTurns == 7) {
//					game.setState(GameState.FINISHED);
//				}
			}
		}
		gameRepository.update(game);
		return game;
	}
}