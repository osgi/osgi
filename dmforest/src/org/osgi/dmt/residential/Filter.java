package org.osgi.dmt.residential;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;

import info.dmtree.*;

import java.net.*;

import org.osgi.dmt.ddf.*;
import org.osgi.dmt.residential.ServiceState.Property;

/**
 * A Filter node can find the nodes in a given sub-tree that correspond to a
 * given filter expression. This Filter node is a generic mechanism to select a
 * part of the sub-tree (<em>except</em> itself). This node can be used to find
 * bundles, packages, services and other information in the tree.
 * <p>
 * A Filter node is used to search nodes located under the sub-trees specified
 * by the {@link #Target()} node value. Searching is done by treating leaf nodes
 * and LIST nodes as attributes for the filter expression. An interior node
 * matches when a filter matches its children. The matching nodes' URIs are
 * gathered under a {@link #ResultUriList()} node and as a virtual sub-tree
 * under the {@link #Result()} node.
 * <p>
 * 
 * The Filter node can then specify the {@link #Target()}. The {@link #Target()}
 * is a a URI with wildcards. The URI is relative to the parent of the Filters
 * node of the type {@link OSGi}. The URI must not start with a slash ('/') nor
 * a dollar ('$'). For example: OSGi/Local refers to {@link OSGi#Local()}. The
 * {@link #Target()} should not select nodes below the {@link OSGi#Filters()}
 * sub-tree. If a {@link #Target()} does select such a node then those nodes
 * must be ignored.
 * 
 * <p>
 * There are two different wildcards:
 * <ul>
 * <li><em>Asterisk </em> — (\u002A '*') Specifies a wild card for one node name
 * only. That is {@code A/*} matches {@code A/B}, {@code A/C}, but not
 * {@code A/X/Y}. The asterisk wildcard can be used anywhere in the URI like
 * <code>A/*</code> <code>/C</code>. Partial matches are not supported, that is
 * a URI like <code>A/xyz*</code> is not supported.</li>
 * <li><em>Minus sign ('-')</em> — Specifies a wild for any number of descendant
 * nodes. This is {@code A/-} matches {@code A/B}, {@code A/C}, but also
 * {@code A/X/Y/Z}. Partial matches are not supported, that is a URI like
 * {@code A/xyz-} is not supported. The - wildcard must not be used at the end
 * of the URI</li>
 * <p>
 * The {@link #Target()} node selects a set of nodes {@code N} that can be
 * viewed as a list of URIs or as a virtual sub-tree. The {@link #Result()} node
 * is the virtual sub-tree and the {@link #ResultUriList()} is a LIST of URIs.
 * The actual selection of the nodes must be postponed until either of these
 * nodes (or or one of their their sub-nodes) are accessed the first time.
 * <p>
 * The {@link #Result()} node is on the same level as the parent of the Filters
 * node. However, its descendants occur only in this tree when selected by the
 * {@link #Target()}. That is, when {@link #Target()} is set to
 * <code>ServiceState/*</code><code>/Property/</code> the Result node could look
 * like:
 * 
 * <pre>
 *   Result/
 *     ServiceState/
 *       [1]/
 *          Property/
 *             Key
 *             Type
 *             Cardinality
 *       [25]/
 *          Property/
 *             Key
 *             Type
 *             Cardinality
 *       [16]/
 *          Property/
 *             Key
 *             Type
 *             Cardinality
 * </pre>
 * 
 * In this case the {@link #ResultUriList()} would look like:
 * 
 * <pre>
 * 	[1]="ServiceState/1/Property"
 * 	[2]="ServiceState/25/Property"
 * 	[3]="ServiceState/16/Property"
 * </pre>
 * 
 * <p>
 * It is possible to further refine the selection by specifying the Filter node.
 * The Filter node is an LDAP filter expression or a simple wildcard ('*') which
 * selects all the nodes. As the wildcard is the default, all nodes selected by
 * the {@link #Target()} are selected by default.
 * <p>
 * The Filter must be applied to each of the nodes selected by target in the set
 * {@code N}. By definition, these nodes are interior nodes only. LDAP
 * expressions assert values depending on their <em>key</em>. In this case, the
 * child leaf nodes of a node in set {@code N} are treated as the property on
 * their parent node. That is, the node {@code ServiceState/1/Property} is of
 * type {@link Property} and has the following leaf nodes:
 * <ul>
 * <li>Key</li>
 * <li>Type</li>
 * <li>Cardinality</li>
 * </ul>
 * The attribute name in the LDAP filter can only be a direct leaf node of the
 * node in set {@code N}. If the attribute name contains an unescaped slash then
 * the leaf node must not be found.
 * <p>
 * A special case is for nodes marked as {@link DmtConstants#DDF_LIST_SUBTREE}.
 * Even though this is technically an interior node its collected children must
 * be treated as a comma separated string where the order is defined by the
 * numeric comparison of their names. For example:
 * 
 * <pre>
 *   Fragments/             (LIST)
 *     [1]=24
 *     [8]=15
 *     [2]=38
 * </pre>
 * 
 * This list must be translated into the comma separated list:
 * 
 * <pre>
 * &quot;24,15,38&quot;
 * </pre>
 * 
 * Each of these leafs can be used in the LDAP Filter as a value. Comparisons
 * are done on the string value of the node. A leaf node must be converted to a
 * string with the {@link DmtData#toString()} method before comparing for leaf
 * nodes and the aforementioned string representation for LIST nodes.
 * 
 * 
 * The set {@code N} must therefore consists only of nodes where the Filter
 * matches.
 * <p>
 * For example, if the Filter is {@code (Type=java.lang.String)} for the
 * previous example then the {@link #Result()} and {@link #ResultUriList()} must
 * only contain the Property nodes that match the given filter. That is, only
 * the Property nodes that are strings.
 * <p>
 * It is allowed to change the {@link #Target()} or the Filter node after the
 * results are read. In that case, the {@link #Result()} and
 * {@link #ResultUriList()} must be cleared instantaneously and the search
 * redone once either result node is used in an access.
 * <p>
 * The initial value of {@link #Target()} is the empty string, which indicates
 * no target.
 * 
 * @remark I changed this, can we do this? Make the parent of the parent of the
 *         Filters node the root of the URI?
 * @remark why can we not filter over multiple frameworks? I.e. root = $ to
 *         search from?
 * @remark is this wise? This fails numeric comparisons. We normally use the
 *         type of the Dmt Data and then create an instance of the same type
 *         from the LDAP filter value part. This provides proper comparison
 *         rules. I would strongly prefer that we could use exactly the same
 *         rules as the Filter interface in the framework. I would like to refer
 *         to this.
 * 
 * @remark list nodes are not clearly defined. I assumed they're treated as a
 *         string but they could also be treated as multi valued properties.
 *         Maybe it is better to change it to this?
 */

