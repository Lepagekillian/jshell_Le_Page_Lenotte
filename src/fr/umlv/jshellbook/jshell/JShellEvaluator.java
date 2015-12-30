package fr.umlv.jshellbook.jshell;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import java.util.Objects;
import java.util.stream.Collectors;

import jdk.jshell.DeclarationSnippet;
import jdk.jshell.Diag;
import jdk.jshell.EvalException;
import jdk.jshell.JShell;
import jdk.jshell.MethodSnippet;
import jdk.jshell.Snippet;
import jdk.jshell.JShell.Builder;
import jdk.jshell.Snippet.Status;
import jdk.jshell.SnippetEvent;
import jdk.jshell.UnresolvedReferenceException;

public class JShellEvaluator implements Closeable {

	private final Builder builder;
	private final JShell jShell;
	private final PrintStream printStream;
	private final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

	/**
	 * A JShellEvaluator is an object that allows user to manage the Evaluation
	 * of a JShell command
	 * 
	 * @author Le Page Lenotte
	 * 
	 */
	public JShellEvaluator() {
		this.printStream = new PrintStream(this.arrayOutputStream);
		this.builder = JShell.builder();
		this.builder.err(this.printStream);
		this.builder.out(this.printStream);
		this.jShell = this.builder.build();
	}

	/*
	 * This code is more than 8 lines length because we don't want to
	 * externalize our Snippet and takes the risk that the Snippet could be
	 * modify by an other method
	 */
	private boolean evalOneSnippet(final String snippetToEval) {
		List<SnippetEvent> events = this.jShell.eval(snippetToEval);
		for (SnippetEvent snippetEvent : events) {
			Snippet snippet = snippetEvent.snippet();
			if (snippetEvent.exception() != null) {
				printException(snippetEvent.exception(), snippet.source());
				return false;
			}
			if (snippetEvent.status() == Status.REJECTED) {
				List<Diag> diagnostics = this.jShell.diagnostics(snippet);
				printDiagnostics(snippet.source(), diagnostics);
				return false;
			}
		}
		return true;
	}

	private void printException(Exception exception, String source) {
		this.printStream.println(source);
		if (exception instanceof EvalException) {
			printEvalException((EvalException) exception);
		}
		if (exception instanceof UnresolvedReferenceException) {
			printUnresolvedReferenceException((UnresolvedReferenceException) exception);
		}
	}

	private void printEvalException(EvalException evalException) {
		if (evalException.getMessage() == null) {
			this.printStream.println(String.format("%s thrown", evalException.getExceptionClassName()));
		} else {
			this.printStream.println(
					String.format("%s thrown: %s", evalException.getExceptionClassName(), evalException.getMessage()));
		}
		evalException.printStackTrace(this.printStream);
	}

	/*
	 * This code is more than 8 lines length becausebecause we wanted to treats,
	 * in the same method, that we got 1 or manys errors to diplay it/them to
	 * the use
	 */
	private void printUnresolvedReferenceException(UnresolvedReferenceException ex) {
		MethodSnippet corralled = ex.getMethodSnippet();
		List<Diag> otherErrors = this.jShell.diagnostics(corralled).stream().filter(d -> d.isError())
				.collect(Collectors.toList());
		StringBuilder sb = new StringBuilder();
		if (otherErrors.size() > 0) {
			if (this.jShell.unresolvedDependencies(corralled).size() > 0) {
				sb.append(" and");
			}
			if (otherErrors.size() == 1) {
				sb.append(" this error is addressed --");
			} else {
				sb.append(" these errors are addressed --");
			}
		} else {
			sb.append(".");
		}

		this.printStream.println(String.format("Attempted to call %s which cannot be invoked until%s", corralled.name(),
				unresolved(corralled), sb.toString()));

		if (otherErrors.size() > 0) {
			printDiagnostics(corralled.source(), otherErrors);
		}
	}

	/*
	 * This code is more than 8 lines length because we wanted to
	 * treats, in the same method, that we got 1 or manys errors to diplay
	 * it/them to the use
	 */
	private String unresolved(DeclarationSnippet key) {
		List<String> unr = this.jShell.unresolvedDependencies(key);
		if (unr.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" ");

		for (int fromLast = unr.size(); fromLast > 0; fromLast--) {
			String u = unr.get(fromLast);
			sb.append(u);
			if (fromLast == 0) {
				// No suffix
			} else if (fromLast == 1) {
				sb.append(", and ");
			} else {
				sb.append(", ");
			}
		}
		sb.append((unr.size() == 1) ? " is declared" : " are declared");

		return sb.toString();
	}

	/**
	 * Makes an evaluation of the code and try to execute it
	 * 
	 * @author Le Page Lenotte
	 * @param snippetsToEval
	 *            the snippets to eval
	 * @return The resultat of the execution
	 * @throws NullPointerException
	 *             if the list is null or one line of code is null.
	 */
	public String evalSnippets(List<String> snippetsToEval) {
		Objects.requireNonNull(snippetsToEval);
		for (String snippet : snippetsToEval) {
			if (!evalOneSnippet(Objects.requireNonNull(snippet))) {
				break;// If a snippet is wrong or throw an exception no need to
						// continue
			}
		}
		String resEval = this.arrayOutputStream.toString();
		this.arrayOutputStream.reset();
		return resEval;
	}

	/*
	 * We got more than 8 lines because we don't wanted to externalize 2 loops
	 * and maintains the control during the exploring of our Diags list
	 */
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

	/**
	 * @author Lenotte Close the evaluator
	 */
	@Override
	public void close() throws IOException {
		this.jShell.close();
		this.printStream.close();
		this.arrayOutputStream.close();
	}

}
