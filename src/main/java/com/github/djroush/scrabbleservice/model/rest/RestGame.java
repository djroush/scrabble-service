package com.github.djroush.scrabbleservice.model.rest;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RestGame {
	public String id;
	public String playerId;
	public int version;
	public String state;
	public int playerIndex;
	public int activePlayerIndex;
	public int lastPlayerToPlayTilesIndex;
	public Integer winningPlayerIndex;
}
