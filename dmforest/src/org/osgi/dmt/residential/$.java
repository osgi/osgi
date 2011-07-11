package org.osgi.dmt.residential;

import org.osgi.dmt.ddf.*;

/**
 * $ is the parent node of the residential tree, also called the root. The root
 * sub-tree contains a number of OSGi Frameworks.
 * <p>
 * OSGi nodes are automatically removed after they are shutdown. There is one
 * OSGi node that represents the {@link OSGi#Local()} framework. A
 * Framework must always use the same key when it registers itself.
 * 
 * @remark it seems very awkward that there is no direct path to the local framework
 */
public interface $ {
	/**
	 * The OSGi node represents a list of Framework instances on the current
	 * device. This list can change depending on the number of running framework
	 * instances on this device. If a framework is shutdown its corresponding
	 * node will automatically be removed.
	 * 
	 * @return The roots for the available Residential OSGi Frameworks.
	 */
	MAP<Long, OSGi> OSGi();
}
