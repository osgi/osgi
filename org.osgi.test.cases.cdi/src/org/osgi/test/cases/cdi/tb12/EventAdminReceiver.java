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
package org.osgi.test.cases.cdi.tb12;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Bean;
import org.osgi.service.cdi.annotations.BeanPropertyType;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

@ApplicationScoped
@Bean
@Service
@EventAdminReceiver.Props(event_topics = "com/acme/foo")
public class EventAdminReceiver implements EventHandler {

	@BeanPropertyType
	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface Props {
		String[] event_topics();
		String event_filter() default "";
	}

	@Inject
	javax.enterprise.event.Event<Event> eventAdminEvent;

	@Override
	public void handleEvent(Event event) {
		// fire the event into the CDI container asynchronously
		eventAdminEvent.fireAsync(event);
	}

}
