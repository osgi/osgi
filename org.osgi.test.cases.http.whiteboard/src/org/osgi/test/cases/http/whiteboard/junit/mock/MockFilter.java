package org.osgi.test.cases.http.whiteboard.junit.mock;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MockFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (before != null) {
			response.getWriter().write(before);
		}
		chain.doFilter(request, response);
		if (after != null) {
			response.getWriter().write(after);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	public MockFilter after(String after) {
		this.after = after;

		return this;
	}

	public MockFilter around(String around) {
		before(around);
		after(around);

		return this;
	}

	public MockFilter before(String before) {
		this.before = before;

		return this;
	}

	private String	after;
	private String	before;

}
