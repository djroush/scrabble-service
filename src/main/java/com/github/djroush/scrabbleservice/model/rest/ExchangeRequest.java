package com.github.djroush.scrabbleservice.model.rest;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExchangeRequest {
	private List<RestTile> tiles;
}
