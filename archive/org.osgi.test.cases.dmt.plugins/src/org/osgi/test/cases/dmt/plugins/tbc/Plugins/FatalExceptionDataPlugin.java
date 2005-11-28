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
 * Jun 08, 2005  Luiz Felipe Guimaraes
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.Plugins;

import java.util.Date;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtConstants;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;

public class FatalExceptionDataPlugin implements DataPlugin, TransactionalDataSession {
	
	public static final String ROLLBACK = "FatalExceptionDataPlugin.rollback,";
	public static final String CLOSE = "FatalExceptionDataPlugin.close,";
	public static final String COMMIT = "FatalExceptionDataPlugin.commit,";
	
    private DmtTestControl tbc;
    
	public FatalExceptionDataPlugin(DmtTestControl tbc) {
        this.tbc = tbc;
	}

	
	public void rollback() throws DmtException {
		DmtConstants.TEMPORARY += ROLLBACK; 
	}

	public void setNodeTitle(String[] nodeUri, String title) throws DmtException {
		
	}

	public void setNodeValue(String[] nodeUri, DmtData data) throws DmtException {

	}

	public void setDefaultNodeValue(String[] nodeUri) throws DmtException {

	}

	public void setNodeType(String[] nodeUri, String type) throws DmtException {
	
	}

	public void deleteNode(String[] nodeUri) throws DmtException {
	
	}


	public void createInteriorNode(String[] nodeUri, String type)
			throws DmtException {

	}



	public void createLeafNode(String[] nodeUri, DmtData value, String mimeType)
			throws DmtException {
		throw new DmtException(nodeUri,DmtException.COMMAND_NOT_ALLOWED,null,null,true);
	}

	public void copy(String[] nodeUri, String[] newNodeUri, boolean recursive)
			throws DmtException {
	}

	public void renameNode(String[] nodeUri, String newName) throws DmtException {
	}

	public void close() throws DmtException {
		DmtConstants.TEMPORARY += CLOSE; 
	}

	public boolean isNodeUri(String[] nodeUri) {
        String nodeName = tbc.mangleUri(nodeUri);
        if (nodeName.equals(FatalExceptionDataPluginActivator.INEXISTENT_NODE) ||
    		nodeName.equals(FatalExceptionDataPluginActivator.INEXISTENT_LEAF_NODE)) {
			return false;
		} else {
			return true;
		}
	}

	public DmtData getNodeValue(String[] nodeUri) throws DmtException {
		return null;
	}

	public String getNodeTitle(String[] nodeUri) throws DmtException {
		return null;
	}

	public String getNodeType(String[] nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.COMMAND_NOT_ALLOWED,null);
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
	
	public void commit() throws DmtException {
		DmtConstants.TEMPORARY += COMMIT;
	}

	public boolean isLeafNode(String[] nodeUri) throws DmtException {
		return false; 
	}

	public void nodeChanged(String[] nodeUri) throws DmtException {

	}


    public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
        return null;
    }
    
    public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
        return null;
    }

    public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
        return this;
    }

}
