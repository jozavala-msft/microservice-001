package di;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import database.Database;
import database.DatabaseHelper;
import org.lmdbjava.Dbi;
import org.lmdbjava.Env;

import java.io.File;
import java.nio.ByteBuffer;

public class TodoModule extends AbstractModule {

    @Override
    protected void configure() {
        File file = Files.createTempDir();
        Gson gson = new Gson();
        Env<ByteBuffer> env = DatabaseHelper.createEnvironment(file);
        //
        Dbi<ByteBuffer> todosDbi = DatabaseHelper.openDatabase(env, "todos");
        Database todosDB = new Database(todosDbi, env, gson);
        bind(Database.class).annotatedWith(Names.named("todos")).toInstance(todosDB);
        //
        Dbi<ByteBuffer> usersDbi = DatabaseHelper.openDatabase(env, "users");
        Database usersDb = new Database(usersDbi, env, gson);
        bind(Database.class).annotatedWith(Names.named("users")).toInstance(usersDb);
        //
        bind(Gson.class).toInstance(gson);
        //
        bind(File.class).annotatedWith(Names.named("dbDir")).toInstance(file);
    }
}
