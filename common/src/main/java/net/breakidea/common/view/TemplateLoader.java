package net.breakidea.common.view;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import net.breakidea.common.util.ContextUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

/**
 * @author apple
 *
 */
public class TemplateLoader implements VelocityConfig, ResourceLoaderAware {

    private static String CONFIG_LOCATION = "/WEB-INF/velocity.properties";

    protected final Log logger = LogFactory.getLog(getClass());

    private int mode = 0;

    private String resourceLoaderPath;

    private ResourceLoader resourceLoader;

    @Override
    public VelocityEngine getVelocityEngine() {
        VelocityEngine engine = null;
        try {
            Properties prop = ContextUtils.getProperties(CONFIG_LOCATION);
            engine = new VelocityEngine(prop);
        } catch (IOException e) {
            engine = new VelocityEngine();
        }

        try {
            StringBuilder resolvedPath = new StringBuilder();
            String[] paths = StringUtils.commaDelimitedListToStringArray(resourceLoaderPath);
            for (int i = 0; i < paths.length; i++) {
                String path = paths[i];
                Resource resource = resourceLoader.getResource(path);
                File file = resource.getFile();
                resolvedPath.append(file.getAbsolutePath());
                if (i < paths.length - 1) {
                    resolvedPath.append(',');
                }
            }

            engine.setProperty("input.encoding", "utf-8");
            engine.setProperty("output.encoding", "utf-8");

            engine.setProperty("resource.loader", "file");
            engine.setProperty("file.resource.loader.cache", "true");
            engine.setProperty("file.resource.loader.path", resolvedPath.toString());
            engine.setProperty("velocimacro.library", "macro.vm");
            engine.setProperty("eventhandler.referenceinsertion.class", EscapeReference.class.getName());

            engine.loadDirective(BlockDirective.NAME);
            engine.init();
        } catch (IOException ex) {
            logger.fatal("Cannot resolve resource loader path [" + resourceLoaderPath + "] to [java.io.File]: using SpringResourceLoader", ex);
        }
        return engine;
    }

    @Override
    public void setResourceLoader( ResourceLoader resourceLoader ) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * @param resourceLoaderPath the resourceLoaderPath to set
     */
    public void setResourceLoaderPath( String resourceLoaderPath ) {
        this.resourceLoaderPath = resourceLoaderPath;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode( int mode ) {
        this.mode = mode;
    }

}
