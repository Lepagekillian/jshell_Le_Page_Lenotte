package fr.umlv.jshellbook.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.umlv.jshellbook.jshell.JShellEvaluator;
import fr.umlv.jshellbook.jshell.JShellParser;
import fr.umlv.jshellbook.markdownanalyzer.MarkdownToHTML;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class LightServer extends AbstractVerticle {
	private final Path workingDirectory;

	/**
	 * Construct a server which will observe into a directory and makes some
	 * action depending of the events
	 * @author Le Page
	 * @param path the path of the directory to watch give as a Path
	 */
	public LightServer(Path path) {
		this.workingDirectory = Objects.requireNonNull(path);
	}

	/* Tools methods */

	private void initializeRouter(Router router) {
		router.get("/").handler(this::listLinksRoutine);
		router.get("/:id").handler(this::getDemandExerciseRoutine);		
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
		String body = routingContext.getBodyAsString();
		String res ;
		try(JShellEvaluator evaluator = new JShellEvaluator()){
			 res = evaluator.evalSnippets(JShellParser.parseToSnippets(body));
		} catch (IOException e) {
			res = "Unable to evaluation the code";
		}
		routingContext.response().setChunked(true).putHeader("content-type", "text/html").write(res);
		routingContext.response().end();
	}

	/*
	 * We got more than 8 lines on that method because
	 * we could not really delegates more for other
	 * function. That would made the code kind of hard
	 * to read.
	 * */
	private void getDemandExerciseRoutine(RoutingContext context) {
		String id = context.request().getParam("id");
		Path path = Paths.get(this.workingDirectory.toString(),id+".mkdown");
		HttpServerResponse rep = context.response().setChunked(true).putHeader("content-type", "text/html");
		if(Files.exists(path)){
			MarkdownToHTML markdownToHTML = new MarkdownToHTML();
			try {
				rep.write(markdownToHTML.parse(path));
			} catch (IOException e) {
				rep.write("Unable to load "+id+".mkdow");
			}
		}
		else{
			rep.write("Excercice with id "+id+" no found");
		}
		rep.end();
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
