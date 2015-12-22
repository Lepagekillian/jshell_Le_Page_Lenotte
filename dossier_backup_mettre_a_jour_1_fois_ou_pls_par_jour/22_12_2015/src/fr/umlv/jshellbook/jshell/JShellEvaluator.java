package fr.umlv.jshellbook.jshell;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;

import jdk.jshell.JShell;
import jdk.jshell.JShell.Builder;

public class JShellEvaluator {
    
    private final Builder builder;
    private final PrintStream printStream;
    private final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    
    public JShellEvaluator() {
	this.printStream = new PrintStream(this.arrayOutputStream);
	this.builder = JShell.builder();
	this.builder.err(this.printStream);
	this.builder.out(this.printStream);
    }


    public String eval(final String codeToEval){
	Objects.requireNonNull(codeToEval);
	try(JShell jShell = this.builder.build()){
	    jShell.eval(codeToEval);
	    String resEval = this.arrayOutputStream.toString();
	    this.arrayOutputStream.reset();
	    return resEval;
	}
    }
    
    
    public static void main(String[] args) {
	
	JShellEvaluator parser = new JShellEvaluator();
	String res = parser.eval("System.err.println(\"test\");");
	System.out.println(res);
    }
}
