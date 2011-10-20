/*
 * Copyright (c) OSGi Alliance (2006, 2010). All Rights Reserved.
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

package org.osgi.service.resolver;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.resource.Resource;
import org.osgi.framework.resource.Wire;

/**
 * A resolver is a service interface that can be used to find resolutions for
 * specified {@link Resource resources} based on a supplied {@link Environment}.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface Resolver {
  /**
   * Attempt to resolve the resources based on the specified environment and
   * return any new resources and wires to the caller.
   * 
   * <p>
   * The resolver considers two groups of resources:
   * <ul>
   * <li>Mandatory - any resource in the mandatory group must be resolved, a
   * failure to satisfy any mandatory requirement for these resources will
   * result in a {@link ResolutionException}</li>
   * <li>Optional - any resource in the optional group may be resolved, a
   * failure to satisfy a mandatory requirement for a resource in this group
   * will not fail the overall resolution but no resources or wires will be
   * returned for this resource.</li>
   * </ul>
   * 
   * <h3>Delta</h3>
   * <p>
   * The resolve method returns the delta between the start state defined by
   * {@link Environment#getWiring()} and the end resolved state, i.e. only new
   * resources and wires are included. To get the complete resolution the caller
   * can merge the start state and the delta using something like the following:
   * 
   * <pre>
   * Map&lt;Resource, List&lt;Wire&gt;&gt; delta = resolver.resolve(env, resources, null);
   * Map&lt;Resource, List&lt;Wire&gt;&gt; wiring = env.getWiring();
   * 
   * for (Map.Entry&lt;Resource, List&lt;Wire&gt;&gt; e : delta.entrySet()) {
   *   Resource res = e.getKey();
   *   List&lt;Wire&gt; newWires = e.getValue();
   * 
   *   List&lt;Wire&gt; currentWires = wiring.get(res);
   *   if (currentWires != null) {
   *     newWires.addAll(currentWires);
   *   }
   * 
   *   wiring.put(res, newWires);
   * }
   * </pre>
   * 
   * <h3>Consistency</h3>
   * <p>
   * For a given resolve operation the parameters to the resolve method should be
   * considered immutable. This means that resources should have constant
   * capabilities and requirements and an environment should return a consistent
   * set of capabilities, wires and effective requirements.
   * 
   * <p>
   * The behavior of the resolver is not defined if resources or the environment
   * supply inconsistent information.
   * 
   * @param environment
   *          the environment into which to resolve the requirements
   * @param mandatoryResources
   *          The resources that must be resolved during this resolution step or
   *          null if no resources must be resolved
   * @param optionalResources
   *          Any resources which the resolver should attempt to resolve but
   *          that will not cause an exception if resolution is impossible or
   *          null if no resources are optional.
   * 
   * @return the new resources and wires required to satisfy the requirements
   * 
   * @throws ResolutionException
   *           if the resolution cannot be satisfied for any reason
   * @throws NullPointerException
   *           if environment is null
   */
	Map<Resource, List<Wire>> resolve(Environment environment,
			Collection<? extends Resource> mandatoryResources,
			Collection<? extends Resource> optionalResources)
			throws ResolutionException;
}
