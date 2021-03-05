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

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Feb 25, 2005  Andre Assad
 * 11            Implement DMT Use Cases 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin;

import java.util.Date;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.dmt.spi.MountPlugin;
import org.osgi.service.dmt.spi.MountPoint;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * A test implementation of a Plugin. This implementation validates the
 * DmtSession calls to a subtree handled by Plugin.
 * 
 */
public class TestExecPlugin implements DataPlugin, ExecPlugin, TransactionalDataSession, MountPlugin {

	private DmtTestControl tbc;
	
    private static int createInteriorNodeCount;
    
    private static int createLeafNodeCount; 
    
    private static String nodeTitle;
    
    private static boolean exceptionAtCreateInteriorNode;
    
	private static boolean allUriIsExistent = false;
	
	private DmtData dataComplex = new DmtData(Integer.valueOf("1"));
	
	private DmtData dataString = new DmtData("");
	
	private static String[] newInteriorNodeName = null;
	
	public static final String[] CHILDREN_NAMES = new String[] { "leaf_b","leaf_a" };
	
	public TestExecPlugin(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	
	@Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
		return this;
	}

	@Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
	    return this;
	}

	@Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
	    return this;
	}

	@Override
	public void execute(DmtSession session, String[] nodePath, String correlator, String data) throws DmtException {
		
	}

	@Override
	public void setNodeTitle(String[] nodeUri, String title) throws DmtException {
		checkExistence(nodeUri);
	}

	@Override
	public void setNodeValue(String[] nodeUri, DmtData data) throws DmtException {
		checkExistence(nodeUri);
	}

	public void setDefaultNodeValue(String[] nodeUri) throws DmtException {
		checkExistence(nodeUri);
	}

	@Override
	public void setNodeType(String[] nodeUri, String type) throws DmtException {
		checkExistence(nodeUri);
	}

	@Override
	public void deleteNode(String[] nodeUri) throws DmtException {
		checkExistence(nodeUri);
	}

	@Override
	public void createInteriorNode(String[] nodeUri, String type)
			throws DmtException {
		newInteriorNodeName = nodeUri;
        createInteriorNodeCount++;
        if (exceptionAtCreateInteriorNode) {
            throw new DmtIllegalStateException();
        }
	}

	@Override
	public void createLeafNode(String[] nodeUri, DmtData value, String mimeType)
			throws DmtException {
        createLeafNodeCount++;
	}

	@Override
	public void copy(String[] nodeUri, String[] newNodeUri, boolean recursive)
			throws DmtException {

	}

	@Override
	public void renameNode(String[] nodeUri, String newName) throws DmtException {

	}
	@Override
	public void close() throws DmtException {

	}

	@Override
	public boolean isNodeUri(String[] nodeUri) {
		String nodeName = tbc.mangleUri(nodeUri);
		if (allUriIsExistent 
		        || nodeName.equals(TestExecPluginActivator.ROOT)
				|| nodeName.equals(TestExecPluginActivator.INTERIOR_NODE)
				|| nodeName.equals(TestExecPluginActivator.INTERIOR_NODE_COPY)
				|| nodeName.equals(TestExecPluginActivator.INTERIOR_NODE2)
				|| nodeName.equals(TestExecPluginActivator.CHILD_INTERIOR_NODE)
				|| nodeName.equals(TestExecPluginActivator.LEAF_NODE)
				|| nodeName.equals(TestExecPluginActivator.INTERIOR_NODE_WITH_NULL_VALUES)
				|| nodeName.startsWith(TestExecPluginActivator.INTERIOR_NODE_WITH_TWO_CHILDREN)) {

			return true;
		} else {
			return false;
		}
	}

	@Override
	public DmtData getNodeValue(String[] nodeUri) throws DmtException {
		checkExistence(nodeUri);
		String uri = tbc.mangleUri(nodeUri);
		if (TestExecPluginActivator.INTERIOR_NODE.equals(uri)) {
			return dataComplex;
		} else if (TestExecPluginActivator.INTERIOR_NODE_WITH_NULL_VALUES.equals(uri)) {
			return null;
		} else {
			return dataString;
		}
	}

	@Override
	public String getNodeTitle(String[] nodeUri) throws DmtException {
		checkExistence(nodeUri);
		return nodeTitle;
	}

	@Override
	public String getNodeType(String[] nodeUri) throws DmtException {
		checkExistence(nodeUri);
		return null;
	}

	@Override
	public int getNodeVersion(String[] nodeUri) throws DmtException {
		checkExistence(nodeUri);
		return 0;
	}

	@Override
	public Date getNodeTimestamp(String[] nodeUri) throws DmtException {
		checkExistence(nodeUri);
		return null;
	}


	@Override
	public int getNodeSize(String[] nodeUri) throws DmtException {
		checkExistence(nodeUri);
		return 0;
	}

	@Override
	public String[] getChildNodeNames(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestExecPluginActivator.INTERIOR_NODE_WITH_NULL_VALUES)) {
			return new String[] { TestExecPluginActivator.INTERIOR_NODE, null,
					TestExecPluginActivator.INTERIOR_NODE2, null };
		} else if (nodeName.equals(TestExecPluginActivator.INTERIOR_NODE_WITH_TWO_CHILDREN)){
			return CHILDREN_NAMES;
		} else if (nodeName.equals(TestExecPluginActivator.INEXISTENT_NODE)){
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "Node with uri: " + nodeName + " not found.");
		} else {
			return null;
		}

	}

	@Override
	public MetaNode getMetaNode(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestExecPluginActivator.LEAF_NODE)
				|| nodeName.equals(TestExecPluginActivator.INEXISTENT_LEAF_NODE)
                | nodeName.equals(TestExecPluginActivator.INEXISTENT_INTERIOR_AND_LEAF_NODES)) {
			return new TestMetaNode(DmtData.FORMAT_INTEGER
					| DmtData.FORMAT_STRING,true);
		} else 	if (nodeName.equals(TestExecPluginActivator.INTERIOR_NODE_COPY)
				|| nodeName.equals(TestExecPluginActivator.INTERIOR_NODE2_COPY)) {
		    return new TestMetaNode(false);
		} else {
			return new TestMetaNode(true);
		}
	}

	
	@Override
	public boolean isLeafNode(String[] nodeUri) throws DmtException {
		checkExistence(nodeUri);
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestExecPluginActivator.LEAF_NODE) || nodeName.startsWith(TestExecPluginActivator.INTERIOR_NODE_WITH_TWO_CHILDREN + "/")) { 
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void nodeChanged(String[] nodeUri) throws DmtException {

	}

	@Override
	public void commit() throws DmtException {
		
	}

	@Override
	public void rollback() throws DmtException {
		
	}	

    public static void setAllUriIsExistent(boolean allUriIsExistent) {
        TestExecPlugin.allUriIsExistent = allUriIsExistent;
    }
    
    public static void resetCount() {
        createInteriorNodeCount=0;
        createLeafNodeCount=0;
    }

    public static int getCreateInteriorNodeCount() {
        return createInteriorNodeCount;
    }

    public static int getCreateLeafNodeCount() {
        return createLeafNodeCount;
    }

    public static void setExceptionAtCreateInteriorNode(
            boolean exceptionAtCreateInteriorNode) {
        TestExecPlugin.exceptionAtCreateInteriorNode = exceptionAtCreateInteriorNode;
    }

    public static void setDefaultNodeTitle(String nodeUri) {
        nodeTitle = nodeUri;
    }


	public static String[] getNewInteriorNodeName() {
		return newInteriorNodeName;
	}


	public void setMountPoints(MountPoint[] mountPoints) {
		System.out.println( "setMountPoints invoked with mountPoints:" );
		for (int i = 0; i < mountPoints.length; i++) {
			System.out.println( Uri.toUri(mountPoints[i].getMountPath()) );
		}
	}


	@Override
	public void mountPointAdded(MountPoint mountPoint) {
		System.out.println( "mountPointsAdded invoked with mountPoints:" );
			System.out.println( Uri.toUri(mountPoint.getMountPath()) );
	}


	@Override
	public void mountPointRemoved(MountPoint mountPoint) {
		System.out.println( "mountPointsRemoved invoked with mountPoints:" );
			System.out.println( Uri.toUri(mountPoint.getMountPath()) );
	}
    
	private void checkExistence(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (! allUriIsExistent && (nodeName.equals(TestExecPluginActivator.INEXISTENT_NODE) || nodeName.equals(TestExecPluginActivator.INEXISTENT_LEAF_NODE)))
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "Node with uri: " + nodeName + " not found.");
	}
}
