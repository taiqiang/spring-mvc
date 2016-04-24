package net.breakidea.common.support.view;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import net.breakidea.common.ConfigConstants;
import net.breakidea.common.util.ContextUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.beans.BeansException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.velocity.VelocityToolboxView;

/**
 * @author apple
 *
 */
public final class VelocityView extends VelocityToolboxView implements ConfigConstants, RuntimeConstants {

    private Log logger = LogFactory.getLog(getClass());

    private static String CONFIG_LOCATION = "/WEB-INF/velocity.properties";

    @Override
    protected void doRender( Context context, HttpServletResponse response ) throws Exception {
        Viewport page = new Viewport(this, context);

        StringWriter body = mergeTemplate(getUrl(), context);
        String layoutPath = page.getLayout();

        if (!StringUtils.hasText(layoutPath)) {
            if (logger.isDebugEnabled()) {
                logger.debug("No layoutPath was assigned, response it. url:" + getUrl());
            }
            response.getWriter().write(body.toString());
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Rendering layout [" + layoutPath + "] for url:" + getUrl());
            }

            page.setBody(body);
            mergeTemplate(getTemplate(layoutPath), context, response);
        }
    }

    /**
     * @param templateName
     * @param velocityContext
     * @return
     * @throws Exception
     */
    public StringWriter mergeTemplate( String templateName, Context velocityContext ) throws Exception {
        StringWriter out = new StringWriter();

        if (logger.isDebugEnabled()) {
            logger.debug("Rendering screen template [" + templateName + "]");
        }

        try {
            Template tpl = getTemplate(templateName);
            tpl.merge(velocityContext, out);

            return out;
        } catch (Exception e) {
            logger.error("Rendering screen [" + templateName + "] with error", e);
            throw e;
        }
    }

    @Override
    protected VelocityEngine autodetectVelocityEngine() throws BeansException {
        VelocityEngine velocityEngine;
        setEncoding(CHARSET);
        try {
            Properties prop = PropertiesLoaderUtils.loadProperties(ContextUtils.getResource(CONFIG_LOCATION));
            velocityEngine = new VelocityEngine(prop);
        } catch (IOException e) {
            velocityEngine = new VelocityEngine();
        }

        StringBuilder resolvedPath = new StringBuilder();
        String[] paths = new String[] { "/WEB-INF/views", "/WEB-INF/external" };

        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            Resource resource = ContextUtils.getResource(path);
            try {
                File file = resource.getFile();
                resolvedPath.append(file.getAbsolutePath());
                if (i < paths.length - 1) {
                    resolvedPath.append(',');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 用户目录
        resolvedPath.append("," + System.getProperty("user.home") + "/output");

        velocityEngine.setProperty(VM_LIBRARY, "macro.vm");
        velocityEngine.setProperty(RESOURCE_LOADER, "file");
        velocityEngine.setProperty(FILE_RESOURCE_LOADER_CACHE, "true");
        velocityEngine.setProperty(FILE_RESOURCE_LOADER_PATH, resolvedPath.toString());
        velocityEngine.setProperty(EVENTHANDLER_REFERENCEINSERTION, EscapeReference.class.getName());

        if (logger.isInfoEnabled()) {
            logger.info("set velocityTemplate loader path: " + resolvedPath);
        }
        velocityEngine.loadDirective(BlockDirective.class.getName());

        return velocityEngine;
    }
}
