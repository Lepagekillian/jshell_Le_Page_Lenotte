package fr.umlv.jshellbook.server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class LightServer extends AbstractVerticle {
	private final Path workingDirectory;

	/**
	 * Construct a server which will observe into a directory and makes some
	 * action depending of the events
	 * 
	 * @param path
	 */
	public LightServer(Path path) {
		this.workingDirectory = Objects.requireNonNull(path);
	}

	/* Tools methods */

	private void initializeRouter(Router router) {
		router.get("/").handler(this::listLinksRoutine);
		router.get("/:id").handler(this::helloIdRoutine);
		router.route().handler(this::readingDirectoryRoutine);
		router.post().handler(this::postRoutine);
	}

	private StringBuilder linksIntoThePage(FileSystem fs) {
		StringBuilder allLinks = new StringBuilder();
		List<String> toTreat = fs.readDirBlocking(
				this.workingDirectory.toString(), "[^.]*.mkdown");
		List<JsonObject> jsonObjects = new ArrayList<>();
		for (String line : toTreat) {
			jsonObjects.add(LightServer.makeExoJson(line));
		}
		linkConstructor(allLinks, jsonObjects);
		return allLinks;
	}

	private static void linkConstructor(StringBuilder allLinks,
			List<JsonObject> jsonObjects) {
		for (JsonObject joo : jsonObjects) {
			allLinks.append("<a href = \"http://localhost:8989/")
					.append(joo.getString("id")).append("\">")
					.append(joo.getString("id")).append("<br>");
		}
	}

	private static JsonObject makeExoJson(String line) {
		JsonObject jsonObject = new JsonObject();
		Path path = Paths.get(line);
		if (Files.exists(path)) {
			jsonObject.put("full", line);
			String id = path.getFileName().toString();
			id = id.substring(0, id.indexOf('.'));
			jsonObject.put("id", id);
		}
		return jsonObject;
	}

	/* Routine's method to use with our Server */

	private void postRoutine(RoutingContext routingContext) {
		System.out.println("HELLO post/submit method :)");
		routingContext.response().end();
	}

	private void helloIdRoutine(RoutingContext routingContext) {
		String myId = routingContext.request().getParam("id");
		routingContext.response().end("<h1>Hello from : " + myId + "</h1>");
	}

	private void listLinksRoutine(RoutingContext routingContext) {
		routingContext.response().setChunked(true);
		FileSystem fs = routingContext.vertx().fileSystem();
		StringBuilder allLinks = linksIntoThePage(fs);
		routingContext.response().write(allLinks.toString());
		routingContext.response().end();
	}

	private void readingDirectoryRoutine(RoutingContext routingContext) {
		routingContext.response().setChunked(true);
		routingContext.response().sendFile("webroot/index.html");

	}

	private static Handler<AsyncResult<HttpServer>> futureTreatment(
			Future<Void> fut) {
		return result -> {
			if (result.succeeded()) {
				System.out.println("You can work on : http://localhost:8989/");
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		};
	}

	@Override
	public void start(Future<Void> fut) {
		Router router = Router.router(this.vertx);
		initializeRouter(router);
		this.vertx.createHttpServer().requestHandler(router::accept)
				.listen(8989, futureTreatment(fut));
	}

}
