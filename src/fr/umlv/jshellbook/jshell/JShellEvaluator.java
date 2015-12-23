package fr.umlv.jshellbook.jshell;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jdk.jshell.JShell;
import jdk.jshell.JShell.Builder;
import jdk.jshell.SnippetEvent;

public class JShellEvaluator implements Closeable {

	private final Builder builder;
	private final JShell jShell;

	private final PrintStream printStream;
	private final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

	public JShellEvaluator() {
		this.printStream = new PrintStream(this.arrayOutputStream);
		this.builder = JShell.builder();
		this.builder.err(this.printStream);
		this.builder.out(this.printStream);
		this.jShell = this.builder.build();
	}

	private void evalOneCodeLine(final String lineToEval) {
		Objects.requireNonNull(lineToEval);
		this.jShell.eval(lineToEval);
	}

	public String evalCodeLines(List<String> linesToEval) {
		Objects.requireNonNull(linesToEval);

		for (String line : linesToEval) {
			evalOneCodeLine(line);
		}
		String resEval = this.arrayOutputStream.toString();
		this.arrayOutputStream.reset();
		return resEval;
	}

	@Override
	public void close() throws IOException {
		this.jShell.close();
		this.printStream.close();
		this.arrayOutputStream.close();
	}

	public static void main(String[] args) throws IOException {
		try (JShellEvaluator evaluator = new JShellEvaluator()) {
			List<String> lines = new ArrayList<>();
			lines.add("int i =0;");
			lines.add("while(i<100){i++;}");
			lines.add("System.out.println(i);");
			evaluator.evalCodeLines(lines);
		}
	}

}
