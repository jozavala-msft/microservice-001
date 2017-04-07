package endpoints;

import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;
import com.spotify.apollo.route.RouteProvider;
import endpoints.utils.RequestHelper;
import models.User;
import repositories.UsersRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Stream;

@Singleton
public class UsersController implements RouteProvider {

    private RequestHelper helper;
    private UsersRepository repository;

    @Inject
    public UsersController(RequestHelper helper, UsersRepository repository) {
        this.helper = helper;
        this.repository = repository;
    }

    @Override
    public Stream<? extends Route<? extends AsyncHandler<?>>> routes() {
        return Stream.of(
            Route.sync("GET", "/users/<username>", context->
                repository.getUser(helper.fromContext(context).getPathArg("username")).map(
                    user -> Response.of(Status.OK, user)
                ).orElse(Response.forStatus(Status.NOT_FOUND))
            ),
            Route.sync("POST", "/users", context -> helper.fromContext(context).fetchJson(User.class).map(user ->
                repository.createUser(user.getUsername(), user.getPassword()).map(createdUser ->
                    Response.of(Status.CREATED, createdUser)
                ).orElse(Response.forStatus(Status.CONFLICT))
            ).orElse(Response.forStatus(Status.BAD_REQUEST)))
        );
    }
}
