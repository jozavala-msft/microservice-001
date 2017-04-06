package repositories;

import database.Database;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TodoRepository {

    private Database database;

    @Inject
    public TodoRepository(Database database) {
        this.database = database;
    }
}
