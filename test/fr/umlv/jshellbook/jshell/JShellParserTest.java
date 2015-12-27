package fr.umlv.jshellbook.jshell;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class JShellParserTest {

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("static-method")
	public void testNull() {
		JShellParser.parse(null);
	}

	@Test
	@SuppressWarnings("static-method")
	public void testIfElse() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("if(true) ");
		sb.append("{System.out.println(\"fasle\");}");
		sb.append(" esle ");
		sb.append("{System.out.println(\"true\");}");
		List<String> lines = new ArrayList<>();
		lines.add("if(false) {System.out.println(\"fasle\");} else {System.out.println(\"true\");}");

		//assertTrue(lines.equals(JShellParser.parse(sb.toString())));
		try(JShellEvaluator evaluator = new JShellEvaluator()){
			String res = evaluator.evalSnippets(lines);
			System.out.println(res);
		}
	}
	
	@Test
	@SuppressWarnings("static-method")
	public void testBoucle() {
		StringBuilder sb = new StringBuilder();
		sb.append("for (int i = 0; i < 100; i++) {");
		sb.append("System.out.println(i);");
		sb.append("}");
		List<String> lines = new ArrayList<>();
		lines.add(sb.toString());
		assertTrue(lines.equals(JShellParser.parse(sb.toString())));

	}

	@Test
	@SuppressWarnings("static-method")
	public void testClass() {
		StringBuilder sb = new StringBuilder();
		sb.append("import java.util.List;");
		sb.append("public class Test {private int count = 0;");
		sb.append("public int getCount() {return this.count;}");
		sb.append("public void countTo100() {" + "while(this.count <100 )" + "{this.count++;}}" + "}");
		sb.append("Test test = new Test();");
		sb.append("System.out.println(test.getCount());");
		sb.append("test.countTo100();");
		sb.append("System.out.println(test.getCount());");

		List<String> lines = new ArrayList<>();
		lines.add("import java.util.List;");
		lines.add("public class Test {private int count = 0;" + "public int getCount() {return this.count;}"
				+ "public void countTo100() {" + "while(this.count <100 )" + "{this.count++;}}" + "}");
		lines.add("Test test = new Test();");
		lines.add("System.out.println(test.getCount());");
		lines.add("test.countTo100();");
		lines.add("System.out.println(test.getCount());");

		List<String> codes = JShellParser.parse(sb.toString());
		assertTrue(lines.equals(codes));
	}

}
