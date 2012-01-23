package org.osgi.service.resolver;

import org.osgi.framework.resource.Capability;
import org.osgi.framework.resource.Requirement;
import org.osgi.framework.resource.Resource;

/**
 * <p>
 * During the resolution process it is necessary for the resolver to synthesize
 * resources to represent the merge of bundles and fragments.
 * 
 * <p>
 * For example if we have one bundle A and two fragments X and Y then resolver
 * will create two synthesized resources AX and AY.
 * 
 * <p>
 * In order for the Environment to provide a policy for which synthesized
 * resources are preferred, both the host bundle and providing fragment need to
 * be visible. This interface must therefore be used on all synthesized resource
 * elements created by a resolver.
 * 
 * <p>
 * The environment can then enforce the ordering policy when a capability that
 * is provided by a synthesized resource is added to the SortedSet returned by
 * {@link Environment#findProviders(org.osgi.framework.resource.Requirement)}.
 * 
 * <p>
 * The following is a short example of how this may look in practice:
 * 
 * <pre>
 * class PreferrSmallResourcesEnvironment implements Environment {
 * 	static class SizeComparator extends Comparator {
 * 		public int compare(Capability capA, Capability capB) {
 * 			Resource hostA = capA.getResource();
 * 			Resource hostB = capB.getResource();
 * 
 * 			Resource synthA = getSynthesizedResource(hostA);
 * 			Resource synthB = getSynthesizedResource(hostA);
 * 
 * 			long sizeA = getSize(hostA) + getSize(synthA);
 * 			long sizeB = getSize(hostB) + getSize(synthB);
 * 
 * 			if (sizeA &gt; sizeB) {
 * 				return +1;
 * 			}
 * 			else
 * 				if (sizeA &lt; sizeB) {
 * 					return -1;
 * 				}
 * 				else {
 * 					return 0;
 * 				}
 * 		}
 * 
 * 		private int getSize(Resource resource) {
 * 			if (resource == null)
 * 				return 0;
 * 
 * 			Capability contentCapability = getCapability(resource,
 * 					ContentNamespace.CAPABILITY);
 * 
 * 			if (contentCapability == null)
 * 				return 0;
 * 
 * 			Integer size = (Integer) contentCapability.getAttributes().get(
 * 					ContentNamespace.SIZE_ATTRIBUTE);
 * 
 * 			return size == null ? Integer.MAX_VALUE : size;
 * 		}
 * 
 * 		private Capability getCapability(Resource res, String namespace) {
 * 			List&lt;Capability&gt; caps = res.getCapabilities(namespace);
 * 			return caps.isEmpty() ? null : caps.get(0);
 * 		}
 * 
 * 		private Resource getSynthesizedResource(Resource res) {
 * 			if (res instanceof Synthesized) {
 * 				return (Resource) ((Synthesized) res).getOriginal();
 * 			}
 * 			else {
 * 				return null;
 * 			}
 * 		}
 * 	};
 * 
 * 	private List	repositories;
 * 
 * 	public PreferrSmallResourcesEnvironment(List repositories) {
 * 		this.repositories = repositories;
 * 	}
 * 
 * 	public SortedSet findProviders(Requirement requirement) {
 * 		SortedSet caps = new ConcurrentSkipListSet(new SizeComparator());
 * 
 * 		for (Repository r : repositories) {
 * 			caps.addAll(r.findProviders(requirement));
 * 		}
 * 
 * 		return caps;
 * 	}
 * 
 * 	public boolean isEffective(Requirement requirement) {
 * 		return true;
 * 	}
 * 
 * 	public Map getWirings() {
 * 		return Collections.EMPTY_MAP;
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * A Synthesized capability must report the Host Bundle via the
 * {@link Capability#getResource()} method. It must also report the original
 * Capability from the Fragment Bundle via the {@link #getOriginal()} method.
 * The Environment may then access the fragment resource that this capability
 * originates from via {@link Capability#getResource()}.
 */
public interface Synthesized {
	/**
	 * The original {@link Capability}, {@link Requirement} or {@link Resource}
	 * that backs this synthesized element.
	 * 
	 * @return the original capability, requirement or resource
	 */
	Object getOriginal();
}
