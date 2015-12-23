package fr.umlv.jshellbook.jshell;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class JShellEvaluatorTest {

	@SuppressWarnings("static-method")
	@Test(expected = NullPointerException.class)
	public void testNull() throws IOException {
		try (JShellEvaluator shellEvaluator = new JShellEvaluator()) {
			shellEvaluator.evalCodeLines(null);
		}
	}

	@Test
	@SuppressWarnings("static-method")
	public void testEval() throws IOException {
		try (JShellEvaluator shellEvaluator = new JShellEvaluator()) {
			List<String> lines = new ArrayList<>();
			lines.add("System.out.println(\"test\");");
			String res = shellEvaluator.evalCodeLines(lines);
			assertEquals("test\n", res);
		}
	}

}
