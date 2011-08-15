package org.osgi.dmt.service.log;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;

import org.osgi.dmt.ddf.*;

/**
 * Represents access to the Log service. 
 *  */

public interface Log {

	/**
	 * All data related to log search requests is stored as a LIST the {@link #LogResult()}
	 * node. The {@link #LogResult()} node will return an implementation dependent number 
	 * of log records. 
	 * <p>
	 * It is possible to filter this list with the {@code Filter} node.
	 * 
	 * @return The root node for the Log results
	 */
	@Scope(A)
	LIST<LogResult> LogResult();
}
