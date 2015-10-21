package org.ionnic.config.view;import java.io.StringWriter;import java.util.Map;import javax.servlet.http.HttpServletRequest;import org.apache.velocity.VelocityContext;import org.apache.velocity.app.VelocityEngine;import org.apache.velocity.exception.ParseErrorException;import org.apache.velocity.tools.view.ViewToolContext;import org.ionnic.config.Security;import org.springframework.util.Assert;/** * @author apple * */public class PageTool {	private ViewToolContext context;	/**	 * @param name	 * @param data	 * @return	 */	public String exec(String content, Map<String, Object> data) {		StringWriter writer = new StringWriter();		VelocityEngine engine = context.getVelocityEngine();		try {			data.putAll(context.getToolbox());			if (engine.evaluate(new VelocityContext(data), writer, "PageTool.eval()", content)) {				return "" + writer;			}		} catch (ParseErrorException e) {			// noop		}		return "<!-- ERROR -->";	}	public String getToken() {		HttpServletRequest request = context.getRequest();		return Security.generateToken(request);	}	/**	 * 请求级别的初始化函数	 *	 * @param obj	 * @throws Exception	 */	public void init(Object object) {		Assert.isInstanceOf(ViewToolContext.class, object, "");		context = (ViewToolContext) object;	}}