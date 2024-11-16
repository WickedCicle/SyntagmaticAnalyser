package ru.textanalysis.syntagmatic.analyser.example;

import ru.textanalysis.syntagmatic.analyser.SyntagmaticAnalyser;
import ru.textanalysis.syntagmatic.analyser.SyntagmaticAnalyserImpl;
import ru.textanalysis.syntagmatic.analyser.syntagmatic.WordForSyntagmatic;
import ru.textanalysis.syntagmatic.analyser.syntagmatic.WordWithSyntagmaticLinks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class SyntagmaticAnalyserExample {

	public static void main(String[] args) {
		String textFilePath = System.getProperty("text-file-path");
		String outputRelationshipFilePath = System.getProperty("output-relationship-file-path");
		String outputSyntagmaticLinksFilePath = System.getProperty("output-syntagmatic-links-file-path");
		String allowedTypeOfSpeechForResultFilePath = System.getProperty("allowed-type-of-speech-for-result-file-path");

		if (textFilePath == null || (outputRelationshipFilePath == null && outputSyntagmaticLinksFilePath == null)) {
			System.out.println("Должны быть указаны пути как для входного, так хотя бы для одного из выходных файлов");
		} else {
			SyntagmaticAnalyser syntagmaticAnalyser = new SyntagmaticAnalyserImpl();
			syntagmaticAnalyser.init();

			if (allowedTypeOfSpeechForResultFilePath != null) {
				addAllowedTypeOfSpeechFile(allowedTypeOfSpeechForResultFilePath, syntagmaticAnalyser);
			}

			Status statusThread = new Status();

			statusThread.start();

			readTextFile(textFilePath, syntagmaticAnalyser);

			if (outputRelationshipFilePath != null) {
				createRelationshipFile(outputRelationshipFilePath, syntagmaticAnalyser.getRelationshipsResult());
			}
			if (outputSyntagmaticLinksFilePath != null) {
				createSyntagmaticLinksFile(outputSyntagmaticLinksFilePath, syntagmaticAnalyser.getSyntagmaticLinksResult());
			}

			syntagmaticAnalyser.finish();

			statusThread.shutdown = true;
			System.out.println("\nDone");
		}
	}

	private static void addAllowedTypeOfSpeechFile(String filePath, SyntagmaticAnalyser syntagmaticAnalyser) {
		try {
			List<String> typesOfSpeech = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
			typesOfSpeech.forEach(type -> syntagmaticAnalyser.addAllowedTypeOfSpeechesForGetResult(Byte.parseByte(type)));
		} catch (IOException e) {
			System.out.println("Ошибка при чтении файла с разрешенными частями речи");
		} catch (NumberFormatException e) {
			System.out.println("Файл с разрешенными частями речи содержит некорректные значения");
		}
	}

	private static void readTextFile(String textFilePath, SyntagmaticAnalyser syntagmaticAnalyser) {
		try {
			List<String> sentences = Files.readAllLines(Paths.get(textFilePath), StandardCharsets.UTF_8);
			sentences.forEach(syntagmaticAnalyser::analyse);
		} catch (IOException e) {
			System.out.println("Ошибка при чтении файла с текстом");
		}
	}

	private static void createRelationshipFile(String outputRelationshipFilePath, Map<WordForSyntagmatic, List<String>> result) {
		File file = new File(outputRelationshipFilePath);
		try (
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
		) {
			bw.write(result.toString());
		} catch (IOException e) {
			System.out.println("Ошибка при создании файла с результатами связей");
		}
	}

	private static void createSyntagmaticLinksFile(String outputSyntagmaticLinksFilePath, List<WordWithSyntagmaticLinks> result) {
		File file = new File(outputSyntagmaticLinksFilePath);
		try (
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
		) {
			bw.write(result.toString());
		} catch (IOException e) {
			System.out.println("Ошибка при создании файла с результатами синтагматических ссылок");
		}
	}
}
