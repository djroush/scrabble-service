package com.github.djroush.scrabbleservice.serdes;

import java.io.IOException;
import java.util.Iterator;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.djroush.scrabbleservice.model.rest.AnagramDictionary;

@Component
public class AnagramDictionaryDeserializer extends StdDeserializer<AnagramDictionary> {
	private static final long serialVersionUID = -2904547968877720366L;

	public AnagramDictionaryDeserializer() {
		super(AnagramDictionary.class);
	}
	
	/*
	  {
	    ...
	    "ABERS":["BARES","BASER","BEARS","BRAES","SABER","SABRE"],
	    ...
	  }
	*/
	@Override
	public AnagramDictionary deserialize(JsonParser parser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		AnagramDictionary anagramDictionary = new AnagramDictionary();
		
		TreeNode node = parser.readValueAsTree();
		Iterator<String> nodeKeys = node.fieldNames();
		while (nodeKeys.hasNext()) {
			String key = nodeKeys.next();
			ArrayNode value = (ArrayNode)node.get(key);
			Iterator<JsonNode> valueIter = value.iterator();
			while (valueIter.hasNext()) {
				JsonNode item = valueIter.next();
				String anagram = item.asText();
				anagramDictionary.addEntry(key, anagram);
			}
		}
		return anagramDictionary;
	}

}
