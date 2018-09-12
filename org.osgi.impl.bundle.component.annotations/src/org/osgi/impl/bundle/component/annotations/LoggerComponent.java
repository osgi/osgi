/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
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
package org.osgi.impl.bundle.component.annotations;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.FormatterLogger;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

/**
 *
 *
 */
@Component(name = "testLoggerComponent")
public class LoggerComponent {

	@Activate
	public LoggerComponent(
			@Reference(name = "loggerC", service = LoggerFactory.class) Logger loggerC,
			@Reference(name = "formatterLoggerC", service = LoggerFactory.class) FormatterLogger formatterLoggerC,
			@Reference(name = "listLoggerC", service = LoggerFactory.class) List<ServiceReference<Logger>> listLoggerC,
			@Reference(name = "collectionFormatterLoggerC", service = LoggerFactory.class) Collection<FormatterLogger> collectionFormatterLoggerC) {/**/}

	@Reference(service = LoggerFactory.class)
	private Map.Entry<Map<String,Object>,Logger>						loggerF;

	@Reference(service = LoggerFactory.class)
	private FormatterLogger												formatterLoggerF;

	@Reference(service = LoggerFactory.class)
	private List<ComponentServiceObjects<Logger>>						listLoggerF;

	@Reference(service = LoggerFactory.class)
	private Collection<Map.Entry<Map<String,Object>,FormatterLogger>>	collectionFormatterLoggerF;

	@Reference(service = LoggerFactory.class)
	void bindLogger(Logger loggerM) {/**/}

	@Reference(service = LoggerFactory.class)
	void bindFormatterLogger(FormatterLogger formatterLoggerM) {/**/}
}
