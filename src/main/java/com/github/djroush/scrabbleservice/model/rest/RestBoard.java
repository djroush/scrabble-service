package com.github.djroush.scrabbleservice.model.rest;

import com.github.djroush.scrabbleservice.model.service.PlayedTile;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RestBoard {
	private PlayedTile[] squares;
}