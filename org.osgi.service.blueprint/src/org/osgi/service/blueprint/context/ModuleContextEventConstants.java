package org.osgi.service.blueprint.context;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * Event property names used in EventAdmin events published for a 
 * module context.
 *
 */
public interface ModuleContextEventConstants {

	/**
	 * The version property defining the bundle on whose behalf a module context
	 * event has been issued.
	 * 
	 * @see Version
	 */
	public static final String BUNDLE_VERSION = "bundle.version";
	
	/**
	 * The extender bundle property defining the extender bundle processing the
	 * module context for which an event has been issued.
	 * 
	 * @see Bundle
	 */
	public static final String EXTENDER_BUNDLE = "extender.bundle";
	
	/**
	 * The extender bundle id property defining the id of the extender bundle
	 * processing the module context for which an event has been issued. 
	 */
	public static final String EXTENDER_ID = "extender.bundle.id";
	
	/**
	 * The extender bundle symbolic name property defining the symbolic name of 
	 * the extender bundle processing the module context for which an event 
	 * has been issued.
	 */
	public static final String EXTENDER_SYMBOLICNAME = "extender.bundle.symbolicName";
	
}
