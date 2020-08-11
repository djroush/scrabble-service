package com.github.djroush.scrabbleservice.service;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.model.Game;

import reactor.core.publisher.Flux;

@Service
public class GamePublisher implements Publisher<Game> {

	Flux<Game> gameUpdates = Flux.empty();
	
	@Override
	public void subscribe(Subscriber<? super Game> subscriber) {
        try {
        	gameUpdates.toStream().forEach(subscriber::onNext);
	        subscriber.onComplete();
	    } catch (Throwable e) {
	        subscriber.onError(e);
	    }
	}
	
	public void addUpdate(Game... games) {
		gameUpdates.concatWithValues(games);
	}
}
