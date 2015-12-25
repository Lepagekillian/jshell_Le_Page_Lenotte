package fr.umlv.jshellbook.jshell;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class JShellEvaluatorTest {

	@SuppressWarnings("static-method")
	@Test(expected = NullPointerException.class)
	public void testNullLines() throws IOException {
		try (JShellEvaluator shellEvaluator = new JShellEvaluator()) {
			shellEvaluator.evalCodeLines(null);
		}
	}

	@SuppressWarnings("static-method")
	@Test(expected = NullPointerException.class)
	public void testNullLine() throws IOException {
		try (JShellEvaluator shellEvaluator = new JShellEvaluator()) {
			List<String> lines = new ArrayList<>();
			lines.add(null);
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

	@Test
	@SuppressWarnings("static-method")
	public void testEvalClasse() throws IOException {
		try (JShellEvaluator shellEvaluator = new JShellEvaluator()) {
			List<String> lines = new ArrayList<>();

			lines.add(
					"public class Test \n {private int count = 0;public int getCount() {return this.count;}public void countTo100() {while(this.count <100 ){this.count++;};}");
			lines.add("Test test = new Test();");
			lines.add("System.out.println(test.getCount())");
			lines.add("System.out.println(test.countTo())");
			lines.add("System.out.println(test.getCount())");
			String res = shellEvaluator.evalCodeLines(lines);
			assertEquals("0\n99\n", res);
		}
	}
}
