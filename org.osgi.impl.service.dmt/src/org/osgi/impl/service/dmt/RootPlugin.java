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

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.osgi.impl.service.dmt.dispatcher.Dispatcher;
import org.osgi.impl.service.dmt.dispatcher.Segment;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;


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
    @Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
            DmtSession session) {
        return this;
    }
    
    @Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, 
            DmtSession session) {
        return null;
    }

    @Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot, 
            DmtSession session) {
        return null;
    }
    
    @Override
	public void nodeChanged(String[] nodePath) {
		// empty
	}


	@Override
	public MetaNode getMetaNode(String[] nodePath)
			throws DmtException {
		return new ScaffoldMetaNode();
	}

	//----- DmtReadOnly methods -----//
	@Override
	public void close() throws DmtException {
		// empty
	}

	@Override
	public boolean isNodeUri(String[] nodePath) {
		Segment<DataPlugin> segment = dispatcher.findSegment(nodePath);
//		System.out.println( "isNodeUri: segment is: " + segment.getUri());
		return segment != null;
	}
    
    @Override
	public boolean isLeafNode(String[] nodePath) throws DmtException {
		if ( ! isNodeUri(nodePath))
			throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
			"Node not found: " + Uri.toUri(nodePath));
        return false; // currently all nodes are internal
    }

	@Override
	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		if ( ! isNodeUri(nodePath))
			throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
			"Node not found: " + Uri.toUri(nodePath));
		return null;
	}

	@Override
	public String getNodeTitle(String[] nodePath) throws DmtException {
		if ( ! isNodeUri(nodePath))
			throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
			"Node not found: " + Uri.toUri(nodePath));
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property not supported.");
	}

	@Override
	public String getNodeType(String[] nodePath) throws DmtException {
		if ( ! isNodeUri(nodePath))
			throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
			"Node not found: " + Uri.toUri(nodePath));
		return null;
	}

	@Override
	public int getNodeVersion(String[] nodePath) throws DmtException {
		if ( ! isNodeUri(nodePath))
			throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
			"Node not found: " + Uri.toUri(nodePath));
        throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
                "Version property not supported.");
	}

	@Override
	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		if ( ! isNodeUri(nodePath))
			throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
			"Node not found: " + Uri.toUri(nodePath));
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property not supported.");
	}

	@Override
	public int getNodeSize(String[] nodePath) throws DmtException {
		if ( ! isNodeUri(nodePath))
			throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
			"Node not found: " + Uri.toUri(nodePath));
		return 0;
	}

	@Override
	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		Segment<DataPlugin> segment = dispatcher.findSegment(nodePath);
		if ( segment == null ) 
			return null;
		Vector<String> childNodeNames = new Vector<String>();
		List<Segment<DataPlugin>> children = segment.getChildren();
		for (Segment<DataPlugin> child : children)
			childNodeNames.add( child.getName() );
		return childNodeNames.toArray( new String[childNodeNames.size()] );
	}
    
}
