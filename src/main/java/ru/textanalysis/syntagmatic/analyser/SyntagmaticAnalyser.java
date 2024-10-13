package ru.textanalysis.syntagmatic.analyser;

import lombok.Getter;
import ru.textanalysis.syntagmatic.analyser.syntagmatic.PartOfSpeechRelationship;
import ru.textanalysis.syntagmatic.analyser.syntagmatic.WordForSyntagmatic;
import ru.textanalysis.syntagmatic.analyser.syntagmatic.WordWithSyntagmaticLinks;
import ru.textanalysis.tawt.ms.model.sp.Sentence;

import java.util.*;

public interface SyntagmaticAnalyser {

	void init();

	void analyse(String text);

	Sentence getLastSentenceSyntaxAnalyseResult();

	Map<WordForSyntagmatic, List<String>> getRelationshipsResult();

	List<WordWithSyntagmaticLinks> getSyntagmaticLinksResult();

	void clearRelationshipResult();

	void clearAllowedRelationship();

	void setAllowedRelationship(Set<PartOfSpeechRelationship> relationships);

	void addAllowedRelationship(Set<PartOfSpeechRelationship> relationships);

	void addAllowedRelationship(PartOfSpeechRelationship relationship);

	void addAllowedRelationship(byte partOfSpeechMain, byte partOfSpeechDependency);

	void clearAllowedTypeOfSpeechesForGetResult();

	void addAllowedTypeOfSpeechesForGetResult(byte typeOfSpeech);

	void removeAllowedTypeOfSpeechesForGetResult(byte typeOfSpeech);

	void finish();
}
