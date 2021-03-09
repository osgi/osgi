/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
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
