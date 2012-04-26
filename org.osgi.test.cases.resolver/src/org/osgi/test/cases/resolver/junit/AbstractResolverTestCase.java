/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.resolver.junit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.resource.Capability;
import org.osgi.resource.Namespace;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.HostedCapability;
import org.osgi.service.resolver.ResolutionException;
import org.osgi.service.resolver.ResolveContext;
import org.osgi.service.resolver.Resolver;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public abstract class AbstractResolverTestCase extends DefaultTestBundleControl {

	protected static final String TEST_NAMESPACE = "org.osgi.test.cases.resolver";

	protected Resolver getResolverService() {
		final Resolver res = getService(Resolver.class);
		assertNotNull(res);
		return res;
	}

	protected Map<Resource, List<Wire>> shouldResolve(
			final TestResolveContext context) {
		final Resolver resolver = getResolverService();
		try {
			final Map<Resource, List<Wire>> result = resolver.resolve(context);
			assertNotNull(result);

			context.checkMandatoryResources(result);
			return result;
		} catch (final ResolutionException re) {
			fail(re.getMessage());
			return null;
		}
	}

	protected void shouldNotResolve(final TestResolveContext context) {
		final Resolver resolver = getResolverService();
		try {
			resolver.resolve(context);
		} catch (final ResolutionException re) {
			return;
		}
		fail();
	}

	/*
	 * this resource is not effectively immutable but that makes testing more
	 * convenient.
	 */
	protected final class TestResource implements Resource {

		private Map<String, List<Requirement>> requirements = new HashMap<String, List<Requirement>>();
		private Map<String, List<Capability>> capabilities = new HashMap<String, List<Capability>>();

		// convenience method, expects attributes in the form
		// "key1=value2,key2=value2,..."
		protected Capability addCapability(final String namespace,
				final String attributes) {
			final Map<String, Object> attributeMap = new HashMap<String, Object>();
			final String[] pairs = attributes.split(",");
			for (int i = 0; i < pairs.length; i++) {
				final String[] tokens = pairs[i].split("=");
				assertTrue(tokens.length == 2);
				attributeMap.put(tokens[0], tokens[1]);
			}

			return addCapability(namespace, attributeMap);
		}

		protected Capability addCapability(final String namespace,
				final Map<String, Object> attributes) {
			final Capability cap = new TestCapability(namespace, attributes);
			final String ns = cap.getNamespace();
			List<Capability> caps = capabilities.get(ns);
			if (caps == null) {
				caps = new ArrayList<Capability>();
				capabilities.put(ns, caps);
			}
			caps.add(cap);
			return cap;
		}

		protected Requirement addRequirement(final String namespace,
				final String filter) {
			final Requirement req = new TestRequirement(namespace, filter);
			final String ns = req.getNamespace();
			List<Requirement> reqs = requirements.get(ns);
			if (reqs == null) {
				reqs = new ArrayList<Requirement>();
				requirements.put(ns, reqs);
			}
			reqs.add(req);
			
			return req;
		}

		public List<Capability> getCapabilities(final String namespace) {
			return get(capabilities, namespace);
		}

		public List<Requirement> getRequirements(final String namespace) {
			return get(requirements, namespace);
		}

		private <T> List<T> get(final Map<String, List<T>> map,
				final String namespace) {
			final List<T> ts;
			if (namespace != null) {
				ts = map.get(namespace);
			} else {
				ts = new ArrayList<T>();
				for (final List<T> t : map.values()) {
					ts.addAll(t);
				}
			}
			return ts != null ? Collections.unmodifiableList(ts) : Collections
					.<T> emptyList();
		}

		protected class TestRequirement implements Requirement {

			private final String namespace;

			private final Map<String, String> directives;

			protected TestRequirement(final String namespace,
					final String filter) {
				this.namespace = namespace == null ? TEST_NAMESPACE : namespace;
				this.directives = new HashMap<String, String>();
				directives.put(Namespace.REQUIREMENT_FILTER_DIRECTIVE, filter);
			}

			public String getNamespace() {
				return namespace;
			}

			public Map<String, String> getDirectives() {
				return directives;
			}

			public Map<String, Object> getAttributes() {
				return Collections.<String, Object> emptyMap();
			}

			public Resource getResource() {
				return TestResource.this;
			}

			@Override
			public String toString() {
				return "Requirement (" + namespace + ") " + directives;
			}
		}

		protected class TestCapability implements Capability {

			private final String namespace;

			private final Map<String, Object> attributes;

			protected TestCapability(final String namespace,
					final Map<String, Object> attributes) {
				this.namespace = namespace == null ? TEST_NAMESPACE : namespace;
				this.attributes = attributes;
			}

			public String getNamespace() {
				return namespace;
			}

			public Map<String, String> getDirectives() {
				return Collections.<String, String> emptyMap();
			}

			public Map<String, Object> getAttributes() {
				return attributes;
			}

			public Resource getResource() {
				return TestResource.this;
			}
		}

	}

	protected class TestResolveContext extends ResolveContext {

		protected final Collection<Resource> mandatoryResources;
		protected final Collection<Resource> optionalResources;
		protected final Collection<Resource> allResources;

		protected final Set<Requirement> callbackMemory = new HashSet<Requirement>();

		protected TestResolveContext(
				final Collection<Resource> mandatoryResources,
				final Collection<Resource> optionalResources,
				final Collection<Resource> passiveResources) {
			this.mandatoryResources = mandatoryResources == null ? Collections
					.<Resource> emptyList() : mandatoryResources;
			this.optionalResources = optionalResources == null ? Collections
					.<Resource> emptyList() : optionalResources;

			this.allResources = new ArrayList<Resource>();
			this.allResources.addAll(this.mandatoryResources);
			this.allResources.addAll(this.optionalResources);
			if (passiveResources != null) {
				this.allResources.addAll(passiveResources);
			}
		}

		protected TestResolveContext() {
			this.mandatoryResources = new ArrayList<Resource>();
			this.optionalResources = new ArrayList<Resource>();
			this.allResources = new ArrayList<Resource>();
		}

		protected void checkMandatoryResources(
				final Map<Resource, List<Wire>> resolution) {
			final Map<Resource, List<Wire>> res = new HashMap<Resource, List<Wire>>(
					resolution);
			for (final Resource resource : mandatoryResources) {
				assertNotNull(res.remove(resource));
			}
			
			//assertTrue(res.isEmpty());
		}

		protected void checkCallback(final Requirement... reqs) {
			for (final Requirement req : reqs) {
				assertTrue(callbackMemory.remove(req));
			}
			assertTrue(callbackMemory.isEmpty());
		}

		public Collection<Resource> getMandatoryResources() {
			return mandatoryResources;
		}

		public Collection<Resource> getOptionalResources() {
			return optionalResources;
		}

		@Override
		public List<Capability> findProviders(final Requirement requirement) {
			callbackMemory.add(requirement);

			final List<Capability> result = new ArrayList<Capability>();
			final String namespace = requirement.getNamespace();

			for (final Resource resource : allResources) {
				final List<Capability> caps = resource
						.getCapabilities(namespace);
				for (final Capability cap : caps) {
					if (matches(requirement, cap)) {
						result.add(cap);
					}
				}
			}

			return result;
		}

		protected boolean matches(final Requirement req, final Capability cap) {
			final String reqNamespace = req.getNamespace();
			final String capNamespace = cap.getNamespace();

			if (!reqNamespace.equals(capNamespace)) {
				return false;
			}

			final String effective = req.getDirectives().get(
					Namespace.REQUIREMENT_EFFECTIVE_DIRECTIVE);
			if (!(effective == null || effective
					.equals(Namespace.EFFECTIVE_RESOLVE))) {
				return false;
			}

			final String filter = req.getDirectives().get(
					Namespace.REQUIREMENT_FILTER_DIRECTIVE);

			try {
				if (!(filter == null || FrameworkUtil.createFilter(filter)
						.matches(cap.getAttributes()))) {
					return false;
				}
			} catch (final InvalidSyntaxException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}

		@Override
		public int insertHostedCapability(final List<Capability> capabilities,
				HostedCapability hostedCapability) {
			return 0;
		}

		@Override
		public boolean isEffective(final Requirement requirement) {
			return true;
		}

		@Override
		public Map<Resource, Wiring> getWirings() {
			return Collections.<Resource, Wiring> emptyMap();
		}

	};

}
