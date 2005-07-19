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

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.dmt.*;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

// TODO replace System.out-s with log messages
public class DmtPluginDispatcher implements ServiceTrackerCustomizer {
    private BundleContext  bc;
    private ArrayList      plugins;
    private Plugin         rootPlugin;

    DmtPluginDispatcher(BundleContext bc) {
        this.bc = bc;
        plugins = new ArrayList();
        // root plugin could be a normal plugin when overlapping is allowed
        DmtReadOnlyDataPlugin root = new RootPlugin(this); 
        rootPlugin = new Plugin(root, new String[] { "." }, new String[] {});
    }

    public synchronized Object addingService(ServiceReference serviceRef) {
        String[] roots = getURIs(serviceRef, "dataRootURIs");
        String[] execs = getURIs(serviceRef, "execRootURIs");

        if (roots == null || execs == null)
            return null;
        if (roots.length == 0 && execs.length == 0) {
            System.out.println("Plugin is not registered for any nodes, ignoring plugin.");
            return null;
        }

        if (pluginConflict(roots, execs, plugins)) {
            System.out.println("Plugin data or exec roots (" +
                    Arrays.asList(roots) + ", " + Arrays.asList(execs) +
                    ") conflict with a previously registered plugin; " +
                    "ignoring this plugin.");
            return null;
        } 
        
        List invalidRoots = getInvalidRoots(roots);
        if(invalidRoots.size() != 0) {
            System.out.println("Ignoring plugin because of invalid "
                    + "plugin data roots '" + invalidRoots
                    + "': the node or the parent must already exist.");
            return null;
        }

        Plugin plugin = new Plugin(bc.getService(serviceRef), roots, execs);
        plugins.add(plugin);
        
        return plugin;
    }
    
    public void modifiedService(ServiceReference serviceRef, Object plugin) {
        // not updating the plugin list, reg. properties shouldn't be updated
        // runtime
    }

    public synchronized void removedService(ServiceReference serviceRef, Object plugin) {
        // removes the plugin and ungets the service
        plugins.remove(plugin);
        bc.ungetService(serviceRef);
    }

    synchronized Plugin getDataPlugin(String nodeUri) {
        Iterator i = plugins.iterator();
        while (i.hasNext()) {
            Plugin plugin = (Plugin) i.next();
            if (plugin.handlesData(nodeUri))
                return plugin;
        }
        return rootPlugin;
    }

    synchronized DmtExecPlugin getExecPlugin(String nodeUri) {
        Iterator i = plugins.iterator();
        while (i.hasNext()) {
            Plugin plugin = (Plugin) i.next();
            if (plugin.handlesExec(nodeUri))
                return plugin.getExecPlugin();
        }
        return null;
    }

    synchronized boolean handledBySameDataPlugin(String nodeUri1, String nodeUri2) {
        Iterator i = plugins.iterator();
        while (i.hasNext()) {
            Plugin plugin = (Plugin) i.next();
            if (plugin.handlesData(nodeUri1))
                return plugin.handlesData(nodeUri2);
            if (plugin.handlesData(nodeUri2))
                return false;
        }
        return true; // both URIs are handled by the root plugin
    }

    // finds all plugin root URIs that are directly below the given uri, and
    // returns the set of their last segments
    synchronized Set getChildPluginNames(String uri) {
        Set childPluginNames = new HashSet();
        
        Iterator i = plugins.iterator();
        while(i.hasNext())
            childPluginNames.addAll(((Plugin) i.next()).getChildRootNames(uri));

        return childPluginNames;
    }
    
    private String[] getURIs(ServiceReference serviceRef, String propertyName) {
        // property might be modified later, but framework RI gives us a copy
        Object property = serviceRef.getProperty(propertyName);
        if (property == null)
            return new String[] {};
        
        String[] uris;
        if (property instanceof String)
            uris = new String[] {(String) property};
        else if (property instanceof String[])
            uris = (String[]) property;
        else {
            System.out.println("Plugin property '" + propertyName + "' does "
                    + "not contain 'String' or 'String[]', ignoring plugin.");
            return null;
        }
        
        for (int i = 0; i < uris.length; i++) {
            try {
                uris[i] = Utils.validateAndNormalizeAbsoluteUri(uris[i]);
            } catch(DmtException e) {
                System.out.println("Invalid URI '" + uris[i] + "' in property '" 
                        + propertyName + "', ignoring plugin.");
                e.printStackTrace(System.out);
                return null;
            }
            if (!Utils.isAbsoluteUri(uris[i])) {
                System.out.println("Relative URI '" + uris[i] + "' in " +
                        "property '" + propertyName + "', ignoring plugin.");
                return null;
            }
        }
        return uris;
    }

    private static boolean pluginConflict(String[] roots, String[] execs,
                                          ArrayList plugins) {
        Iterator i = plugins.iterator();
        while (i.hasNext())
            if(((Plugin) i.next()).conflictsWith(roots, execs))
                return true;
        return false;
    }

    private List getInvalidRoots(String[] roots) {
        DmtReadOnlyDataPlugin root = rootPlugin.getReadOnlyDataPlugin();
        List list = new Vector();
        for (int i = 0; i < roots.length; i++) {
            // roots must not contain "." for the parent to be valid, this is
            // guaranteed by the conflict check (the root plugin provides ".")
            if(!root.isNodeUri(roots[i]) && 
                    !root.isNodeUri(Utils.parentUri(roots[i])))
                list.add(roots[i]);
        }
        
        return list;
    }
}
