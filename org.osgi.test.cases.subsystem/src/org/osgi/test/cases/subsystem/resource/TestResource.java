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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.repository.RepositoryContent;

public class TestResource implements Resource, RepositoryContent {
	public static volatile boolean failContent = false;
	private final Map<String, List<Capability>> capabilities;
	private final Map<String, List<Requirement>> requirements;
	private final URL content;
	private final String toString;

	public TestResource(Map<String, ? extends Object> identityAttrs, Map<String, String> identityDirs, URL content) {
		this.requirements = Collections.emptyMap();
		this.capabilities = new HashMap<String, List<Capability>>(); 
		this.capabilities.put(
				IdentityNamespace.IDENTITY_NAMESPACE,
				new ArrayList<Capability>(Arrays.asList(new TestCapability(
						IdentityNamespace.IDENTITY_NAMESPACE, identityAttrs,
						identityDirs, this))));
		this.content = content;
		this.toString = getIdentity(this.capabilities);
	}
	public TestResource(Bundle bundle, URL content) {
		BundleRevision revision = bundle.adapt(BundleRevision.class);
		this.capabilities = createCapabilities(revision.getCapabilities(null));
		this.requirements = createRequirements(revision.getRequirements(null));
		this.content = content;
		this.toString = getIdentity(this.capabilities);
	}

	private static String getIdentity(Map<String, List<Capability>> capabilities) {
		List<Capability> identities = capabilities.get(IdentityNamespace.IDENTITY_NAMESPACE);
		if (identities == null)
			return "NO IDENTITY";
		try {
			Capability identity = identities.iterator().next();
			return identity.toString();
		} catch (NoSuchElementException e) {
			return "NO IDENTITY";
		}
	}
	private Map<String, List<Capability>> createCapabilities(
			List<Capability> capabilityList) {
		Map <String, List<Capability>> result = new HashMap<String, List<Capability>>();
		for (Capability capability : capabilityList) {
			List<Capability> capabilities = result.get(capability.getNamespace());
			if (capabilities == null) {
				capabilities = new ArrayList<Capability>();
				result.put(capability.getNamespace(), capabilities);
			}
			capabilities.add(new TestCapability(capability, this));
		}
		return result;
	}

	private Map<String, List<Requirement>> createRequirements(
			List<Requirement> requirementList) {
		Map <String, List<Requirement>> result = new HashMap<String, List<Requirement>>();
		for (Requirement requirement : requirementList) {
			List<Requirement> requirements = result.get(requirement.getNamespace());
			if (requirements == null) {
				requirements = new ArrayList<Requirement>();
				result.put(requirement.getNamespace(), requirements);
			}
			requirements.add(new TestRequirement(requirement, this));
		}
		return result;
	}

	public List<Capability> getCapabilities(String namespace) {
		List<Capability> result;
		if (namespace == null) {
			result = new ArrayList<Capability>();
			for (List<Capability> list : capabilities.values()) {
				result.addAll(list);
			}
		} else {
			result = capabilities.get(namespace);
		}
		if (result == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(result);
	}

	public List<Requirement> getRequirements(String namespace) {
		List<Requirement> result;
		if (namespace == null) {
			result = new ArrayList<Requirement>();
			for (List<Requirement> list : requirements.values()) {
				result.addAll(list);
			}
		} else {
			result = requirements.get(namespace);
		}
		if (result == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(result);
	}

	public String toString() {
		return toString;
	}

	static String toString(String namespace, Map<String, String> directives, Map<String, ?> attributes) {
		return namespace + toString(attributes, false) + toString(directives, true);
	}

	static <V> String toString(Map<String, V> map, boolean directives) {
		if (map.size() == 0)
			return "";
		String assignment = directives ? ":=" : "=";
		Set<Entry<String, V>> set = map.entrySet();
		StringBuffer sb = new StringBuffer();
		for (Entry<String, V> entry : set) {
			sb.append("; ");
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof List) {
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) value;
				if (list.size() == 0)
					continue;
				Object component = list.get(0);
				String className = component.getClass().getName();
				String type = className.substring(className.lastIndexOf('.') + 1);
				sb.append(key).append(':').append("List<").append(type).append(">").append(assignment).append('"'); //$NON-NLS-1$ //$NON-NLS-2$
				for (Object object : list)
					sb.append(object).append(',');
				sb.setLength(sb.length() - 1);
				sb.append('"');
			} else {
				String type = ""; //$NON-NLS-1$
				if (!(value instanceof String)) {
					String className = value.getClass().getName();
					type = ":" + className.substring(className.lastIndexOf('.') + 1); //$NON-NLS-1$
				}
				sb.append(key).append(type).append(assignment).append('"').append(value).append('"');
			}
		}
		return sb.toString();
	}

	static class TestCapability implements Capability {
		private final String namespace;
		private final Map<String, String> directives;
		private final Map<String, Object> attributes;
		private final TestResource resource;

		TestCapability(String namespace, Map<String, ? extends Object> attrs, Map<String, String> dirs, TestResource resource) {
			this.namespace = namespace;
			if (dirs == null)
				this.directives = Collections.emptyMap();
			else 
				this.directives = Collections.unmodifiableMap(new HashMap<String, String>(dirs));
			if (attrs == null)
				this.attributes = Collections.emptyMap();
			else
				this.attributes = Collections.unmodifiableMap(new HashMap<String, Object>(attrs));
			this.resource = resource;
		}

		TestCapability(Capability capability, TestResource resource) {
			namespace = capability.getNamespace();
			directives = Collections.unmodifiableMap(new HashMap<String, String>(capability.getDirectives()));
			attributes = Collections.unmodifiableMap(new HashMap<String, Object>(capability.getAttributes()));
			this.resource = resource;
		}

		public String getNamespace() {
			return namespace;
		}

		public Map<String, String> getDirectives() {
			return directives;
		}

		public Map<String, Object> getAttributes() {
			return attributes;
		}

		public Resource getResource() {
			return resource;
		}

		public String toString() {
			return TestResource.toString(namespace, directives, attributes);
		}
	}

	static class TestRequirement implements Requirement {
		private final String namespace;
		private final Map<String, String> directives;
		private final Map<String, Object> attributes;
		private final TestResource resource;

		TestRequirement(Requirement requirement, TestResource resource) {
			namespace = requirement.getNamespace();
			directives = Collections.unmodifiableMap(new HashMap<String, String>(requirement.getDirectives()));
			attributes = Collections.unmodifiableMap(new HashMap<String, Object>(requirement.getAttributes()));
			this.resource = resource;
		}

		public String getNamespace() {
			return namespace;
		}

		public Map<String, String> getDirectives() {
			return directives;
		}

		public Map<String, Object> getAttributes() {
			return attributes;
		}

		public Resource getResource() {
			return resource;
		}
		public String toString() {
			return TestResource.toString(namespace, directives, attributes);
		}
	}

	public InputStream getContent() {
		try {
			if (failContent) {
				return new InputStream() {
					@Override
					public int read() throws IOException {
						throw new IOException("Testing failed stream.");
					}
				};
			}
			return content.openStream();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
