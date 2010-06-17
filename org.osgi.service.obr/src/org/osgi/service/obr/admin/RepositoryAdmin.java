/*
 * Copyright (c) OSGi Alliance (2006). All Rights Reserved.
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

package org.osgi.service.obr.admin;

import org.osgi.service.obr.Part;

/**
 * Provides centralized access to the distributed repository.
 * 
 * A repository contains a set of <i>parts</i>. A part contains a number
 * of fixed attributes (name, version, etc) and sets of:
 * <ol>
 * <li>Capabilities - Capabilities provide a named aspect: a bundle, a display,
 * memory, etc.</li>
 * <li>Requirements - A named filter expression. The filter must be satisfied by
 * one or more Capabilties with the given name. These capabilities can come from
 * other resources or from the platform. If multiple resources provide the
 * requested capability, one is selected. (### what algorithm? ###)</li>
 * <li>Resources - A physical artifact that can be downloaded via URL and installed
 * into a framework or other target</li>
 * </ol>
 * 
 * Parts are resolved by a {@link Resolver} relative to a repository path which are 
 * defined by an ordered list of repositories to search for capabilities that match 
 * requirements. If a solution can be found in an earlier repository then this will 
 * win over an equivalent part in a later repository.
 * 
 * A repository path consists of an ordered list of repository names with an 
 * optional wild card at the end to say search known repositories, for example:
 * 
 * repo-path= local,team,organisation,*
 * 
 * Implies search for a resolution in my local repository, followed by a shared 
 * team repository, followed by the corporate repository and finally look at any 
 * other configured repository with no implied preference.
 * 
 * @version $Id$
 * @deprecated This is proposed API. As a result, this API may never be
 *             published or the final API may change substantially by the time
 *             of final publication. You are cautioned against relying upon this
 *             API.
 */
public interface RepositoryAdmin {
	public static final char PATH_SEP = ',';
	
	public static final char WILD_CARD = '*';
	
	/**
	 * Discover any resources that match the given filter.
	 * 
	 * This is not a detailed search, but a first scan of applicable 
	 * resources. 
	 * 
	 * ### Checking the capabilities of the filters is not
	 * possible because that requires a new construct in the 
	 * filter. 
	 * 
	 * The filter expression can assert any of the main headers
	 * of the resource. The attributes that can be checked are:
	 * 
	 * <ol>
	 * 	<li>name</li>
	 * 	<li>version (uses filter matching rules)</li>
	 * 	<li>description</li>
	 * 	<li>category</li>
	 * 	<li>copyright</li>
	 * 	<li>license</li>
	 * 	<li>source</li>
	 * </ol>
	 * 
	 * @param filterExpr A standard OSGi filter
	 * @return List of resources matching the filters.
	 * @throws IllegalArgumentException If the filter expression is invalid.
	 */
	Part[] discoverParts(String filterExpr);
	
	/**
	 * List all the repositories.
	 * 
	 * @return
	 */
	Repository[] listRepositories();
	
	Part getPart(String partId);
	
	Part getPart(String partId, String repositoryPath);
	
	String getDefaultRepositoryPath();
	
	void setDefaultRepositoryPath(String repositoryPath);	
}
