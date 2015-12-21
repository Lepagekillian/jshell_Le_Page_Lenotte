package fr.umlv.jshellbook.jshell;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import jdk.jshell.JShell;
import jdk.jshell.JShell.Builder;

public class JShellParser {
    
    private final Builder builder;
    private final PrintStream printStream;
    private final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    
    private JShellParser() {
	this.printStream = new PrintStream(this.arrayOutputStream);
	this.builder = JShell.builder();
	this.builder.err(this.printStream);
	this.builder.out(this.printStream);
    }


    public String test(final String s){
	try(JShell jShell = this.builder.build()){
	    jShell.eval(s);
	    String resEval = this.arrayOutputStream.toString();
	    this.arrayOutputStream.reset();
	    return resEval;
	}
    }
    
    
    public static void main(String[] args) {
	
	JShellParser parser = new JShellParser();
	String res = parser.test("System.err.println(\"test\");");
	System.out.println(res);
    }
}
