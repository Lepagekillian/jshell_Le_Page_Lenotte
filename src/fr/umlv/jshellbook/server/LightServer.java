package fr.umlv.jshellbook.server;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

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
		//System.out.println(".*\\.\\(mkdown\\)" );
		System.out.println(routingContext.normalisedPath());
		System.out.println("hello");
		List<String> toTreat = routingContext.vertx().fileSystem()
				.readDirBlocking(this.workingDirectory.toString()+routingContext.normalisedPath());
		routingContext.response().end(toTreat.toString());
		
	
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
		router.get("/*.mkdown").handler(this::readingDirectoryRoutine);
		router.route().handler(StaticHandler.create(this.workingDirectory.toString())
		.setDirectoryListing(true).setIncludeHidden(false));
		router.post().handler(this::postRoutine);
		this.vertx.createHttpServer().requestHandler(router::accept)
				.listen(8989, futureTreatment(fut));
	}

}
