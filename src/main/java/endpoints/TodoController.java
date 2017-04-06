package endpoints;

import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;
import com.spotify.apollo.route.RouteProvider;
import models.Todo;
import repositories.TodoRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Stream;

@Singleton
public class TodoController implements RouteProvider {

    private TodoRepository todoRepository;
    private RequestHelper helper;

    @Inject
    public TodoController(TodoRepository todoRepository, RequestHelper helper) {
        this.todoRepository = todoRepository;
        this.helper = helper;
    }

    @Override
    public Stream<? extends Route<? extends AsyncHandler<?>>> routes() {
        return Stream.of(
            Route.sync("GET", "/todos/<uuid>", context ->
                todoRepository.getTodo(helper.fromContext(context).getPathArg("uuid")).map(
                    foundTodo -> Response.of(Status.OK, foundTodo)
                ).orElse(Response.forStatus(Status.NOT_FOUND))
            ),
            Route.sync("POST", "/todos", context ->
                helper.fromContext(context).fetchJson(Todo.class).map(todo -> {
                    Todo createdTodo = todoRepository.createTodo(todo.getName(), todo.getDescription(), todo.getDueDate(), todo.getCreatedBy());
                    return Response.of(Status.CREATED, createdTodo);
                }).orElse(Response.forStatus(Status.BAD_REQUEST))
            )
        );
    }
}
