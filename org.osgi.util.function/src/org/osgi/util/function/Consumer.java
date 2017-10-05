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

package org.osgi.util.function;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * A function that accepts a single argument and produces no result.
 * <p>
 * This is a functional interface and can be used as the assignment target for a
 * lambda expression or method reference.
 * 
 * @param <T> The type of the function input.
 * @ThreadSafe
 * @since 1.1
 * @author $Id$
 */
@ConsumerType
@FunctionalInterface
public interface Consumer<T> {
	/**
	 * Applies this function to the specified argument.
	 * 
	 * @param t The input to this function.
	 * @throws Exception An exception thrown by the method.
	 */
	void accept(T t) throws Exception;
}
