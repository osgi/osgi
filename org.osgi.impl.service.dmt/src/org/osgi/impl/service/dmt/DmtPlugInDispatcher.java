package org.osgi.impl.service.dmt;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.dmt.*;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class DmtPlugInDispatcher implements ServiceTrackerCustomizer {
	private BundleContext			bc;
	private ArrayList				plugins;
	private DmtReadOnlyDataPlugIn	dataRoot;

	public DmtPlugInDispatcher(BundleContext bc) {
		this.bc = bc;
		plugins = new ArrayList();
		// TODO maybe get the root plugin as a service (but this needs a new
		// service interface to control permissions)
		dataRoot = new RootPlugin();
	}

	public Object addingService(ServiceReference serviceRef) {
		String[] roots = getURIs(serviceRef, "dataRootURIs");
		String[] execs = getURIs(serviceRef, "execRootURIs");
		if (roots == null || execs == null)
			return null;
		if (roots.length == 0 && execs.length == 0) {
			System.out
					.println("Plugin is not registered for any nodes, ignoring plugin.");
			return null;
		}
		// gets the service and stores the plugin
		Object pluginService = bc.getService(serviceRef);
		Plugin plugin = new Plugin(pluginService, roots, execs);
		// TODO the check and the add operation should be performed atomically
		if (!pluginConflict(plugin, plugins))
			plugins.add(plugin);
		else {
			System.out
					.println("Plugin manages some nodes already handled by another plugin; ignoring this plugin.");
			// TODO pluginService should be "ungotten", (or not even gotten
			// before the conflict is checked)
			return null;
		}
		return pluginService;
	}

	public void modifiedService(ServiceReference serviceRef, Object plugin) {
		// TODO! update the plugin list
	}

	public void removedService(ServiceReference serviceRef, Object plugin) {
		// removes the plugin and ungets the service
		plugins.remove(plugin);
		bc.ungetService(serviceRef);
	}

	public Object getDataPlugin(String nodeUri) {
		Iterator i = plugins.iterator();
		while (i.hasNext()) {
			Plugin plugin = (Plugin) i.next();
			if (plugin.handlesData(nodeUri))
				return plugin.getDataPlugin();
		}
		return dataRoot;
	}

	public DmtExecPlugIn getExecPlugin(String nodeUri) {
		Iterator i = plugins.iterator();
		while (i.hasNext()) {
			Plugin plugin = (Plugin) i.next();
			if (plugin.handlesExec(nodeUri))
				return plugin.getExecPlugin();
		}
		return null;
	}

	public boolean handledBySameDataPlugin(String nodeUri1, String nodeUri2) {
		Iterator i = plugins.iterator();
		while (i.hasNext()) {
			Plugin plugin = (Plugin) i.next();
			if (plugin.handlesData(nodeUri1))
				return plugin.handlesData(nodeUri2);
			if (plugin.handlesData(nodeUri2))
				return false;
		}
		return false;
	}

	private String[] getURIs(ServiceReference serviceRef, String propertyName) {
		Object property = serviceRef.getProperty(propertyName);
		//        System.out.println("Plugin property '" + propertyName + "': " +
		// property);
		if (property == null)
			return new String[] {};
		String[] uris;
		if (property instanceof String)
			uris = new String[] {(String) property};
		else
			if (property instanceof String[])
				uris = (String[]) property;
			else {
				System.out
						.println("Plugin property '"
								+ propertyName
								+ "' does "
								+ "not contain 'String' or 'String[]', ignoring plugin.");
				return null;
			}
		for (int i = 0; i < uris.length; i++) {
			if (!Utils.isValidUri(uris[i])) {
				System.out.println("Invalid URI '" + uris[i]
						+ "' in property '" + propertyName
						+ "', ignoring plugin.");
				return null;
			}
		}
		return uris;
	}

	private boolean pluginConflict(Plugin plugin, ArrayList plugins) {
		// TODO! check that the new plugin does not conflict with any of the
		// previously registered ones.
		return false;
	}
}
