package fr.umlv.jshellbook.server;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

public class SimpleExample extends AbstractVerticle {
  static class Exercise {
    private final ArrayList<Object> items = new ArrayList<>();
    
    public void addQuestion(String text) {
      Objects.requireNonNull(text);
      this.items.add(text);
    }
    
    public void addAnswer(int lines) {
    	this.items.add(lines);
    }
    
    public String toJSON() {
      return this.items.stream().map(o -> {
        if (o instanceof String) {  // UGLY isn't it ?
            return "{ \"question\": \"" + o + "\" }";
        }
        return "{ \"answer\": " + o + " }";
      }).collect(Collectors.joining(", ", "[", "]"));
    }
  }

  private static Exercise createFakeExercise() {
    Exercise exercise = new Exercise();
    exercise.addQuestion("This a question");
    exercise.addAnswer(3 /*lines*/);
    return exercise;
  }
  
  private final Exercise exercise = createFakeExercise();
  
  @Override
  public void start() {
    Router router = Router.router(this.vertx);
    // route to JSON REST APIs 
    router.get("/exercise/:id").handler(this::getAnExercise);
    // otherwise serve static pages
    router.route().handler(StaticHandler.create());

    this.vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    System.out.println("listen on port 8080");
  }
  
  private void getAnExercise(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    System.out.println("ask for an exercise by id " + id);
    routingContext.response()
       .putHeader("content-type", "application/json")
       .end(this.exercise.toJSON());
  }
  
  public static void main(String[] args) {   
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new SimpleExample());
  }
}
