package ru.textanalysis.syntagmatic.analyser.syntagmatic;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PartOfSpeechRelationship {

	/**
	 * Часть речи основного слова
	 */
	byte partOfSpeechMain;

	/**
	 * Часть речи зависимого слова
	 */
	byte partOfSpeechDependency;
}
