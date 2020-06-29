package com.github.djroush.scrabbleservice.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.github.djroush.scrabbleservice.model.dictionary.Anagram;
import com.github.djroush.scrabbleservice.repository.DictionaryRepository;

@Service
public class DictionaryService {
	@Autowired @Qualifier("length2")
	private DictionaryRepository length2Repository;
	@Autowired @Qualifier("length3")
	private DictionaryRepository length3Repository;
	@Autowired @Qualifier("length4")
	private DictionaryRepository length4Repository;
	@Autowired @Qualifier("length5")
	private DictionaryRepository length5Repository;
	@Autowired @Qualifier("length6")
	private DictionaryRepository length6Repository;
	@Autowired @Qualifier("length7")
	private DictionaryRepository length7Repository;
	@Autowired @Qualifier("length8")
	private DictionaryRepository length8Repository;
	@Autowired @Qualifier("length9")
	private DictionaryRepository length9Repository;
	@Autowired @Qualifier("length10")
	private DictionaryRepository length10Repository;
	@Autowired @Qualifier("length11")
	private DictionaryRepository length11Repository;
	@Autowired @Qualifier("length12")
	private DictionaryRepository length12Repository;
	@Autowired @Qualifier("length13")
	private DictionaryRepository length13Repository;
	@Autowired @Qualifier("length14")
	private DictionaryRepository length14Repository;
	@Autowired @Qualifier("length15")
	private DictionaryRepository length15Repository;
	
	public boolean searchFor(String input) {
		final int length = input.length();
		final DictionaryRepository fbr = getRepository(length);
		final boolean result = fbr.isValid(input.toUpperCase());
		return result;
	}

	public Anagram getAnagrams(String letters) {
		int length = letters.length();
		DictionaryRepository fbr = getRepository(length);
		letters = letters.toUpperCase();
		String sortedLetters = sort(letters);
		//todo sort string by letters
		List<String> anagrams = fbr.getAnagrams(sortedLetters);
		Anagram anagram = new Anagram();
		anagram.setLetters(letters);
		anagram.setAnagrams(anagrams);;
		return anagram;
	}
	
	private DictionaryRepository getRepository(int length) {
		switch (length) {
		case 2:  return length2Repository;
		case 3:  return length3Repository;
		case 4:  return length4Repository;
		case 5:  return length5Repository;
		case 6:  return length6Repository;
		case 7:  return length7Repository;
		case 8:  return length8Repository;
		case 9:  return length9Repository;
		case 10: return length10Repository;
		case 11: return length11Repository;
		case 12: return length12Repository;
		case 13: return length13Repository;
		case 14: return length14Repository;
		case 15: return length15Repository;
		default: throw new IllegalArgumentException("length must be between 2 and 15");
		}
	}

	private String sort(String letters) {
		char[] letterArray = letters.toCharArray();
		Arrays.sort(letterArray);
		String result = String.valueOf(letterArray, 0, letterArray.length);
		return result;
	}
}
