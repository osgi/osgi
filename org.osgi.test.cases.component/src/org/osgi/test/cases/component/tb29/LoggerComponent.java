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


package org.osgi.test.cases.component.tb29;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.osgi.service.component.ComponentConstants;
import org.osgi.service.log.FormatterLogger;
import org.osgi.service.log.Logger;
import org.osgi.test.cases.component.service.ObjectProvider1;

public class LoggerComponent implements ObjectProvider1<Logger> {
	private static final String	name	= LoggerComponent.class.getName();
	private Map<String,Object>	properties;
	private Logger				loggerF;
	private FormatterLogger		formatterLoggerF;
	private Logger				loggerM;
	private FormatterLogger		formatterLoggerM;
	boolean						bindLoggerInvoked			= false;
	boolean						bindFormatterLoggerInvoked	= false;

	public LoggerComponent() {
		throw new RuntimeException("called default constructor");
	}

	public LoggerComponent(Logger loggerC, FormatterLogger formatterLoggerC) {
		assertThat(loggerC).as("Logger constructor param").isNotNull();
		assertThat(formatterLoggerC).as("FormatterLogger constructor param")
				.isNotNull();
		assertThat(loggerC.getName()).as("Logger constructor param name")
				.isEqualTo(name);
		assertThat(formatterLoggerC.getName())
				.as("FormatterLogger constructor param name")
				.isEqualTo(name);
	}


	void bindLogger(Logger loggerM) {
		bindLoggerInvoked = true;
		this.loggerM = loggerM;
	}

	void bindFormatterLogger(FormatterLogger formatterLoggerM) {
		bindFormatterLoggerInvoked = true;
		this.formatterLoggerM = formatterLoggerM;
	}

	void activate(Map<String,Object> p) {
		properties = p;
		System.out.printf("activate: %s[%X]\n",
				properties.get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
		assertThat(loggerF).as("Logger field").isNotNull();
		assertThat(formatterLoggerF).as("FormatterLogger field").isNotNull();
		assertThat(loggerF.getName()).as("Logger field name").isEqualTo(name);
		assertThat(formatterLoggerF.getName()).as("FormatterLogger field name")
				.isEqualTo(name);

		assertThat(bindLoggerInvoked).as("bindLogger method invoked").isTrue();
		assertThat(bindFormatterLoggerInvoked)
				.as("bindFormatterLogger method invoked")
				.isTrue();

		assertThat(loggerM).as("Logger bind param").isNotNull();
		assertThat(formatterLoggerM).as("FormatterLogger bind param")
				.isNotNull();
		assertThat(loggerM.getName()).as("Logger bind param name")
				.isEqualTo(name);
		assertThat(formatterLoggerM.getName())
				.as("FormatterLogger bind param name")
				.isEqualTo(name);
	}

	void deactivate() {}

	public Logger get1() {
		return loggerF;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}


}
