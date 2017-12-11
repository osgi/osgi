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

package org.osgi.service.cdi.event;

import javax.enterprise.context.Destroyed;
import org.osgi.service.cdi.annotations.ComponentScoped;

/**
 * A CDI event delivered with the qualifier
 * {@link Destroyed @Destoyed(ComponentScoped.class)} when the
 * {@link ComponentScoped @ComponentScoped} context has been destroyed.
 *
 * @author $Id$
 */
public interface ComponentProperties {

	/**
	 * The properties that were used to initialize the component converted into
	 * {@code P}, using the converter specification. {@code null} otherwise.
	 *
	 * @return the properties that were used to initialize the component converted
	 *         into {@code P}, using the converter specification. {@code null}
	 *         otherwise
	 */
	public <P> P getProperties();

}
