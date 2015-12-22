package fr.umlv.jshellbook.server;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import org.pegdown.PegDownProcessor;

import fr.umlv.jshellbook.markdownanalyzer.MarkdownToHTML;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class MarkdownServer extends AbstractVerticle {

	private final Path fileToParse;

	public MarkdownServer(Path fileToParse) {
		this.fileToParse = Objects.requireNonNull(fileToParse);
	}

	private static void markdownRoutine(Router router, Path file)throws IOException {
		MarkdownToHTML work = new MarkdownToHTML(new PegDownProcessor());

		router.route("/").handler(
				routingContext -> {
					HttpServerResponse response = routingContext.response();
						response.putHeader("content-type", "text/html").end(
								work.parse(file));
				});
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
	public void start(Future<Void> fut) throws IOException {
		Router router = Router.router(this.vertx);
		markdownRoutine(router, this.fileToParse);
		this.vertx.createHttpServer().requestHandler(router::accept)
				.listen(8989, futureTreatment(fut));
	}

}
