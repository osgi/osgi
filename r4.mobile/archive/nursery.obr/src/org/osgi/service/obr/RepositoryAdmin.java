/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.service.obr;

import java.net.URL;
import java.util.List;

/**
 * Provides centralized access to the distributed
 * repository.
 * 
 * A repository contains a set of <i>resources</i>. A resource
 * contains a number of fixed attributes (name, version, etc) 
 * and sets of:
 * <ol>
 * 	<li>Capabilities - Capabilities provide a named aspect: a bundle, a display, 
 * memory, etc.</li>
 *  <li>Requirements - A named filter expression. The filter must be satisfied
 *  by one or more Capabilties with the given name. These capabilities can come
 *  from other resources or from the platform. If multiple resources provide the
 *  requested capability, one is selected. (### what algorithm? ###)</li>
 *  <li>Requests - Requests are like requirements, except that a request can be
 *  fullfilled by 0..n resources. This feature can be used to link to resources
 *  that are compatible with the given resource and provide extra functionality. 
 *  For example, a bundle could request all its known fragments. The UI associated
 *  with the repository could list these as optional downloads.</li>
 * 
 * @version $Revision$
 */
public interface RepositoryAdmin {
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
	 */
	Resource[] discoverResources(String filterExpr);

	/**
	 * Create a resolver and try to resolve the given resource.
	 * 
	 * @param resource
	 * @return
	 */
	Resolver resolver();

	
	/**
	 * Add a new repository to the federation. 
	 * 
	 * The url must point to a repository XML file.
	 * 
	 * @param repository
	 * @return
	 * @throws Exception
	 */
	Repository addRepository(URL repository) throws Exception;


	/**
	 * List all the repositories.
	 * 
	 * @return
	 */
	Repository[] listRepositories();

	Resource getResourceById(String string);

}
