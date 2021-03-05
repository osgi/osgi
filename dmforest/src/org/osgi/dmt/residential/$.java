/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.dmt.residential;

import static org.osgi.dmt.ddf.Scope.SCOPE.P;

import org.osgi.dmt.ddf.MutableMAP;
import org.osgi.dmt.ddf.Opt;
import org.osgi.dmt.ddf.Scope;
import org.osgi.dmt.service.log.Log;

/**
 * The $ describes the root node for OSGi Residential Management. The path to
 * this node is defined in the system property: {@code org.osgi.dmt.residential}
 * .
 */
public interface $ {
	/**
	 * The Framework node used to manage the local framework.
	 * 
	 * @return Framework node
	 */
	@Scope(P)
	Framework Framework();

	/**
	 * The Filter node searches the nodes in a tree that correspond to a target
	 * URI and an optional filter expression. A new {@link Filter} is created by
	 * adding a node to the Filter node. The name of the node is chosen by the
	 * remote manager. If multiple managers are active they must agree on a
	 * scheme to avoid conflicts or an atomic sessions must be used to claim
	 * exclusiveness.
	 * <p>
	 * Filter nodes are persistent but an implementation can remove the node
	 * after a suitable timeout that should at least be 1 hour.
	 * <p>
	 * If this functionality is not supported on this device then the node is
	 * not present.
	 * 
	 * @return The Filter Node
	 */
	@Scope(P)
	Opt<MutableMAP<String, Filter>> Filter();

	/**
	 * Access to the optional Log.
	 * <p>
	 * If this functionality is not supported on this device then the node is
	 * not present.
	 * 
	 * @return The value for the optional Log node
	 */

	@Scope(P)
	Opt<Log> Log();

}
