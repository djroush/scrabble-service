package com.github.djroush.scrabbleservice.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.model.Game;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class BlockingGameService {
	@Autowired
	private GamePublisher gamePublisher;
	
	//START ACTIVE GAME ACTIONS
	public Game awaitUpdate(String gameId, int currentVersion) {
		
		Game game = null; 
		int count = 0;
		
		do {
			game = Flux 
			.from(gamePublisher)
			.filter(game1 -> game1.getId() == gameId && game1.getVersion() < currentVersion)
			.subscribeOn(Schedulers.elastic())
			.blockLast(Duration.ofMinutes(1L));

			try {
				Thread.sleep(1000L);
			} catch (Exception e) {};
			count++;
			
		} while (game == null || count == 60);
		
//		Mono<Game> game = Mono.fromCallable(() -> {
//			return Mono
//			.from(gamePublisher)
//			.filter(game1 -> game1.getId() == gameId)
//			.subscribeOn(Schedulers.elastic())
//			.block(Duration.ofMinutes(1L));
//		});
		
//		Mono<Game> game = Mono.fromCallable(() -> {
//			return Mono
//			.from(gamePublisher)
//			.filter(game1 -> game1.getId() == gameId)
//			.subscribeOn(Schedulers.elastic())
//			.block(Duration.ofMinutes(1L));
//		});		
//			
		return game;
	}
}
