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
 * Feb 24, 2005  Luiz Felipe Guimaraes
 * 31            [MEGTCK][DMT] Validation of ConfigAdmin DMT Structure
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.TreeStructure;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * @generalDescription This Test Class Validates the implementation of RFC87 section 5.2 (Configuration)
 */
public class Configuration extends DefaultTestBundleControl {
	private DmtTestControl tbc;
	
	public final static String BUNDLE_PID = "br.org.cesar.test";
	
	public final static String URI_BUNDLE = DmtTestControl.OSGi_CFG + "/" + BUNDLE_PID;

	public final static String URI_BUNDLE_KEYS = DmtTestControl.OSGi_CFG + "/" + BUNDLE_PID + "/keys";
	
	public final static String LOCATION = "testlocation";
	
	public final static String URI_BUNDLE_LOCATION = URI_BUNDLE + "/" + LOCATION;
	
	public final static String URI_BUNDLE_PID = URI_BUNDLE +"/service.pid";
	
	public final static String BUNDLE_2_FACTORY_PID = "br.org.cesar.test2";

	public static String BUNDLE_2_PID = "";
	
	public static String URI_BUNDLE_2 = "";

	public static String URI_BUNDLE_2_KEYS = "";
	
	public static String URI_BUNDLE_2_LOCATION = "";
	
	public static String URI_BUNDLE_2_PID = "";
	
	public static String URI_BUNDLE_2_FACTORY_PID = "";
	
	public final static String LOCATION_2 = "testlocation2";

	public final static String SCALAR = "scalar";
	
	public final static String ARRAY = "array";
	
	public final static String VECTOR = "vector";
	
	public final static String CARDINALITY = "/cardinality";
	
	public final static String TYPE = "/type";
	
	public final static String VALUE = "/value";

	private org.osgi.service.cm.Configuration config = null;

	private org.osgi.service.cm.Configuration config2 = null;


	private void init() {
		try {
			Dictionary properties = new Hashtable();
			config = tbc.getConfigurationAdmin().getConfiguration(BUNDLE_PID,LOCATION);
			config2 = tbc.getConfigurationAdmin().createFactoryConfiguration(BUNDLE_2_FACTORY_PID,LOCATION_2);
			BUNDLE_2_PID = config2.getPid();
			URI_BUNDLE_2 = DmtTestControl.OSGi_CFG + "/" + BUNDLE_2_PID;
			URI_BUNDLE_2_KEYS = DmtTestControl.OSGi_CFG + "/" + BUNDLE_2_PID + "/keys";
			URI_BUNDLE_2_LOCATION = URI_BUNDLE_2 + "/" + LOCATION_2;
			URI_BUNDLE_2_PID = URI_BUNDLE_2_KEYS + "/service.pid";
			URI_BUNDLE_2_FACTORY_PID = URI_BUNDLE_2_KEYS + "/service.factoryPid";
			config.update(properties);
			config2.update(properties);
		} catch (Exception e) {
			tbc.fail("Failed creating a ManagedService");
		}
	}
	public Configuration(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		init();
		testConfiguration001();
		testConfiguration002();
		testConfiguration003();
		testConfiguration004();
		testConfiguration005();
		testConfiguration006();
		testConfiguration007();
		testConfiguration008();
		testConfiguration009();
		testConfiguration010();
		testConfiguration011();
		testConfiguration012();
		testConfiguration013();
		testConfiguration014();
		testConfiguration015();
	}

