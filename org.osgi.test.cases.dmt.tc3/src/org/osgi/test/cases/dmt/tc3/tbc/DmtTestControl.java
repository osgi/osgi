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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jan 21, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 * Mar 02, 2005  Andre Assad
 * 11            Implement DMT Use Cases 
 * ===========   ==============================================================
 * Mar 04, 2005  Alexandre Santos
 * 23            Updates due to changes in the DmtAcl API
 * ===========   ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc;
import info.dmtree.DmtAdmin;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.Uri;

import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;
import org.osgi.test.cases.dmt.tc3.tbc.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.Can;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.DmtMetaNodeConstants;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetDefault;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetDescription;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetExtensionProperty;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetExtensionPropertyKeys;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetFormat;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetMax;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetMaxOccurrence;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetMimeTypes;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetMin;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetRawFormatNames;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetScope;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetValidNames;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.GetValidValues;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.IsLeaf;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.IsValidName;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.IsValidValue;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.IsZeroOccurrenceAllowed;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.TestMetaNodeDataPluginActivator;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.MetaData.MetaData;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.MetaData.TestPluginMetaDataActivator;
import org.osgi.test.cases.dmt.tc3.tbc.Others.DmtSessionConstraints;
import org.osgi.test.cases.dmt.tc3.tbc.Others.OpenSession;
import org.osgi.test.cases.dmt.tc3.tbc.Others.OverlappingPlugins;
import org.osgi.test.cases.dmt.tc3.tbc.Others.UseCases;
import org.osgi.test.cases.dmt.tc3.tbc.Plugins.FatalExceptionDataPluginActivator;
import org.osgi.test.cases.dmt.tc3.tbc.Plugins.NewDataPluginActivator;
import org.osgi.test.cases.dmt.tc3.tbc.Plugins.OverlappingDataPluginActivator;
import org.osgi.test.cases.dmt.tc3.tbc.Plugins.OverlappingExecPluginActivator;
import org.osgi.test.cases.dmt.tc3.tbc.Plugins.OverlappingSubtreeDataPluginActivator;
import org.osgi.test.cases.dmt.tc3.tbc.Plugins.ToBeOverlappedDataPluginActivator;
import org.osgi.test.cases.dmt.tc3.tbc.TreeStructure.Configuration;
import org.osgi.test.cases.dmt.tc3.tbc.TreeStructure.Log;
import org.osgi.test.cases.util.DefaultTestBundleControl;

public class DmtTestControl extends DefaultTestBundleControl {

	private DmtAdmin dmtAdmin;

	private TestDataPluginActivator testDataPluginActivator;
	
	private TestExecPluginActivator testExecPluginActivator;
	
	private TestMetaNodeDataPluginActivator testMetaNodeDataPluginActivator;
	
	private OverlappingDataPluginActivator overlappingDataPluginActivator;
	
	private OverlappingExecPluginActivator overlappingExecPluginActivator;
	
	private OverlappingSubtreeDataPluginActivator overlappingSubtreeDataPluginActivator;
	
	private ToBeOverlappedDataPluginActivator toBeOverlappedDataPluginActivator;
	
	private NewDataPluginActivator newDataPluginActivator;
	
	private FatalExceptionDataPluginActivator fatalExceptionDataPluginActivator;
	
	private TestPluginMetaDataActivator testPluginMetaDataActivator; 
	
	public void prepare() {
		try {
			dmtAdmin = (DmtAdmin) getContext().getService(
					getContext().getServiceReference(DmtAdmin.class.getName()));
		} catch (NullPointerException e) {
			log("There is no DmtAdmin service in the service registry, tests will not be executed correctly");
		}

		
		registerTestPlugins();
		
	}


