package com.github.djroush.scrabbleservice.model.dictionary;

import java.util.List;

public class Anagram {
	private String letters;
	private List<String> anagrams;
	
	public String getLetters() {
		return letters;
	}
	public void setLetters(String letters) {
		this.letters = letters;
	}
	public List<String> getAnagrams() {
		return anagrams;
	}
	public void setAnagrams(List<String> anagrams) {
		this.anagrams = anagrams;
	}
	
}
