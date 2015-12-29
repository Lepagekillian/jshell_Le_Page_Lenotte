package fr.umlv.jshellbook.server;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class LightServer extends AbstractVerticle {
	private final Path workingDirectory;

	public LightServer(Path path) {
		this.workingDirectory = Objects.requireNonNull(path);
	}

	private void postRoutine(RoutingContext routingContext) {
		System.out.println("HELLO post/submit method :)");
		routingContext.response().end();
	}

	private void jsRoutine(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "text/html")
				.sendFile("./javascript/communication_server_test.html");
	}

	private void readingDirectoryRoutine(RoutingContext routingContext) {

		FileSystem fs = routingContext.vertx().fileSystem();
		routingContext.response().setChunked(true);
		routingContext.response().putHeader("content-type", "text/html");
		List<String> toTreat = fs.readDirBlocking(this.workingDirectory.toString(), "[^.]*.mkdown");
		
		List<JsonObject> jsonObjects = new ArrayList<>();
		for (String line : toTreat) {
			jsonObjects.add(LightServer.makeExoJson(line));
		}
		routingContext.response().write(Json.encode(jsonObjects));
		routingContext.response().end();
	}

	private static Handler<AsyncResult<HttpServer>> futureTreatment(Future<Void> fut) {
		return result -> {
			if (result.succeeded()) {
				System.out.println("You can work on : http://localhost:8989/");
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		};
	}

	private static JsonObject makeExoJson(String line) {
		JsonObject jsonObject = new JsonObject();
		List<String> lineSplit = Arrays.asList(line.split("/|\\\\"));
		jsonObject.put("full", line);
		String id = lineSplit.get(lineSplit.size() - 1);
		id = id.substring(0, id.indexOf('.'));
		jsonObject.put("id", id);

		return jsonObject;
	}

	@Override
	public void start(Future<Void> fut) {
		Router router = Router.router(this.vertx);
		router.get("/jsRoom").handler(this::jsRoutine);
		router.route().handler(this::readingDirectoryRoutine);
		router.post().handler(this::postRoutine);
		this.vertx.createHttpServer().requestHandler(router::accept).listen(8989, futureTreatment(fut));
	}

}
