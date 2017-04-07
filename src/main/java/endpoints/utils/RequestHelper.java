package endpoints.utils;

import com.google.gson.Gson;
import com.spotify.apollo.RequestContext;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RequestHelper {

    private Gson gson;

    @Inject
    public RequestHelper(Gson gson) {
        this.gson = gson;
    }

    /**
     * Creates a new request repository
     * @param context
     * @return {@link RequestRepository}
     */
    public RequestRepository fromContext(RequestContext context) {
        return new RequestRepository(context, gson);
    }


}
