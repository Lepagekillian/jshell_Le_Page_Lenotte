package fr.umlv.jshellbook.server;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
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
		System.out.println(this.workingDirectory);
		List<String> toTreat = fs.readDirBlocking(
				this.workingDirectory.toString() , "[^.]*.mkdown");
		for (String line : toTreat) {
			routingContext.response().write(
					"<a href =\" " + line + "\">" + line);
			routingContext.response().write("<br>");
		}
		routingContext.response().end();
	}


	private static Handler<AsyncResult<HttpServer>> futureTreatment(
			Future<Void> fut) {
		return result -> {
			if (result.succeeded()) {
				System.out.println("You can work on : http://localhost:8989");
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		};
	}

	@Override
	public void start(Future<Void> fut) {
		Router router = Router.router(this.vertx);
		router.get("/jsRoom").handler(this::jsRoutine);
		router.route().handler(this::readingDirectoryRoutine);
		router.post().handler(this::postRoutine);
		this.vertx.createHttpServer().requestHandler(router::accept)
				.listen(8989, futureTreatment(fut));
	}

}