public interface Filter {
	/**
	 * A URI relative the ancestor of this node, with optional wildcards,
	 * selecting a set of sub-nodes {@code N}. Wildcards can be an asterisk
	 * (\u002A '*') or a minus sign (\u002D '-'). An asterisk can be used in place of a single
	 * node name in the URI, a minus sign stands for any number of consecutive
	 * node names. The default value of this node is the empty string, which
	 * indicates that no nodes must be selected. Changing this value has no
	 * effect until the {@link #Result()} or {@link #ResultUriList()} is read to
	 * get {@code N}.
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
	 * selected by {@link #Target()}. The filter expression is an LDAP filter or
	 * an asterisk ('*'). An asterisk is the default value and matches any node
	 * in set {@code N}. If an LDAP expression is set in the Filter node then
	 * the set {@code N} must only contain nodes that match the given filter.
	 * The values the filter asserts are the immediate leafs and LIST nodes of
	 * the nodes in set {@code N}. The name of these child nodes is the name of
	 * the attribute matched in the filter.
	 * <p>
	 * 
	 * @return A mutable Filter node.
	 */

	@Scope(A)
	Mutable<String> Filter();

	/**
	 * The Result tree is a virtual tree of all nodes that were selected by the
	 * {@link #Target()} and matched the Filter, that is, all nodes in set
	 * {@code N}. The {@link #Target()} contains a relative URI (with optional
	 * wildcards) from the parent of the Filters node. The {@link #Result()}
	 * node acts as the parent of this same relative path for each node in
	 * {@code N}.
	 * <p>
	 * The {@link #Result()} node is initially childless, when the first time it
	 * is accessed (or its sibling {@link #ResultUriList()}) it will be
	 * populated. Any change to {@link #Target()} or Filter will clear this list
	 * and will then be re-populated once the {@link #Result()} or
	 * {@link #ResultUriList()} is accessed.
	 * 
	 * @return The root of the result tree
	 */
	@Scope(A)
	NODE Result();

	/**
	 * A list of URIs of nodes in the Device Management Tree from the node
	 * selected by the {@link #Target()} that match the Filter node. All URIs
	 * are relative to the parent node of the (the Filters) node.
	 * 
	 * @return List of URIs
	 */
	@Scope(A)
	@NodeType(DmtConstants.DDF_LIST_SUBTREE)
	LIST<URI> ResultUriList();

	/**
	 * Optional extension node.
	 * 
	 * @return optional extension node.
	 */
	@Scope(A)
	Opt<NODE> Ext();

}
