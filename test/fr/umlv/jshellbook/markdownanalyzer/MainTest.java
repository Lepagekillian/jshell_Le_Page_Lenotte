package fr.umlv.jshellbook.markdownanalyzer;

import java.io.IOException;
import java.nio.file.Paths;

import fr.umlv.jshellbook.markdownanalyzer.MarkdownToHTML;

public class MainTest {
	
	public static void main(String[] args) {
		MarkdownToHTML work =  new MarkdownToHTML();
		try {
			System.out.println(work.parse(Paths.get("ressources/exercice1.mkdown")));
		} catch (IOException e) {
			System.out.println("File not found.");
			return;
		}
	}
	
}
