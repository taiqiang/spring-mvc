package net.breakidea.common.view;

import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.breakidea.common.ConfigConstants;
import net.breakidea.common.support.DigestSupport;
import net.breakidea.common.util.ContextUtils;
import net.breakidea.common.util.WebUtils;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.ViewToolContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author apple
 *
 */
public class Viewport implements ConfigConstants {

    public static final String BLANK = new String();

    public static final String CONTEXT_NAME = "page";

    public static final String SCREEN_KEY = "body";

    public static final String TEMPLATE_EXT = ".vm";

    public static final String HTML_EXT = ".html";

    public static final String PREFIX_NAME = Viewport.class.getName();

    private static WebApplicationContext application = ContextUtils.getApplicationContext();

    /**
     * @param data
     * @return
     */
    public static String encrypt( String data ) {
        return DigestSupport.encrypt(data);
    }

    /**
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String encrypt( String data, String key ) throws Exception {
        return DigestSupport.encrypt(data, key);
    }

    /**
     * @param code
     * @return
     */
    public static String getConfig( String code ) {
        return application.getMessage(code, null, null, null);
    }

    /**
     * @param code
     * @return
     */
    public static String getConfig( String code, String defaultValue ) {
        return application.getMessage(code, null, defaultValue, null);
    }

    /**
     * @return
     */
    public static String getGuid() {
        return DigestSupport.getGuid();
    }

    /**
     * @return
     */
    public static String getTokenHeader() {
        return WebUtils.HEADER_NAME;
    }

    /**
     * @return
     */
    public static String getTokenName() {
        return WebUtils.PARAMETER_NAME;
    }

    /**
     * @param urlName
     * @param param
     * @return
     */
    public static String getURI( String urlName ) {
        return urlName;
    }

    /**
     * For the default, render the screen first.
     */
    private boolean isRenderLayout = false;

    private VelocityView velocityView = null;

    private String layoutName = null;

    private ViewToolContext context = null;

    /**
     * @param view
     * @param context
     */
    public Viewport( VelocityView view, Context context ) {
        this.context = (ViewToolContext) context;
        this.velocityView = view;

        this.context.put(SCREEN_KEY, BLANK);
        this.context.put(CONTEXT_NAME, this);
    }

    /**
     * @return
     */
    public String getLayout() {
        return layoutName;
    }

    /**
     * @return
     */
    public String getPageId() {
        return velocityView.getBeanName();
    }

    /**
     * @return
     */
    public String getToken() {
        HttpServletRequest request = context.getRequest();
        return WebUtils.getSessionToken(request);
    }

    /**
     * @return
     */
    public boolean isRenderLayout() {
        return isRenderLayout;
    }

    /**
     * @param templateName
     * @return
     */
    public boolean isTemplateExist( String templateName ) {
        try {
            context.getVelocityEngine().getTemplate(templateName, CHARSET);
            context.hashCode();

            return true;
        } catch (Exception e) {
        }

        return false;
    }

    /**
     * @param templateName
     * @return
     */
    public StringWriter renderTpl( String templateName ) {
        if (!templateName.endsWith(HTML_EXT)) {
            templateName = templateName.concat(HTML_EXT);
        }

        return renderTpl(templateName, null);
    }

    /**
     * @param templateName
     * @param varMap
     * @param safeMode
     * @return
     */
    public StringWriter renderTpl( String templateName, Map<String, Object> varMap ) {
        StringWriter writer = new StringWriter();

        try {
            if (!templateName.endsWith(TEMPLATE_EXT) && !templateName.endsWith(HTML_EXT)) {
                templateName = templateName.concat(TEMPLATE_EXT);
            }

            if (varMap != null) {
                varMap.putAll(context.getToolbox());
                varMap.put(CONTEXT_NAME, this);
            }

            VelocityContext velocityContext = new VelocityContext(varMap);
            writer = velocityView.mergeTemplate(templateName, velocityContext);
        } catch (Exception e) {
            writer.write("<!-- BAD TEMPLATE: [" + templateName + "] -->");
        }

        return writer;
    }

    /**
     * @param body
     */
    public void setBody( StringWriter body ) {
        isRenderLayout = true;
        context.put(SCREEN_KEY, body);
    }

    /**
     * @return
     */
    public String setLayout( String layoutName ) {
        if (StringUtils.hasText(layoutName)) {
            this.layoutName = layoutName + TEMPLATE_EXT;
        } else {
            this.layoutName = null;
        }
        return BLANK;
    }

    @Override
    public String toString() {
        return BLANK;
    }

}
