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

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
