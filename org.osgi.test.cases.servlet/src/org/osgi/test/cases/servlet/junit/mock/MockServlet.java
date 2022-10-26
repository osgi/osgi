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
package org.osgi.test.cases.servlet.junit.mock;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
		this.code = Integer.valueOf(code);
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
