package fr.umlv.jshellbook.server;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class HelloServer extends AbstractVerticle {
 
  @Override
  public void start(Future<Void> fut) {
    Router router = Router.router(this.vertx);
    router.route("/").handler(routingContext -> {
    	HttpServerResponse response = routingContext.response();
    	response
    	.putHeader("content-type", "text/html")
    	.end("<h1>Hello copinou Vertx 3.0.0 Represent</h1>");
    });
    //router.route().handler(StaticHandler.create());

    this.vertx.createHttpServer().requestHandler(router::accept).listen(
    		config().getInteger("http.port", 8080),
    		result -> {
    			if(result.succeeded()) {
    			    System.out.println("listen on port 8080");
    				fut.complete();
    			} else {
    				fut.fail(result.cause());
    			}
    		}
    		);
  }
  

  
  public static void main(String[] args) {   
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new HelloServer());
  }
}
