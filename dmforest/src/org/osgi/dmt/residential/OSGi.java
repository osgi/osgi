package org.osgi.dmt.residential;

import info.dmtree.*;

import org.osgi.dmt.ddf.*;
import org.osgi.dmt.service.log.*;

/**
 * The Residential type is the functional root for an OSGi framework running in
 * a residential gateway. It describes the layout of the nodes available in a
 * residential gateway like the Framework, the BundleState, etc. It is available
 * from $/OSGi/[id]. $ represents an arbitrary node in the Residential
 * Management Tree that hosts the OSGi Device Management tree. The [id] is a
 * unique key for a framework that runs on the device.
 * 
 * @remark why do some have NodeType and others not?
 */

public interface OSGi {
	/**
	 * Local framework. In some cases there can exist several OSGi framework
	 * instances in a Residential Management Tree. The <em>local</em> OSGi
	 * framework on which the bundle registering data plug-in of the Residential
	 * Management Tree is running should be identified. Therefore, the Local
	 * node represents whether the OSGi framework object is local ({@code true})
	 * or not ({@code false}). How the data plug-in of the Residential
	 * Management Tree on the local OSGi framework finds and communicates with
	 * the other OSGi frameworks is out of scope of this document. At least one
	 * Framework must be available, therefore the manipulation of the OSGi
	 * residential platform is always available.
	 * 
	 * @remark I guess by definition the local must always be available?
	 * 
	 * @remark not sure if this makes sense, why are the other fws not local?
	 *         Shouldn't we not just have the root be in $ of the local
	 *         framework and have a Other node in it?
	 * 
	 * @return If {@code true} this framework is the <em>local</em> framework.
	 */
	boolean Local();

	/**
	 * Return the Framework node that is used to control the framework life
	 * cycle.
	 * 
	 * @return the Framework node
	 */
	@NodeType("org.osgi/1.0/FrameworkManagementObject")
	Framework Framework();

	/**
	 * Return the dynamic states of all installed bundles.The sub-nodes are
	 * named according to the bundle id. Each sub-node is a {@link BundleState}
	 * node.
	 * 
	 * @remark ensure that bundle-id or some mapping can be used. Disagree with
	 *         the +1
	 * @remark Should does not be part of Framework?
	 * 
	 * @return The Bundle State for the installed bundles.
	 */
	@NodeType("org.osgi/1.0/BundleStateManagementObject")
	MAP<Long, BundleState> BundleState();

	/**
	 * The Package State node maintains the information about packages. This
	 * object can be used to retrieve package dependencies between bundles and
	 * maps to the Bundle Wiring API. The 
	 * 
	 * @remark This node is indicated as transient but I do not understand why?
	 * @remark Should this node not be part of the Bundle State?
	 * 
	 * @return The PackageState Node
	 */
	@NodeType("org.osgi/1.0/PackageStateManagementObject")
	LIST<PackageState> PackageState();

	/**
	 * Service State contains the information about the registered services. The
	 * name of the child nodes is maintained by the Framework node and does not
	 * correspond to the service id. The number must start at least at 1.
	 * 
	 * @remark This node is indicated as transient but I do not understand why?
	 * @remark Should this node not be part of the Bundle State?
	 * @remark The spec says the id can be a service.pid or a generated number.
	 *         This is not possible because these spaces can overlap?
	 * 
	 * @return The ServiceState Node
	 */
	@NodeType("org.osgi/1.0/ServiceStateManagementObject")
	MAP<Long, ServiceState> ServiceState();

	/**
	 * The Filters node searches the nodes in a tree that correspond to a target
	 * URI and an optional filter expression. A new {@link Filter} is created by
	 * adding a node to the Filters node with a unique number, it must be a long
	 * higher than any of the existing numbers in the MAP.
	 * 
	 * A {@link Filter} uses a root for its relative path. This root for filters
	 * created through this {@link #Filters()} node is the $/[id]/OSGi node,
	 * i.e. this node.
	 * 
	 * @remark The interface is very cumbersome because the management system
	 *         must now first read all nodes to find the highest value.
	 * @remark This node is indicated as transient but I do not understand why?
	 * @return The registered Filters
	 */
	Opt<MutableMAP<Long, Filter>> Filters();

	/**
	 * Provides access to the entries of a bundle, the key is the Bundle Id and
	 * the value is the root of the entries in the JAR file as defined with
	 * {@code getEntry(path)}.
	 * 
	 * The Node in the MAP maps to the root of the JAR file. To access an entry
	 * the path of the entry must be suffixed to the path of the Entry for the
	 * corresponding bundle. For example, to read the manifest of bundle 15, the
	 * following path is used:
	 * 
	 * <pre>
	 * &quot;$/OSGi/1/Entry/15/META-INF/MANIFEST.MF&quot;
	 * </pre>
	 * 
	 * It is theoretically possible that an entry path could not be mapped to a
	 * node URI. It is therefore necessary to mangle each path segment, this
	 * will escape an backslashes and in the unlikely case the path segment is
	 * too long, replace the name with a hash. If this happens, then the path
	 * cannot be found in the bundle. The implementation must then mangle the
	 * segments of the paths in the bundle to find the matching entry. The
	 * resulting node must have a format of {@link DmtData#FORMAT_BINARY}.
	 * 
	 * @remark renamed to Entry
	 * @remark
	 * @return The Entry node
	 */
	Opt<MAP<Long, MAP<String,BundleResource>>> Entry();

	/**
	 * Access to the optional Log. The key of the MAP is a unique identifier for
	 * the Log request that must be generated by the initiator. This Log request
	 * must then filled and after which the result can be obtained. See
	 * {@link Log}.
	 * 
	 * @return The value for the optional Log node
	 */

	Opt<MutableMAP<Long, Log>> Log();

	/**
	 * Optional extension node.
	 * 
	 * @return optional extension node.
	 */
	Opt<NODE> Ext();
}
