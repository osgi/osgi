/*
 * Copyright (c) OSGi Alliance (2011, 2012). All Rights Reserved.
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

package org.osgi.service.repository;

import org.osgi.resource.Namespace;

/**
 * Content Capability and Requirement Namespace.
 * 
 * <p>
 * This class defines the names for the attributes and directives for this
 * namespace. All unspecified capability attributes are of type {@code String}
 * and are used as arbitrary matching attributes for the capability. The values
 * associated with the specified directive and attribute keys are of type
 * {@code String}, unless otherwise indicated.
 * 
 * @Immutable
 * @version $Id$
 */
public final class ContentNamespace extends Namespace {

	/**
	 * Namespace name for content capabilities and requirements.
	 * 
	 * <p>
	 * Also, the capability attribute used to specify the unique identifier of
	 * the content. This identifier is the {@code SHA-256} hash of the content.
	 */
	public static final String	CONTENT_NAMESPACE			= "osgi.content";

	/**
	 * The capability attribute that contains the URL to the content.
	 */
	public static final String	CAPABILITY_URL_ATTRIBUTE	= "url";

	/**
	 * The capability attribute that contains the size, in bytes, of the
	 * content. The value of this attribute must be of type {@code Long}.
	 */
	public static final String	CAPABILITY_SIZE_ATTRIBUTE	= "size";

	/**
	 * The capability attribute that defines the IANA MIME Type/Format for this
	 * content.
	 */
	public static final String	CAPABILITY_MIME_ATTRIBUTE	= "mime";

	private ContentNamespace() {
		// empty
	}
}
