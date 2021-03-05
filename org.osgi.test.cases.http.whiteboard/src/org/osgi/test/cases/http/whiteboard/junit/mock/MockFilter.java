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
