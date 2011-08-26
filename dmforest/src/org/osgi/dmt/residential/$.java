package org.osgi.dmt.residential;

import org.osgi.dmt.ddf.*;
import org.osgi.dmt.service.log.*;
import static org.osgi.dmt.ddf.Scope.SCOPE.*;

/**
 * The $ describes the root node for OSGi Residential Management. The path to this node is
 * defined in the system property: {@code org.osgi.dmt.residential}.
 */
public interface $ {
	/**
	 * The Filters node searches the nodes in a tree that correspond to a target
	 * URI and an optional filter expression. A new {@link Filter} is created by
	 * adding a node to the Filters node. The name of the node is chosen by the
	 * remote manager. If multiple managers are active they must agree on a
	 * scheme to avoid conflicts or an atomic sessions must be used to claim
	 * exclusiveness.
	 * <p>
	 * Filter nodes are persistent but an implementation can remove the node 
	 * after a suitable timeout that should at least be 1 hour.
	 * <p>
	 * If this functionality is not supported on this device then the node is not present.
	 * 
	 * @return The Filter Node
	 */
	@Scope(P)
	Opt<MutableMAP<String, Filter>> Filter();

	/**
	 * The Framework node used to manage the local framework.
	 * 
	 * @return Framework node
	 */
	@Scope(P)
	Framework Framework();

	/**
	 * Access to the optional Log.
	 * <p>
	 * If this functionality is not supported on this device then the node is not present.
	 * 
	 * @return The value for the optional Log node
	 */

	@Scope(P)
	Opt<Log> Log();

}
