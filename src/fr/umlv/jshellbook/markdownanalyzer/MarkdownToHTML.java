package fr.umlv.jshellbook.markdownanalyzer;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
//import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.pegdown.PegDownProcessor;

public class MarkdownToHTML {

	private final PegDownProcessor processor ;

		public MarkdownToHTML(PegDownProcessor process) {
			this.processor= Objects.requireNonNull(process);
		}
		
		public  String parse(Path theFile) throws IOException{

			try(Stream<String> fi = Files.lines(theFile)){
				String str = fi.collect(Collectors.joining("\n"));
				str = this.processor.markdownToHtml(str);
				return str;
			}
		}
}
