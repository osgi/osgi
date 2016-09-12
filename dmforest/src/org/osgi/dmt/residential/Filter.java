
package org.osgi.dmt.residential;

import static org.osgi.dmt.ddf.Scope.SCOPE.A;

import org.osgi.dmt.ddf.LIST;
import org.osgi.dmt.ddf.Mutable;
import org.osgi.dmt.ddf.NODE;
import org.osgi.dmt.ddf.Scope;

/**
 * A Filter node can find the nodes in a given sub-tree that correspond to a
 * given filter expression. This Filter node is a generic mechanism to select a
 * part of the sub-tree (<em>except</em> itself).
 * <p>
 * Searching is done by treating an interior node as a map where its leaf nodes
 * are attributes for a filter expression. That is, an interior node matches
 * when a filter matches on its children. The matching nodes' URIs are gathered
 * under a {@link #ResultUriList() ResultUriList} node and as a virtual sub-tree
 * under the {@link #Result() Result} node.
 * <p>
 * The Filter node can specify the {@link #Target() Target} node. The
 * {@link #Target() Target} is an absolute URI ending in a slash, potentially
 * with wild cards. Only nodes that match the target node are included in the
 * result.
 * <p>
 * There are two different wild cards:
 * <ul>
 * <li><em>Asterisk</em> ({@code '*'} &#92;u002A) - Specifies a wild card for
 * one interior node name only. That is {@code A/*}{@code /} matches an interior
 * nodes {@code A/B}, {@code A/C}, but not {@code A/X/Y}. The asterisk wild card
 * can be used anywhere in the URI like {@code A/*}{@code /C}. Partial matches
 * are not supported, that is a URI like {@code A/xyz*} is invalid.</li>
 * <li><em>Minus sign</em> ({@code '-'} &#92;u002D) - Specifies a wildcard for
 * any number of descendant nodes. This is {@code A/-/X/} matches {@code A/B/X},
 * {@code A/C/X}, but also {@code A/X}. Partial matches are not supported, that
 * is a URI like {@code A/xyz-} is not supported. The - wild card must not be
 * used at the last segment of a URI</li>
 * </ul>
 * <p>
 * The {@link #Target() Target} node selects a set of nodes {@code N} that can
 * be viewed as a list of URIs or as a virtual sub-tree. The {@link #Result()
 * Result} node is the virtual sub-tree (beginning at the session base) and the
 * {@link #ResultUriList() ResultUriList} is a LIST of session relative URIs.
 * The actual selection of the nodes must be postponed until either of these
 * nodes (or one of their sub-nodes) is accessed for the first time. Either
 * nodes represent a read-only snapshot that is valid until the end of the
 * session.
 * <p>
 * It is possible to further refine the selection by specifying the Filter node.
 * The Filter node is an LDAP filter expression or a simple wild card ('*')
 * which selects all the nodes. As the wild card is the default, all nodes
 * selected by the {@link #Target() Target} are selected by default.
 * <p>
 * The Filter must be applied to each of the nodes selected by target in the set
 * {@code N}. By definition, these nodes are <em>interior nodes only</em>. LDAP
 * expressions assert values depending on their <em>key</em>. In this case, the
 * child leaf nodes of a node in set {@code N} are treated as the property on
 * their parent node.
 * <p>
 * The attribute name in the LDAP filter can only reference a direct leaf node
 * of the node in the set {@code N} or an interior node with the DDF type
 * {@link org.osgi.service.dmt.DmtConstants#DDF_LIST} with leaf nodes as
 * children, i.e. a <em>LIST</em>. A LIST of primitives must be treated in the
 * filter as a multi valued property, any of its values satisfy an assertion on
 * that attribute.
 * <p>
 * Attribute names must not contains a slash, that is, it is only possible to
 * assert values directly below the node selected by the {@code target}.
 * <p>
 * 
 * Each of these leaf nodes and LISTs can be used in the LDAP Filter as a
 * key/value pair. The comparison must be done with the type used in the Dmt
 * Data object of the compared node. That is, if the Dmt Admin data is a number,
 * then the comparison rules of the number must be used. The attributes given to
 * the filter must be converted to the Java object that represents their type.
 * <p>
 * The set {@code N} must therefore consists only of nodes where the Filter
 * matches.
 * <p>
 * It is allowed to change the {@link #Target() Target} or the Filter node after
 * the results are read. In that case, the {@link #Result() Result} and
 * {@link #ResultUriList() ResultUriList} must be cleared instantaneously and
 * the search redone once either result node is read.
 * <p>
 * The initial value of {@link #Target() Target} is the empty string, which
 * indicates no target.
 */

public interface Filter {
	/**
	 * An absolute URI always ending in a slash ('/'), with optional wildcards,
	 * selecting a set of sub-nodes {@code N}. Wildcards can be an asterisk (
	 * {@code '*'} &#92;u002A) or a minus sign ({@code '-'} &#92;u002D). An
	 * asterisk can be used in place of a single node name in the URI, a minus
	 * sign stands for any number of consecutive node names. The default value
	 * of this node is the empty string, which indicates that no nodes must be
	 * selected. Changing this value must clear any existing results. If the
	 * {@link #Result()} or {@link #ResultUriList() ResultUriList} is read to
	 * get {@code N} then a new search must be executed.
	 * <p>
	 * A URI must always end in '/' to indicate that the target can only select
	 * interior nodes.
	 * 
	 * @return A mutable Target node.
	 */
	@Scope(A)
	Mutable<String> Target();

	/**
	 * An optional filter expression that filters nodes in the set {@code N}
	 * selected by {@link #Target() Target}. The filter expression is an LDAP
	 * filter or an asterisk ('*'). An asterisk is the default value and matches
	 * any node in set {@code N}. If an LDAP expression is set in the Filter
	 * node then the set {@code N} must only contain nodes that match the given
	 * filter. The values the filter asserts are the immediate leafs and LIST
	 * nodes of the nodes in set {@code N}. The name of these child nodes is the
	 * name of the attribute matched in the filter.
	 * <p>
	 * The nodes can be removed by the Filter implementation after a timeout
	 * defined by the implementation.
	 * 
	 * @return A mutable Filter node.
	 */

	@Scope(A)
	Mutable<String> Filter();

	/**
	 * Limits the number of results to the given number. If this node is not set
	 * there is no limit. The default value is not set, thus no limit.
	 * 
	 * @return Limit
	 */

	@Scope(A)
	Mutable<Integer> Limit();

	/**
	 * The Result tree is a virtual read-only tree of all nodes that were
	 * selected by the {@link #Target() Target} and matched the Filter, that is,
	 * all nodes in set {@code N}. The {@link #Result() Result} node acts as a
	 * parent instead of the session root for each node in {@code N}.
	 * <p>
	 * The {@link #Result() Result} node is a snapshot taken the first time it
	 * is accessed after a change in the {@code Filter} and/or the
	 * {@code Target} nodes.
	 * 
	 * @return The root of the result tree
	 */
	@Scope(A)
	NODE Result();

	/**
	 * A list of URIs of nodes in the Device Management Tree from the node
	 * selected by the {@link #Target() Target} that match the Filter node. All
	 * URIs are relative to current session.
	 * 
	 * The {@link #Result() Result} node is a snapshot taken the first time it
	 * is accessed after a change in the {@code Filter} and/or the
	 * {@code Target} nodes.
	 * 
	 * @return List of URIs
	 */
	@Scope(A)
	LIST<String> ResultUriList();

	/**
	 * Instance Id to allow addressing by Instance Id.
	 * 
	 * @return The InstanceId
	 */
	@Scope(A)
	int InstanceId();
}
