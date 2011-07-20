package org.osgi.dmt.residential;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;
import info.dmtree.*;

import java.net.*;

import org.osgi.dmt.ddf.*;

/**
 * A Filter node can find the nodes in a given sub-tree that correspond to a
 * given filter expression. This Filter node is a generic mechanism to select a
 * part of the sub-tree (<em>except</em> itself). This node can be used to find
 * any node in the tree.
 * <p>
 * Searching is done by treating an interior node as a map where its leaf nodes
 * are attributes for a filter expression. That is, an interior node matches
 * when a filter matches on its children. The matching nodes' URIs are gathered
 * under a {@link #ResultUriList()} node and as a virtual sub-tree under the
 * {@link #Result()} node.
 * <p>
 * 
 * The Filter node can specify the {@link #Target()} node. The {@link #Target()}
 * is a URI relative to the <em>current session</em>, potentially with wild
 * cards. If a {@link #Target()} selects nodes that descend the $/Filters node
 * then those nodes must be ignored in the search.
 * 
 * <p>
 * There are two different wild cards:
 * <ul>
 * <li><em>Asterisk </em> — (\u002A '*') Specifies a wild card for one interior
 * node name only. That is {@code A/*} matches {@code A/B}, {@code A/C}, but not
 * {@code A/X/Y}. The asterisk wild card can be used anywhere in the URI like
 * <code>A/*</code><code>/C</code>. Partial matches are not supported, that is a
 * URI like <code>A/xyz*</code> is not supported.</li>
 * <li><em>Minus sign ('-')</em> — Specifies a wild for any number of descendant
 * nodes. This is {@code A/-} matches {@code A/B}, {@code A/C}, but also
 * {@code A/X/Y/Z}. Partial matches are not supported, that is a URI like
 * {@code A/xyz-} is not supported. The - wild card must not be used at the end
 * of the URI</li>
 * <p>
 * The {@link #Target()} node selects a set of nodes {@code N} that can be
 * viewed as a list of URIs or as a virtual sub-tree. The {@link #Result()} node
 * is the virtual sub-tree and the {@link #ResultUriList()} is a LIST of URIs.
 * The actual selection of the nodes must be postponed until either of these
 * nodes (or or one of their sub-nodes) is accessed for the first time.
 * <p>
 * The {@link #Result()} node is on the same level as the parent of the Filters
 * node. However, its descendants occur only in this tree when selected by the
 * {@link #Target()}. That is, when {@link #Target()} is set to
 * <code>Log/LogResult/*</code> the Result node could look like:
 * 
 * <pre>
 *   Result/
 *     Log/
 *       LogResult
 *         [0]/
 *           Time
 *           Severity
 *           Message
 *           System
 *           SubSystem
 *           Data
 *         [1]
 *           Time
 *           Severity
 *           Message
 *           System
 *           SubSystem
 *           Data
 *         [2]
 *           Time
 *           Severity
 *           Message
 *           System
 *           SubSystem
 *           Data
 * </pre>
 * 
 * In this case the {@link #ResultUriList()} would look like:
 * 
 * <pre>
 * 	[0]="Log/LogResult/0"
 * 	[1]="Log/LogResult/1"
 * 	[2]="Log/LogResult/2"
 * </pre>
 * 
 * <p>
 * It is possible to further refine the selection by specifying the Filter node.
 * The Filter node is an LDAP filter expression or a simple wild card ('*')
 * which selects all the nodes. As the wild card is the default, all nodes
 * selected by the {@link #Target()} are selected by default.
 * <p>
 * The Filter must be applied to each of the nodes selected by target in the set
 * {@code N}. By definition, these nodes are <em>interior nodes only</em>. LDAP
 * expressions assert values depending on their <em>key</em>. In this case, the
 * child leaf nodes of a node in set {@code N} are treated as the property on
 * their parent node. That is, the interior node {@code Log/LogResult/1} has the
 * following leaf nodes:
 * <ul>
 * <li>Severity</li>
 * <li>Time</li>
 * <li>Message</li>
 * <li>System</li>
 * <li>SubSystem</li>
 * <li>Data</li>
 * </ul>
 * The attribute name in the LDAP filter can only reference a direct leaf node
 * of the node in the set {@code N} or an interior node with the MIME type
 * {@link DmtConstants#DDF_LIST_SUBTREE} with leaf nodes as children, i.e. a
 * <em>list</em>. A list must be treated in the filter as a multi valued
 * property, any of its values satisfy an assertion on that attribute. 
 * <p>
 * If the attribute name contains an unescaped slash then the leaf
 * node must be ignored.
 * <p>
 * 
 * 
 * Each of these leaf nodes and lists can be used in the LDAP Filter as a value. The
 * comparison must be done with the type used in the Dmt Data object of the compared
 * node. That is, if the Dmt Admin data is a number, then the comparison rules
 * of the number must be used. That is, the attributes given to the filter
 * must be converted to the Java object that represents their type.
 * <p>
 * The set {@code N} must therefore consists only of nodes where the Filter
 * matches.
 * <p>
 * For example, if the Filter is {@code (Severity>=2)} for the
 * previous example then the {@link #Result()} and {@link #ResultUriList()} must
 * only contain the Log result nodes that match the given filter. That is, only
 * the LogResult nodes that have a severity of more than 2.
 * <p>
 * It is allowed to change the {@link #Target()} or the Filter node after the
 * results are read. In that case, the {@link #Result()} and
 * {@link #ResultUriList()} must be cleared instantaneously and the search
 * redone once either result node is used in an access.
 * <p>
 * The initial value of {@link #Target()} is the empty string, which indicates
 * no target.
 * <p>
 * The search must take place with the same session as the session used to create 
 * the Filter child node; this maintains any security scope that is in effect.  
 *  
 * @remark Modified this to use standard filter comparison rules.
 * 
 * @remark list nodes are not clearly defined. I assumed they're treated as a
 *         string but they could also be treated as multi valued properties.
 *         Maybe it is better to change it to this?
 */

public interface Filter {
	/**
	 * A URI relative the ancestor of this node, with optional wildcards,
	 * selecting a set of sub-nodes {@code N}. Wildcards can be an asterisk
	 * (\u002A '*') or a minus sign (\u002D '-'). An asterisk can be used in
	 * place of a single node name in the URI, a minus sign stands for any
	 * number of consecutive node names. The default value of this node is the
	 * empty string, which indicates that no nodes must be selected. Changing
	 * this value has no effect until the {@link #Result()} or
	 * {@link #ResultUriList()} is read to get {@code N}.
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

}
