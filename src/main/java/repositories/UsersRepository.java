package repositories;

import database.Database;
import models.User;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class UsersRepository {

    private Database database;

    @Inject
    public UsersRepository(@Named("users") Database database) {
        this.database = database;
    }

    /**
     * Perform a login
     * @param username
     * @param password
     * @return {@link Optional<User>}
     */
    public Optional<User> login(String username, String password) {
        return database.fetch(username + ":" + password, User.class);
    }

    /**
     * Create a new user
     * @param username
     * @param password
     * @return
     */
    public Optional<User> createUser(String username, String password) {
        String uuid = UUID.randomUUID().toString();
        User user = User.builder().uuid(uuid).username(username).password(password).build();
        boolean bRet = database.store(username, user, true);
        if(bRet) {
            database.store(username + ":" + password, username, true);
        }
        return bRet ? Optional.of(user) : Optional.empty();
    }

    /**
     * Gets a user by its username
     * @param username
     * @return
     */
    public Optional<User> getUser(String username) {
        return database.fetch(username, User.class);
    }
}
