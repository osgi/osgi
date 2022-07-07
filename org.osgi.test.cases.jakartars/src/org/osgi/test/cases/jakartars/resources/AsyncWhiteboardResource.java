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
package org.osgi.test.cases.jakartars.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;

@Path("whiteboard/async")
public class AsyncWhiteboardResource {

	private final Runnable	preResume;
	private final Runnable postResume;

	public AsyncWhiteboardResource(Runnable preResume, Runnable postResume) {
		this.preResume = preResume;
		this.postResume = postResume;
	}

	@GET
	@Path("{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public void echo(@Suspended AsyncResponse async,
			@PathParam("name") String value) {

		new Thread(() -> {
			try {
				try {
					Thread.sleep(3000);
				} catch (Exception e) {
					preResume.run();
					async.resume(e);
					return;
				}
				preResume.run();
				async.resume(value);
			} finally {
				postResume.run();
			}
		}).start();
	}
}
