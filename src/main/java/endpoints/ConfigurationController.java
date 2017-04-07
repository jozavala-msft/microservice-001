package endpoints;

import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;
import com.spotify.apollo.route.RouteProvider;
import database.DatabaseHelper;
import endpoints.utils.RequestHelper;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.util.stream.Stream;

@Singleton
public class ConfigurationController implements RouteProvider {

    private File directory;
    private RequestHelper helper;

    @Inject
    public ConfigurationController(@Named("dbDir") File directory, RequestHelper helper) {
        this.helper = helper;
        this.directory = directory;
    }

    @Override
    public Stream<? extends Route<? extends AsyncHandler<?>>> routes() {
        return Stream.of(
            Route.sync("GET", "/configuration/databaseDirectory", context -> Response.of(Status.OK, directory.getPath()))
        );
    }
}
