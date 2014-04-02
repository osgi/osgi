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

package org.osgi.service.repository;

import java.util.Map;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

/**
 * A builder for requirements.
 * 
 * @since 1.1
 */
@ProviderType
public interface RequirementBuilder {
	/**
	 * Add an attribute to the set of attributes.
	 * 
	 * @param name The attribute name.
	 * @param value The attribute value.
	 * @return This requirement builder.
	 */
	RequirementBuilder addAttribute(String name, Object value);

	/**
	 * Add a directive to the set of directives.
	 * 
	 * @param name The directive name.
	 * @param value The directive value.
	 * @return This requirement builder.
	 */
	RequirementBuilder addDirective(String name, String value);

	/**
	 * Replace all attributes with the attributes in the specified map.
	 * 
	 * @param attributes The map of attributes.
	 * @return This requirement builder.
	 */
	RequirementBuilder setAttributes(Map<String, Object> attributes);

	/**
	 * Replace all directives with the directives in the specified map.
	 * 
	 * @param directives The map of directives.
	 * @return This requirement builder.
	 */
	RequirementBuilder setDirectives(Map<String, String> directives);

	/**
	 * Set the {@code Resource}.
	 * 
	 * <p>
	 * A resource is optional. This method will replace any previously set
	 * resource.
	 * 
	 * @param resource The resource.
	 * @return This requirement builder.
	 */
	RequirementBuilder setResource(Resource resource);

	/**
	 * Create a requirement based upon the values set in this requirement
	 * builder.
	 * 
	 * @return A requirement created based upon the values set in this
	 *         requirement builder.
	 */
	Requirement build();

	/**
	 * Create a requirement expression for a requirement based upon the values
	 * set in this requirement builder.
	 * 
	 * @return A requirement expression created for a requirement based upon the
	 *         the values set in this requirement builder.
	 */
	IdentityExpression buildExpression();
}
