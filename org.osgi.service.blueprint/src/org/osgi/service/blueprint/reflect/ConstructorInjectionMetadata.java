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

import java.util.List;

/**
 * Metadata describing how to instantiate a component instance by
 * invoking one of its constructors.
 */
public interface ConstructorInjectionMetadata {
	
	/**
	 * The parameter specifications that determine which constructor to invoke
	 * and what arguments to pass to it.
	 * 
	 * @return an immutable list of ParameterSpecification, or an empty list if the
	 * default constructor is to be invoked. The list is ordered by ascending parameter index.
	 * I.e., the first parameter is first in the list, and so on.
	 */
	List /*ParameterSpecification*/ getParameterSpecifications();

}
