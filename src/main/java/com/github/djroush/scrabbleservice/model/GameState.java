package com.github.djroush.scrabbleservice.model;

public enum GameState {
  //UNKNOWN = Default to this state when the game state is not known
  //PENDING - Waiting for players to join before starting game
  //ABANDONED - All players left before the game started
  //ACTIVE - Players joined, the game started and is now in progress
  //ENDGAME - A player is out of tiles and no tiles remain, only challenges allowed
  //ABORTED - An ACTIVE game lost players and ended before finishing
  //FINISHED - An ACTIVE game finished and a winner exists
  UNKNOWN, PENDING, ABANDONED, ACTIVE, ENDGAME, ABORTED, FINISHED;
}
