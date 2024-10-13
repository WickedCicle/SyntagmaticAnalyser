package ru.textanalysis.syntagmatic.analyser.syntagmatic;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WordForSyntagmatic {

	String word;

	byte partOfSpeech;

	@Override
	public String toString() {
		return "\n" +  word + ", часть речи = " + partOfSpeech;
	}
}
