package com.github.djroush.scrabbleservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.github.djroush.scrabbleservice.model.rest.RestGame;
import com.github.djroush.scrabbleservice.model.rest.RestPlayerGame;
import com.github.djroush.scrabbleservice.model.service.Game;
import com.github.djroush.scrabbleservice.model.service.GameState;
import com.github.djroush.scrabbleservice.repository.GameRepository;

@Service
public class SseService {

	private Map<String, List<Pair<String, SseEmitter>>> gameEmitterPair = new ConcurrentHashMap<>();

	@Autowired
	private ConverterService converterService;
	
	@Autowired
	private GameRepository gameRepository;

	public Pair<String, SseEmitter> connect(String gameId, String playerId) {
		final List<Pair<String, SseEmitter>> emitterPairList = getEmitterPairList(gameId);
		final Pair<String, SseEmitter> playerEmitterPair = getPlayerEmitterPair(emitterPairList, playerId);
		final SseEmitter sseEmitter = playerEmitterPair.getRight(); 
		
		sseEmitter.onCompletion(() -> {
	        emitterPairList.remove(playerEmitterPair);
	    });
		return playerEmitterPair;
	}
	
	private List<Pair<String, SseEmitter>> getEmitterPairList(String gameId) {
		List<Pair<String, SseEmitter>> emitterPairList = gameEmitterPair.get(gameId);
		if (emitterPairList == null) {
			emitterPairList = new ArrayList<>();
			gameEmitterPair.put(gameId, emitterPairList);
		}
		return emitterPairList;
	}
	
	private Pair<String, SseEmitter> getPlayerEmitterPair(List<Pair<String, SseEmitter>> emitterPairList,
			String playerId) {
		Optional<Pair<String, SseEmitter>> playerEmitterPairOpt = emitterPairList.stream()
				.filter(pair -> playerId.equals(pair.getLeft()))
				.findFirst();
		
		Pair<String, SseEmitter> playerEmitterPair;
		if (playerEmitterPairOpt.isEmpty()) {
			SseEmitter sseEmitter = new SseEmitter(30_000L*1200);
			playerEmitterPair = Pair.of(playerId, sseEmitter);
			emitterPairList.add(playerEmitterPair);
		} else {
			playerEmitterPair = playerEmitterPairOpt.get();
		}

		return playerEmitterPair;
	}

	//FIXME: need to send out updates about end game 
	public void publishUpdate(Game game) {
		final List<Pair<String, SseEmitter>> emitterPairList = gameEmitterPair.get(game.getId());
		if (emitterPairList != null) {
			emitterPairList.forEach(playerEmitterPair -> {
				final String playerId = playerEmitterPair.getLeft();
				final SseEmitter sseEmitter = playerEmitterPair.getRight();
				final RestPlayerGame playerGame = converterService.convertModels(game, playerId);
				try {
					System.out.println("Publishing update to emitter: " + game.getVersion());
					final SseEventBuilder event = SseEmitter.event()
						.name("game-update")
						.id(String.valueOf(game.getVersion()))
						.data(playerGame);
					sseEmitter.send(event);
				} catch (Exception e) {
					System.out.println("Error attempting to publish update for game");
				}
			});
		}
	}
	
	@Async
	public void handleKeepAlive(String gameId) {
		int i = 0;
		while (i++ < 1200) {
			try {
				Thread.sleep(30_000L);
			} catch (InterruptedException ie) {
				//TODO: log an error
			}
			
			//TODO: if keep-alive and hearbeat send at same time, a ConcurrentModificationException happens
			
			final Game game = gameRepository.find(gameId);
			if (game.getState() == GameState.ABORTED || game.getState() == GameState.FINISHED) {
				break;
			}
			List<Pair<String, SseEmitter>> emitterPairList = gameEmitterPair.get(gameId);
			emitterPairList.forEach(playerEmitterPair -> {
				final String playerId = playerEmitterPair.getLeft();
				final SseEmitter sseEmitter = playerEmitterPair.getRight();
				try {
					System.out.println("Publishing heartbeat to emitter: " + game.getId());

					final SseEventBuilder event = SseEmitter.event()
						.name("game-heartbeat")
						.data(game.getId() + " is still alive");
					sseEmitter.send(event);
				} catch (Exception e) {
					System.out.println("Error attempting to publish heartbeat for " + game.getId() + "/" + playerId);
				}
			});
		}
	}

    @Async
	public void completeGame(Game game) {
		final List<Pair<String, SseEmitter>> emitterPairList = gameEmitterPair.get(game.getId());
		if (emitterPairList != null) {
			emitterPairList.forEach(playerEmitterPair -> {
				final String playerId = playerEmitterPair.getLeft();
				final SseEmitter sseEmitter = playerEmitterPair.getRight();
				final RestGame restGame = new RestGame();
			    restGame.setId(game.getId());
				restGame.setPlayerId(playerId);
				try {
					final SseEventBuilder event = SseEmitter.event()
						.name("game-complete")
						.id(String.valueOf(game.getVersion()))
						.data(restGame);
					sseEmitter.send(event);
				} catch (Exception e) {
					System.out.println("Error attempting to publish game complete");
				}
			});
			emitterPairList.clear();
			gameEmitterPair.remove(game.getId());
		}
	}
}

