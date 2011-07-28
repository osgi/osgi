package org.osgi.dmt.residential;

import org.osgi.dmt.ddf.*;

/**
 * A Wire is a link between two bundles where the semantics of this link is
 * defined by the used name space.
 */
public interface Wire {
	/**
	 * The name space of this wire. Can be:
	 * <ul>
	 * <li>osgi.wiring.bundle - Defined in the OSGi Core</li>
	 * <li>osgi.wiring.package - Defined in the OSGi Core</li>
	 * <li>osgi.wiring.host - Defined in the OSGi Core</li>
	 * <li>osgi.residential.service - Defined in this specification</li>
	 * <li>* - Generic name spaces</li>
	 * </ul>
	 * The osgi.residential.service name space is not defined by the OSGi Core
	 * as it is not part of the module layer. The name space has the following
	 * layout:
	 * <ul>
	 * <li>Requirement - A filter on the service.id service property. No
	 * attributes are defined</li>
	 * <li>Capability - All service properties as attributes. No defined
	 * directives.</li>
	 * <li>Requirer - The bundle that has gotten the service</li>
	 * <li>Provider - The bundle that has registered the service</li>
	 * </ul>
	 * There is a wire for each registration-get pair. That is, if a service is
	 * registered by A and gotten by B and C then there are two wires:
	 * {@code B->A} and {@code C->A}.
	 * 
	 * @return The name space for this wire.
	 */
	String NameSpace();

	/**
	 * The Requirement that caused this wire.
	 * 
	 * @return The requirement that caused this wire.
	 */
	Requirement Requirement();

	/**
	 * The Capability that satisfied the requirement of this wire.
	 * 
	 * @return The capability that satisfied the requirement for this wire.
	 */
	Capability Capability();

	/**
	 * The location of the Bundle that contains the requirement for this wire.
	 * 
	 * @return The requirer's location
	 */
	String Requirer();

	/**
	 * The location of the Bundle that provides the capability for this wire.
	 * 
	 * @return The provider's location
	 */
	String Provider();

	/**
	 * Instance Id to allow addressing by Instance Id.
	 * 
	 * @return The InstanceId
	 */

	int InstanceId();

	/**
	 * Describes a Requirement.
	 */
	public interface Requirement {
		/**
		 * The Filter string for this requirement.
		 * 
		 * @return The Filter string for this requirement
		 */
		String Filter();

		/**
		 * The Directives for this requirement. These directives must contain
		 * the filter: directive as described by the Core.
		 * 
		 * @return The Directives for this requirement.
		 */
		MAP<String, String> Directives();

		/**
		 * The Attributes for this requirement.
		 * 
		 * @return The Attributes for this requirement.
		 */
		MAP<String, String> Attributes();

		/**
		 * Instance Id to allow addressing by Instance Id.
		 * 
		 * @return The InstanceId
		 */

		int InstanceId();
	}

	/**
	 * Describes a Capability.
	 */
	public interface Capability {
		/**
		 * The Directives for this requirement.
		 * 
		 * @return The Directives for this capability.
		 */
		MAP<String, String> Directives();

		/**
		 * The Attributes for this capability.
		 * 
		 * @return The Attributes for this requirement.
		 */
		MAP<String, String> Attributes();

		/**
		 * Instance Id to allow addressing by Instance Id.
		 * 
		 * @return The InstanceId
		 */

		int InstanceId();
	}
}
