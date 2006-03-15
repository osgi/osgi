/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2006). All Rights Reserved.
 *
 * This document is an experimental draft to enable interoperability
 * between bundle repositories. There is currently no plan to 
 * turn this draft into an official specification.  
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
package org.osgi.service.obr;

import java.net.URI;
import java.util.Map;

import org.osgi.framework.Version;

/**
 * A resource is an abstraction of a downloadable thing, like a bundle.
 * 
 * Resources have capabilities and requirements. All a resource's 
 * requirements must be satisfied before it can be installed.
 *
 * @version $Revision$
 */
public interface Resource {
	final String LICENSE_URI = "license";
	final String DESCRIPTION = "description";
	final String DOCUMENTATION_URI = "documentation";
	final String COPYRIGHT = "copyright";
	final String SOURCE_URI = "source";
	final String SYMBOLIC_NAME = "symbolicname";
	final String PRESENTATION_NAME = "presentationname";
	final String ID = "id";
	final String VERSION = "version";
	final String URI = "uri";
	final String SIZE = "size";
	
	final static String [] KEYS = { DESCRIPTION, SIZE, ID, LICENSE_URI, DOCUMENTATION_URI, COPYRIGHT, SOURCE_URI, PRESENTATION_NAME, SYMBOLIC_NAME, VERSION, URI };
	
	// get readable name
	
	Map getProperties();
	String getSymbolicName();
	String getPresentationName();
	Version getVersion();
	String getId();
	URI getURI();
	Requirement [] getRequirements();
	Requirement [] getRequests();
	Requirement [] getExtends();
	Capability [] getCapabilities();
	String [] getCategories();
	Repository getRepository();
}