	/**
	 * @testID testConfiguration001
	 * @testDescription Asserts that a configuration dictionary from a Managed Service is stored in a expected node 
	 * 					and that there is a location node
	 */
	private void testConfiguration001() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration001");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that a configuration dictionary from a Managed Service is stored in the expected node ["+ URI_BUNDLE +"]",session.isNodeUri(URI_BUNDLE));
			tbc.assertEquals("Asserts that the service.pid node contains the expected value",BUNDLE_PID,session.getNodeValue(URI_BUNDLE_PID).getString());
			tbc.assertTrue("Asserts that there is a location node [" + URI_BUNDLE_LOCATION + "]",session.isNodeUri(URI_BUNDLE_LOCATION));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * @testID testConfiguration002
	 * @testDescription Asserts that a configuration dictionary from a Managed Service Factory is stored in a expected node 
	 * 					and that there is a location node
	 */
	private void testConfiguration002()  {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration002");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the <id> set by the Configuration Admin was created in the expected node ["+ URI_BUNDLE_2 +"]",session.isNodeUri(URI_BUNDLE_2));
			tbc.assertTrue("Asserts that there is a location node [" + URI_BUNDLE_2_LOCATION + "]",session.isNodeUri(URI_BUNDLE_2_LOCATION));
			tbc.assertEquals("Asserts that the service.pid node contains the expected value",BUNDLE_2_PID,session.getNodeValue(URI_BUNDLE_2_PID).getString());
			tbc.assertTrue("Asserts that there is a service.factoryPid node [" + URI_BUNDLE_2_FACTORY_PID + "]",session.isNodeUri(URI_BUNDLE_2_FACTORY_PID));
			tbc.assertEquals("Asserts that the service.factoryPid node contains the expected value",BUNDLE_2_FACTORY_PID,session.getNodeValue(URI_BUNDLE_2_FACTORY_PID).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * @testID testConfiguration003
	 * @testDescription Asserts that a String property is created correctly
	 */
	private void testConfiguration003() {
		tbc.log("#testConfiguration003");
		String propertyName = "stringproperty";
		String propertyValue = new String("StringProperty");
		String propertyValueClassName = "String";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE ;
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		try {	
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).getString());
			DmtData data = session.getNodeValue(uriPropertyValue);
			tbc.assertEquals("Asserts that the property has the correct value format set.",DmtData.FORMAT_STRING,data.getFormat());
			tbc.assertEquals("Asserts that the property has the correct value set.",propertyValue.toString(),data.getString());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",SCALAR,session.getNodeValue(uriPropertyCardinality).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * @testID testConfiguration004
	 * @testDescription Asserts that an Integer property is created correctly
	 */
	private void testConfiguration004() {
		tbc.log("#testConfiguration004");
		String propertyName = "integerproperty";
		Integer propertyValue = new Integer(10);
		String propertyValueClassName = "Integer";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE ;
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		try {
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).getString());
			DmtData data = session.getNodeValue(uriPropertyValue);
			tbc.assertEquals("Asserts that the property has the correct value format set.",DmtData.FORMAT_STRING,data.getFormat());
			tbc.assertEquals("Asserts that the property has the correct value set.",propertyValue.toString(),data.getString());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",SCALAR,session.getNodeValue(uriPropertyCardinality).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	/**
	 * @testID testConfiguration005
	 * @testDescription Asserts that a Long property is created correctly
	 */
	private void testConfiguration005() {
		tbc.log("#testConfiguration005");
		String propertyName = "longproperty";
		Long propertyValue = new Long(12);
		String propertyValueClassName = "Long";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE ;
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		try {
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).getString());
			DmtData data = session.getNodeValue(uriPropertyValue);
			tbc.assertEquals("Asserts that the property has the correct value format set.",DmtData.FORMAT_STRING,data.getFormat());
			tbc.assertEquals("Asserts that the property has the correct value set.",propertyValue.toString(),data.getString());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",SCALAR,session.getNodeValue(uriPropertyCardinality).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}			
	/**
	 * @testID testConfiguration006
	 * @testDescription Asserts that a Float property is created correctly
	 */
	private void testConfiguration006() {
		tbc.log("#testConfiguration006");
		String propertyName = "floatproperty";
		Float propertyValue = new Float(12);
		String propertyValueClassName = "Float";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE ;
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		try {
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).getString());
			DmtData data = session.getNodeValue(uriPropertyValue);
			tbc.assertEquals("Asserts that the property has the correct value format set.",DmtData.FORMAT_STRING,data.getFormat());
			tbc.assertEquals("Asserts that the property has the correct value set.",propertyValue.toString(),data.getString());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",SCALAR,session.getNodeValue(uriPropertyCardinality).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	/**
	 * @testID testConfiguration007
	 * @testDescription Asserts that a Double property is created correctly
	 */
	private void testConfiguration007() {
		tbc.log("#testConfiguration007");
		String propertyName = "doubleproperty";
		Double propertyValue = new Double(2.2);
		String propertyValueClassName = "Double";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE ;
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		try {
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).getString());
			DmtData data = session.getNodeValue(uriPropertyValue);
			tbc.assertEquals("Asserts that the property has the correct value format set.",DmtData.FORMAT_STRING,data.getFormat());
			tbc.assertEquals("Asserts that the property has the correct value set.",propertyValue.toString(),data.toString());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",SCALAR,session.getNodeValue(uriPropertyCardinality).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}		
	/**
	 * @testID testConfiguration008
	 * @testDescription Asserts that a Byte property is created correctly
	 */
	private void testConfiguration008() {
		tbc.log("#testConfiguration008");
		String propertyName = "byteproperty";
		Byte propertyValue = new Byte((byte)10);
		String propertyValueClassName = "Byte";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE ;
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		try {
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).getString());
			DmtData data = session.getNodeValue(uriPropertyValue);
			tbc.assertEquals("Asserts that the property has the correct value format set.",DmtData.FORMAT_STRING,data.getFormat());
			tbc.assertEquals("Asserts that the property has the correct value set.",propertyValue.toString(),data.getString());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",SCALAR,session.getNodeValue(uriPropertyCardinality).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * @testID testConfiguration009
	 * @testDescription Asserts that a Short property is created correctly
	 */
	private void testConfiguration009() {
		tbc.log("#testConfiguration009");
		String propertyName = "shortproperty";
		Short propertyValue = new Short((short)2);
		String propertyValueClassName = "Short";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE ;
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		try {
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).getString());
			DmtData data = session.getNodeValue(uriPropertyValue);
			tbc.assertEquals("Asserts that the property has the correct value format set.",DmtData.FORMAT_STRING,data.getFormat());
			tbc.assertEquals("Asserts that the property has the correct value set.",propertyValue.toString(),data.getString());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",SCALAR,session.getNodeValue(uriPropertyCardinality).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * @testID testConfiguration010
	 * @testDescription Asserts that a Character property is created correctly
	 */
	private void testConfiguration010() {
		tbc.log("#testConfiguration010");
		String propertyName = "characterproperty";
		Character propertyValue = new Character('A');
		String propertyValueClassName = "Character";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE ;
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		try {
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).getString());
			DmtData data = session.getNodeValue(uriPropertyValue);
			tbc.assertEquals("Asserts that the property has the correct value format set.",DmtData.FORMAT_STRING,data.getFormat());			
			tbc.assertEquals("Asserts that the property has the correct value set.",propertyValue.toString(),data.getString());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",SCALAR,session.getNodeValue(uriPropertyCardinality).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}		
	/**
	 * @testID testConfiguration011
	 * @testDescription Asserts that a Boolean property is created correctly
	 */
	private void testConfiguration011() {
		tbc.log("#testConfiguration011");
		String propertyName = "booleanproperty";
		Boolean propertyValue = new Boolean(true);
		String propertyValueClassName = "Boolean";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE;
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		try {
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).getString());
			DmtData data = session.getNodeValue(uriPropertyValue);
			tbc.assertEquals("Asserts that the property has the correct value format set.",DmtData.FORMAT_BOOLEAN,data.getFormat());
			tbc.assertEquals("Asserts that the property has the correct value set.",propertyValue.booleanValue(),data.getBoolean());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",SCALAR,session.getNodeValue(uriPropertyCardinality).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	/**
	 * @testID testConfiguration012
	 * @testDescription Asserts that an array property is created correctly
	 */
	private void testConfiguration012() {
		tbc.log("#testConfiguration012");
		String propertyName = "arrayproperty";
		int[] propertyValue = {1,2};
		String propertyValueClassName = "int";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE ;
		String uriPropertyValue_0 = uriPropertyValue + "/0";
		String uriPropertyValue_1 = uriPropertyValue + "/1";
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		try {
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).toString());
			tbc.assertTrue("Asserts that the value node ["+ uriPropertyValue + "] exists",session.isNodeUri(uriPropertyValue));
			tbc.assertEquals("Asserts that the property has the correct value set in ["+ uriPropertyValue_0 + "].",Integer.toString(propertyValue[0]),session.getNodeValue(uriPropertyValue_0).toString());
			tbc.assertEquals("Asserts that the property has the correct value set in ["+ uriPropertyValue_1 + "].",Integer.toString(propertyValue[1]),session.getNodeValue(uriPropertyValue_1).toString());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",ARRAY,session.getNodeValue(uriPropertyCardinality).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	/**
	 * @testID testConfiguration013
	 * @testDescription Asserts that a Vector property is created correctly and that null is permitted
	 * 					in Vectors.
	 */
	private void testConfiguration013() {
		tbc.log("#testConfiguration013");
		String propertyName = "vectoryproperty";
		Vector propertyValue = new Vector();
		String propertyValueClassName = "Integer";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE ;
		String uriPropertyValue_0 = uriPropertyValue + "/0";
		String uriPropertyValue_1 = uriPropertyValue + "/1";
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		try {
			propertyValue.add(0,new Integer(3));
			propertyValue.add(1,new Integer(5));
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).toString());
			tbc.assertTrue("Asserts that the value node ["+ uriPropertyValue + "] exists",session.isNodeUri(uriPropertyValue));
			tbc.assertEquals("Asserts that the property has the correct value set in ["+ uriPropertyValue_0 + "].",propertyValue.elementAt(0).toString(),session.getNodeValue(uriPropertyValue_0).toString());
			tbc.assertEquals("Asserts that the property has the correct value set in ["+ uriPropertyValue_1 + "].",propertyValue.elementAt(1).toString(),session.getNodeValue(uriPropertyValue_1).toString());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",VECTOR,session.getNodeValue(uriPropertyCardinality).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}			
	
	/**
	 * @testID testConfiguration014
	 * @testDescription Asserts that a byte[] property is created correctly
	 */
	private void testConfiguration014() {
		tbc.log("#testConfiguration014");
		String propertyName = "byteproperty";
	    byte[] propertyValue = new byte[1024];
		String propertyValueClassName = "byte";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE ;
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		
		try {
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).getString());
			DmtData data = session.getNodeValue(uriPropertyValue);
			tbc.assertEquals("Asserts that the property has the correct value format set.",DmtData.FORMAT_BINARY,data.getFormat());
			tbc.assertEquals("Asserts that the property has the correct value set.",propertyValue,data.getBinary());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",SCALAR,session.getNodeValue(uriPropertyCardinality).getString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	/**
	 * @testID testConfiguration015
	 * @testDescription Asserts that null is permitted in Vectors.
	 */
	private void testConfiguration015() {
		
		tbc.log("#testConfiguration015");
		String propertyName = "vectorynewproperty";
		Vector propertyValue = new Vector();
		String propertyValueClassName = "Integer";
		String uriProperty = URI_BUNDLE_KEYS + "/" + propertyName;
		String uriPropertyType = uriProperty + TYPE ;
		String uriPropertyCardinality = uriProperty + CARDINALITY ;
		String uriPropertyValue = uriProperty + VALUE ;
		String uriPropertyValue_0 = uriPropertyValue + "/0";
		String uriPropertyValue_1 = uriPropertyValue + "/1";
		Dictionary properties = new Hashtable();
		
		DmtSession session = null;
		try {
			propertyValue.add(0,new Integer(3));
			propertyValue.add(1,null);
			properties.put(propertyName, propertyValue);
			config.update(properties);
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that the property node ["+ uriProperty + "] exists",session.isNodeUri(uriProperty));
			tbc.assertEquals("Asserts that the property has the correct type set.",propertyValueClassName,session.getNodeValue(uriPropertyType).toString());
			tbc.assertTrue("Asserts that the value node ["+ uriPropertyValue + "] exists",session.isNodeUri(uriPropertyValue));
			tbc.assertEquals("Asserts that the property has the correct value set in ["+ uriPropertyValue_0 + "].",propertyValue.elementAt(0).toString(),session.getNodeValue(uriPropertyValue_0).toString());
			tbc.assertEquals("Asserts that the property has the correct value set in ["+ uriPropertyValue_1 + "].",propertyValue.elementAt(1).toString(),session.getNodeValue(uriPropertyValue_1).toString());
			tbc.assertEquals("Asserts that the property has the correct cardinality set.",VECTOR,session.getNodeValue(uriPropertyCardinality).toString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}		

}
