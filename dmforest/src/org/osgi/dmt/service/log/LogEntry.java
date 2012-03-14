
package org.osgi.dmt.service.log;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;
import java.util.*;
import org.osgi.dmt.ddf.*;

/**
 * A Log Entry node is the representation of a LogEntry from the OSGi Log
 * Service.
 * 
 */
public interface LogEntry {
	/**
	 * Time of the Log Entry.
	 * 
	 * @return The Time the log entry was created.
	 */
	@Scope(A)
	Date Time();

	/**
	 * The severity level of the log entry. The value is the same as the Log
	 * Service level values:
	 * <ul>
	 * <li>LOG_ERROR 1</li>
	 * <li>LOG_WARNING 2</li>
	 * <li>LOG_INFO 3</li>
	 * <li>LOG_DEBUG 4</li>
	 * </ul>
	 * <p>
	 * Other values are possible because the Log Service allows custom levels.
	 * 
	 * @return The log entry's severity.
	 */
	@Scope(A)
	Integer Level();

	/**
	 * Textual, human-readable description of the log entry.
	 * 
	 * @return Textual, human-readable description of the log entry.
	 */
	@Scope(A)
	String Message();

	/**
	 * The location of the bundle that originated this log or an empty string.
	 * 
	 * @return The location of the bundle that originated this log entry or an
	 *         empty string.
	 */
	@Scope(A)
	String Bundle();

	/**
	 * Human readable information about an exception.
	 * 
	 * Provides the exception information if any, optionally including the stack
	 * trace.
	 * 
	 * @return The value of the Exception node
	 */
	@Scope(A)
	Opt<String> Exception();
}
