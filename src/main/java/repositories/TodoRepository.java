package repositories;

import com.google.gson.Gson;
import database.Database;
import models.Todo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class TodoRepository {

    private Database database;
    private Gson gson;

    @Inject
    public TodoRepository(Database database, Gson gson) {
        this.database = database;
        this.gson = gson;
    }

    /**
     * Stores a todo in the database
     * @param name        The name of the todo
     * @param description Description of the todo
     * @param dueDate     Due date
     * @param createdBy   Created by
     * @return {@link Todo}
     */
    public Todo createTodo(String name, String description, Date dueDate, String createdBy) {
        Todo.TodoBuilder todoBldr = Todo.builder()
            .name(name)
            .description(description)
            .dueDate(dueDate)
            .createdBy(createdBy);
        String uuid = UUID.randomUUID().toString();
        database.put(uuid, gson.toJson(todoBldr.build()));
        return todoBldr.uuid(uuid).build();
    }

    /**
     * Fetches a todo from the database
     * @param uuid
     * @return
     */
    public Optional<Todo> getTodo(String uuid) {
        return database.get(uuid)
            .map(Database::decodeToString)
            .map(value -> gson.fromJson(value, Todo.class));
    }
}
