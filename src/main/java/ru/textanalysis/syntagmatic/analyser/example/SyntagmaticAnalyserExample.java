package ru.textanalysis.syntagmatic.analyser.example;

import ru.textanalysis.syntagmatic.analyser.SyntagmaticAnalyser;
import ru.textanalysis.syntagmatic.analyser.SyntagmaticAnalyserImpl;

public class SyntagmaticAnalyserExample {

	public static void main(String[] args) {
		SyntagmaticAnalyser syntagmaticAnalyser = new SyntagmaticAnalyserImpl();
		syntagmaticAnalyser.init();

		syntagmaticAnalyser.addAllowedTypeOfSpeechesForGetResult((byte) 17);
		syntagmaticAnalyser.addAllowedTypeOfSpeechesForGetResult((byte) 20);

		String sentence = "Бабушка тоже знала эту историю";
		String sentence2 = "Дедушка отлично знал ту историю";
		syntagmaticAnalyser.analyse(sentence);
		syntagmaticAnalyser.analyse(sentence2);

		System.out.println(syntagmaticAnalyser.getRelationshipsResult());
		System.out.println("-------------------------------------------");
		System.out.println(syntagmaticAnalyser.getSyntagmaticLinksResult());

		syntagmaticAnalyser.finish();
	}
}
