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
package org.osgi.service.blueprint.reflect;

/**
 * Metadata describing a property to be injected. Properties are defined
 * following JavaBeans conventions.
 */
public interface PropertyInjectionMetadata {
	
	/**
	 * The name of the property to be injected, following JavaBeans conventions.
	 * 
	 * @return the property name.
	 */
	String getName();
	
	/**
	 * The value to inject the property with.
	 * 
	 * @return the property value.
	 */
	Value getValue();
}
