/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.servlet.tb1;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class TestFilter implements Filter {

	public TestFilter(char c) {
		this.c = c;
	}

	@Override
	public void destroy() {
		//
	}

	@Override
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

	@Override
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
				public boolean isReady() {
					return true;
				}

				@Override
				public void write(int b) throws IOException {
					output.write(b);
				}

				@Override
				public void setWriteListener(WriteListener arg0) {
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
