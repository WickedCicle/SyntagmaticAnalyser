package ru.textanalysis.syntagmatic.analyser.example;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import ru.textanalysis.syntagmatic.analyser.SyntagmaticAnalyser;
import ru.textanalysis.syntagmatic.analyser.SyntagmaticAnalyserImpl;
import ru.textanalysis.syntagmatic.analyser.graphemathic.SyntagmaticAnalysisGParser;
import ru.textanalysis.syntagmatic.analyser.syntagmatic.WordForSyntagmatic;
import ru.textanalysis.syntagmatic.analyser.syntagmatic.WordWithSyntagmaticLinks;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class SyntagmaticAnalyserExample {

	public static void main(String[] args) {
		String textFilePath = System.getProperty("text-file-path");
		String outputRelationshipFilePath = System.getProperty("output-relationship-file-path");
		String outputSyntagmaticLinksFilePath = System.getProperty("output-syntagmatic-links-file-path");
		String outputTableFilePath = System.getProperty("output-table-file-path");
		String allowedTypeOfSpeechForResultFilePath = System.getProperty("allowed-type-of-speech-for-result-file-path");

		if (textFilePath == null || (outputRelationshipFilePath == null && outputSyntagmaticLinksFilePath == null && outputTableFilePath == null)) {
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
			if (outputSyntagmaticLinksFilePath != null || outputTableFilePath != null) {
				List<WordWithSyntagmaticLinks> syntagmaticLinks = syntagmaticAnalyser.getSyntagmaticLinksResult();
				if (outputSyntagmaticLinksFilePath != null) {
					createSyntagmaticLinksFile(outputSyntagmaticLinksFilePath, syntagmaticLinks);
				}
				if (outputTableFilePath != null) {
					createTableFile(outputTableFilePath, syntagmaticLinks);
				}
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
			SyntagmaticAnalysisGParser gParser = new SyntagmaticAnalysisGParser();
			String text = Files.readString(Paths.get(textFilePath), StandardCharsets.UTF_8);
			List<List<String>> paragraphs = gParser.parserText(text);
			paragraphs.forEach(paragraph -> paragraph.forEach(syntagmaticAnalyser::analyse));
		} catch (IOException e) {
			System.out.println("Ошибка при чтении файла с текстом");
		}
	}

	private static void createRelationshipFile(String outputRelationshipFilePath, Map<WordForSyntagmatic, List<String>> result) {
		try (
			BufferedWriter out = Files.newBufferedWriter(Paths.get(outputRelationshipFilePath));
		) {
			out.write(result.toString());
		} catch (IOException e) {
			System.out.println("Ошибка при создании файла с результатами связей");
		}
	}

	private static void createSyntagmaticLinksFile(String outputSyntagmaticLinksFilePath, List<WordWithSyntagmaticLinks> result) {
		try (
			BufferedWriter out = Files.newBufferedWriter(Paths.get(outputSyntagmaticLinksFilePath));
		) {
			out.write(result.toString());
		} catch (IOException e) {
			System.out.println("Ошибка при создании файла с результатами синтагматических ссылок");
		}
	}

	private static void createTableFile(String outputTableFilePath, List<WordWithSyntagmaticLinks> result) {
		try (
			FileOutputStream outputStream = new FileOutputStream(outputTableFilePath);
			Workbook workbook = new XSSFWorkbook();
		) {
			AtomicInteger wordCount = new AtomicInteger();
			Map<Double, Integer> syntagmaticLinks = new HashMap<>();
			result.forEach(word -> {
				wordCount.getAndIncrement();
				double linkForWord = ((double) word.getNumberOfUniqueWords() / (double) word.getNumberOfLinks());
				BigDecimal bd = new BigDecimal(Double.toString(linkForWord));
				bd = bd.setScale(3, RoundingMode.HALF_UP);
				syntagmaticLinks.merge(bd.doubleValue(), 1, Integer::sum);
			});
			Stream<Map.Entry<Double, Integer>> sorted =
				syntagmaticLinks.entrySet().stream()
					.sorted(Collections.reverseOrder(Map.Entry.comparingByKey()));

			List<Double> doubleValues = new ArrayList<>();
			List<Integer> integerValues = new ArrayList<>();

			sorted.forEach(sort -> {
				doubleValues.add(sort.getKey());
				integerValues.add(sort.getValue());
			});

			Sheet sheet = workbook.createSheet("Связи");

			Row header = sheet.createRow(0);

			CellStyle style = workbook.createCellStyle();
			style.setWrapText(true);

			Cell headerCell = header.createCell(0);
			headerCell.setCellValue("С-связь");
			headerCell.setCellStyle(style);

			headerCell = header.createCell(1);
			headerCell.setCellValue("Слов");
			headerCell.setCellStyle(style);

			headerCell = header.createCell(2);
			headerCell.setCellValue("%");
			headerCell.setCellStyle(style);

			headerCell = header.createCell(3);
			headerCell.setCellValue("С-вес");
			headerCell.setCellStyle(style);

			int wordLinksCount = 0;
			for (int i = 0; i < syntagmaticLinks.size(); i++) {
				Row row = sheet.createRow(1 + i);
				Cell cell = row.createCell(0);
				cell.setCellValue(doubleValues.get(i).toString());
				cell.setCellStyle(style);

				cell = row.createCell(1);
				cell.setCellValue(integerValues.get(i).toString());
				cell.setCellStyle(style);

				cell = row.createCell(2);
				double percentage = ((double) integerValues.get(i) / (double) wordCount.get());
				BigDecimal bd = new BigDecimal(Double.toString(percentage));
				bd = bd.setScale(5, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
				cell.setCellStyle(style);

				wordLinksCount += integerValues.get(i);
				cell = row.createCell(3);
				double ww = ((double) (wordCount.get() - wordLinksCount) / (double) wordCount.get());
				bd = new BigDecimal(Double.toString(ww));
				bd = bd.setScale(5, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
				cell.setCellStyle(style);
			}

			workbook.write(outputStream);
		} catch (IOException e) {
			System.out.println("Ошибка при создании таблицы с результатами синтагматических ссылок");
		}
	}
}
