
package fr.umlv.jshellbook.serverTest;




import java.nio.file.Paths;

import io.vertx.core.Vertx;
import fr.umlv.jshellbook.server.MarkdownServer;


public class MarkdownMain  {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MarkdownServer(Paths.get("re")));
	}
}
