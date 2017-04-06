package endpoints;

import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;
import com.spotify.apollo.route.RouteProvider;
import database.Database;
import endpoints.utils.RequestHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Stream;

@Singleton
public class ConfigurationController implements RouteProvider {

    private Database database;
    private RequestHelper helper;

    @Inject
    public ConfigurationController(Database database, RequestHelper helper) {
        this.helper = helper;
        this.database = database;
    }

    @Override
    public Stream<? extends Route<? extends AsyncHandler<?>>> routes() {
        return Stream.of(
            Route.sync("GET", "/configuration/filename", context -> Response.of(Status.OK, database.getDirectoryPath()))
        );
    }
}
