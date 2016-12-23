/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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


package org.osgi.test.cases.subsystem.junit;

import java.util.Collection;
import java.util.Iterator;

import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;

public class PreventResolution implements ResolverHookFactory, ResolverHook {

	public void filterResolvable(Collection<BundleRevision> candidates) {
		for (Iterator<BundleRevision> iCandidates = candidates.iterator(); iCandidates.hasNext();) {
			if (!iCandidates.next().getSymbolicName().startsWith("org.osgi.service.subsystem.region.context.")) {
				iCandidates.remove();
			}
		}
	}

	public void filterSingletonCollisions(BundleCapability singleton,
			Collection<BundleCapability> collisionCandidates) {
		// nothing
	}

	public void filterMatches(BundleRequirement requirement,
			Collection<BundleCapability> candidates) {
		// Nothing
	}

	public void end() {
		// Nothing
	}

	public ResolverHook begin(Collection<BundleRevision> triggers) {
		return this;
	}
	
}
