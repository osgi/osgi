package org.osgi.dmt.service.log;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;

import org.osgi.dmt.ddf.*;

/**
 * Represents a Log search request and its result. Typically, a remote manager
 * is interested in a subset of the log records, for example, the highest
 * severity entries originating from a specific application within the last 24
 * hours. Doing the filtering on the server side is not an option because of the
 * high bandwidth required to transfer all the records. Therefore, the remote
 * manager must have means to issue log search requests and receive only the log
 * records in which it is interested. The standard OSGi Log Reader service, does
 * not provide filtering, it simply returns the full list of available log
 * records. The Log node is responsible for mapping this repository of log
 * records to the Device Management Tree. The initiator must create a search
 * node with the appropriate parameters, and then read back the result from an
 * automatically created node. The Log node does not have to support
 * transactions. Log searches can be initiated using the $/Log sub-tree.
 * <p>
 * All data related to a log search request is stored in the log sub-tree under
 * the following URI: $/Log/[id] where [id] is a unique long identifier of the
 * search request, given by the initiator when it creates the node representing
 * the search request. To prepare a log search, the client should first create a
 * new node in the DMT in the $/Log sub-tree and optionally fill in the
 * following nodes: {@link #Filter()}, {@link #Exclude()}, and
 * {@link #MaxRecords()}.
 * <p>
 * At any moment, the initiator can start reading the then automatically created
 * {@link #LogResult()} sub-tree. This node must contain as its children only
 * the log records that match the criteria of the settings at the moment of the
 * first read, in other words, the selection must be frozen at the time the
 * first node is read. The sub-tree must only contain the leaf nodes that are
 * not barred by the Excluded node. The [id] node can be deleted by the
 * management server at its convenience. The node can therefore be used as a
 * prepared script. The Log node can delete the [id] after an
 * implementation-defined time that should allow ample time for reading the
 * results.
 * 
 * @remark Shouldn't we use the Filter node and get rid of the filtering here?
 */

public interface Log {
	/**
	 * Contains the filtering expression. The {@link #LogResult()} node must
	 * include only those log entries that satisfy the specified filter. The
	 * filter should be given in the OSGi LDAP Filter format.
	 * <p>
	 * The filter expression treats the leaf child nodes of a {@link LogResult}
	 * as the attributes, the name of the child node is the attribute name and
	 * the value is the attribute value. For example:
	 * 
	 * <pre>
	 *   (&amp;(Severity>=2) (Time>=20040720T194223Z))
	 * </pre>
	 * 
	 * The empty string indicates that no filtering must be done. An empty
	 * string is the default value for this node.
	 * 
	 * @return A Mutable for the current Filter node
	 */
	@Scope(A)
	Mutable<String> Filter();

	/**
	 * A comma-separated list of log entry nodes. All node names that are
	 * specified below the {@link LogResult} node can be used. If specified, the
	 * listed node names must not be included in the search result. The filter
	 * expression may contain (and filtering should be done against) conditions
	 * for a node even if it is added to the exclude list. For example:
	 * 
	 * <pre>
	 *     Severity, Data
	 * </pre>
	 * 
	 * If the Exclude node is empty, all log entry sub-nodes must be included in
	 * the search result, which is the default value for this node.
	 * 
	 * @return A Mutable for the Exclude node
	 */
	@Scope(A)
	Mutable<String> Exclude();

	/**
	 * The maximum number of log records to be included in the search result.
	 * The default value for this node is zero, which means no limit.
	 * 
	 * @return The Mutable for the MaxRecords node.
	 */
	@Scope(A)
	Mutable<Integer> MaxRecords();

	/**
	 * All data related to log search requests is stored under the LogResult
	 * node. The children of this node are only generated when this sub-tree is
	 * first accessed, based on the actual log request parameters like
	 * {@link #Filter()}, {@link #Exclude()}, and {@link #MaxRecords()}. The
	 * name of the child nodes is automatically generated.
	 * 
	 * @return The root node for the Log results
	 */
	@Scope(A)
	LIST<LogResult> LogResult();
}
