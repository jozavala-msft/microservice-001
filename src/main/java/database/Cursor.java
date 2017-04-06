package database;

import org.lmdbjava.CursorIterator;
import org.lmdbjava.Txn;

import java.nio.ByteBuffer;
import java.util.Iterator;

public class Cursor<T> implements Iterator<T>, AutoCloseable {

    private Database database;
    private CursorIterator<ByteBuffer> backend;
    private Class<T> clzz;
    private Txn<ByteBuffer> txn;

    public Cursor(Database database, CursorIterator<ByteBuffer> backend, Class<T> clzz, Txn<ByteBuffer> txn) {
        this.backend = backend;
        this.database = database;
        this.clzz = clzz;
        this.txn = txn;
    }

    @Override
    public void close() throws Exception {
        this.backend.close();
        this.txn.close();
    }

    @Override
    public boolean hasNext() {
        return this.backend.hasNext();
    }

    @Override
    public T next() {
        CursorIterator.KeyVal<ByteBuffer> buffer = this.backend.next();
        return database.decode(buffer.val(), clzz);
    }

    public Iterable<T> iterable() {
        return () -> Cursor.this;
    }
}
