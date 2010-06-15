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

/**
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Id$
 * @deprecated This is proposed API. As a result, this API may never be
 *             published or the final API may change substantially by the time
 *             of final publication. You are cautioned against relying upon this
 *             API.
 */
public interface Resolution {
	ResolutionConfig getInitialConfig();
	
	Requirement[] getInitialRequirements();
	
	/**
	 * Equivalent to testing: 
	 * 
	 * getInitialConfig().getDepth() == RepositoryConfig.DEPTH_INFINITE 
	 *   && 
	 * getUnsatisfiedRequirements().length == 0
	 * 
	 * @return
	 */
	boolean isComplete();
	
	/**
	 * Checks if all repositories that provide parts
	 * for this resolution are unchanged. If a repository has been
	 * updated then it is possible to rerun this resolution
	 * which may end up with a different result. 
	 * 
	 * @return
	 */
	boolean isStale();
	
	Requirement[] getUnsatisfiedRequirements();

	Part[] getRequiredParts();
	
	Part[] getOptionalParts();
	
	/**
	 * Parts may need to be installed in certain orders to 
	 * satisfy resolution constraints.
	 * 
	 * @return
	 */
	int getPartLoops();
	
	/**
	 * Get the parts in the that they need to be installed to
	 * satisfy resolution constraints
	 * 
	 * @return
	 */
	Part[] getPartLoop(int i);

	/**
	 * Find the requirements that brought in a given part.
	 * 
	 * @param part
	 * @return
	 */
	Requirement[] getConsumers(Part part);
	
	/**
	 * Find the parts that satisfy a given requirement.
	 * 
	 * @param requirement
	 * @return
	 */
	Part[] getProviders(Requirement requirement);
	
	/**
	 * Attempt to deploy the parts referenced by this resolution. If the 
	 * deployment fails for any reason then the implementation will make a best effort
	 * attempt to undeploy the parts that had so far been deployed.
	 *  
	 * The actual meaning of what it means to install depends on the environment
	 * that this OBR service is deployed within:
	 * 
	 * <ul>
	 * <li>In an OSGi framework this might mean to install and start the various 
	 * bundles.</li>
	 * <li>In and OSGi development environment this might mean to download and 
	 * add the various resources to the classpath.</li>
	 * </ul>
	 * 
	 * @throws Exception if the deployment fails for any reason
	 */
	void deploy() throws Exception;	
}
