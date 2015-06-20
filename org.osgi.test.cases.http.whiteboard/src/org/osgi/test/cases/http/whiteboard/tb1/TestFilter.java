package org.osgi.test.cases.http.whiteboard.tb1;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class TestFilter implements Filter {

	public TestFilter(char c) {
		this.c = c;
	}

	public void destroy() {
		//
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response);

		filterChain.doFilter(request, responseWrapper);

		String output = responseWrapper.toString();

		response.setContentLength(output.length() + 2);

		PrintWriter writer = response.getWriter();
		writer.print(c);
		writer.print(output);
		writer.print(c);
		writer.close();
	}

	public void init(FilterConfig arg0) throws ServletException {
		//
	}

	private final char	c;

	private static class ResponseWrapper extends HttpServletResponseWrapper {

		public ResponseWrapper(HttpServletResponse response) {
			super(response);
			output = new CharArrayWriter();
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			return new PrintWriter(output);
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {

			return new ServletOutputStream() {

				@Override
				public void write(int b) throws IOException {
					output.write(b);
				}

			};
		}

		@Override
		public String toString() {
			return output.toString();
		}

		CharArrayWriter	output;

	}

}
