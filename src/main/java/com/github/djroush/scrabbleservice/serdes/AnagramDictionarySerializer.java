package com.github.djroush.scrabbleservice.serdes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.djroush.scrabbleservice.model.rest.AnagramDictionary;

@Component
public class AnagramDictionarySerializer extends StdSerializer<AnagramDictionary> {
	private static final long serialVersionUID = -2904547968877720366L;

	protected AnagramDictionarySerializer() {
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
	public void serialize(AnagramDictionary anagramDictionary, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		for (Map.Entry<String, List<String>> entry: anagramDictionary.getEntries()) {
			String key = entry.getKey();
			List<String> value = entry.getValue();
			gen.writeFieldName(key);
			gen.writeRaw('=');
			String[] valArray = value.toArray(new String[0]);
			gen.writeArray(valArray, 0, valArray.length);
			gen.writeRaw('\n');
		}
		gen.writeEndObject();
	}
}