	public void registerTestPlugins() {
		try {
			testDataPluginActivator = new TestDataPluginActivator(this);
			testDataPluginActivator.start(getContext());
			
			//Tries to register an overlapping Plugin, DmtAdmin must ignore
			overlappingDataPluginActivator = new OverlappingDataPluginActivator();
			overlappingDataPluginActivator.start(getContext()); 

			//Tries to register a plugin that is part of the same subtree that the plugin above controls, DmtAdmin must ignore
			overlappingSubtreeDataPluginActivator = new OverlappingSubtreeDataPluginActivator();
			overlappingSubtreeDataPluginActivator.start(getContext());

			//Registers a DataPlugin to be overlapped by the ExecPlugin below
			toBeOverlappedDataPluginActivator = new ToBeOverlappedDataPluginActivator();
			toBeOverlappedDataPluginActivator.start(getContext());
			
			//Registers a ExecPlugin that overlaps the DataPlugin above 
			testExecPluginActivator = new TestExecPluginActivator(this);
			testExecPluginActivator.start(getContext());	

			//Tries to register an overlapping ExecPlugin, DmtAdmin must ignore
			overlappingExecPluginActivator = new OverlappingExecPluginActivator();
			overlappingExecPluginActivator.start(getContext());	

			//----------------------------------------------------------------------------------//
			//Plugin to the MetaNode tests
			testMetaNodeDataPluginActivator = new TestMetaNodeDataPluginActivator(this);
			testMetaNodeDataPluginActivator.start(getContext());

			//Plugin to metadata tests
			testPluginMetaDataActivator = new TestPluginMetaDataActivator(this);
			testPluginMetaDataActivator.start(getContext());
			//----------------------------------------------------------------------------------//
			
			//Plugin that throws a fatal exception
			fatalExceptionDataPluginActivator = new FatalExceptionDataPluginActivator(this);
			fatalExceptionDataPluginActivator.start(getContext());
			
			newDataPluginActivator = new NewDataPluginActivator(this);
			newDataPluginActivator.start(getContext());		
			

		} catch (Exception e) {
			log("#TestControl: Fail to register a TestPlugin");
		}
	}
	
	
	//OverlappingPlugins
	public void testOverlappingPlugins() {
		new OverlappingPlugins(this).run();
	}
	//DataPlugin methods
	public void testDataPluginClose() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.Close(this).run();
	}
	public void testDataPluginGetChildNodeNames() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.GetChildNodeNames(this).run();
	}

	public void testDataPluginGetMetaNode() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.GetMetaNode(this).run();
	}

	public void testDataPluginGetNodeSize() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.GetNodeSize(this).run();
	}

	public void testDataPluginGetNodeTimestamp() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.GetNodeTimestamp(this).run();
	}

	public void testDataPluginGetNodeTitle() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.GetNodeTitle(this).run();
	}

	public void testDataPluginGetNodeType() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.GetNodeType(this).run();
	}

	public void testDataPluginGetNodeValue() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.GetNodeValue(this).run();
	}

	public void testDataPluginGetNodeVersion() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.GetNodeVersion(this).run();
	}

	public void testDataPluginIsNodeUri() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.IsNodeUri(this).run();
	}

	public void testDataPluginIsLeafNode() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.IsLeafNode(this).run();
	}
	public void testDataPluginNodeChanged() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.NodeChanged(this).run();
	}	
	
	public void testDataPluginCommit() { 
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.Commit(this).run();
	}
	
	public void testDataPluginCopy() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.Copy(this).run();
	}
	
	public void testDataPluginCreateInteriorNode() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.CreateInteriorNode(this).run();
	}
	
	public void testDataPluginCreateLeafNode() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.CreateLeafNode(this).run();
	}
	
	public void testDataPluginDeleteNode() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.DeleteNode(this).run();
	}

	public void testDataPluginRenameNode() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.RenameNode(this).run();
	}

	public void testDataPluginRollback() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.Rollback(this).run();
	}

	public void testDataPluginSetNodeTitle() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.SetNodeTitle(this).run();
	}
	
	public void testDataPluginSetNodeType() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.SetNodeType(this).run();
	}

	public void testDataPluginSetNodeValue() {
		new org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession.SetNodeValue(this).run();
	}
	
	
	//ExecPlugin Method
	public void testExecPluginExecute() {
		new org.osgi.test.cases.dmt.tc3.tbc.ExecPlugin.Execute(this).run();
	}
	
	
	// DmtMetaNode
	public void testDmtMetaNodeConstants() {
		new DmtMetaNodeConstants(this).run();
	}
	
	public void testDmtMetaNodeCan() {
		new Can(this).run();
	}

	public void testDmtMetaNodeGetDefault() {
		new GetDefault(this).run();
	}

	public void testDmtMetaNodeGetDescription() {
		new GetDescription(this).run();
	}

	public void testDmtMetaNodeGetFormat() {
		new GetFormat(this).run();
	}

	public void testDmtMetaNodeGetMax() {
		new GetMax(this).run();
	}

	public void testDmtMetaNodeGetMaxOccurrence() {
		new GetMaxOccurrence(this).run();
	}

	public void testDmtMetaNodeGetMimeTypes() {
		new GetMimeTypes(this).run();
	}

	public void testDmtMetaNodeGetMin() {
		new GetMin(this).run();
	}

	public void testDmtMetaNodeisValidName() {
		new IsValidName(this).run();
	}

	public void testDmtMetaNodeisValidValue() {
		new IsValidValue(this).run();
	}

	public void testDmtMetaNodeGetValidValues() {
		new GetValidValues(this).run();
	}

	public void testDmtMetaNodeGetValidNames() {
		new GetValidNames(this).run();
	}

	public void testDmtMetaNodeIsLeaf() {
		new IsLeaf(this).run();
	}

	public void testDmtMetaNodeGetScope() {
		new GetScope(this).run();
	}

	public void testDmtMetaNodeIsZeroOccurrenceAllowed() {
		new IsZeroOccurrenceAllowed(this).run();
	}
	
	public void testDmtMetaNodeGetExtensionProperty() {
		new GetExtensionProperty(this).run();
	}

	public void testDmtMetaNodeGetExtensionPropertyKeys() {
		new GetExtensionPropertyKeys(this).run();
	}

	public void testDmtMetaNodeGetRawFormatNames() {
		new GetRawFormatNames(this).run();
	}
	
	//TreeStructure test cases
	public void testTreeStructureLog() {
		new Log(this).run();
	}

	public void testTreeStructureConfiguration() {
		new Configuration(this).run();
	}
	
	//Use cases test cases
	public void testUseCases() {
		new UseCases(this).run();
	}

	//Meta data test cases
	public void testMetaData() {
		new MetaData(this).run();
	}
	public void testDmtSessionConstraints() {
	    new DmtSessionConstraints(this).run();
	}		
    public void testOpenSession() {
        new OpenSession(this).run();
    }       

	/**
	 * @return Returns the factory.
	 */
	public DmtAdmin getDmtAdmin() {
		if (dmtAdmin != null)
			return dmtAdmin;
		else
			throw new NullPointerException("DmtAdmin is null");
	}

	/**
	 * It deletes all the nodes created during the execution of the test. It
	 * receives a String array containing all the node URIs.
	 */
	public void cleanUp(DmtSession session,String[] nodeUri) {
		if (session != null && session.getState() == DmtSession.STATE_OPEN) {
			if (nodeUri == null) {
				closeSession(session);
			} else {
				for (int i = 0; i < nodeUri.length; i++) {
					try {
						session.deleteNode(nodeUri[i]);
					} catch (Throwable e) {
						log("#Exception at cleanUp: "+e.getClass().getName() + " [Message: " +e.getMessage() +"]");
					}
				}
				closeSession(session);
			}
		}
	}
	public void cleanUp(DmtSession session,boolean cleanTemporary) {
	    closeSession(session);
		if (cleanTemporary) {
		    DmtConstants.TEMPORARY="";
		    DmtConstants.PARAMETER_2="";
		    DmtConstants.PARAMETER_3="";
	    }

	    
	}
	public void closeSession(DmtSession session) {
		if (null != session) {
			if (session.getState() == DmtSession.STATE_OPEN) {
				try {
					session.close();
				} catch (DmtException e) {
					log("#Exception closing the session: "+e.getClass().getName() + ". Message: [" +e.getMessage() +"]");
				}
			}
		}
	}	

	public FatalExceptionDataPluginActivator getFatalExceptionDataPluginActivator() {
		return fatalExceptionDataPluginActivator;
	}

	public String mangleUri(String[] nodeUri) {
		StringBuffer nodeNameBuffer= new StringBuffer();
		String nodeName="";
		if (nodeUri.length>0) {
			for (int i=0;i<nodeUri.length;i++) {
				nodeNameBuffer = nodeNameBuffer.append(Uri.mangle(nodeUri[i]) + "/");
			}
			nodeName = nodeNameBuffer.substring(0,nodeNameBuffer.length()-1);
		}
		
		return nodeName;
	}

	public void cleanAcl(String nodeUri) {
		DmtSession session = null;
		try {
			session = getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(nodeUri,null);
		} catch (Exception e) {
			log("#Exception cleaning the acl from "+ nodeUri +" : "+e.getClass().getName() + "Message: [" +e.getMessage() +"]");
		} finally {
			closeSession(session);
		}
		
	}
	public void failUnexpectedException(Exception exception) {
		fail("Unexpected Exception: " + exception.getClass().getName() + " [Message: " + exception.getMessage() +"]");
	}
	
	public void failExpectedOtherException(Class expected,Throwable found) {
		fail("Expected " + expected.getName()+ " but was " + found.getClass().getName());
	}

}
