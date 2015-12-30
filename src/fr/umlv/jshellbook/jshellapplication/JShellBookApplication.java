package fr.umlv.jshellbook.jshellapplication;



import java.nio.file.Paths;

import io.vertx.core.Vertx;
import fr.umlv.jshellbook.server.LightServer;

public class JShellBookApplication {
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();	
		vertx.deployVerticle(new LightServer(Paths.get(System.getProperty("user.dir"))));
	}
}
