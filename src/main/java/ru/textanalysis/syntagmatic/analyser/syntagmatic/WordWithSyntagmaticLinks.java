package ru.textanalysis.syntagmatic.analyser.syntagmatic;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WordWithSyntagmaticLinks {

	WordForSyntagmatic word;

	long numberOfUniqueWords;

	long numberOfLinks;

	@Override
	public String toString() {
		return "\n" + word + "\n" +
			"количестве уникальных слов = " + numberOfUniqueWords + "\n" +
			"С-связей = " + numberOfLinks;
	}
}
