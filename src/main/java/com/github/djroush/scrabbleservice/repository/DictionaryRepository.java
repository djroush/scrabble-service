package com.github.djroush.scrabbleservice.repository;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.djroush.scrabbleservice.model.rest.AnagramDictionary;

public class DictionaryRepository {
	private List<String> wordList;
	private AnagramDictionary anagrams;

	private ObjectMapper objectMapper = new ObjectMapper();
	
	
	public DictionaryRepository(int length) {
		try {
			String wordListFile = String.format("wordList/%d.txt", length);
			String anagramListFile = String.format("anagramList/%d.json", length);  
			//TODO: add dirty words?
			//TODO add SerDes for wordList
			final URL wordListResource = this.getClass().getClassLoader().getResource(wordListFile);
			final URL anagramListResource = this.getClass().getClassLoader().getResource(anagramListFile);
			wordList = Files.readAllLines(Paths.get(wordListResource.getPath()));
			
			this.anagrams = objectMapper.readValue(anagramListResource, AnagramDictionary.class);
			} catch (IOException e) {
			throw new IllegalArgumentException("Unable to load wordlist", e);
		}
	}
	
    public boolean isValid(String word) {
		int result = Collections.binarySearch(wordList, word);
		return result > -1;
	}
    public List<String> getAnagrams(String letters) {
    	return anagrams.getEntry(letters);
    }
	
	public List<String> getWordList() {
		return wordList;
	}
}
