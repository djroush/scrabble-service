package com.github.djroush.scrabbleservice.model.service;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayedTile {
	private char letter;
	private boolean isBlank;
}
