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

package org.osgi.service.obr.admin;

import org.osgi.service.obr.Part;

/**
 * @deprecated This is proposed API. As a result, this API may never be
 *             published or the final API may change substantially by the time
 *             of final publication. You are cautioned against relying upon this
 *             API.
 */
public class RepositoryChangeEvent {
	private final Part[] addedParts;
	private final Part[] changedParts;
	private final Part[] removedParts;
	private final Repository repository;
	
	public RepositoryChangeEvent(Repository repository, Part[] addedParts, Part[] changedParts, Part[] removedParts) {
		this.repository = repository;
		this.addedParts = protect(addedParts);
		this.changedParts = protect(changedParts);
		this.removedParts = protect(removedParts);
	}
	
	public Part[] getAddedParts() {
		return protect(addedParts);
	}
	
	public Part[] getChangedParts() {
		return protect(changedParts);
	}
	
	public Part[] getRemovedParts() {
		return protect(removedParts);
	}
	
	public Repository getRepository() {
		return repository;
	}
	
	private static final Part[] protect(Part[] parts) {
		Part[] p = new Part[parts.length];
		System.arraycopy(parts, 0, p, 0, p.length);
		return p;
	}
}
