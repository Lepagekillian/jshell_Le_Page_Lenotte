package fr.umlv.jshellbook.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HelloServer extends AbstractVerticle {

	private void helloRoutine(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "text/html").end("<h1>Hello copinou Vertx 3.0.0 Represent</h1>");
	}
	
	private void defaultRoutine(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "text/html").end("D&eacute;fault page");
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
		router.get("/hello").handler(this::helloRoutine);
		router.get().handler(this::defaultRoutine);
		this.vertx.createHttpServer().requestHandler(router::accept)
				.listen(8989, futureTreatment(fut));
	}

}
