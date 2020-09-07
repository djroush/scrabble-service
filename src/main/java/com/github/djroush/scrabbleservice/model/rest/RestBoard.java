package com.github.djroush.scrabbleservice.model.rest;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RestBoard {
	private List<Square> squares;
}