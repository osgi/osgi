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

package org.osgi.service.typedevent.propertytypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.typedevent.TypedEventConstants;
import org.osgi.service.typedevent.TypedEventHandler;
import org.osgi.service.typedevent.UntypedEventHandler;
import org.osgi.service.typedevent.annotations.RequireTypedEvent;

/**
 * Component Property Type for the
 * {@link TypedEventConstants#TYPED_EVENT_FILTER} service property of an Event
 * Handler service.
 * <p>
 * This annotation can be used on an {@link TypedEventHandler} or
 * {@link UntypedEventHandler} component to declare the value of the
 * {@link TypedEventConstants#TYPED_EVENT_FILTER} service property.
 * 
 * @see "Component Property Types"
 * @author $Id$
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@RequireTypedEvent
public @interface EventFilter {
	/**
	 * Service property specifying the event filter for a
	 * {@link TypedEventHandler} or {@link UntypedEventHandler} service.
	 * 
	 * @return The event filter.
	 * @see TypedEventConstants#TYPED_EVENT_FILTER
	 */
	String value();
}
