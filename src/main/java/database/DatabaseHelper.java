package database;

import org.lmdbjava.Dbi;
import org.lmdbjava.Env;

import java.io.File;
import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.lmdbjava.DbiFlags.MDB_CREATE;
import static org.lmdbjava.Env.create;

public class DatabaseHelper {

    /**
     * Creates an environment object based in the current database
     * @return {@link Env<ByteBuffer>}
     */
    public static Env<ByteBuffer> createEnvironment(File dir) {
        return create()
            // LMDB also needs to know how large our DB might be. Over-estimating is OK.
            .setMapSize(10_485_760)
            // LMDB also needs to know how many DBs (Dbi) we want to store in this Env.
            .setMaxDbs(1)
            .setMaxReaders(10)
            .setMaxDbs(10)
            // Now let's open the Env. The same path can be concurrently opened and
            // used in different processes, but do not open the same path twice in
            // the same process at the same time.
            .open(dir);
    }

    /**
     * Gets or create a database
     * @param env
     * @param name
     * @return {@link Dbi<ByteBuffer>}
     */
    public static Dbi<ByteBuffer> openDatabase(Env<ByteBuffer> env, String name) {
        return env.openDbi(name, MDB_CREATE);
    }

    /**
     * Util method to transform a buffer to a string
     * @param buffer
     * @return The decoded string from byte buffer
     */
    public static String decodeToString(ByteBuffer buffer) {
        return UTF_8.decode(buffer).toString();
    }

}
