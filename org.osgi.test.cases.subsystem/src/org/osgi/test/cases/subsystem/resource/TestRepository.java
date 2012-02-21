/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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
package org.osgi.test.cases.subsystem.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.resource.Capability;
import org.osgi.framework.resource.Namespace;
import org.osgi.framework.resource.Requirement;
import org.osgi.framework.resource.Resource;
import org.osgi.service.repository.Repository;

public class TestRepository implements Repository {
	private final Collection<TestResource> resources;
	public TestRepository(Collection<TestResource> resources) {
		super();
		this.resources = new ArrayList<TestResource>(resources);
	}

	public Collection<Capability> findProviders(Requirement requirement) {
		String filterSpec = requirement.getDirectives().get(
				Namespace.REQUIREMENT_FILTER_DIRECTIVE);
		Filter filter = null;
		if (filterSpec != null) {
			try {
				filter = FrameworkUtil.createFilter(filterSpec);
			} catch (InvalidSyntaxException e) {
				return Collections.emptyList();
			}
		}
		
		Collection<Capability> result = new ArrayList<Capability>();
		for (Resource resource : resources) {
			List<Capability> capabilities = resource.getCapabilities(requirement.getNamespace());
			// not testing mandatory here
			for (Capability capability : capabilities) {
				if (filter == null || filter.matches(capability.getAttributes()))
					result.add(capability);
			}
		}
		return result;
	}

	public Map<Requirement, Collection<Capability>> findProviders(
			Collection<? extends Requirement> requirements) {
		Map<Requirement, Collection<Capability>> result = new HashMap<Requirement, Collection<Capability>>();
		for (Requirement requirement : requirements) {
			result.put(requirement, findProviders(requirement));
		}
		return result;
	}

}
