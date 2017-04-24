package org.osgi.test.cases.http.whiteboard.junit.mock;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockServlet extends HttpServlet {

	private static final long serialVersionUID = 1720520918005054191L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (content != null) {
			response.getWriter().write(content);
		}
		if (code != null) {
			response.sendError(code, errorMessage);
		}
		if (exception != null) {
			if (exception instanceof IOException) {
				throw (IOException) exception;
			}
			throw (ServletException) exception;
		}
	}

	public MockServlet content(String content) {
		this.content = content;

		return this;
	}

	public MockServlet error(int code, String errorMessage) {
		this.code = new Integer(code);
		this.errorMessage = errorMessage;

		return this;
	}

	public MockServlet exception(Exception exception) {
		if (!(exception instanceof ServletException) &&
				!(exception instanceof IOException)) {
			this.exception = new ServletException(exception);
		}

		this.exception = exception;

		return this;
	}

	private Integer	code;
	private String	content;
	private String	errorMessage;
	private Exception exception;

}
