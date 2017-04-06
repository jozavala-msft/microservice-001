package database;

import org.lmdbjava.Dbi;
import org.lmdbjava.Env;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lmdbjava.DbiFlags.MDB_CREATE;
import static org.lmdbjava.Env.create;

public class Database {

    private String name;
    private Dbi<ByteBuffer> dbi;

    public Database(String name, Dbi<ByteBuffer> dbi) {
        this.name = name;
        this.dbi = dbi;
    }

    public static Database createDB(String name) throws IOException {
        File dir = File.createTempFile("temp", Long.toString(System.nanoTime()));
        // We always need an Env. An Env owns a physical on-disk storage file. One
        // Env can store many different databases (ie sorted maps).
        final Env<ByteBuffer> env = create()
            // LMDB also needs to know how large our DB might be. Over-estimating is OK.
            .setMapSize(10_485_760)
            // LMDB also needs to know how many DBs (Dbi) we want to store in this Env.
            .setMaxDbs(1)
            // Now let's open the Env. The same path can be concurrently opened and
            // used in different processes, but do not open the same path twice in
            // the same process at the same time.
            .open(dir);

        // We need a Dbi for each DB. A Dbi roughly equates to a sorted map. The
        // MDB_CREATE flag causes the DB to be created if it doesn't already exist.
        final Dbi<ByteBuffer> dbi = env.openDbi(name, MDB_CREATE);
        return new Database(name, dbi);
    }
}
