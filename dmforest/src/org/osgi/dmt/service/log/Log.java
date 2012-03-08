
package org.osgi.dmt.service.log;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;
import org.osgi.dmt.ddf.*;

/**
 * Provides access to the Log Entries of the Log Service.
 */

public interface Log {

	/**
	 * A potentially long list of Log Entries. The length of this list is
	 * implementation dependent. The order of the list is most recent event at
	 * index 0 and later events with higher consecutive indexes.
	 * 
	 * No new entries must be added to the log when there is an open exclusive
	 * or atomic session.
	 * 
	 * @return LIST of Log Entry nodes.
	 */
	@Scope(A)
	LIST<LogEntry> LogEntries();
}
