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

package org.osgi.service.cdi.reference;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.cdi.annotations.Greedy;
import org.osgi.service.cdi.annotations.Reference;

/**
 * This interface is used at injection points marked with {@link Reference} to
 * indicate that a suitable service is mandatory and that dynamic behavior of
 * services is expected. The result is that the component will not be created
 * unless a service is available and it will not be destroyed as services come
 * and go provided at least one suitable service is available. Applying
 * {@link Greedy} will determine if that service is the "best" service
 * (according to natural service ordering) rather than any suitable one.
 *
 * @param <T> the service argument type.
 *
 * @author $Id$
 */
@ProviderType
public interface DynamicMandatory<T> {

	/**
	 * Get the currently available service.
	 *
	 * @return the currently available service
	 */
	public T get();

}
