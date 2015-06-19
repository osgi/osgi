/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package org.osgi.service.metatype;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Provides access to metatypes. This interface can be implemented on a Managed
 * Service or Managed Service Factory as well as registered as a service. When
 * registered as a service, it must be registered with a
 * {@link #METATYPE_FACTORY_PID} or {@link #METATYPE_PID} service property (or
 * both). Any PID mentioned in either of these factories must be a valid
 * argument to the {@link #getObjectClassDefinition(String, String)} method.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ConsumerType
public interface MetaTypeProvider {

	/**
	 * Service property to signal that this service has
	 * {@link ObjectClassDefinition} objects for the given PIDs. The type of
	 * this service property is {@code String+}.
	 * 
	 * @since 1.2
	 */
	String	METATYPE_PID			= "metatype.pid";

	/**
	 * Service property to signal that this service has
	 * {@link ObjectClassDefinition} objects for the given factory PIDs. The
	 * type of this service property is {@code String+}.
	 * 
	 * @since 1.2
	 */
	String	METATYPE_FACTORY_PID	= "metatype.factory.pid";

	/**
	 * Returns an object class definition for the specified id localized to the
	 * specified locale.
	 * 
	 * <p>
	 * The locale parameter must be a name that consists of {@code language}[
	 * "_" {@code country}[ "_" {@code variation}] ] as is customary in the
	 * {@code Locale} class. This {@code Locale} class is not used because
	 * certain profiles do not contain it.
	 * 
	 * @param id The ID of the requested object class. This can be a pid or
	 *        factory pid returned by getPids or getFactoryPids.
	 * @param locale The locale of the definition or {@code null} for default
	 *        locale.
	 * @return A {@code ObjectClassDefinition} object.
	 * @throws IllegalArgumentException If the id or locale arguments are not
	 *         valid
	 */
	public ObjectClassDefinition getObjectClassDefinition(String id, String locale);

	/**
	 * Return a list of available locales.
	 * 
	 * The results must be names that consists of language [ _ country [ _
	 * variation ]] as is customary in the {@code Locale} class.
	 * 
	 * @return An array of locale strings or {@code null} if there is no locale
	 *         specific localization can be found.
	 * 
	 */
	public String[] getLocales();
}
