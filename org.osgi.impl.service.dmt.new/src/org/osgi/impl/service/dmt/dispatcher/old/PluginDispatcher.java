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
package org.osgi.impl.service.dmt.dispatcher.old;

import java.util.*;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.dmt.Context;
import org.osgi.impl.service.dmt.RootPlugin;
import org.osgi.impl.service.dmt.dispatcher.Dispatcher;
import org.osgi.impl.service.dmt.dispatcher.Node;
import org.osgi.impl.service.dmt.dispatcher.Plugin;

import info.dmtree.DmtException;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ExecPlugin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class PluginDispatcher implements ServiceTrackerCustomizer, Dispatcher {
    private Context            context;
    private ArrayList          plugins;
    private Plugin rootPlugin;

    public PluginDispatcher(Context context) {
        this.context = context;
        plugins = new ArrayList();
        // root plugin could be a normal plugin when overlapping is allowed
        DataPlugin root = new RootPlugin(this); 
        rootPlugin = 
            new PluginRegistration(null, root, new NodeImpl[] { NodeUtil.ROOT_NODE }, 
                    new NodeImpl[] {});
    }

    public synchronized Object addingService(ServiceReference serviceRef) {
        NodeImpl[] roots = getURIs(serviceRef, "dataRootURIs");
        NodeImpl[] execs = getURIs(serviceRef, "execRootURIs");
        
        String description = (String) 
            serviceRef.getProperty(Constants.SERVICE_DESCRIPTION);
        if(description == null)
            description = "";
        else
            description = " '" + description + "'";

        if (roots == null || execs == null)
            return null;
        if (roots.length == 0 && execs.length == 0) {
            context.log(LogService.LOG_WARNING, "Plugin" + description + 
                    " is not registered for any nodes, ignoring plugin.", null);
            return null;
        }

        if (selfConflict(roots) || selfConflict(execs)) {
            context.log(LogService.LOG_WARNING, "Plugin data or exec roots (" +
                    Arrays.asList(NodeUtil.getUriArray(roots)) + ", " +
                    Arrays.asList(NodeUtil.getUriArray(execs)) +
                    ") contain conflicting entries; ignoring plugin" + 
                    description + ".", 
                    null);
            return null;
        }
            
        if (pluginConflict(roots, execs, plugins)) {
            context.log(LogService.LOG_WARNING, "Plugin data or exec roots (" +
                    Arrays.asList(NodeUtil.getUriArray(roots)) + ", " +
                    Arrays.asList(NodeUtil.getUriArray(execs)) +
                    ") conflict with a previously registered plugin; " +
                    "ignoring plugin" + description + ".", null);
            return null;
        } 
        
        List invalidRootUris = getInvalidRoots(roots);
        if(invalidRootUris.size() != 0) {
            context.log(LogService.LOG_WARNING, "Ignoring plugin" + 
                    description + " because of invalid plugin data roots '" + 
                    invalidRootUris + "': the node or the parent must " +
                    "already exist.", null);
            return null;
        }

        Plugin plugin = new PluginRegistration(serviceRef,
                context.getBundleContext().getService(serviceRef), roots, execs);
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
        context.getBundleContext().ungetService(serviceRef);
    }

    public synchronized Plugin getDataPlugin(Node node) {
        Iterator i = plugins.iterator();
        while (i.hasNext()) {
            PluginRegistration plugin = (PluginRegistration) i.next();
            if (plugin.handlesData(node))
                return plugin;
        }
        return rootPlugin;
    }

    public synchronized ExecPlugin getExecPlugin(Node node) {
        Iterator i = plugins.iterator();
        while (i.hasNext()) {
            PluginRegistration plugin = (PluginRegistration) i.next();
            if (plugin.handlesExec(node))
                return plugin.getExecPlugin();
        }
        return null;
    }

    public synchronized boolean handledBySameDataPlugin(Node node1, Node node2) {
        Iterator i = plugins.iterator();
        while (i.hasNext()) {
            PluginRegistration plugin = (PluginRegistration) i.next();
            if (plugin.handlesData(node1))
                return plugin.handlesData(node2);
            if (plugin.handlesData(node2))
                return false;
        }
        return true; // both URIs are handled by the root plugin
    }

    // finds all plugin root URIs that are directly below the given node, and
    // returns the set of their last segments
    // INTERFACE
    public synchronized Set getChildPluginNames(String[] path) {
        Set childPluginNames = new HashSet();
        
        Iterator i = plugins.iterator();
        while(i.hasNext())
            childPluginNames.addAll(((PluginRegistration) i.next())
                    .getChildRootNames(path));

        return childPluginNames;
    }
    
    private NodeImpl[] getURIs(ServiceReference serviceRef, String propertyName) {
        // property might be modified later, but framework RI gives us a copy
        Object property = serviceRef.getProperty(propertyName);
        if (property == null)
            return new NodeImpl[] {};
        
        String[] uris;
        if (property instanceof String)
            uris = new String[] {(String) property};
        else if (property instanceof String[])
            uris = (String[]) property;
        else {
            context.log(LogService.LOG_WARNING, "Plugin property '" + 
                    propertyName + "' does not contain 'String' or " +
                    "'String[]', ignoring plugin.", null);
            return null;
        }
        
        NodeImpl[] nodes = new NodeImpl[uris.length];
        for (int i = 0; i < uris.length; i++) {
            try {
                nodes[i] = NodeUtil.validateAndNormalizeUri(uris[i]);
            } catch(DmtException e) {
                context.log(LogService.LOG_WARNING, "Invalid URI '" + uris[i] + 
                        "' in property '" + propertyName + 
                        "', ignoring plugin.", e);
                return null;
            }
            if (!nodes[i].isAbsolute()) {
                context.log(LogService.LOG_WARNING, "Relative URI '" + uris[i] +
                        "' in property '" + propertyName + "', ignoring " +
                        "plugin.", null);
                return null;
            }
        }
        return nodes;
    }

    private static boolean pluginConflict(NodeImpl[] roots, NodeImpl[] execs,
                                          ArrayList plugins) {
        Iterator i = plugins.iterator();
        while (i.hasNext())
            if(((PluginRegistration) i.next()).conflictsWith(roots, execs))
                return true;
        return false;
    }
    
    private static boolean selfConflict(NodeImpl[] roots) {
        for(int i = 0; i < roots.length-1; i++)
            for(int j = i+1; j < roots.length; j++)
                if(roots[i].isOnSameBranch(roots[j]))
                    return true;
        return false;
    }

    private List getInvalidRoots(NodeImpl[] roots) {
        List list = new Vector();
        for (int i = 0; i < roots.length; i++) {
            // roots must not contain "." for the parent to be valid, this is
            // guaranteed by the conflict check (the root plugin provides ".")
            if(!RootPlugin.isValidDataPluginRoot(roots[i].getPath()))
                list.add(roots[i].getUri());
        }
        
        return list;
    }
}
