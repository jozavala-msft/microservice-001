package di;

import com.google.inject.AbstractModule;
import database.Database;

import java.io.IOException;

public class TodoModule extends AbstractModule {

    private static final String DB_NAME = "TODO";
    private final Database database;

    public TodoModule() throws IOException {
        this.database = Database.createDB(DB_NAME);
    }

    @Override
    protected void configure() {
        bind(Database.class).toInstance(this.database);
    }
}
