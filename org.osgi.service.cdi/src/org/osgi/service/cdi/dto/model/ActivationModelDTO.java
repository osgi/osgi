
package org.osgi.service.cdi.dto.model;

import java.util.Map;
import org.osgi.service.cdi.dto.ActivationDTO;

/**
 * A description an activation of a component
 * <p>
 * The content of this DTO is resolved form metadata at initialization time and
 * remains the same between the CDI bundle restarts.
 * <p>
 * Can be either an OSGi service publication or a creation of a bean or both in
 * case of {@link Scope#SINGLETON}.
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public class ActivationModelDTO {
	/**
	 * Possible values for {@link #scope}.
	 */
	public enum Scope {
		/**
		 * This activation will only ever create one instance
		 * <p>
		 * The instance is created as after the parent component becomes satisfied and
		 * is destroyed before the parent component becomes unsatisfied.
		 * <p>
		 * The value of {@link ActivationDTO#instances} is at most <code>1</code> when
		 * this scope is used.
		 * <p>
		 * If {@link #serviceClasses} is not empty the instance will be registered as an
		 * OSGi service with <code>service.scope=singletion</code>
		 */
		SINGLETON,
		/**
		 * This activation will register an OSGi service with
		 * <code>service.scope=bundle</code>
		 * <p>
		 * The service is registered just after all {@link #SINGLETON} activations ares
		 * set up and just before all {@link #SINGLETON} activations are torn down.
		 * <p>
		 * The {@link #serviceClasses} is not empty when this scope is used.</code>
		 * <p>
		 * The value of {@link ActivationDTO#instances} is equal to the number of
		 * bundles bound to the registered service.
		 */
		BUNDLE,
		/**
		 * This activation will register an OSGi service with
		 * <code>service.scope=prototype</code>
		 * <p>
		 * The service is registered just after all {@link #SINGLETON} activations ares
		 * set up and just before all {@link #SINGLETON} activations are torn down.
		 * <p>
		 * The {@link #serviceClasses} is not empty when this scope is used.</code>
		 * <p>
		 * The value of {@link ActivationDTO#instances} is equal to the number of bound
		 * service objects produced by the registered service.
		 */
		PROTOTYPE
	}

	/**
	 * The {@link Scope} of this activation
	 * <p>
	 * Must not be null.
	 */
	public Scope				scope;

	/**
	 * Describes the set of fully qualified names of the interfaces/classes under
	 * which this activation will publish and OSGi service
	 * <p>
	 * Must not be null. An empty array indicated this activation will not publish
	 * an OSGi service
	 */
	public String[]				serviceClasses;

	/**
	 * The default properties which will be used to configure this activation.
	 * <p>
	 * These will be merged with the properties of all configurations this
	 * activation is exposed to.
	 * <p>
	 * Must not be null. May be empty if this activation does not specify default
	 * properties.
	 */
	public Map<String, Object>	properties;
}
