package fr.umlv.jshellbook.jshell;

import static org.junit.Assert.*;


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
	public void testIfElse()  {
		StringBuilder sb = new StringBuilder();
		sb.append("if(true) ");
		sb.append("{System.out.println(\"fasle\");}");
		sb.append("else if(true) ");
		sb.append("{System.out.println(\"youhou\");}");
		sb.append(" esle ");
		sb.append("{System.out.println(\"true\");}");
		List<String> lines = new ArrayList<>();
		lines.add(sb.toString());
		assertTrue(lines.equals(JShellParser.parse(sb.toString())));
		
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
	@Test
	@SuppressWarnings("static-method")
	public  void testSwitch(){
		StringBuilder sb = new StringBuilder();
		sb.append("switch (1) {");
		sb.append("case 1:{");
		sb.append("System.out.println(\"1\");");
		sb.append("break;");
		sb.append("}");
		sb.append("default:");
		sb.append("System.out.println(\"break\");");
		sb.append("break;");
		sb.append("}");
		
		List<String> lines = new ArrayList<>();
		lines.add(sb.toString());
		
		assertTrue(lines.equals(JShellParser.parse(sb.toString())));
		
	}

}
