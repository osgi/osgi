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

import java.util.List;
import java.util.Vector;
import org.osgi.service.dmt.*;

class Plugin {
	private Object   plugin;
	private String[] roots;
	private String[] execs;
    
	// redundant information
    private boolean  isWritable; // if the plugin is a writable data plugin 
    private boolean  isReadOnly; // if the plugin is a read-only data plugin

	// precondition: roots != null && execs != null && 
    //               (roots.length != 0 || execs.length != 0)
	Plugin(Object plugin, String[] roots, String[] execs) {
        isReadOnly = plugin instanceof DmtReadOnlyDataPlugin;
        isWritable = plugin instanceof DmtDataPlugin;
        
		if (roots.length > 0 && !isWritable && !isReadOnly)
			throw new IllegalArgumentException(
					"The plugin must implement DmtDataPlugin or " +
                    "DmtReadOnlyDataPlugin if data roots are specified.");
		if (execs.length > 0 && !(plugin instanceof DmtExecPlugin))
			throw new IllegalArgumentException(
					"The plugin must implement DmtExecPlugin if exec nodes " + 
                    "are specified");
        
		this.plugin = plugin;
		this.roots = Utils.normalizeAbsoluteUris(roots);
		this.execs = Utils.normalizeAbsoluteUris(execs);
	}

	DmtDataPlugin getWritableDataPlugin() {
		if (!isWritable)
			throw new IllegalStateException(
					"Plugin object is not a writable data plugin.");
		return (DmtDataPlugin) plugin;
	}

	DmtReadOnlyDataPlugin getReadOnlyDataPlugin() {
		if (!isReadOnly)
			throw new IllegalStateException(
					"Plugin object is not a read-only data plugin.");
		return (DmtReadOnlyDataPlugin) plugin;
	}
    
	DmtExecPlugin getExecPlugin() {
		if (!(plugin instanceof DmtExecPlugin))
			throw new IllegalStateException(
					"Plugin object is not an exec plugin.");
		return (DmtExecPlugin) plugin;
	}
    
    String[] getDataRoots() {
        return roots;
    }
    
    boolean isWritableDataPlugin() {
        return isWritable;
    }
    
    boolean isReadOnlyDataPlugin() {
        return isReadOnly;
    }

	boolean handlesData(String subtreeUri) {
        return handles(subtreeUri, roots);
	}

	boolean handlesExec(String subtreeUri) {
        return handles(subtreeUri, execs);
	}
    
    private static boolean handles(String uri, String[] roots) {
        for (int i = 0; i < roots.length; i++)
            if (Utils.isAncestor(roots[i], uri))
                return true;
        return false;
    }
    
    boolean conflictsWith(String[] otherRoots, String[] otherExecs) {
        return 
            conflicts(roots, otherRoots) ||
            conflicts(execs, otherExecs);
    }
    
    private static boolean conflicts(String[] roots, String[] otherRoots) {
        for(int i = 0; i < roots.length; i++)
            for(int j = 0; j < otherRoots.length; j++)
                if (Utils.isOnSameBranch(roots[i], otherRoots[j]))
                    return true;
        return false;
    }
    
    // finds the data root URIs that are directly below the given uri, and
    // returns the list of their last segments
    List getChildRootNames(String uri) {
        List childRootNames = new Vector();
        for(int i = 0; i < roots.length; i++)
            if(Utils.isParent(uri, roots[i]))
                childRootNames.add(Utils.lastSegment(roots[i]));
            
        return childRootNames;
    }
}
