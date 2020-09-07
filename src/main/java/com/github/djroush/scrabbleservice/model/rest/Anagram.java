package com.github.djroush.scrabbleservice.model.rest;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Anagram {
	private String letters;
	private List<String> anagrams;
}
