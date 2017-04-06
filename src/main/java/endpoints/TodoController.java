package endpoints;

import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;
import com.spotify.apollo.route.RouteProvider;
import repositories.TodoRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Stream;

@Singleton
public class TodoController implements RouteProvider {

    private TodoRepository todoRepository;

    @Inject
    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public Stream<? extends Route<? extends AsyncHandler<?>>> routes() {
        return Stream.of(
            Route.sync("/todos", "GET", context -> {
                return Response.of(Status.NOT_FOUND, "Not implemented");
            }),
            Route.sync("/todos", "POST", context -> {
                return Response.of(Status.NOT_FOUND, "Not implemented");
            })
        );
    }
}
