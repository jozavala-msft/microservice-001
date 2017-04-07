package database;

import com.google.gson.Gson;
import org.lmdbjava.CursorIterator;
import org.lmdbjava.Dbi;
import org.lmdbjava.Env;
import org.lmdbjava.Txn;

import java.nio.ByteBuffer;
import java.util.Optional;

import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.lmdbjava.CursorIterator.IteratorType.FORWARD;

public class Database {

    private Env<ByteBuffer> env;
    private Dbi<ByteBuffer> dbi;
    private Gson gson;

    public Database(Dbi<ByteBuffer> dbi, Env<ByteBuffer> env, Gson gson) {
        this.dbi = dbi;
        this.env = env;
        this.gson = gson;
    }

    /**
     * Stores an object
     * @param key
     * @param entity
     * @return The number of bytes left in the key
     */
    public int store(String key, Object entity) {
        String payload = gson.toJson(entity);
        return put(key, payload);
    }

    /**
     * Decode an entity
     * @param value
     * @param clzz
     * @param <T>
     * @return {@link T}
     */
    public <T> T decode(ByteBuffer value, Class<T> clzz) {
        String decoded = DatabaseHelper.decodeToString(value);
        return gson.fromJson(decoded, clzz);
    }

    /**
     * Fetches an object from the database
     * @param key
     * @param <T>
     * @return {@link Optional<T>}
     */
    public <T> Optional<T> fetch(String key, Class<T> clzz) {
        return get(key).map(DatabaseHelper::decodeToString).map(d -> gson.fromJson(d, clzz));
    }

    /**
     * Puts a value/key pair in the database
     * @param key   The key in its string representation
     * @param value The value in its string representation
     * @return The number of bytes left in the key
     */
    private int put(String key, String value) {
        return put(key.getBytes(UTF_8), value.getBytes(UTF_8));
    }

    /**
     * Allocates a byte buffer
     * @param bytes The raw bytes
     * @param size  The allocation size
     * @return {@link ByteBuffer}
     */
    private ByteBuffer allocate(byte[] bytes, int size) {
        final ByteBuffer buffer = allocateDirect(size);
        buffer.put(bytes).flip();
        return buffer;
    }

    /**
     * Puts a value/key pair in the database
     * @param bKey
     * @param bValue
     */
    private int put(byte[] bKey, byte[] bValue) {
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
     * Retrieves data based in a string key
     * @param key The key associated with the value to be retrieved
     * @return {@link Optional<ByteBuffer>}
     */
    private Optional<ByteBuffer> get(String key) {
        return get(key.getBytes(UTF_8));
    }

    /**
     * Retrieves a bytebuffer from the specified key
     * @param bKey
     * @return {@link Optional<ByteBuffer>}
     */
    private Optional<ByteBuffer> get(byte[] bKey) {
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
     * Retrieve a cursor iterator
     * @return {@link Cursor<T>}
     */
    public <T> Cursor<T> getForwardIterator(Class<T> clzz) {
        final Txn<ByteBuffer> txn = env.txnRead();
        CursorIterator<ByteBuffer> cursorIterator = dbi.iterate(txn, FORWARD);
        return new Cursor<>(this, cursorIterator, clzz, txn);
    }


}
