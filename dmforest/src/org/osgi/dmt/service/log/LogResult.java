package org.osgi.dmt.service.log;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;

import org.osgi.dmt.ddf.*;

/**
 * All data related to log search requests is stored under the LogResult node.
 * The children of this node are only generated when this sub-tree is first
 * accessed, based on the actual log request parameters.
 * 
 */
public interface LogResult {
	/**
	 * The value is the UTC based date and time of the creation of the log entry
	 * in basic representation, complete format as defined by ISO-8601 with the
	 * pattern yyyymmddThhmmssZ. For example:
	 * 
	 * <pre>
	 * 20040720T221011Z
	 * </pre>
	 * 
	 * @return The ISO-8601 time the Log event occurred
	 */
	@Scope(A)
	Opt<String> Time();

	/**
	 * A The severity level of the log entry. The value is the same as the Log
	 * Service level values:
	 * <ul>
	 * <li>LOG_ERROR 1</li>
	 * <li>LOG_WARNING 2</li>
	 * <li>LOG_INFO 3</li>
	 * <li>LOG_DEBUG 4</li>
	 * </ul>
	 * Other values are possible because the Log Service allows custom levels.
	 * 
	 * @return The log record's severity.
	 */
	@Scope(A)
	Opt<Integer> Severity();

	/**
	 * Textual, human-readable description of the log entry.
	 * 
	 * @return Textual, human-readable description of the log entry.
	 */
	@Scope(A)
	Opt<String> Message();

	/**
	 * The name of the large-scale functional unit that generated the entry, for
	 * example, the ID of the originator bundle.
	 * 
	 * @return The System node's value
	 */
	@Scope(A)
	Opt<String> System();

	/**
	 * The name of a related service.
	 * 
	 * @return The SubSystem node
	 */
	@Scope(A)
	Opt<String> SubSystem();

	/**
	 * Supplementary data for the log entry, it can be empty. The content is
	 * log-entry specific. Normally, this attribute should contain exception
	 * information associated with the log entry, if any. The attribute should
	 * also contain the name of the exception class, the message, and the stack
	 * trace associated with the Exception object.
	 * 
	 * @return The value of the Data node
	 */
	@Scope(A)
	Opt<String> Data();
}
