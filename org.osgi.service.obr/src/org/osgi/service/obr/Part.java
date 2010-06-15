/*
 * Copyright (c) OSGi Alliance (2006, 2009). All Rights Reserved.
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

// This document is an experimental draft to enable interoperability
// between bundle repositories. There is currently no commitment to 
// turn this draft into an official specification.  

package org.osgi.service.obr;

import java.util.Map;

/**
 * Parts have capabilities and requirements. All a part's requirements
 * must be satisfied before it can be installed.
 * 
 * Parts have an implied capability to represent itself, for example a 
 * part with a category:name:version of bundle:foo:1.0.0 has an implied
 * capability of bundle:foo:1.0.0
 * 
 * @deprecated This is proposed API. As a result, this API may never be
 *             published or the final API may change substantially by the time
 *             of final publication. You are cautioned against relying upon this
 *             API.
 */
public interface Part {
	final String LICENSE_URL = "license";
	final String DESCRIPTION = "description";
	final String DOCUMENTATION_URL = "documentation";
	final String COPYRIGHT = "copyright";
	final String SYMBOLIC_NAME = "symbolicname";
	final String PRESENTATION_NAME = "presentationname";
	final String ID = "id";
	final String VERSION = "version";
	
	/**
	 * The category of part that this represents. Parts
	 * can have category resolved-bundle, active-bundle, config, etc.
	 * 
	 * The combination of category + name + version must be unique within 
	 * the repository that contains this part.  
	 * @return
	 */
	String getCategory();
	
	/**
	 * The name of the part. The combination of category + name + version 
	 * must be unique within the repository that contains this part.
	 * @return
	 */
	String getName();
	
	/**
	 * The version of the part. The combination of category + name + version 
	 * must be unique within the repository that contains this part.
	 * @return
	 */
	String getVersion();
		
	/**
	 * External properties of the part. This includes 
	 * {@link #DOCUMENTATION_URL}, {@link #COPYRIGHT}, etc.
	 * @return
	 */
	Map getProperties();
	
	/**
	 * Zero or more resources associated with this part
	 * @return
	 */
	Resource[] getResources();
	
	/**
	 * The capabilities provided by this part
	 * @return
	 */
	Capability[] getCapabilities();
	
	/**
	 * The requirements needed to satisfy this part
	 * @return
	 */
	Requirement[] getRequirements();
	
	/**
	 * The repository that provided this part
	 * @return
	 */
	Repository getRepository();
}
