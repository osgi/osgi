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

package org.osgi.service.log;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * An operation that accepts a {@link Logger} argument and produces no result.
 * <p>
 * This is a functional interface and can be used as the assignment target for a
 * lambda expression or method reference.
 * 
 * @param <E> The type of the exception that may be thrown.
 * @ThreadSafe
 * @since 1.4
 * @author $Id$
 */
@ConsumerType
@FunctionalInterface
public interface LoggerConsumer<E extends Exception> {
	/**
	 * Perform this operation on the specified {@link Logger}.
	 * 
	 * @param l The {@link Logger} input to this operation.
	 * @throws E An exception thrown by the operation.
	 */
	void accept(Logger l) throws E;
}
