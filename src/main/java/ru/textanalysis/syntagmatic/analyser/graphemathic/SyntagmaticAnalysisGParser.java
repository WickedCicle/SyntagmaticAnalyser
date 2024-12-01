package ru.textanalysis.syntagmatic.analyser.graphemathic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.textanalysis.tawt.graphematic.parser.exception.NotParserTextException;

import java.util.LinkedList;
import java.util.List;

public class SyntagmaticAnalysisGParser {

	private Logger log = LoggerFactory.getLogger(getClass());

	public SyntagmaticAnalysisGParser() {
		log.debug("Syntagmatic Analysis Parser is inited!");
	}

	public List<String> parserParagraph(String paragraph) throws NotParserTextException {
		List<String> paragraphList = new LinkedList<>();

		for (String sentence : paragraph.split("[@\"№#;$%^:&*()!?.]+")) {
			if (!sentence.isBlank()) {
				try {
					paragraphList.add(sentence);
				} catch (NotParserTextException ex) {
//                    log.debug("Paragraph = {}, exception: {}", sentence, ex.getMessage());
				}
			}
		}

		if (paragraphList.isEmpty()) {
//            throw new NotParserTextException("Передана пустая строка");
		}

		return paragraphList;
	}

	public List<List<String>> parserText(String text) throws NotParserTextException {
		List<List<String>> textList = new LinkedList<>();

		for (String paragraph : text.split("[\\r\\n]")) {
			if (!paragraph.isBlank()) {
				try {
					textList.add(parserParagraph(paragraph));
				} catch (NotParserTextException ex) {
//                    log.debug("Text = {}, exception: {}", text, ex.getMessage());
				}
			}
		}

		if (textList.isEmpty()) {
//            throw new NotParserTextException("Передана пустая строка");
		}

		return textList;
	}
}
