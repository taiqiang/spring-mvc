package net.breakidea.common.util;

import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author apple
 *
 */
public abstract class ContextUtils {

    /**
     * @return
     */
    public static WebApplicationContext getApplicationContext() {
        return ContextLoader.getCurrentWebApplicationContext();
    }

    /**
     * @param path
     * @return
     */
    public static Resource getResource( String location ) {
        return getApplicationContext().getResource(location);
    }
}
