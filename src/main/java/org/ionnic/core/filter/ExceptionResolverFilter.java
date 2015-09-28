package org.ionnic.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class ExceptionResolverFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("org.ionnic.core.filter.ExceptionResolverFilter");
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}