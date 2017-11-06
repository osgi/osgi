
package org.osgi.service.cdi.dto;

import java.util.Map;
import org.osgi.service.cdi.dto.model.ConfigurationModelDTO;
import org.osgi.service.cdi.dto.model.DependencyModelDTO.MaximumCardinality;

/**
 * A snapshot of the runtime state of a {@link ComponentFactoryDTO component
 * factory} configuration dependency
 * 
 * @NotThreadSafe
 * @author $Id: $
 */
public class ConfigurationDTO extends DependencyDTO {
	/**
	 * The static model of this configuration dependency as resolved at
	 * initialization time.
	 */
	public ConfigurationModelDTO	model;

	/**
	 * The set of configuration properties that match this configuration
	 * dependencies.
	 * <p>
	 * The value must not be null. An empty array indicates no matching
	 * configurations.
	 * <p>
	 * This dependency is satisfied when.
	 * <p>
	 * <pre>
	 * {@link DependencyDTO#minimumCardinality minimumCardinality} <= matches.size <= {@link MaximumCardinality#toInt() model.maximumCardinality.toInt()}
	 * </pre>
	 * <p>
	 * Each map contains a the standard Configuration Admin keys
	 * <code>service.pid</code> and optionally <code>service.factoryPid<code>
	 * depending on the {@link MaximumCardinality model.maximumCardinality}
	 * 
	 */
	public Map<String, Object>[]	matches;
}
