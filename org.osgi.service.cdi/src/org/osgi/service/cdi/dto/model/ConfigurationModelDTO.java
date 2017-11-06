
package org.osgi.service.cdi.dto.model;

/**
 * A description of a configuration dependency of a component
 * 
 * The content of this DTO is resolved form metadata at initialization time and
 * remains the same between the CDI bundle restarts.
 * 
 * Singleton configuration:
 * <ul>
 * <li>{@link DependencyModelDTO#maximumCardinality maximumCardinality} =
 * {@link DependencyModelDTO.MaximumCardinality#ONE ONE}</li>
 * <li>{@link DependencyModelDTO#minimumCardinality minimumCardinality} = 1</li>
 * <li>{@link DependencyModelDTO#dynamic dynamic} = false</li>
 * <li>{@link DependencyModelDTO#greedy greedy} = true</li>
 * </ul>
 *
 * Factory configuration:
 * <ul>
 * <li>{@link DependencyModelDTO#maximumCardinality maximumCardinality} =
 * {@link DependencyModelDTO.MaximumCardinality#MANY MANY}</li>
 * <li>{@link DependencyModelDTO#minimumCardinality minimumCardinality} = 0</li>
 * <li>{@link DependencyModelDTO#dynamic dynamic} = true</li>
 * <li>{@link DependencyModelDTO#greedy greedy} = true</li>
 * </ul>
 * 
 * @NotThreadSafe
 * @author $Id: $
 */
public class ConfigurationModelDTO extends DependencyModelDTO {
	/**
	 * The pid of the tracked configuration objects
	 * <p>
	 * The value must not be null.
	 */
	public String pid;
}
