package net.breakidea.common.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
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
     * @return
     * @throws IOException
     */
    public static Properties getProperties( String location ) throws IOException {
        return PropertiesLoaderUtils.loadProperties(ContextUtils.getResource(location));
    }

    /**
     * @param path
     * @return
     */
    public static Resource getResource( String location ) {
        return getApplicationContext().getResource(location);
    }
}
