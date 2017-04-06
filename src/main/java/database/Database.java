package database;

import com.google.common.io.Files;
import com.google.gson.Gson;
import org.lmdbjava.Dbi;
import org.lmdbjava.Env;
import org.lmdbjava.Txn;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Optional;

import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.lmdbjava.DbiFlags.MDB_CREATE;
import static org.lmdbjava.Env.create;

public class Database {

    private Gson gson = new Gson();
    private String name;
    private Dbi<ByteBuffer> dbi;
    private Env<ByteBuffer> env;

    private Database(String name, Dbi<ByteBuffer> dbi, Env<ByteBuffer> env) {
        this.name = name;
        this.dbi = dbi;
        this.env = env;
    }

    public static Database createDB(String name) throws IOException {
        File dir = Files.createTempDir();
        // We always need an Env. An Env owns a physical on-disk storage file. One
        // Env can store many different databases (ie sorted maps).
        final Env<ByteBuffer> env = create()
            // LMDB also needs to know how large our DB might be. Over-estimating is OK.
            .setMapSize(10_485_760)
            // LMDB also needs to know how many DBs (Dbi) we want to store in this Env.
            .setMaxDbs(1)
            .setMaxReaders(10)
            // Now let's open the Env. The same path can be concurrently opened and
            // used in different processes, but do not open the same path twice in
            // the same process at the same time.
            .open(dir);

        // We need a Dbi for each DB. A Dbi roughly equates to a sorted map. The
        // MDB_CREATE flag causes the DB to be created if it doesn't already exist.
        final Dbi<ByteBuffer> dbi = env.openDbi(name, MDB_CREATE);
        return new Database(name, dbi, env);
    }

    /**
     * Stores an object
     * @param key
     * @param entity
     * @return
     */
    public int store(String key, Object entity) {
        String payload = gson.toJson(entity);
        return put(key, payload);
    }

    /**
     * Fetches an object from the database
     * @param key
     * @param <T>
     * @return
     */
    public <T> Optional<T> fetch(String key, Class<T> clzz) {
        return get(key).map(Database::decodeToString).map(d -> gson.fromJson(d, clzz));
    }

    /**
     * Puts a value/key pair in the database
     * @param key   The key in its string representation
     * @param value The value in its string representation
     */
    public int put(String key, String value) {
        return put(key.getBytes(UTF_8), value.getBytes(UTF_8));
    }

    public ByteBuffer allocate(byte[] bytes, int size) {
        final ByteBuffer buffer = allocateDirect(size);
        buffer.put(bytes).flip();
        return buffer;
    }

    /**
     * Puts a value/key pair in the database
     * @param bKey
     * @param bValue
     */
    public int put(byte[] bKey, byte[] bValue) {
        // We want to store some data, so we will need a direct ByteBuffer.
        // Note that LMDB keys cannot exceed maxKeySize bytes (511 bytes by default).
        // Values can be larger.
        final ByteBuffer key = allocate(bKey, env.getMaxKeySize());
        final ByteBuffer val = allocate(bValue, 700);
        final int valSize = val.remaining();

        // Now store it. Dbi.put() internally begins and commits a transaction (Txn).
        dbi.put(key, val);
        return valSize;
    }

    /**
     *
     * @param key
     * @return
     */
    public Optional<ByteBuffer> get(String key) {
        return get(key.getBytes(UTF_8));
    }

    /**
     * Retrieves a bytebuffer from the specified key
     * @param bKey
     * @return
     */
    public Optional<ByteBuffer> get(byte[] bKey) {
        ByteBuffer found;
        final ByteBuffer key = allocate(bKey, env.getMaxKeySize());
        try (Txn<ByteBuffer> txn = env.txnRead()) {
            found = dbi.get(txn, key);
            // The fetchedVal is read-only and points to LMDB memory
            //final ByteBuffer fetchedVal = txn.val();
            //return fetchedVal;
        }
        return Optional.ofNullable(found);
    }

    /**
     * Util method to transform a buffer to a string
     * @param buffer
     * @return
     */
    public static String decodeToString(ByteBuffer buffer) {
        return UTF_8.decode(buffer).toString();
    }
}
