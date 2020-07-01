package com.github.djroush.scrabbleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.djroush.scrabbleservice.model.rest.Anagram;
import com.github.djroush.scrabbleservice.model.rest.Entry;
import com.github.djroush.scrabbleservice.service.DictionaryService;

@RestController
@RequestMapping("/dictionary")
public class DictionaryController {
	//These methods are only here for troubleshooting and should not be available to scrabble players
	
	@Autowired
	private DictionaryService dictionaryService;
	
	@GetMapping(path = "/word/{letters}")
	public ResponseEntity<?> isValid(@PathVariable String letters) {
		letters = letters.toUpperCase();
		boolean result = dictionaryService.searchFor(letters);
		if (result) {
			Entry word = new Entry();
			word.setWord(letters);
			return ResponseEntity.ok(word);  
		}
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping(path = "/anagram/{letters}")
	public ResponseEntity<?> getAnagrams(@PathVariable String letters) {
		Anagram result = dictionaryService.getAnagrams(letters);
		if ( result == null ) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(result);  
	}		
}
