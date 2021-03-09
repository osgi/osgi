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

package org.osgi.service.event.propertytypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.annotations.RequireEventAdmin;

/**
 * Component Property Type for the {@link EventConstants#EVENT_FILTER} service
 * property of an {@link EventHandler} service.
 * <p>
 * This annotation can be used on an {@link EventHandler} component to declare
 * the value of the {@link EventConstants#EVENT_FILTER} service property.
 * 
 * @see "Component Property Types"
 * @author $Id$
 * @since 1.4
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@RequireEventAdmin
public @interface EventFilter {
	/**
	 * Service property specifying the {@code Event} filter to an
	 * {@link EventHandler} service.
	 * 
	 * @return The event filter.
	 * @see EventConstants#EVENT_FILTER
	 */
	String value();
}
