/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.jaxrs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

@Path("whiteboard/stream")
public class SseResource {

	@Context
	Sse						sse;

	private final MediaType	type;

	public SseResource(MediaType type) {
		this.type = type;
	}

	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void stream(@Context SseEventSink sink) {
		new Thread(() -> {

			CompletionStage< ? > cs = CompletableFuture.completedFuture(null);

			try {
				for (int i = 0; i < 10; i++) {
					Thread.sleep(500);
					cs = cs.thenCombine(sink.send(sse.newEventBuilder()
							.data(i)
							.mediaType(type)
							.build()), (a, b) -> null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				cs = cs.thenCombine(
						sink.send(sse.newEvent("error", e.getMessage())),
						(a, b) -> null);
			}
			cs.whenComplete((a, b) -> sink.close());
		}).start();
	}
}
