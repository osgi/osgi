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

import java.util.List;
import java.util.Map;

import org.osgi.framework.resource.Requirement;
import org.osgi.framework.resource.Resource;
import org.osgi.framework.resource.Wire;

/**
 * A resolver is a service interface that can be used to find resolutions for
 * specified {@link Requirement requirements} based on a supplied
 * {@link Environment}.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface Resolver {
  /**
   * Attempt to resolve the requirements based on the specified environment and
   * return any new revisions or wires to the caller.
   * 
   * <p>
   * The resolve method returns the delta between the start state defined by
   * {@link Environment#getWiring()} and the end resolved state, i.e. only new
   * resources and wires are included. To get the complete resolution the caller
   * can merge the start state and the delta using something like the following:
   * 
   * <pre>Map&lt;Resource, List&lt;Wire&gt;&gt; delta = resolver.resolve(env, requirement);
Map&lt;Resource, List&lt;Wire&gt;&gt; wiring = env.getWiring();
      
for(Map.Entry&lt;Resource, List&lt;Wire&gt;&gt; e : delta.entrySet()) {
  Resource res = e.getKey();
  List&lt;Wire&gt; newWires = e.getValue();
  
  List&lt;Wire&gt; currentWires = wiring.get(res);
  if (currentWires != null) {
    newWires.addAll(currentWires);
  }
  
  wiring.put(res, newWires);
}</pre>
   * 
   * <h3>Consistency</h3>
   * <p>
   * For a given resolve call an environment should return a consistent set of
   * capabilities and wires. The simplest mechanism of achieving this is by
   * creating an immutable snapshot of the environment state and passing this to
   * the resolve method.
   * 
   * <p>
   * If {@link Requirement#getResource} returns null then the
   * requirement can be wired to any matching capability regardless of the
   * "uses" constraint directive from the capability. This is because there is
   * no Resource available to do a class space consistency check against.
   * 
   * @param environment
   *          the environment into which to resolve the requirements
   * 
   * @param requirements The requirements that the resolver must satisfy
   * @return the new resources and wires required to satisfy the requirements
   * 
   * @throws ResolutionException if the resolution cannot be satisified for any reason
   * @throws NullPointerException if environment or any of the requirements are null
   */
  Map<Resource, List<Wire>> resolve(Environment environment,
      Requirement... requirements) throws ResolutionException,
      NullPointerException;  
}
