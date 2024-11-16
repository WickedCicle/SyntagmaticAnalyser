package ru.textanalysis.syntagmatic.analyser.example;

import lombok.Getter;
import lombok.Setter;

@Setter
public class Status extends Thread {

	volatile boolean shutdown = false;

	public void run() {
		while (!shutdown) {
			System.out.println("Work in progress...");
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
