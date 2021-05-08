package com.github.djroush.scrabbleservice.controller;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.github.djroush.scrabbleservice.exception.InvalidInputException;
import com.github.djroush.scrabbleservice.service.SseService;

@Controller
@RequestMapping("/v1/scrabble/sse/")
public class SSEV1Controller {

  @Autowired
  private SseService sseService;
  
  @GetMapping("{gameId}/{playerId}")
  public SseEmitter sse(@PathVariable String gameId, @PathVariable String playerId) {
	checkInputParameters(gameId, playerId);
    Pair<String, SseEmitter> playerEmitterPair = sseService.connect(gameId, playerId);
    return playerEmitterPair.getRight();
  }
  
  private void checkInputParameters(String gameId, String playerId) {
    if (gameId == null || gameId.isBlank() || "".equals(gameId)) {
      throw new InvalidInputException();
    }
  }
  
}
