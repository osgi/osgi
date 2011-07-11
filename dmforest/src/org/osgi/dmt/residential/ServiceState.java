package org.osgi.dmt.residential;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;
import info.dmtree.*;

import org.osgi.dmt.ddf.*;

/**
 * ServiceState sub-tree root node containing information existing on the OSGi
 * framework. The children of this node must represent the actual service status
 * when this sub-tree is accessed.
 * 
 * @remark should this not be part of the BundleState?
 */

public interface ServiceState {

	/**
	 * The Properties for this service.The list only contains properties that
	 * have values from the simple set: boolean, byte, char, short, int, long,
	 * float, double, String, the primitive wrappers, or arrays/collections of
	 * these.
	 * 
	 * @remark This node is indicated as transient but I do not understand why?
	 * 
	 * @return MAP of Property
	 */
	@Scope(A)
	MAP<Long, Property> Property();

	/**
	 * The Bundle Id of the Bundle that registered this service. This value is
	 * derived from the Service Reference {@code getBundle()} method.
	 * 
	 * @return The Bundle id of the Bundle that registered this service.
	 */

	@Scope(A)
	long RegisteringBundle();

	/**
	 * A LIST of Bundle Ids of the Bundles that are using this service. This
	 * list is derived from the Service Reference {@code getUsingBundles()}
	 * method.
	 * 
	 * @return A LIST of Bundle Ids of the Bundles that are using this service.
	 */
	@Scope(A)
	@NodeType(DmtConstants.DDF_LIST_SUBTREE)
	MAP<Long, Long> UsingBundles();

	/**
	 * The optional extension node.
	 * 
	 * @return The extension node.
	 */
	@Scope(A)
	Opt<NODE> Ext();

	/**
	 * A node that holds a single property key/value pair. The Property node
	 * contains a service property. A Property node must be able to contain all
	 * service property types: string, boolean or numeric data types including
	 * single-dimension arrays or collections. However non-serializable data
	 * types can be discarded.
	 */
	public interface Property {
		/**
		 * The key of the property.
		 * 
		 * @return The key of the property
		 */
		@Scope(A)
		String Key();

		/**
		 * The type of the property. This must be the fully qualified name of
		 * the Java class. This is only guaranteed to work for the base types
		 * like:
		 * <ul>
		 * <li>java.lang.String</li>
		 * <li>java.lang.Byte</li>
		 * <li>java.lang.Short</li>
		 * <li>java.lang.Integer</li>
		 * <li>java.lang.Long</li>
		 * <li>java.lang.Float</li>
		 * <li>java.lang.Double</li>
		 * </ul>
		 * 
		 * @remark not clear how primitive array like byte[] are handled?
		 * 
		 * @return The value of Type node
		 */
		@Scope(A)
		String Type();

		/**
		 * The Cardinality of the property. The value of the properties is
		 * always a Multiple in the Device Management Tree but as a service it
		 * could be a scalar, an array, or a collection. The Cardinality
		 * indicates what form was used in the service registration.
		 * 
		 * <ul>
		 * <li>{@code SCALAR} - The value was registered as a single scalar.
		 * {@link #Values()} contains 1 value.</li>
		 * <li>{@code ARRAY} - The value was registered as an array.</li>
		 * <li>{@code COLLECTION} - The value was registered as a Collection.</li>
		 * </ul>
		 * 
		 * @return The Cardinality of the property
		 */
		@Scope(A)
		String Cardinality();

		/**
		 * A list of values. The {@link #Cardinality()} defines how this list
		 * must be interpreted. The values are leaf nodes of data type defined
		 * in {@link #Type()}. If there is a mapping from the Java type to a Dmt
		 * Format then the node must have the corresponding format. That is, for
		 * java.lang.Long, the Dmt Format {@code long} must be used. If there
		 * does not exist a mapping, the nodes must have format {@code string}.
		 * <p>
		 * 
		 * @return The list of values
		 */
		@Scope(A)
		@NodeType(DmtConstants.DDF_LIST_SUBTREE)
		MAP<Long, NODE> Values();

		/**
		 * Optional extension node.
		 * 
		 * @return optional extension node.
		 */
		@Scope(A)
		Opt<NODE> Ext();
	}
}
