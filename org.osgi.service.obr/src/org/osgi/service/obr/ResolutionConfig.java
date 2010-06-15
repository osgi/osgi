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
 * A ResolutionConfig defines external parameters that can be set by
 * a client wishing to perform a resolution.
 * 
 * This includes concepts such as:
 * 
 * <ul>
 * <li>Depth - how far to traverse the requirements graph. This is useful
 * for debug and analysis situations where you only need a top level view
 * of the resolution.</li>
 * <li>Non Local Constraints - In OSGi this implies uses validation, but 
 * can be mapped to other concepts for other dependency types.</li>
 * <li>Repository Path - a path to use to find a resolution different from
 * the default path defined in {@link RepositoryAdmin}.</li>
 * <li>Timeout - how long to search for a result. Some resolution problems
 * can take a <em>very</em> long time and it is useful to signify up front
 * that the resolver should not wait for ever to find a solution</li>
 * </ul>
 * 
 * @version $Id$
 * @deprecated This is proposed API. As a result, this API may never be
 *             published or the final API may change substantially by the time
 *             of final publication. You are cautioned against relying upon this
 *             API.
 */
public class ResolutionConfig {
	public static final int DEPTH_ZERO = 0;
	
	public static final int DEPTH_INFINITE = -1;
		
    private int depth;
	private boolean checkNonLocalConstraints;	
	private String repositoryPath;
	private int timeout;
	
	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public boolean isCheckNonLocalConstraints() {
		return checkNonLocalConstraints;
	}
	
	public void setCheckNonLocalConstraints(boolean checkNonLocalConstraints) {
		this.checkNonLocalConstraints = checkNonLocalConstraints;
	}
	
	public String getRepositoryPath() {
		return repositoryPath;
	}
	
	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}		
}
