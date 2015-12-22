package fr.umlv.jshellbook.markdownanalyzer;

import java.io.IOException;
import java.nio.file.Paths;

import fr.umlv.jshellbook.markdownanalyzer.MarkdownToHTML;

public class MainTest {
	
	public static void main(String[] args) {
		try {
			System.out.println(MarkdownToHTML.parse(Paths.get("ressources/example_markdown")));
		} catch (IOException e) {
			System.out.println("File not found.");
			return;
		}
	}
	
}
