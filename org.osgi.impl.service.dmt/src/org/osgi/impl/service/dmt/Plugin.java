/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.dmt;

import org.osgi.service.dmt.*;

class Plugin {
	Object		plugin;
	String[]	roots;
	String[]	execs;

	// precondition: roots != null && execs != null && (roots.length != 0 ||
	// execs.length != 0)
	public Plugin(Object plugin, String[] roots, String[] execs) {
		if (roots.length > 0 && !isDataPlugin(plugin))
			throw new IllegalArgumentException(
					"The plugin must implement DmtDataPlugin or DmtReadOnlyDataPlugin "
							+ "if data roots are specified.");
		if (execs.length > 0 && !(plugin instanceof DmtExecPlugin))
			throw new IllegalArgumentException(
					"The plugin must implement DmtExecPlugin if exec nodes are specified");
		this.plugin = plugin;
		this.roots = Utils.normalizeAbsoluteUris(roots);
		this.execs = Utils.normalizeAbsoluteUris(execs);
	}

	public Object getDataPlugin() {
		if (!isDataPlugin(plugin))
			throw new IllegalStateException(
					"Plugin object is not a data plugin.");
		return (DmtDataPlugin) plugin;
	}

	private boolean isDataPlugin(Object plugin) {
		return (plugin instanceof DmtDataPlugin)
				|| (plugin instanceof DmtReadOnlyDataPlugin);
	}

	public DmtExecPlugin getExecPlugin() {
		if (!(plugin instanceof DmtExecPlugin))
			throw new IllegalStateException(
					"Plugin object is not an exec plugin.");
		return (DmtExecPlugin) plugin;
	}

	// TODO both data and exec URIs now indicate subtrees, common code should be
	// used
	public boolean handlesData(String subtreeUri) {
		for (int i = 0; i < roots.length; i++)
			if (Utils.isAncestor(roots[i], subtreeUri))
				return true;
		return false;
	}

	public boolean handlesExec(String nodeUri) {
		for (int i = 0; i < execs.length; i++)
			if (Utils.isAncestor(execs[i], nodeUri))
				return true;
		return false;
		// return Arrays.asList(execs).contains(nodeUri);
	}
}
