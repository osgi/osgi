package org.osgi.service.blueprint.reflect;

import java.util.Map;

/**
 * A map-based value. Map keys are instances of Value, as are the Map entry
 * values themselves.
 * 
 */
public interface MapValue extends Value, Map/*<Value,Value>*/ {

}
