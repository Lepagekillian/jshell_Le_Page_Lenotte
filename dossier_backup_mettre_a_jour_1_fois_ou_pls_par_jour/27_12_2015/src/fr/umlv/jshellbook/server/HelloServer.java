package fr.umlv.jshellbook.server;

import java.nio.file.Path;
import java.util.Objects;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;


public class HelloServer extends AbstractVerticle {
	private final Path workingDirectory;
	
	public HelloServer(Path path){
		this.workingDirectory=Objects.requireNonNull(path);
	}
	
	private void helloRoutine(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "text/html").end("<h1>Hello copinou Vertx 3.0.0 Represent</h1>");
	}
	
	private void postRoutine(RoutingContext routingContext) {
		System.out.println("HELLO post/submit method :)");
		routingContext.response().end();
	}
	
	private void jsRoutine(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "text/html").sendFile("./javascript/communication_server_test.html");
	}
	
	
	
	private void readingDirectoryRoutine(RoutingContext routingContext) {
		routingContext.vertx().fileSystem().readDir(this.workingDirectory.toString(), null);

	}

	private void defaultRoutine(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "text/html").end("D&eacute;fault page");
	}

	private static Handler<AsyncResult<HttpServer>> futureTreatment(
			Future<Void> fut) {
		return result -> {
			if (result.succeeded()) {
				System.out.println("You can work on : http://localhost:8989" );
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		};
	}

	@Override
	
	public void start(Future<Void> fut) {
		Router router = Router.router(this.vertx);
		router.get("/hello").handler(this::helloRoutine);
		router.get("/debugRoom").handler(this::jsRoutine);
		router.get("/default").handler(this::defaultRoutine);
		router.route().handler(StaticHandler.create(this.workingDirectory.toString()).setDirectoryListing(true));
		router.get().handler(this::readingDirectoryRoutine);
		router.post().handler(this::postRoutine);
		this.vertx.createHttpServer().requestHandler(router::accept)
				.listen(8989, futureTreatment(fut));
	}

}
