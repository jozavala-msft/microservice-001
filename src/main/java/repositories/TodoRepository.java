package repositories;

import org.lmdbjava.Dbi;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TodoRepository {

    private Dbi dbi;

    @Inject
    public TodoRepository(Dbi dbi) {
        this.dbi = dbi;
    }
}
