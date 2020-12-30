package com.github.djroush.scrabbleservice.model.rest;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChallengeRequest {
	public boolean challengeTurn;
	public int version;
}
