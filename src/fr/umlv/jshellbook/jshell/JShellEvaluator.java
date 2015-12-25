package fr.umlv.jshellbook.jshell;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdk.jshell.Diag;
import jdk.jshell.EvalException;
import jdk.jshell.JShell;
import jdk.jshell.JShell.Builder;
import jdk.jshell.Snippet.Status;
import jdk.jshell.SnippetEvent;
import jdk.jshell.UnresolvedReferenceException;

public class JShellEvaluator implements Closeable {

	private final Builder builder;
	private final JShell jShell;
	private static final Pattern LINEBREAK = Pattern.compile("\\R");
	private final PrintStream printStream;
	private final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

	public JShellEvaluator() {
		this.printStream = new PrintStream(this.arrayOutputStream);
		this.builder = JShell.builder();
		this.builder.err(this.printStream);
		this.builder.out(this.printStream);
		this.jShell = this.builder.build();
	}

	private boolean evalOneCodeLine(final String lineToEval) {
		List<SnippetEvent> events = this.jShell.eval(lineToEval);
		for (SnippetEvent snippetEvent : events) {
			List<Diag> diagnostics = this.jShell.diagnostics(snippetEvent.snippet());
			if (snippetEvent.exception() != null) {
				printException(snippetEvent);
				return false;
			}
			if (snippetEvent.status() == Status.REJECTED) {
				printDiagnostics(snippetEvent.snippet().source(), diagnostics);
				return false;
			}
		}
		return true;
	}

	private void printException(SnippetEvent snippetEvent) {
		Exception exception = snippetEvent.exception();
		if (exception instanceof EvalException) {
			EvalException evalException = (EvalException) exception;
			this.printStream.println(evalException.getExceptionClassName());
		}
		if (exception instanceof UnresolvedReferenceException) {
			UnresolvedReferenceException exception2 = (UnresolvedReferenceException) exception;
			this.printStream.println(exception2.getMethodSnippet().source());
		}
		exception.printStackTrace(this.printStream);
		this.printStream.println(snippetEvent.snippet().source());
	}

	/**
	 * 
	 * @param linesToEval
	 * @return
	 * @throws Null
	 *             pointer exception if the list is null or one line of code is
	 *             null.
	 */
	public String evalCodeLines(List<String> linesToEval) {
		Objects.requireNonNull(linesToEval);

		for (String line : linesToEval) {
			if (!evalOneCodeLine(Objects.requireNonNull(line))) {
				break;// If a line is wrong or throw an exception no need to
						// continue
			}
		}
		String resEval = this.arrayOutputStream.toString();
		this.arrayOutputStream.reset();
		return resEval;
	}

	void printDiagnostics(String source, List<Diag> diagnostics) {
		for (Diag diag : diagnostics) {

			if (diag.isError()) {
				this.printStream.print("Error:");
			} else {
				this.printStream.print("Warning:");
			}

			for (String line : diag.getMessage(null).split("\\r?\\n")) {
				if (!line.trim().startsWith("location:")) {
					this.printStream.println(line);
				}
			}
			this.printStream.println(source);
		}
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
			lines.add("pouet");
			String res = evaluator.evalCodeLines(lines);
			System.out.println(res);
		}
	}

}
