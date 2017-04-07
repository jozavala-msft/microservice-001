import com.google.inject.Guice;
import com.google.inject.Injector;
import com.spotify.apollo.Environment;
import com.spotify.apollo.httpservice.HttpService;
import com.spotify.apollo.httpservice.LoadingException;
import di.TodoModule;
import endpoints.ConfigurationController;
import endpoints.TodoController;
import endpoints.UsersController;

import java.io.IOException;

public class App {

    static Injector injector;

    public static void main(String[] args) throws LoadingException, IOException {
        injector = Guice.createInjector(new TodoModule());
        HttpService.boot(App::init, "microservice-001", args);
    }

    static void init(Environment environment) {
        environment.routingEngine()
            .registerAutoRoutes(injector.getInstance(TodoController.class))
            .registerAutoRoutes(injector.getInstance(ConfigurationController.class))
            .registerAutoRoutes(injector.getInstance(UsersController.class));
    }
}
