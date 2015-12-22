package fr.umlv.jshellbook.jshell;

import static org.junit.Assert.*;

import org.junit.Test;

public class JShellEvaluatorTest {

    @SuppressWarnings("static-method")
    @Test
    public void testEval() {
	JShellEvaluator parser = new JShellEvaluator();
	String res = parser.eval("System.out.println(\"test\");");
	assertEquals("test\n", res);
    }

}
