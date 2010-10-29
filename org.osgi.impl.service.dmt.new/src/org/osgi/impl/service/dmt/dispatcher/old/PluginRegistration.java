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

import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ExecPlugin;

import java.util.*;

import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.dmt.dispatcher.Node;
import org.osgi.impl.service.dmt.dispatcher.Plugin;


class PluginRegistration implements Plugin {
	private Object plugin;
	private final NodeImpl[] roots;
	private final NodeImpl[] execs;
    private ServiceReference pluginRef;
    
    private String infoString;
    
	// precondition: roots != null && execs != null && 
    //               (roots.length != 0 || execs.length != 0)
	PluginRegistration(ServiceReference pluginRef, Object plugin, 
            NodeImpl[] roots, NodeImpl[] execs) {
		if (roots.length > 0 && !(plugin instanceof DataPlugin))
			throw new IllegalArgumentException(
					"The plugin must implement DataPlugin if data " +
                    "roots are specified.");
		if (execs.length > 0 && !(plugin instanceof ExecPlugin))
			throw new IllegalArgumentException(
					"The plugin must implement ExecPlugin if exec roots are " + 
                    "specified");
        
		this.plugin = plugin;
        this.pluginRef = pluginRef;
		this.roots = roots;
		this.execs = execs;
        
        infoString = null;
	}

    public DataPlugin getDataPlugin() {
        if(!(plugin instanceof DataPlugin))
            throw new IllegalStateException("Plugin object is not a data " +
                    "plugin.");
        
        return (DataPlugin) plugin;
    }
    
	public ExecPlugin getExecPlugin() {
		if (!(plugin instanceof ExecPlugin))
			throw new IllegalStateException(
					"Plugin object is not an exec plugin.");
		return (ExecPlugin) plugin;
	}
    
    public Node[] getDataRoots() {
        return roots;
    }
    
	public boolean handlesData(Node subtreeUri) {
        return handles(subtreeUri, roots);
	}

	public boolean handlesExec(Node subtreeUri) {
        return handles(subtreeUri, execs);
	}
    
    private static boolean handles(Node uri, NodeImpl[] roots) {
        for(int i = 0; i < roots.length; i++)
            if(roots[i].isAncestorOf(uri))
                return true;
        return false;
    }
    
    boolean conflictsWith(NodeImpl[] otherRoots, NodeImpl[] otherExecs) {
        return 
            conflicts(roots, otherRoots) ||
            conflicts(execs, otherExecs);
    }
    
    private static boolean conflicts(NodeImpl[] roots, NodeImpl[] otherRoots) {
        for(int i = 0; i < roots.length; i++)
            for(int j = 0; j < otherRoots.length; j++)
                if(roots[i].isOnSameBranch(otherRoots[j]))
                    return true;
        return false;
    }
    
    // finds the data root URIs that are directly below the given uri, and
    // returns the list of their last segments
    List getChildRootNames(String[] path) {
    	NodeImpl uri = new NodeImpl(path);
        List childRootNames = new Vector();
        for(int i = 0; i < roots.length; i++)
            if(uri.isParentOf(roots[i]))
                childRootNames.add(roots[i].getLastSegment());
            
        return childRootNames;
    }
    
    public boolean isRegistered() {
        return pluginRef == null || pluginRef.getBundle() != null;
    }
    
    public String toString() {
        if(infoString == null)
            infoString = "PluginRegistration(" + Arrays.asList(roots) +
                ", " + Arrays.asList(execs) + ")";

        return infoString;
    }
}
