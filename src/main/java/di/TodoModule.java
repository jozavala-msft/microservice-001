package di;

import com.google.inject.AbstractModule;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;

public class TodoModule extends AbstractModule {

    @Override
    protected void configure() {
        Ignite ignite = Ignition.ignite();
        bind(Ignite.class).toInstance(ignite);
        bind(IgniteCache.class).toInstance(ignite.cache("todo"));
    }
}
