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

import java.util.Set;

/**
 * Metadata for a component defined within a given module context.
 * 
 * @see LocalComponentMetadata
 * @see ServiceReferenceComponentMetadata
 * @see ServiceExportComponentMetadata
 */
public interface ComponentMetadata {
	
	/**
	 * The name of the component.
	 * 
	 * @return component name. The component name may be null if this is an anonymously
	 * defined inner component.
	 */
	String getName();
		
	/**
	 * The names of any components listed in a "depends-on" attribute for this
	 * component.
	 * 
	 * @return an immutable set of component names for components that we have explicitly
	 * declared a dependency on, or an empty set if none.
	 */
	Set getExplicitDependencies();
}
