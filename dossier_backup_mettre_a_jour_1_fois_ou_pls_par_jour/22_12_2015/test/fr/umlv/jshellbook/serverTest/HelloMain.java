
package fr.umlv.jshellbook.serverTest;


import io.vertx.core.Vertx;
import fr.umlv.jshellbook.server.HelloServer;


public class HelloMain  {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new HelloServer());
	}
}
