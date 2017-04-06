package endpoints;

import com.google.gson.Gson;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;
import com.spotify.apollo.route.RouteProvider;
import models.Todo;
import repositories.TodoRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.stream.Stream;

@Singleton
public class TodoController implements RouteProvider {

    private TodoRepository todoRepository;
    private Gson gson;

    @Inject
    public TodoController(TodoRepository todoRepository, Gson gson) {
        this.todoRepository = todoRepository;
        this.gson = gson;
    }

    @Override
    public Stream<? extends Route<? extends AsyncHandler<?>>> routes() {
        return Stream.of(
            Route.sync("GET", "/todos/<uuid>", context ->
                Optional.of(context.pathArgs().get("uuid")).map(todoRepository::getTodo).map(
                    maybeTodo -> maybeTodo.map(foundTodo ->
                        Response.of(Status.OK, foundTodo)
                    ).orElse(Response.forStatus(Status.NOT_FOUND))
                ).orElse(Response.forStatus(Status.BAD_REQUEST))
            ),
            Route.sync("POST", "/todos", context ->
                context.request().payload().map(bsPayload -> {
                    Todo todo = gson.fromJson(bsPayload.utf8(), Todo.class);
                    Todo createdTodo = todoRepository.createTodo(todo.getName(), todo.getDescription(), todo.getDueDate(), todo.getCreatedBy());
                    return Response.of(Status.CREATED, createdTodo);
                }).orElse(Response.forStatus(Status.BAD_REQUEST))
            )
        );
    }
}
