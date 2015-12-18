/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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

package org.osgi.util.pushstream;

import java.util.concurrent.BlockingQueue;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * A {@link PushbackPolicy} is used to calculate how much back pressure to apply
 * based on the current buffer. The {@link PushbackPolicy} will be called after
 * an event has been queued, and the returned value will be used as back
 * pressure.
 * 
 * @see PushbackPolicyOption
 * 
 *
 * @param <T> The type of the data
 * @param <U> The type of the queue
 */
@ConsumerType
@FunctionalInterface
public interface PushbackPolicy<T, U extends BlockingQueue<PushEvent<? extends T>>> {
	
	/**
	 * Given the current state of the queue, determine the level of back
	 * pressure that should be applied
	 * 
	 * @param queue
	 * @return a back pressure value in nanoseconds
	 * @throws Exception
	 */
	public long pushback(U queue) throws Exception;
	
}
