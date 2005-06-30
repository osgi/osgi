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

package org.osgi.test.cases.dmt.plugins.tbc;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.log.LogService;
import org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.TestDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.DmtExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.Can;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.DmtMetaNodeConstants;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.GetDefault;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.GetDescription;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.GetFormat;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.GetMax;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.GetMaxOccurrence;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.GetMimeTypes;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.GetMin;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.GetNamePattern;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.GetPattern;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.GetScope;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.GetValidNames;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.GetValidValues;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.IsLeaf;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.IsZeroOccurrenceAllowed;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.TestMetaNodeDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.MetaData.MetaData;
import org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.MetaData.TestPluginMetaDataActivator;
import org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.TestReadOnlyDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Others.FatalExceptionDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Others.NewDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Others.OverlappingDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Others.OverlappingExecPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Others.OverlappingSubtreeDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Others.ToBeOverlappedDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Others.UseCases;
import org.osgi.test.cases.dmt.plugins.tbc.TreeStructure.Configuration;
import org.osgi.test.cases.dmt.plugins.tbc.TreeStructure.Log;
import org.osgi.test.cases.dmt.plugins.tbc.TreeStructure.TestManagedService;
import org.osgi.test.cases.dmt.plugins.tbc.TreeStructure.TestManagedServiceFactory;
import org.osgi.test.cases.util.DefaultTestBundleControl;

public class DmtTestControl extends DefaultTestBundleControl {

	public static String TEMPORARY = "";
	
	public static String PARAMETER_2 = "";
	
	public static String PARAMETER_3 = "";
	
	public static final String OSGi_ROOT = "./OSGi";

	public static final String OSGi_LOG = OSGi_ROOT + "/log";
	
	public static final String OSGi_CFG = OSGi_ROOT + "/cfg";
	
	public static final String XMLSTR = "<?xml version=\"1.0\"?><data><name>data name</name><value>data value</value></data>";

	public static final String LOG_SEARCH = "./OSGi/log/search";

	public static final String LOG_SEARCH_MAXRECORDS = LOG_SEARCH + "/maxrecords";

	public static final String LOG_SEARCH_FILTER = LOG_SEARCH + "/filter";

	public static final String LOG_SEARCH_EXCLUDE = LOG_SEARCH + "/exclude";

	public static final String LOG_SEARCH_INVALID = LOG_SEARCH + "/invalid";

	public static final String LOG_LEAF = DmtTestControl.OSGi_LOG + "/testleaf";
	
	public static final String DDF = "http://www.openmobilealliance.org/tech/DTD/OMA-SyncML-DMDDF-V1_2_0.dtd";
	
	private DmtAdmin dmtAdmin;

	private TestDataPluginActivator testDataPluginActivator;
	
	private TestExecPluginActivator testExecPluginActivator;
	
	private TestReadOnlyDataPluginActivator testReadOnlyDataPluginActivator;
	
	private TestMetaNodeDataPluginActivator testMetaNodeDataPluginActivator;
	
	private OverlappingDataPluginActivator overlappingDataPluginActivator;
	
	private OverlappingExecPluginActivator overlappingExecPluginActivator;
	
	private OverlappingSubtreeDataPluginActivator overlappingSubtreeDataPluginActivator;
	
	private ToBeOverlappedDataPluginActivator toBeOverlappedDataPluginActivator;
	
	private NewDataPluginActivator newDataPluginActivator;
	
	private FatalExceptionDataPluginActivator fatalExceptionDataPluginActivator;
	
	private TestPluginMetaDataActivator testPluginMetaDataActivator; 
	
	private TestManagedService testManagedService;
	
	private LogService logService;
	
	private TestManagedServiceFactory testManagedServiceFactory;
	
	private ConfigurationAdmin configAdmin;
	
	public void prepare() {
		log("#before each run");
		ServiceReference dmtAdminReference = getContext().getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) getContext().getService(dmtAdminReference);

		configAdmin = (ConfigurationAdmin) getContext().getService(getContext().getServiceReference(ConfigurationAdmin.class.getName()));
		
