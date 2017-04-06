import com.google.inject.Guice;
import com.google.inject.Injector;
import com.spotify.apollo.Environment;
import com.spotify.apollo.httpservice.HttpService;
import com.spotify.apollo.httpservice.LoadingException;
import di.TodoModule;
import endpoints.TodoController;

public class App {

    static Injector injector = Guice.createInjector(new TodoModule());

    public static void main(String[] args) throws LoadingException {
        HttpService.boot(App::init, "microservice-001", args);
    }

    static void init(Environment environment) {
        environment.routingEngine()
            .registerAutoRoutes(injector.getInstance(TodoController.class));
    }
}
