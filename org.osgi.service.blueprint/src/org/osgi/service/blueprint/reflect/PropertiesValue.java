package org.osgi.service.blueprint.reflect;

import java.util.Properties;

/**
 * A java.util.Properties based value
 */
public interface PropertiesValue extends Value {

	Properties getPropertiesValue();
	
}
