/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

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

package org.osgi.test.cases.dmt.tc2.tbc.Plugin.ReadOnly;

import java.util.Date;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ReadWriteDataSession;
import info.dmtree.spi.ReadableDataSession;
import info.dmtree.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * A test implementation of DataPluginFactory. This implementation validates the
 * DmtSession calls to a subtree handled by a DataPluginFactory.
 * 
 */
public class TestReadOnlyPlugin implements DataPlugin, ReadableDataSession {
	
	private DmtTestControl tbc;
	private static boolean exceptionAtClose;
    
	public TestReadOnlyPlugin(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
		return this;
	}

	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return null;
	}

	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return null;
	}
	

	public void close() throws DmtException {
	    if (exceptionAtClose) {
	        throw new DmtException((String)null,DmtException.DATA_STORE_FAILURE,null);
        }
	}

	public boolean isNodeUri(String[] nodeUri) {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestReadOnlyPluginActivator.ROOT)
				|| nodeName.equals(TestReadOnlyPluginActivator.INTERIOR_NODE)
				|| nodeName.equals(TestReadOnlyPluginActivator.LEAF_NODE)
				) {
			return true;
		} else {
			return false;
		}
	}

	public DmtData getNodeValue(String[] nodeUri) throws DmtException {
		return new DmtData("");
	}

	public String getNodeTitle(String[] nodeUri) throws DmtException {
		return null;
	}

	public String getNodeType(String[] nodeUri) throws DmtException {
		return null;
	}

	public int getNodeVersion(String[] nodeUri) throws DmtException {
		return 0;
	}

	public Date getNodeTimestamp(String[] nodeUri) throws DmtException {
		return null;
	}

	public int getNodeSize(String[] nodeUri) throws DmtException {
		return 0;
	}

	public String[] getChildNodeNames(String[] nodeUri) throws DmtException {
			return null;
	}

	public MetaNode getMetaNode(String[] nodeUri) throws DmtException {
		return null;
	}

	
	public boolean isLeafNode(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestReadOnlyPluginActivator.LEAF_NODE)) {
			return true;
		} else {
			return false;
		}
	}

	public void nodeChanged(String[] nodeUri) throws DmtException {

	}

    public static void setExceptionAtClose(boolean exceptionAtClose) {
        TestReadOnlyPlugin.exceptionAtClose = exceptionAtClose;
    }

}
