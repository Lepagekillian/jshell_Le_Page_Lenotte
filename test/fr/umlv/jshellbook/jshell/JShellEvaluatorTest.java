package fr.umlv.jshellbook.jshell;

import static org.junit.Assert.*;

import org.junit.Test;

public class JShellEvaluatorTest {

    
    @Test(expected=NullPointerException.class)
    public void testNull(){
	JShellEvaluator shellEvaluator  = new JShellEvaluator();
	shellEvaluator.eval(null);
    }
    
    
    @Test
    public void testEval() {
	JShellEvaluator parser = new JShellEvaluator();
	String res = parser.eval("System.out.println(\"test\");");
	assertEquals("test\n", res);
    }

}
