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
package org.osgi.test.cases.jakartars.extensions;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

public class HeaderStringReplacer extends StringReplacer
		implements ContainerRequestFilter, ContainerResponseFilter {

	public HeaderStringReplacer(String toReplace, String replaceWith) {
		super(toReplace, replaceWith);
	}

	@Override
	public void filter(ContainerRequestContext arg0,
			ContainerResponseContext ctx) throws IOException {
		String header = ctx.getHeaderString("echo");
		ctx.getHeaders().remove("echo");
		ctx.getHeaders().putSingle("echo",
				header.replace(getToReplace(), getReplaceWith()));
	}

	@Override
	public void filter(ContainerRequestContext ctx) throws IOException {
		String header = ctx.getHeaderString("echo");
		ctx.getHeaders().remove("echo");
		ctx.getHeaders().putSingle("echo",
				header.replace(getToReplace(), getReplaceWith()));
	}
}
