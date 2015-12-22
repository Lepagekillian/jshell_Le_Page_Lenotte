package fr.umlv.jshellbook.markdownanalyzer;

import java.io.IOException;
import java.nio.file.*;
//import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import org.pegdown.PegDownProcessor;

public class MarkdownToHTML {

	
		private MarkdownToHTML() {

		}
		
		public static String parse(Path theFile) throws IOException{
			PegDownProcessor processor = new PegDownProcessor();
			try(Stream<String> fi = Files.lines(theFile)){
				String str = fi.collect(Collectors.joining("\n"));
				str = processor.markdownToHtml(str);
				return str;
			}
		}
}
