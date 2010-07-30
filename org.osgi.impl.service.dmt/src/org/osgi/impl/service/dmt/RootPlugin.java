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

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ReadWriteDataSession;
import info.dmtree.spi.ReadableDataSession;
import info.dmtree.spi.TransactionalDataSession;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.osgi.impl.service.dmt.dispatcher.Dispatcher;
import org.osgi.impl.service.dmt.dispatcher.Segment;


public class RootPlugin implements DataPlugin, ReadableDataSession {

    
    private Dispatcher dispatcher;

    public RootPlugin(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
//    // precondition: path must be absolute
//    static boolean isValidDataPluginRoot(String[] path) {
//        return root.findNode(path, 1, false) != null ||
//            root.findNode(path, 1, true) != null;
//    }

	//----- DmtReadOnlyDataPlugin methods -----//
    public ReadableDataSession openReadOnlySession(String[] sessionRoot,
            DmtSession session) {
        return this;
    }
    
    public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, 
            DmtSession session) {
        return null;
    }

    public TransactionalDataSession openAtomicSession(String[] sessionRoot, 
            DmtSession session) {
        return null;
    }
    
    public void nodeChanged(String[] nodePath) {}


	public MetaNode getMetaNode(String[] nodePath)
			throws DmtException {
		return new StructureMetaNode();
	}

	//----- DmtReadOnly methods -----//
	public void close() throws DmtException {}

	public boolean isNodeUri(String[] nodePath) {
		Segment segment = dispatcher.findSegment(nodePath);
//		System.out.println( "isNodeUri: segment is: " + segment.getUri());
		return segment != null;
	}
    
    public boolean isLeafNode(String[] nodePath) throws DmtException {
//        findNode(nodePath); // check that the node exists
        return false; // currently all nodes are internal
    }

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		// should never be called because all nodes are internal
		return null;
	}

	public String getNodeTitle(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property not supported.");
	}

	public String getNodeType(String[] nodePath) throws DmtException {
		return null;
	}

	public int getNodeVersion(String[] nodePath) throws DmtException {
        throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
                "Version property not supported.");
	}

	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property not supported.");
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
		// should never be called because all nodes are internal
		return 0;
	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		Segment segment = dispatcher.findSegment(nodePath);
		if ( segment == null ) 
			return null;
		Vector<String> childNodeNames = new Vector<String>();
		List<Segment> children = segment.getChildren();
		for ( Segment child : children )
			childNodeNames.add( child.getName() );
		return (String[]) childNodeNames.toArray( new String[childNodeNames.size()] );
	}
    
}
