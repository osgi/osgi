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
public interface Resolver {
	
	/**
	 * Resolve a set of requirements. A bundle install can be modeled as a top level 
	 * require bundle requirement.
	 * 
	 * @param requirements
	 * 
	 * @return
	 * @throws InterruptedException if the thread that calls this resolve is interrupted.
	 */
	Resolution resolve(Requirement[] requirements) throws InterruptedException;
	Resolution resolve(Requirement[] requirements, ResolutionConfig config) throws InterruptedException;
}
