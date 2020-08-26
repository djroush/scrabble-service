package com.github.djroush.scrabbleservice.model.rest;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
public class RestBoard {
	private List<Square> squares;

	public void setSquares(List<Square> squares) {
		this.squares = squares;
	}

	public List<Square> getSquares() {
		return squares;
	}
}