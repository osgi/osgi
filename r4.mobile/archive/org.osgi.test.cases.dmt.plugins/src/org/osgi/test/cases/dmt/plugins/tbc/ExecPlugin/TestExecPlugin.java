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
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 * Mar 04, 2005  Luiz Felipe Guimaraes
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.ExecPlugin;

import java.util.Date;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtConstants;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * A test implementation of ExecPlugin. This implementation validates the
 * DmtSession calls to a subtree handled by the ExecPlugin.
 */
public class TestExecPlugin implements ExecPlugin, DataPlugin, ReadableDataSession {

	public static final String EXECUTE = "TestExecPlugin.execute";
	
	public static final String GETNODETITLE = "TestExecPlugin.getNodeTitle";

	private DmtTestControl tbc;

	public TestExecPlugin(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public void execute(DmtSession session, String[] nodeUri, String correlator,String data)
			throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestExecPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri, DmtException.CONCURRENT_ACCESS,
					EXECUTE);
			
		} else {
			if (null == session) {
				tbc
						.fail("A reference to the session in which the operation was issued is null");
			} else {
				tbc
						.assertEquals(
								"Asserts that DmtSession is fowarded to ExecPlugin with the same lock type",
								DmtSession.LOCK_TYPE_SHARED, session
										.getLockType());
				tbc
						.assertEquals(
								"Asserts that DmtSession is fowarded to ExecPlugin with the same subtree",
								TestExecPluginActivator.ROOT, session
										.getRootUri());
			}
			DmtConstants.TEMPORARY = EXECUTE;
			DmtConstants.PARAMETER_2 = correlator;
			DmtConstants.PARAMETER_3 = data;

		}

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


    public void nodeChanged(String[] nodePath) throws DmtException {

        
    }


    public void close() throws DmtException {
        
        
    }

    public String[] getChildNodeNames(String[] nodePath) throws DmtException {
        
        return null;
    }


    public MetaNode getMetaNode(String[] nodePath) throws DmtException {
        
        return null;
    }

    public int getNodeSize(String[] nodePath) throws DmtException {
        
        return 0;
    }


    public Date getNodeTimestamp(String[] nodePath) throws DmtException {
        
        return null;
    }


    public String getNodeTitle(String[] nodePath) throws DmtException {
        
        return GETNODETITLE;
    }


    public String getNodeType(String[] nodePath) throws DmtException {
        
        return null;
    }


    public boolean isNodeUri(String[] nodePath) {
        
        return true;
    }


    public boolean isLeafNode(String[] nodePath) throws DmtException {
        
        return false;
    }


    public DmtData getNodeValue(String[] nodePath) throws DmtException {
        
        return null;
    }
 
    public int getNodeVersion(String[] nodePath) throws DmtException {
        return 0;
    }
}
