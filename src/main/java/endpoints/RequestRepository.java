package endpoints;


import com.google.gson.Gson;
import com.spotify.apollo.RequestContext;

import java.util.Optional;

class RequestRepository {

    private RequestContext context;
    private Gson gson;

    public RequestRepository(RequestContext context, Gson gson) {
        this.context = context;
        this.gson = gson;
    }

    /**
     * Get a path arg from the request
     * @param name
     * @return
     */
    public String getPathArg(String name) {
        return context.pathArgs().get("uuid");
    }

    /**
     * Fetches an object from the repository
     * @param <T>
     * @return {@link T}
     */
    public <T> Optional<T> fetchJson(Class<T> clzz) {
        return context.request().payload().map(bsPayload -> gson.fromJson(bsPayload.utf8(), clzz));
    }
}
