package net.breakidea.common.view;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import net.breakidea.common.ConfigConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.velocity.VelocityToolboxView;

/**
 * @author apple
 *
 */
public final class TemplateView extends VelocityToolboxView implements ConfigConstants {

    private Log logger = LogFactory.getLog(getClass());

    @Override
    public boolean checkResource( Locale locale ) throws Exception {
        return true;
    }

    @Override
    protected void doRender( Context context, HttpServletResponse response ) throws Exception {
        Writer out = response.getWriter();
        Viewport page = new Viewport(this, context);

        StringWriter body = mergeTemplate(getUrl(), context);
        String layoutPath = page.getLayout();

        if (!StringUtils.hasText(layoutPath)) {
            if (logger.isDebugEnabled()) {
                logger.debug("No layoutPath was assigned, response it. url:" + getUrl());
            }
            out.write(body.toString());
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
        Template tpl = getTemplate(templateName);
        tpl.merge(velocityContext, out);
        return out;
    }
}
