package ru.textanalysis.syntagmatic.analyser;

import lombok.Getter;
import ru.textanalysis.syntagmatic.analyser.syntagmatic.PartOfSpeechRelationship;
import ru.textanalysis.syntagmatic.analyser.syntagmatic.WordForSyntagmatic;
import ru.textanalysis.syntagmatic.analyser.syntagmatic.WordWithSyntagmaticLinks;
import ru.textanalysis.tawt.ms.model.sp.Sentence;
import ru.textanalysis.tawt.ms.model.sp.Word;
import ru.textanalysis.tawt.sp.api.SyntaxParser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class SyntagmaticAnalyserImpl implements SyntagmaticAnalyser {

	private final Map<WordForSyntagmatic, List<String>> relationshipsResult;
	private final Set<PartOfSpeechRelationship> allowedRelationships;
	private final Set<Byte> allowedTypeOfSpeechesForGetResult;
	private final Map<WordForSyntagmatic, List<String>> lastSentenceSyntagmaticAnalyseResult;
	private Sentence lastSentenceSyntaxAnalyseResult;

	private final SyntaxParser syntaxParser = new SyntaxParser();

	@Override
	public void init() {
		this.syntaxParser.init();
	}

	public SyntagmaticAnalyserImpl() {
		relationshipsResult = new HashMap<>();
		allowedRelationships = new HashSet<>();
		allowedTypeOfSpeechesForGetResult = new HashSet<>();
		lastSentenceSyntagmaticAnalyseResult = new HashMap<>();
		lastSentenceSyntaxAnalyseResult = null;
	}

	@Override
	public void analyse(String text) {
		Sentence sentence = syntaxParser.getTreeSentence(text);

		Map<WordForSyntagmatic, List<String>> currentSentenceRelationships = new HashMap<>();
		sentence.getBearingPhrases()
			.forEach(bearingPhrase -> bearingPhrase.getWords()
				.forEach(dependency -> dependency.getMains().forEach(main -> {
					PartOfSpeechRelationship relationship = PartOfSpeechRelationship.builder()
						.partOfSpeechMain(main.getForms().get(0).getTypeOfSpeech())
						.partOfSpeechDependency(dependency.getForms().get(0).getTypeOfSpeech())
						.build();
					if (allowedRelationships.isEmpty() || allowedRelationships.contains(relationship)) {
						setRelations(main, dependency, currentSentenceRelationships);
						setRelations(dependency, main, currentSentenceRelationships);
					}
				}))
			);
		lastSentenceSyntagmaticAnalyseResult.clear();
		lastSentenceSyntagmaticAnalyseResult.putAll(currentSentenceRelationships);
		lastSentenceSyntaxAnalyseResult = sentence;
	}

	@Override
	public Map<WordForSyntagmatic, List<String>> getRelationshipsResult() {
		Stream<Map.Entry<WordForSyntagmatic, List<String>>> relationshipStream = relationshipsResult.entrySet().stream();
		if (!allowedTypeOfSpeechesForGetResult.isEmpty()) {
			relationshipStream = relationshipStream.filter(relationship -> allowedTypeOfSpeechesForGetResult.contains(relationship.getKey().getPartOfSpeech()));
		}
		return relationshipStream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public List<WordWithSyntagmaticLinks> getSyntagmaticLinksResult() {
		List<WordWithSyntagmaticLinks> result = new ArrayList<>();
		Stream<Map.Entry<WordForSyntagmatic, List<String>>> relationshipStream = relationshipsResult.entrySet().stream();
		if (!allowedTypeOfSpeechesForGetResult.isEmpty()) {
			relationshipStream = relationshipStream.filter(relationship -> allowedTypeOfSpeechesForGetResult.contains(relationship.getKey().getPartOfSpeech()));
		}
		relationshipStream.forEach(relationship -> {
			result.add(WordWithSyntagmaticLinks.builder()
					.word(relationship.getKey())
					.numberOfUniqueWords(relationship.getValue().stream().distinct().count())
					.numberOfLinks(relationship.getValue().size())
				.build());
		});
		return result;
	}

	@Override
	public void clearRelationshipResult() {
		relationshipsResult.clear();
	}

	@Override
	public void clearAllowedRelationship() {
		allowedRelationships.clear();
	}

	@Override
	public void setAllowedRelationship(Set<PartOfSpeechRelationship> relationships) {
		clearAllowedRelationship();
		allowedRelationships.addAll(relationships);
	}

	@Override
	public void addAllowedRelationship(Set<PartOfSpeechRelationship> relationships) {
		allowedRelationships.addAll(relationships);
	}

	@Override
	public void addAllowedRelationship(PartOfSpeechRelationship relationship) {
		allowedRelationships.add(relationship);
	}

	@Override
	public void addAllowedRelationship(byte partOfSpeechMain, byte partOfSpeechDependency) {
		PartOfSpeechRelationship allowedRelationship = PartOfSpeechRelationship.builder()
			.partOfSpeechMain(partOfSpeechMain)
			.partOfSpeechDependency(partOfSpeechDependency)
			.build();
		allowedRelationships.add(allowedRelationship);
	}

	@Override
	public void clearAllowedTypeOfSpeechesForGetResult() {
		allowedTypeOfSpeechesForGetResult.clear();
	}

	@Override
	public void addAllowedTypeOfSpeechesForGetResult(byte typeOfSpeech) {
		allowedTypeOfSpeechesForGetResult.add(typeOfSpeech);
	}

	@Override
	public void removeAllowedTypeOfSpeechesForGetResult(byte typeOfSpeech) {
		allowedTypeOfSpeechesForGetResult.remove(typeOfSpeech);
	}

	@Override
	public void finish() {
		clearRelationshipResult();
		clearAllowedRelationship();
	}

	private void setRelations(Word main, Word dependency, Map<WordForSyntagmatic, List<String>> currentSentenceRelationships) {
		WordForSyntagmatic mainWord = WordForSyntagmatic.builder()
			.word(main.getForms().get(0).getInitialForm().getMyString())
			.partOfSpeech(main.getForms().get(0).getTypeOfSpeech())
			.build();
		if (!relationshipsResult.containsKey(mainWord)) {
			List<String> wordsWithRelationships = new ArrayList<>();
			wordsWithRelationships.add(dependency.getForms().get(0).getInitialForm().getMyString());
			relationshipsResult.put(mainWord, wordsWithRelationships);
			List<String> currentSentenceRelationshipWordsList = new ArrayList<>();
			currentSentenceRelationshipWordsList.add(dependency.getForms().get(0).getInitialForm().getMyString());
			currentSentenceRelationships.put(mainWord, currentSentenceRelationshipWordsList);
		} else {
			// Сейчас встречается ситуация, когда в синтаксическом анализе есть несколько связей между словами.
			// Чтобы добавялась только одна из них, проверяем, что в текущем предложении уже была добавлена связеь между словами
			// Если это исправится в синтаксическом анализе, то даннуж проверку монжо будет убрать
			if (currentSentenceRelationships.containsKey(mainWord)) {
				List<String> current = currentSentenceRelationships.get(mainWord);
				if (!current.contains(dependency.getForms().get(0).getInitialForm().getMyString())) {
					List<String> wordsWithRelationships = relationshipsResult.get(mainWord);
					wordsWithRelationships.add(dependency.getForms().get(0).getInitialForm().getMyString());
					relationshipsResult.put(mainWord, wordsWithRelationships);
					List<String> currentSentenceRelationshipWordsList = new ArrayList<>(current);
					currentSentenceRelationshipWordsList.add(dependency.getForms().get(0).getInitialForm().getMyString());
					currentSentenceRelationships.put(mainWord, currentSentenceRelationshipWordsList);
				}
			} else {
				List<String> wordsWithRelationships = relationshipsResult.get(mainWord);
				wordsWithRelationships.add(dependency.getForms().get(0).getInitialForm().getMyString());
				relationshipsResult.put(mainWord, wordsWithRelationships);
				List<String> currentSentenceRelationshipWordsList = new ArrayList<>();
				currentSentenceRelationshipWordsList.add(dependency.getForms().get(0).getInitialForm().getMyString());
				currentSentenceRelationships.put(mainWord, currentSentenceRelationshipWordsList);
			}
		}
	}
}
