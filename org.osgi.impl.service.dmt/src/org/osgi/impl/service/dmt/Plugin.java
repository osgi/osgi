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
					"The plugin must implement DmtDataPlugIn or DmtReadOnlyDataPlugIn "
							+ "if data roots are specified.");
		if (execs.length > 0 && !(plugin instanceof DmtExecPlugIn))
			throw new IllegalArgumentException(
					"The plugin must implement DmtExecPlugIn if exec nodes are specified");
		this.plugin = plugin;
		this.roots = Utils.normalizeAbsoluteUris(roots);
		this.execs = Utils.normalizeAbsoluteUris(execs);
	}

	public Object getDataPlugin() {
		if (!isDataPlugin(plugin))
			throw new IllegalStateException(
					"Plugin object is not a data plugin.");
		return (DmtDataPlugIn) plugin;
	}

	private boolean isDataPlugin(Object plugin) {
		return (plugin instanceof DmtDataPlugIn)
				|| (plugin instanceof DmtReadOnlyDataPlugIn);
	}

	public DmtExecPlugIn getExecPlugin() {
		if (!(plugin instanceof DmtExecPlugIn))
			throw new IllegalStateException(
					"Plugin object is not an exec plugin.");
		return (DmtExecPlugIn) plugin;
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
