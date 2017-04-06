package repositories;

import database.Database;
import models.Todo;
import org.lmdbjava.CursorIterator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.ByteBuffer;
import java.util.*;

@Singleton
public class TodoRepository {

    private Database database;

    @Inject
    public TodoRepository(Database database) {
        this.database = database;
    }

    /**
     * Retrieve all created todos
     * @return {@link List<Todo>}
     */
    public List<Todo> getTodos() {
        List<Todo> todos = new ArrayList<>();
        try (CursorIterator<ByteBuffer> it = database.getIterator()) {
            for (final CursorIterator.KeyVal<ByteBuffer> kv : it.iterable()) {
                todos.add(database.decode(kv.val(), Todo.class));
            }
        }
        return todos;
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
        String uuid = UUID.randomUUID().toString();
        Todo todo = Todo.builder()
            .name(name)
            .description(description)
            .dueDate(dueDate)
            .createdBy(createdBy)
            .uuid(uuid)
            .build();
        database.store(uuid, todo);
        return todo;
    }

    /**
     * Fetches a todo from the database
     * @param uuid
     * @return {@link Optional<Todo>}
     */
    public Optional<Todo> getTodo(String uuid) {
        return database.fetch(uuid, Todo.class);
    }
}
