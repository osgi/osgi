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

import static jakarta.ws.rs.core.HttpHeaders.LAST_EVENT_ID_HEADER;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

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
	public void stream(@HeaderParam(LAST_EVENT_ID_HEADER)
	String lastId, @Context
	SseEventSink sink) throws NoContentException {

		int id;
		boolean hello;
		if (lastId == null) {
			id = -1;
			hello = true;
		} else {
			id = Integer.parseInt(lastId);
			if (id >= 9) {
				throw new NoContentException("No events after " + lastId);
			}
			hello = false;
		}
		
		new Thread(() -> {

			CompletionStage< ? > cs = hello ? sink.send(sse.newEventBuilder()
					.reconnectDelay(500)
					.comment("Hello")
					.data(42)
					.build()) : CompletableFuture.completedFuture(null);

			try {
				for (int i = id + 1; i < 10; i++) {
					Thread.sleep(500);
					cs = cs.thenCombine(sink.send(sse.newEventBuilder()
							.id(String.valueOf(i))
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
