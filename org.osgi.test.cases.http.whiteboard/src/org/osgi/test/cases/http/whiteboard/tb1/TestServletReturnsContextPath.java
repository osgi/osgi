package org.osgi.test.cases.http.whiteboard.tb1;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServletReturnsContextPath extends HttpServlet {

	private static final long	serialVersionUID	= 1366474865750815948L;

	public TestServletReturnsContextPath() {
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.getWriter().print(request.getContextPath());
	}

}
