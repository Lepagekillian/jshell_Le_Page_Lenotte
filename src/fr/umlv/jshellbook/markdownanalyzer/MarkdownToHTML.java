package fr.umlv.jshellbook.markdownanalyzer;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.pegdown.PegDownProcessor;

public class MarkdownToHTML {

	private final PegDownProcessor processor = new PegDownProcessor();

	public String parse(Path theFile) throws IOException {
		Objects.requireNonNull(theFile);

		String[] splitHtml = makeHtml(theFile).split("\n");
		StringBuilder res = new StringBuilder();
		int formCounter = 0;

		for (int i = 0; i < splitHtml.length; i++) {
			String lineHtml = splitHtml[i];
			if (lineHtml.contains("<h2>") && !splitHtml[i-1].contains("<h1>")) {
				res.append(makeFormHtml(formCounter));
				formCounter += 1;
			}
			res.append(lineHtml).append("\n");
		}
		if(res.length() != 0){
			res.append(makeFormHtml(formCounter));
		}
		return res.toString();

	}

	private static String makeFormHtml(int formCounter) {
		StringBuilder res = new StringBuilder();
		res.append("<form id=\"questForm").append(formCounter).append("\"><textarea class = \"myAnswer\" ");
		res.append("rows=\"30\" cols =\"40\" name=\"questForm\" ").append("value = \"Type here your Java Code\"> ");
		res.append("Type here </textarea>\n");
		res.append("<p id =\"resOfEval\"").append(formCounter).append("></p>\n<br>\n");
		res.append("<input type =\"submit\" value = \"EvalCode!\"/>");
		res.append("</form>\n");
		return res.toString();
	}

	private String makeHtml(Path theFile) throws IOException {
		try (Stream<String> fi = Files.lines(theFile)) {

			String str = fi.collect(Collectors.joining("\n"));
			str = this.processor.markdownToHtml(str);
			return str;
		}
	}
}