		logService = (LogService) getContext().getService(
				getContext().getServiceReference(
						LogService.class.getName()));	
		registerTestPlugins();
		installManagedServices();
	}

	public void installManagedServices() {
		try {
			testManagedServiceFactory = new TestManagedServiceFactory();
			testManagedServiceFactory.start(getContext());			
			testManagedService = new TestManagedService();
			testManagedService.start(getContext());				
		} catch (Exception e) {
			log("TestControl: Failed starting a ManagedService bundle");
		}
	}

	public void registerTestPlugins() {
		try {
			testDataPluginActivator = new TestDataPluginActivator(this);
			testDataPluginActivator.start(getContext());
			
			//Tries to register an overlapping DmtDataPlugin, the registration must not occur 
			overlappingDataPluginActivator = new OverlappingDataPluginActivator(this);
			overlappingDataPluginActivator.start(getContext()); 

			//Tries to register a plugin that is part of the same subtree that the plugin above controls, the registration must not occur 
			overlappingSubtreeDataPluginActivator = new OverlappingSubtreeDataPluginActivator(this);
			overlappingSubtreeDataPluginActivator.start(getContext());

			//Registers a DmtDataPlugin to be overlapped by the DmtExecPlugin below 
			toBeOverlappedDataPluginActivator = new ToBeOverlappedDataPluginActivator(this);
			toBeOverlappedDataPluginActivator.start(getContext());
			
			//Registers a DmtExecPlugin that overlaps the DmtDataPlugin above 
			testExecPluginActivator = new TestExecPluginActivator(this);
			testExecPluginActivator.start(getContext());	

			//Tries to register an overlapping DmtExecPlugin, the registration must not occur
			overlappingExecPluginActivator = new OverlappingExecPluginActivator(this);
			overlappingExecPluginActivator.start(getContext());	

			//Registers a DmtExecPlugin that overlaps the DmtDataPlugin above 
			testReadOnlyDataPluginActivator = new TestReadOnlyDataPluginActivator(this);
			testReadOnlyDataPluginActivator.start(getContext());	
			
			//----------------------------------------------------------------------------------//
			//Plugin for the MetaNode tests
			testMetaNodeDataPluginActivator = new TestMetaNodeDataPluginActivator(this);
			testMetaNodeDataPluginActivator.start(getContext());

			//Plugin to test metadata
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
	
	public void setState() {
		log("#before each method");
	}
	
	
	public ConfigurationAdmin getConfigurationAdmin() {
		return configAdmin;
	}
	// TestDataPlugin
	// DmtReadOnly Methods
	public void testDmtDataPluginClose() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.DmtReadOnly.Close(this).run();
	}
	public void testDmtDataPluginGetChildNodeNames() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.DmtReadOnly.GetChildNodeNames(this).run();
	}

	public void testDmtDataPluginGetMetaNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.DmtReadOnly.GetMetaNode(this).run();
	}

	public void testDmtDataPluginGetNodeSize() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.DmtReadOnly.GetNodeSize(this).run();
	}

	public void testDmtDataPluginGetNodeTimestamp() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.DmtReadOnly.GetNodeTimestamp(this).run();
	}

	public void testDmtDataPluginGetNodeTitle() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.DmtReadOnly.GetNodeTitle(this).run();
	}

	public void testDmtDataPluginGetNodeType() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.DmtReadOnly.GetNodeType(this).run();
	}

	public void testDmtDataPluginGetNodeValue() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.DmtReadOnly.GetNodeValue(this).run();
	}

	public void testDmtDataPluginGetNodeVersion() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.DmtReadOnly.GetNodeVersion(this).run();
	}

	public void testDmtDataPluginIsNodeUri() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.DmtReadOnly.IsNodeUri(this).run();
	}

	public void testDmtDataPluginIsLeafNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.DmtReadOnly.IsLeafNode(this).run();
	}
	
	// Dmt Methods
	public void testDmtDataPluginCommit() {
		//TODO Remove - It locks the session when an error occur  
		//new org.osgi.test.cases.dmt.tbc.DmtDataPlugin.Dmt.Commit(this).run();
	}
	
	public void testDmtDataPluginCopy() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.Dmt.Copy(this).run();
	}
	
	public void testDmtDataPluginCreateInteriorNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.Dmt.CreateInteriorNode(this).run();
	}
	
	public void testDmtDataPluginCreateLeafNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.Dmt.CreateLeafNode(this).run();
	}
	
	public void testDmtDataPluginDeleteNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.Dmt.DeleteNode(this).run();
	}

	public void testDmtDataPluginRenameNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.Dmt.RenameNode(this).run();
	}

	public void testDmtDataPluginRollback() {
		//TODO Remove - It locks the session when an error occur  
		//new org.osgi.test.cases.dmt.tbc.DmtDataPlugin.Dmt.Rollback(this).run();
	}
	
	public void testDmtDataPluginSetDefaultNodeValue() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.Dmt.SetDefaultNodeValue(this).run();
	}

	public void testDmtDataPluginSetNodeTitle() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.Dmt.SetNodeTitle(this).run();
	}
	
	public void testDmtDataPluginSetNodeType() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.Dmt.SetNodeType(this).run();
	}

	public void testDmtDataPluginSetNodeValue() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.Dmt.SetNodeValue(this).run();
	}
	
	//ReadOnlyDataPlugin
	//DmtReadOnly Methods
	public void testDmtReadOnlyDataPluginClose() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly.Close(this).run();
	}
	
	public void testDmtReadOnlyDataPluginGetChildNodeNames() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly.GetChildNodeNames(this).run();
	}
	
	public void testDmtReadOnlyDataPluginGetMetaNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly.GetMetaNode(this).run();
	}

	public void testDmtReadOnlyDataPluginGetNodeSize() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly.GetNodeSize(this).run();
	}

	public void testDmtReadOnlyDataPluginGetNodeTimestamp() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly.GetNodeTimestamp(this).run();
	}
	
	public void testDmtReadOnlyDataPluginGetNodeTitle() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly.GetNodeTitle(this).run();
	}

	public void testDmtReadOnlyDataPluginGetNodeType() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly.GetNodeType(this).run();
	}

	public void testDmtReadOnlyDataPluginGetNodeValue() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly.GetNodeValue(this).run();
	}

	public void testDmtReadOnlyDataPluginGetNodeVersion() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly.GetNodeVersion(this).run();
	}

	public void testDmtReadOnlyDataPluginIsNodeUri() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly.IsNodeUri(this).run();
	}

	public void testDmtReadOnlyDataPluginIsLeafNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly.IsLeafNode(this).run();
	}
	
	//DmtExecPlugin Method
	public void testDmtExecPluginExecute() {
		new org.osgi.test.cases.dmt.plugins.tbc.DmtExecPlugin.Execute(this).run();
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

	public void testDmtMetaNodeGetPattern() {
		new GetPattern(this).run();
	}

	public void testDmtMetaNodeGetNamePattern() {
		new GetNamePattern(this).run();
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
	public void testConstraints() {
		//TODO Remove - It's working but it spends a so much time (that's why it's commented)
		//new Constraints(this).run();
	}		
	
	/**
	 * Clean up after each method. Notice that during debugging many times the
	 * unsetState is never reached.
	 */
	public void unsetState() {
		log("#after each method");
	}

	/**
	 * Clean up after a run. Notice that during debugging many times the
	 * unprepare is never reached.
	 */
	public void unprepare() {
		log("#after each run");
	}

	/**
	 * @return Returns the factory.
	 */
	public DmtAdmin getDmtAdmin() {
		if (dmtAdmin != null)
			return dmtAdmin;
		else
			throw new NullPointerException("DmtAdmin factory is null");
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

	public void closeSession(DmtSession session) {
		if (null != session) {
			if (session.getState() == DmtSession.STATE_OPEN) {
				try {
					session.close();
				} catch (DmtException e) {
					log("#Exception closing the session: "+e.getClass().getName() + "Message: [" +e.getMessage() +"]");
				}
			}
		}
	}	

	public FatalExceptionDataPluginActivator getFatalExceptionDataPluginActivator() {
		return fatalExceptionDataPluginActivator;
	}

	public LogService getLogService() {
		return logService;
	}
}
