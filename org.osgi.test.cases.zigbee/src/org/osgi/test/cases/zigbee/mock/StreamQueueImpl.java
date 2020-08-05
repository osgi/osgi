/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.test.cases.zigbee.mock;

import java.util.LinkedList;

public class StreamQueueImpl<T> implements StreamQueue<T> {

	private final Object	lock	= new Object();

	LinkedList<T>			queue	= new LinkedList<>();

	public void add(T element) {
		if (element == null) {
			throw new NullPointerException("Adding null object is not allowed.");
		}

		synchronized (queue) {
			queue.add(element);
			queue.notify();
		}
	}

	public T poll(long timeout) throws InterruptedException {
		synchronized (lock) {
			if (queue.size() == 0) {
				synchronized (queue) {
					queue.wait(timeout);
				}
				if (queue.size() == 0) {
					// timeout
					return null;
				}
			}
			return queue.removeLast();
		}
	}
}
