/*
 * Copyright (c) OSGi Alliance (2000, 2008). All Rights Reserved.
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
package org.osgi.service.blueprint.context;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * Event property names used in EventAdmin events published for a 
 * module context.
 *
 */
public interface ModuleContextEventConstants {

	/**
	 * The version property defining the bundle on whose behalf a module context
	 * event has been issued.
	 * 
	 * @see Version
	 */
	public static final String BUNDLE_VERSION = "bundle.version";
	
	/**
	 * The extender bundle property defining the extender bundle processing the
	 * module context for which an event has been issued.
	 * 
	 * @see Bundle
	 */
	public static final String EXTENDER_BUNDLE = "extender.bundle";
	
	/**
	 * The extender bundle id property defining the id of the extender bundle
	 * processing the module context for which an event has been issued. 
	 */
	public static final String EXTENDER_ID = "extender.bundle.id";
	
	/**
	 * The extender bundle symbolic name property defining the symbolic name of 
	 * the extender bundle processing the module context for which an event 
	 * has been issued.
	 */
	public static final String EXTENDER_SYMBOLICNAME = "extender.bundle.symbolicName";
	
}
