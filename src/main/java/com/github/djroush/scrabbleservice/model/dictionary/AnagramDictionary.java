package com.github.djroush.scrabbleservice.model.dictionary;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.djroush.scrabbleservice.serdes.AnagramDictionaryDeserializer;
import com.github.djroush.scrabbleservice.serdes.AnagramDictionarySerializer;

@JsonSerialize(using = AnagramDictionarySerializer.class)
@JsonDeserialize(using = AnagramDictionaryDeserializer.class)
public class AnagramDictionary {
	private Map<String, List<String>> anagramMap = new TreeMap<>();

	public Set<Map.Entry<String, List<String>>> getEntries() {
		return anagramMap.entrySet();
	}
	
	public List<String> getEntry(String key) {
		return anagramMap.get(key);
	}
	
	public void addEntry(String key, String value) {
		List<String> existingValues = anagramMap.get(key);
		if (existingValues == null) {
			existingValues = new LinkedList<String>();
			anagramMap.put(key, existingValues);
		}
		existingValues.add(value);
	}
}
