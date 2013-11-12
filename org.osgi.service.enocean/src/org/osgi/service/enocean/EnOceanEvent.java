/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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


package org.osgi.service.enocean;

public final class EnOceanEvent {
	
	/**
	 * Main topic for all OSGi dispatched EnOcean messages, imported or exported.
	 */
	public static final String TOPIC_MSG_RECEIVED = "org/osgi/service/enocean/EnOceanEvent/MESSAGE_RECEIVED";
	
	/**
	 * Property key for the {@link EnOceanMessage} object embedded in an event.
	 */
	public static final String PROPERTY_MESSAGE = "enocean.message";
	
	/**
	 * Property key used to tell apart messages that are exported or imported.
	 */
	public static final String PROPERTY_EXPORTED = "enocean.message.is_exported";

}
