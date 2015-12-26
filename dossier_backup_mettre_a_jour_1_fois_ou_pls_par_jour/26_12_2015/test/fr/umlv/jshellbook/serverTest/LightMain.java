
package fr.umlv.jshellbook.serverTest;


import java.nio.file.Paths;

import io.vertx.core.Vertx;
import fr.umlv.jshellbook.server.LightServer;


public class LightMain  {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new LightServer(Paths.get(".")));
	}
}
