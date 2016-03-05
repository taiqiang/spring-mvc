package org.ionnic.common.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ionnic.common.ActionSupport;
import org.ionnic.common.WebException;
import org.ionnic.common.util.WebUtils;
import org.ionnic.common.view.JsonView;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

/**
 * @author apple
 *
 */
public class DefaultExceptionResolver implements HandlerExceptionResolver {

    protected final Log logger = LogFactory.getLog(getClass());

    private String errorView = "error";

    private int statusCode = 0;

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @param status
     * @param ex
     * @return
     */
    protected String getStatusInfo(int status, Exception ex) {
        String message = ex.getMessage();

        return message;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, final Exception ex) {
        if (ex == null) {
            return null;
        }

        ModelAndView mv = new ModelAndView();
        WebException error = null;

        if (!(ex instanceof WebException)) {
            if (ex instanceof NoSuchRequestHandlingMethodException) {
                error = new WebException(404, "Page Not Found");
            } else if (ex instanceof HttpRequestMethodNotSupportedException) {
                error = new WebException(405, "Method Not Allowed");
            } else if (ex instanceof HttpMediaTypeNotSupportedException) {
                error = new WebException(415, "Unsupported Media Type");
            } else if (ex instanceof HttpMediaTypeNotAcceptableException) {
                error = new WebException(406, "Not Acceptable");
            } else if (ex instanceof MissingServletRequestParameterException) {
                error = new WebException(400, "Bad Request");
            } else if (ex instanceof ServletRequestBindingException) {
                error = new WebException(400, "Bad Request");
            } else if (ex instanceof ConversionNotSupportedException) {
                error = new WebException(500, "Internal Server Error");
            } else if (ex instanceof TypeMismatchException) {
                error = new WebException(400, "Bad Request");
            } else if (ex instanceof HttpMessageNotReadableException) {
                error = new WebException(400, "Bad Request");
            } else if (ex instanceof HttpMessageNotWritableException) {
                error = new WebException(400, "Bad Request");
            } else if (ex instanceof MethodArgumentNotValidException) {
                error = new WebException(400, "Bad Request");
            } else if (ex instanceof MissingServletRequestPartException) {
                error = new WebException(400, "Bad Request");
            } else if (ex instanceof BindException) {
                error = new WebException(400, "Bad Request");
            } else if (ex instanceof NoHandlerFoundException) {
                error = new WebException(404, "Page Not Found");
            } else {
                error = new WebException(500, "Internal Server Error");
            }
            error.setData(ex);
        } else {
            error = (WebException) ex;
        }

        mv.addObject(ActionSupport.STATUS, error.getStatus());
        mv.addObject(ActionSupport.STATUS_INFO, error.getStatusInfo());
        mv.addObject(ActionSupport.DATA, error.getData());

        if (WebUtils.hasAnnotation(handler)) {
            mv.setView(new JsonView());
            if (statusCode > 0) {
                response.setStatus(error.getStatus());
            } else {
                response.setStatus(200);
            }
        } else {
            mv.setViewName(errorView);
            response.setStatus(error.getStatus());
        }

        ex.printStackTrace();

        return mv;
    }

    /**
     * @param errorView the errorView to set
     */
    public void setErrorView(String errorView) {
        this.errorView = errorView;
    }

    /**
     * @param statusCode the statusCode to set
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
