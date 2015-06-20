package org.osgi.test.cases.http.whiteboard.tb1;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet {

	private static final long	serialVersionUID	= 1366474865750815948L;

	public TestServlet(String result) {
		this(result, null, null);
	}

	public TestServlet(String result, Integer errorCode) {
		this(result, errorCode, null);
	}

	public TestServlet(String result, Integer errorCode, ServletException servletException) {
		this.result = result;
		this.errorCode = errorCode;
		this.servletException = servletException;
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (servletException != null) {
			throw servletException;
		}
		else if (errorCode != null) {
			response.sendError(errorCode.intValue());
		}
		else {
			response.getWriter().print(result);
		}
	}

	private Integer				errorCode;
	private String				result;
	private ServletException	servletException;

}
