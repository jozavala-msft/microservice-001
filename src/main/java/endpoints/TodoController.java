package endpoints;

import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;
import com.spotify.apollo.route.RouteProvider;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.stream.Stream;

@Singleton
public class TodoController implements RouteProvider {

    @Override
    public Stream<? extends Route<? extends AsyncHandler<?>>> routes() {
        return Arrays.asList(
            Route.sync("/todos", "GET", context -> {
                return Response.of(Status.NOT_FOUND, "Not implemented");
            }),
            Route.sync("/todos", "POST", context -> {
                return Response.of(Status.NOT_FOUND, "Not implemented");
            })
        ).stream();
    }
}
