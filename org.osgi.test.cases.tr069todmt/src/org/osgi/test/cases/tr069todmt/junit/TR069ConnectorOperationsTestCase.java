package org.osgi.test.cases.tr069todmt.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.tr069todmt.ParameterInfo;
import org.osgi.service.tr069todmt.ParameterValue;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069ConnectorFactory;
import org.osgi.service.tr069todmt.TR069Exception;
import org.osgi.test.cases.tr069todmt.plugins.MetaNode;
import org.osgi.test.cases.tr069todmt.plugins.Node;
import org.osgi.test.cases.tr069todmt.plugins.TestDataPlugin;

/**
 * This class tests the different methods of the TR069Connector.
 * For all operations it is checked that the connector treats the each parameter or object 
 * path as relative to the root of the underlying Dmt Session.
 * 
 * Not covered are encoding and format conversion tests. 
 * These are topics of other TestCases.
 *
 * @author steffen.druesedow@telekom.de
 *
 */
public class TR069ConnectorOperationsTestCase extends TR069ToDmtTestBase {
	
	// a closed session must un-associate the connector --> How to test un-association?

	public void setUp() throws Exception {
		super.setUp();
		factory = getService(TR069ConnectorFactory.class);
		assertNotNull("Unable to get the TR069ConnectorFactory.", factory);
	}
	
	
	/**
	 * This test checks that "addObject" doesn't work in non-atomic session.
	 * see BUG: 2202
	 * 
	 * @throws Exception
	 */
	public void testAddObjectInNonAtomicSession() throws Exception {
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a non-atomic session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		
		try {
			// associate a new connector with the session on "./testplugin"
			connector = factory.create(session);
			
			// get name for a new object on this list
			connector.addObject(LISTNODE + ".");
			
			fail( "addObject must not be possible in non-atomic session" );
		} catch (TR069Exception e) {
			pass( "addObject is not possible in non-atomic session: " + e.getMessage() );
		}
	}
	
	/************************ addObject() tests ****************************/
	
	
	/**
	 * This test checks that adding an object to a list in lazy mode works as specified.
	 * 
	 * It is checked that the invocation of addObject does not create a new entry immediately 
	 * but returns a new instance id that does not conflict with the already existing indexes.
	 * 
	 * Furthermore it is checked that a subsequent call of getParameterValue really creates the new entry.
	 * 
	 * The spec makes the use of InstanceId nodes on List and Map entries mandatory to persistently 
	 * identify instances. So it is checked that the new entry has a node of name "InstanceId" 
	 * with the value that was returned by "addObject".
	 * 
	 * Spec: 
	 * >>>
	 * These concepts define the use of an InstanceId node that must be used
	 * by the connector to provide a TR-069 table view on the LIST and MAP nodes.
	 * <<<
	 * 
	 * @throws Exception
	 */
	public void testAddObjectToListInLazyMode() throws Exception {
		String nameValue = "name of new node";
		String descrValue = "description of new node";
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		
		try {
			// associate a new connector with the session on "./testplugin"
			connector = factory.create(session);
			
			// get name for a new object on this list
			String instanceId = connector.addObject(LISTNODE + ".");
			
			// no child node with such an instanceId must exist at this point
			assertInstanceIdNodeNotExists(LISTNODE, instanceId);
			
			// trigger creation of newNode by setting parameter values
			connector.setParameterValue(LISTNODE + "." + instanceId + ".name" , "name of new node", TR069Connector.TR069_STRING );
			connector.setParameterValue(LISTNODE + "." + instanceId + ".description" , "description of new node", TR069Connector.TR069_STRING );

			// new child node must now exist and have proper values for InstanceId, name and description
			assertInstanceIdNodeExists(LISTNODE, instanceId, nameValue, descrValue);
			assertUniqueInstanceIds(LISTNODE);
			
		} catch (DmtException e) {
			fail( "unexpected DMTException during test execution: " + e.getMessage() );
		}
		catch (TR069Exception e) {
			fail( "unexpected TR069Exception during test execution: " + e.getMessage() );
		}
	}

	/**
	 * This test checks that adding an object to a Map in lazy mode works as specified.
	 * 
	 * It is checked that the invocation of addObject does not create a new entry immediately 
	 * but returns a new key that does not conflict with the already existing ones.
	 * 
	 * It is checked that a subsequent call of getParameterValue creates the new entry.
	 * 
	 * No Alias is used for this test. 
	 * @throws Exception
	 */
	public void testAddObjectToMapInLazyMode() throws Exception {
		String nameValue = "name of new node";
		String descrValue = "description of new node";
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		
		try {
			// associate a new connector with the session on "./testplugin"
			connector = factory.create(session);
			String instanceId = connector.addObject(MAPNODE + ".");
			
			// no node with such an instanceId must exist at this point
			assertInstanceIdNodeNotExists(MAPNODE, instanceId);
			
			// trigger creation of newNode by setting parameter values
			connector.setParameterValue(MAPNODE + "." + instanceId + ".name" , "name of new node", TR069Connector.TR069_STRING );
			connector.setParameterValue(MAPNODE + "." + instanceId + ".description" , "description of new node", TR069Connector.TR069_STRING );

			// new child node must now exist and have proper values for InstanceId, name and description
			assertInstanceIdNodeExists(MAPNODE, instanceId, nameValue, descrValue);
			assertUniqueInstanceIds(LISTNODE);
			
		} catch (DmtException e) {
			fail( "unexpected DMTException during test execution: " + e.getMessage() );
		}
		catch (TR069Exception e) {
			fail( "unexpected TR069Exception during test execution: " + e.getMessage() );
		}
	}
	
	/**
	 * This test checks that adding an object with a given Alias to a Map works as specified in lazy mode.
	 * 
	 * It is checked that the invocation of addObject does not create a new entry immediately 
	 * and returns the given Alias as new key.
	 * 
	 * Furthermore it is checked that a subsequent call of getParameterValue creates the new entry.
	 * 
	 * The resulting child node must have been created with the given Alias as real Dmt node name
	 * and must furthermore also have an InstanceId to ensure proper table listing in TR069.
	 * 
	 * @throws Exception
	 */
	public void testAddObjectWithAliasToMapInLazyMode() throws Exception {
		String alias = "aliasKey";
		String nameValue = "name of new node";
		String descrValue = "description of new node";
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		
		try {
			// associate a new connector with the session on "./testplugin"
			connector = factory.create(session);
			// add object with Alias
			String newName = connector.addObject(MAPNODE + ".[" + alias + "].");
			assertEquals("AddObject must return the given Alias as new name.", alias, newName );
			
			// no node with such an alias must exist at this point
			assertFalse(session.isNodeUri(MAPNODE + "/" + newName));
			
			// trigger creation of newNode by setting parameter values
			connector.setParameterValue(MAPNODE + "." + newName + ".name" , "name of new node", TR069Connector.TR069_STRING );
			connector.setParameterValue(MAPNODE + "." + newName + ".description" , "description of new node", TR069Connector.TR069_STRING );

			// the node must have been created with the Alias as real Dmt node name 
			assertMapNodeExists(MAPNODE, newName, nameValue, descrValue);
			
			// the node must also have an InstanceId created by the connector
			// this InstanceId must be a long value
			// No other index/key node on the same level must have the same value for the InstanceId node.
			assertUniqueInstanceIds( MAPNODE );
			
		} catch (DmtException e) {
			fail( "unexpected DMTException during test execution: " + e.getMessage() );
		}
		catch (TR069Exception e) {
			fail( "unexpected TR069Exception during test execution: " + e.getMessage() );
		}
	}

	/**
	 * This test checks that adding an object to a Map in eager mode works as specified.
	 * 
	 * It is checked that the invocation of addObject does create a new entry immediately 
	 * Furthermore it is checked that a subsequent calls of getParameterValue sets the 
	 * correct values on the leafs.
	 * 
	 * No Alias is used for this test. 
	 * @throws Exception
	 */
	public void testAddObjectToMapInEagerMode() throws Exception {
		String nameValue = "name of new node";
		String descrValue = "description of new node";
		registerDefaultTestPlugin(ROOT, true);	// set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		
		try {
			// associate a new connector with the session on "./testplugin"
			connector = factory.create(session);
			String newName = connector.addObject(MAPNODE + ".");
			assertTrue( "The new node must be created immediately in the DMT: " + MAPNODE + "/" + newName, session.isNodeUri(MAPNODE + "/" + newName ) );

			// trigger creation of newNode by setting parameter values
			connector.setParameterValue(MAPNODE + "." + newName + ".name" , "name of new node", TR069Connector.TR069_STRING );
			connector.setParameterValue(MAPNODE + "." + newName + ".description" , "description of new node", TR069Connector.TR069_STRING );

			// new child node must now exist and have proper values for InstanceId, name and description
			assertInstanceIdNodeExists(MAPNODE, newName, nameValue, descrValue);
			assertUniqueInstanceIds(MAPNODE);

		} catch (DmtException e) {
			fail( "unexpected DMTException during test execution: " + e.getMessage() );
		}
		catch (TR069Exception e) {
			fail( "unexpected TR069Exception during test execution: " + e.getMessage() );
		}
	}
	
	/**
	 * This test checks that adding an object with a given Alias to a Map in eager 
	 * mode works as specified.
	 * 
	 * It is checked that the invocation of addObject does create a new entry immediately 
	 * Furthermore it is checked that a subsequent calls of getParameterValue sets the 
	 * correct values on the leafs.
	 * 
	 * @throws Exception
	 */
	public void testAddObjectWithAliasToMapInEagerMode() throws Exception {
		String alias = "aliasKey";
		String nameValue = "name of new node";
		String descrValue = "description of new node";
		registerDefaultTestPlugin(ROOT, true);	// set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		
		try {
			// associate a new connector with the session on "./testplugin"
			connector = factory.create(session);
			// add object with Alias
			String newName = connector.addObject(MAPNODE + ".[" + alias + "].");
			assertEquals("AddObject must return the given Alias as new name.", alias, newName );
			assertTrue( "The new node must be created immediately in the DMT.", session.isNodeUri(MAPNODE + "/" + newName ) );

			// set parameter values
			connector.setParameterValue(MAPNODE + "." + newName + ".name" , "name of new node", TR069Connector.TR069_STRING );
			connector.setParameterValue(MAPNODE + "." + newName + ".description" , "description of new node", TR069Connector.TR069_STRING );

			assertMapNodeExists(MAPNODE, newName, nameValue, descrValue);
			assertUniqueInstanceIds(MAPNODE);

		} catch (DmtException e) {
			fail( "unexpected DMTException during test execution: " + e.getMessage() );
		}
		catch (TR069Exception e) {
			fail( "unexpected TR069Exception during test execution: " + e.getMessage() );
		}
	}
	
	/**
	 * This test checks that TR069Exceptions are thrown, if addObject() is called
	 * with invalid pathes, e.g. absolute pathes or on objects that can not be created on the fly.   
	 * 
	 * @throws Exception
	 */
	public void testAddObjectOnInvalidPathes() throws Exception {
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		try {
			connector.addObject(SINGLETON + ".newObject.");
			fail( "addObject() must throw a TR069Exception.");
		} catch (TR069Exception e) {
			pass( "addObject() correctly throws a TR069Exception.");
		}

		try {
			connector.addObject(SINGLETON + ".newParameter");
			fail( "addObject() must throw a TR069Exception.");
		} catch (TR069Exception e) {
			pass( "addObject() correctly throws a TR069Exception.");
		}
		try {
			connector.addObject(MAPNODE   + ".100.newParameter");
			fail( "addObject() must throw a TR069Exception.");
		} catch (TR069Exception e) {
			pass( "addObject() correctly throws a TR069Exception.");
		}

		try {
			connector.addObject(LISTNODE  + ".100.newParameter");
			fail( "addObject() must throw a TR069Exception.");
		} catch (TR069Exception e) {
			pass( "addObject() correctly throws a TR069Exception.");
		}

		// test absolute path
		try {
			connector.addObject(ROOT + "." + MAPNODE + "[aliaskey].");
			fail("addObject() must not accept an absolute path.");
		} catch (TR069Exception e) {
			pass("addObject() does not accept an absolute path.");
		}
	}
	/************************ deleteObject() tests ****************************/
	
	/**
	 * This tests checks that deleteObject() deletes object from a table (list/map)
	 * and that invocations on invalid pathes throw an Exception with correct 
	 * fault code. 
	 * According to BUG-discussion 2202 this is done in an atomic session.
	 */
	public void testDeleteObject() throws Exception {
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		String instanceToDelete = "101";
		String missingInstance = "1001";
		
		// delete from LIST
		// ensure that the node is there
		assertInstanceIdNodeExists(LISTNODE, instanceToDelete, null, null);
		connector.deleteObject(LISTNODE + "." + instanceToDelete + "." );
		assertInstanceIdNodeNotExists(LISTNODE, instanceToDelete );
		
		// delete from MAP
		// ensure that the node is there
		assertInstanceIdNodeExists(MAPNODE, instanceToDelete, null, null);
		connector.deleteObject(MAPNODE + "." + instanceToDelete + "." );
		assertInstanceIdNodeNotExists(MAPNODE, instanceToDelete );
	
		// check that missing nodes are ignored and don't cause an exception
		try {
			connector.deleteObject(MAPNODE + "." + missingInstance );
			pass( "Missing nodes are correctly ignored in 'deleteObject()'; " + MAPNODE + "." + missingInstance);
		} catch (Exception e) {
			fail( "Missing nodes must not cause an exception in 'deleteObject()': " + MAPNODE + "." + missingInstance);
		}
		
		// try to delete an Object that is not part of a table (singleton object)
		try {
			connector.deleteObject(SINGLETON + "." );
			fail( "It must not be possible to delete singleton objects: " + SINGLETON + ".");
		} catch (TR069Exception e) {
			pass( "Calling 'deleteObject()' on singleton correctly throws a TR069Exception: " + SINGLETON + ".");
			assertEquals( "The expected fault code is " + 9005, 9005, e.getFaultCode() );
		}
		
		// try to delete parameters of LIST and MAP entries or singleton objects
		assertInstanceIdNodeExists(LISTNODE, "100", null, null);
		assertInstanceIdNodeExists(MAPNODE, "100", null, null);
		try {
			connector.deleteObject(MAPNODE + ".100.name");
			connector.deleteObject(LISTNODE + ".100.name");
			connector.deleteObject(SINGLETON + ".name");
			fail( "It must not be possible to delete Parameters of list/map or singleton nodes.");
		} catch (TR069Exception e) {
			pass( "Calling 'deleteObject()' on a parameter correctly throws a TR069Exception");
			List<Integer> expectedFaultCodes = Arrays.asList(new Integer[] {9001, 9002, 9003, 9005});
			assertTrue(expectedFaultCodes.contains( e.getFaultCode()) );
		}

		// test absolute path
		try {
			connector.deleteObject(ROOT + "." + LISTNODE + ".100");
			fail("deleteObject() must not accept an absolute path '" + ROOT + "." + SINGLETON + ".' on session root: " + ROOT);
		} catch (TR069Exception e) {
			pass("deleteObject() does not accept an absolute path '" + ROOT + "." + SINGLETON + ".' on session root: " + ROOT);
		}
	}

	/************************ getParameterNames() tests ****************************/

	/**
	 * This test checks that the GetParameterNames returns values as specified.
	 * 
	 * NOT TESTED here: 
	 * - translation of InstanceId node to pathes
	 * - creation of inexistent nodes on-the-fly
	 * - encoding 
	 * 
	 * TESTED:
	 * - Invocation on a parameter with nextLevel set to true
	 * It is expected that a TR069Exception is thrown with fault code 9003 
	 * (Invalid Argument).
	 * 
	 * - invocation on a parameter with nextLevel set to false
	 * The method must only return the ParameterInfo for the given Parameter.
	 * 
	 * - invocation on an Object (Map/List/instance) node with nextLevel = true
	 * Only the first level children must be returned.
	 * 
	 * - invocation on an Object (Map/List/instance) node with nextLevel = false
	 * The whole subtree of the object must be returned.
	 * 
	 * - invocation on a Map- or List- entry in form of a Parameter (without final ".")
	 * must cause a TR069Exception with fault code 9003 (Invalid Argument).
	 * 
	 * @throws Exception
	 */
	public void testGetParameterNamesResults() throws Exception {
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);
		
		Collection<ParameterInfo> infos = null;
		// invocation on a parameter with nextLevel = false
		// only the parameter itself must be returned
		try {
			infos = connector.getParameterNames(SINGLETON + ".name", false);
			assertNotNull(infos);
			assertEquals(1, infos.size());
			ParameterInfo info = infos.iterator().next();
			assertEquals(SINGLETON + ".name", info.getPath());
			assertTrue(info.isParameter());
			// check that the value is of expected type and content
			ParameterValue value = info.getParameterValue();
			assertEquals("plainname", value.getValue());
			assertEquals(TR069Connector.TR069_STRING, value.getType());
		} catch (TR069Exception e) {
			fail("Unexpected TR069Exception in GetParameterNames on a parameter with nextLevel=false.");
		}
		
		// invocation on a Map node with nextLevel = true
		// Only the first level children of the map must be returned.
		List<String> expected = new ArrayList<String>();
		// fill list of expected pathes with direct children
		fillPathSubtree(expected, MAPNODE, MAPNODE, true);
		assertCorrectParameterNames(expected, MAPNODE + ".", true);

		// invocation on a Map node with nextLevel = false
		// the whole subtree of the map must be returned.
		// This also takes care of Alias and NumberOfEntries nodes.
		expected = new ArrayList<String>();
		fillPathSubtree(expected, MAPNODE, MAPNODE, false);
		assertCorrectParameterNames(expected, MAPNODE + ".", false);

		// invocation on a List node with nextLevel = true
		// Only the first level children of the map must be returned.
		expected = new ArrayList<String>();
		fillPathSubtree(expected, LISTNODE, LISTNODE, true);
		assertCorrectParameterNames(expected, LISTNODE + ".", true);

		// invocation on a List node with nextLevel = false
		// the whole subtree of the map must be returned.
		expected = new ArrayList<String>();
		fillPathSubtree(expected, LISTNODE, LISTNODE, false);
		assertCorrectParameterNames(expected, LISTNODE + ".", false);

		assertNumberOfEntries(SINGLETON, SINGLETON);
		assertNumberOfEntries(MAPNODE, MAPNODE);
		assertNumberOfEntries(LISTNODE, LISTNODE);
	}
	
	/**
	 * This test checks that GetParameterNames creates missing elements of Maps and Lists on-the-fly.
	 * >>>
	 * The connector must attempt to create any missing nodes that are needed for the objectOrTablePath 
	 * by using the toURI(String, boolean) method with true.
	 * <<<
	 * According to BUG-discussion 2202 this is done in an atomic session.
	 * 
	 * @throws Exception
	 */
	public void testGetParameterNamesCreatesMissingNodes() throws Exception {
		String newInstanceId = "111";
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		// do the same sequence for a list node
		// no node with such an instanceId must exist at this point
		assertInstanceIdNodeNotExists(LISTNODE, newInstanceId);
		// getParameterName invoked on a Parameter of a non-existing map entry
		connector.getParameterNames(LISTNODE + "." + newInstanceId + ".name" , false);
		assertInstanceIdNodeExists(LISTNODE, newInstanceId, null, null );

		// no node with such an instanceId must exist at this point
		assertInstanceIdNodeNotExists(MAPNODE, newInstanceId);
		// getParameterName invoked on a Parameter of a non-existing map entry
		connector.getParameterNames(MAPNODE + "." + newInstanceId + ".name" , false);
		assertInstanceIdNodeExists(MAPNODE, newInstanceId, null, null );
		
		
	}

	/**
	 * This test checks that TR069Exceptions are thrown, if GetParameterNames is called
	 * with invalid pathes, e.g. absolute pathes or on objects or not existing paramters where the missing 
	 * nodes can not be created on the fly.   
	 * 
	 * @throws Exception
	 */
	public void testGetParameterNamesOnInvalidPathes() throws Exception {
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		@SuppressWarnings("unused")
		Collection<ParameterInfo> infos = null;
		// invocation on a parameter with nextLevel = true
		try {
			infos = connector.getParameterNames(SINGLETON + ".name", true);
			fail( "GetParameterNames on a parameter with nextLevel=true must throw an InvalidArgument exception (9003).");
		} catch (TR069Exception e) {
			assertEquals("GetParameterNames on a parameter with nextLevel=true must throw an InvalidArgument exception (9003).", 
					TR069Exception.INVALID_ARGUMENTS, e.getFaultCode());
		}

		try {
			connector.getParameterNames(LISTNODE + ".0", true);
			connector.getParameterNames(LISTNODE + ".0", false);
			connector.getParameterNames(MAPNODE + ".0", true);
			connector.getParameterNames(MAPNODE + ".0", false);
			fail( "GetParameterNames on an instance id must throw an InvalidArgument exception (9003).");
		} catch (TR069Exception e) {
			pass( "GetParameterNames on an instance id must throw an InvalidArgument exception (9003).");
		}
		
		// test absolute path
		try {
			connector.getParameterValue(ROOT + "." + SINGLETON + ".name");
			fail("getParameterValue() must not accept an absolute path '" + ROOT + "." + SINGLETON + ".' on session root: " + ROOT);
		} catch (TR069Exception e) {
			pass("getParameterValue() does not accept an absolute path '" + ROOT + "." + SINGLETON + ".' on session root: " + ROOT);
		}
	}
	
	/************************ getParameterValue() tests ****************************/
	
	/**
	 * This test checks that GetParameterValue creates missing elements of Maps and Lists on-the-fly.
	 * >>>
	 * The connector must attempt to create any missing nodes along the way, creating parent nodes on
	 * demand.
	 * <<<
	 * According to BUG-discussion 2202 this is done in an atomic session.
	 * 
	 * @throws Exception
	 */
	public void testGetParameterValueCreatesMissingNodes() throws Exception {
		String root = "./testplugin";
		String newInstanceId = "111";
		registerDefaultTestPlugin(root, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(root, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		// no node with such an instanceId must exist at this point
		assertInstanceIdNodeNotExists(MAPNODE, newInstanceId);
		// getParameterValue invoked on a Parameter of a non-existing map entry
		connector.getParameterValue(MAPNODE + "." + newInstanceId + ".name");
		assertInstanceIdNodeExists(MAPNODE, newInstanceId, null, null );
		
		// do the same sequence for a list node
		// no node with such an instanceId must exist at this point
		assertInstanceIdNodeNotExists(LISTNODE, newInstanceId);
		// getParameterValue invoked on a Parameter of a non-existing list entry
		connector.getParameterValue(LISTNODE + "." + newInstanceId + ".name");
		assertInstanceIdNodeExists(LISTNODE, newInstanceId, null, null );
	}

	/**
	 * This test checks the retrieval of synthetic nodes. 
	 * GetParameterValue must return the name of the parent node if the 
	 * Alias is requested.
	 * GetParameterValue must return the number of list/map entries if XNumberOfEntries 
	 * is requested.
	 * 
	 * According to BUG-discussion 2202 this is done in an atomic session.
	 * 
	 * @throws Exception
	 */
	public void testGetParameterValueOnSyntheticNodes() throws Exception {
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		// get Alias for Map entry
		String key = assertInstanceIdNodeExists(MAPNODE, "100", null, null );
		ParameterValue value = connector.getParameterValue(MAPNODE + ".100.Alias");
		assertEquals( TR069Connector.TR069_STRING, value.getType());
		assertEquals( key, value.getValue());
		
		// get Alias for List entry
		key = assertInstanceIdNodeExists(LISTNODE, "100", null, null );
		value = connector.getParameterValue(LISTNODE + ".100.Alias");
		assertEquals( TR069Connector.TR069_STRING, value.getType());
		assertEquals(key, value.getValue());
		
		// check number of entries
		String expected = "" + session.getChildNodeNames(MAPNODE).length;
		value = connector.getParameterValue(MAPNODE + "NumberOfEntries");
		assertEquals( expected, value.getValue() );
		assertEquals( TR069Connector.TR069_UNSIGNED_INT, value.getType());
		
		expected = "" + session.getChildNodeNames(LISTNODE).length;
		value = connector.getParameterValue(LISTNODE + "NumberOfEntries");
		assertEquals( expected, value.getValue() );
		assertEquals( TR069Connector.TR069_UNSIGNED_INT, value.getType());
		
	}
	
	/**
	 * This test checks that TR069Exceptions are thrown, if GetParameterValue is called
	 * with invalid pathes, e.g. on objects or not existing paramters where the missing 
	 * nodes can not be created on the fly.   
	 * 
	 * @throws Exception
	 */
	public void testGetParameterValueOnInvalidPathes() throws Exception {
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		try {
			// map and list nodes
			connector.getParameterValue(MAPNODE + ".");
			connector.getParameterValue(LISTNODE + ".");
			// table instances
			connector.getParameterValue(MAPNODE + ".100." );
			connector.getParameterValue(LISTNODE + ".100." );
			
			connector.getParameterValue(SINGLETON + ".");
			// inexistent
			connector.getParameterValue(SINGLETON + ".inexistentParameter");

			fail( "GetParameterValue on an object nodes must throw a TR069Exception.");
		} catch (TR069Exception e) {
			pass( "GetParameterValue on an object nodes correctly throws a TR069Exception.");
			List<Integer> expectedFaultCodes = Arrays.asList(new Integer[] {9001, 9002, 9003, 9005});
			assertTrue(expectedFaultCodes.contains( e.getFaultCode()) );
		}
		
		// test absolute path
		try {
			connector.getParameterValue(ROOT + "." + SINGLETON + ".name");
			fail("getParameterValue() must not accept an absolute path '" + ROOT + "." + SINGLETON + ".' on session root: " + ROOT);
		} catch (TR069Exception e) {
			pass("getParameterValue() does not accept an absolute path '" + ROOT + "." + SINGLETON + ".' on session root: " + ROOT);
		}
	}

	/************************ setParameterValue() tests ****************************/
	
	/**
	 * This test checks that SetParameterValue creates missing elements of Maps and Lists on-the-fly.
	 * >>>
	 * The connector must attempt to create any missing nodes along the way, creating parent nodes on
	 * demand.
	 * <<<
	 * According to BUG-discussion 2202 this is done in an atomic session.
	 * 
	 * @throws Exception
	 */
	public void testSetParameterValueCreatesMissingNodes() throws Exception {
		String newInstanceId = "111";
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		// no node with such an instanceId must exist at this point
		assertInstanceIdNodeNotExists(MAPNODE, newInstanceId);
		connector.setParameterValue(MAPNODE + "." + newInstanceId + ".name", "newNameValue", TR069Connector.TR069_STRING );
		assertInstanceIdNodeExists(MAPNODE, newInstanceId, null, null );
		assertUniqueInstanceIds(MAPNODE);
		
		// no node with such an instanceId must exist at this point
		assertInstanceIdNodeNotExists(LISTNODE, newInstanceId);
		connector.setParameterValue(LISTNODE + "." + newInstanceId + ".name", "newNameValue", TR069Connector.TR069_STRING );
		assertInstanceIdNodeExists(LISTNODE, newInstanceId, null, null );
		assertUniqueInstanceIds(LISTNODE);
	}
	
	/**
	 * This test checks that setParametValue on non-parameter nodes or on absolute pathes throws a 
	 * TR069Exception.
	 * 
	 * @throws Exception
	 */
	public void testSetParameterValueOnInvalidPathes() throws Exception {
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);
		
		try {
			// map and list nodes
			connector.setParameterValue(MAPNODE + ".", "newName", TR069Connector.TR069_STRING);
			connector.setParameterValue(LISTNODE + ".", "newName", TR069Connector.TR069_STRING);
			// table instances
			connector.setParameterValue(MAPNODE + ".100.", "newName", TR069Connector.TR069_STRING );
			connector.setParameterValue(LISTNODE + ".100.", "newName", TR069Connector.TR069_STRING );
			
			connector.setParameterValue(SINGLETON + ".", "newName", TR069Connector.TR069_STRING);
			// inexistent parameter
			connector.setParameterValue(SINGLETON + ".inexistentParameter", "newName", TR069Connector.TR069_STRING);

			fail( "setParameterValue on an object or an inexistent parameter throw a TR069Exception.");
		} catch (TR069Exception e) {
			pass( "setParameterValue on an object or an inexistent parameter correctly throws a TR069Exception.");
			List<Integer> expectedFaultCodes = Arrays.asList(new Integer[] {9001, 9002, 9003, 9003, 9005, 9006, 9007, 9008 });
			assertTrue(expectedFaultCodes.contains( e.getFaultCode()) );
		}

		// test absolute path
		try {
			connector.setParameterValue(ROOT + "." + SINGLETON + ".name", "newName", TR069Connector.TR069_STRING);
			fail("SetParameterValue() must not accept an absolute path '" + ROOT + "." + SINGLETON + ".' on session root: " + ROOT);
		} catch (TR069Exception e) {
			pass("SetParameterValue() does not accept an absolute path '" + ROOT + "." + SINGLETON + ".' on session root: " + ROOT);
		}
	}

	/**
	 * This test checks that SetParameterValue sets the value of the parameters to the given value.
	 * 
	 * NOTE: 
	 * No type checks will be done here - it is just about setting plain String values to String parameters.
	 * 
	 * @throws Exception
	 */
	public void testSetParameterValue() throws Exception {
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		// check initial value of the default plugins first MAP entry
		assertInstanceIdNodeExists(MAPNODE, "100", "name 0", "description 0");
		connector.setParameterValue(MAPNODE + ".100.name", "newNameValue", TR069Connector.TR069_STRING );
		connector.setParameterValue(MAPNODE + ".100.description", "newDescriptionValue", TR069Connector.TR069_STRING );
		// re-check entry with new values
		assertInstanceIdNodeExists(MAPNODE, "100", "newNameValue", "newDescriptionValue");
		
		// check initial value of the default plugins first LIST entry
		assertInstanceIdNodeExists(LISTNODE, "100", "name 0", "description 0");
		connector.setParameterValue(LISTNODE + ".100.name", "newNameValue", TR069Connector.TR069_STRING );
		connector.setParameterValue(LISTNODE + ".100.description", "newDescriptionValue", TR069Connector.TR069_STRING );
		// re-check entry with new values
		assertInstanceIdNodeExists(LISTNODE, "100", "newNameValue", "newDescriptionValue");
		
		// check initial value of the default plugins Singleton parameters
		assertEquals(session.getNodeValue(SINGLETON + "/name" ).toString(), "plainname");
		assertEquals(session.getNodeValue(SINGLETON + "/description" ).toString(), "plaindescription");
		connector.setParameterValue(SINGLETON + ".name", "newNameValue", TR069Connector.TR069_STRING );
		connector.setParameterValue(SINGLETON + ".description", "newDescriptionValue", TR069Connector.TR069_STRING );
		assertEquals(session.getNodeValue(SINGLETON + "/name" ).toString(), "newNameValue");
		assertEquals(session.getNodeValue(SINGLETON + "/description" ).toString(), "newDescriptionValue");
	}

	
	/**
	 * This test checks that SetParameterValue works correctly, if it is used on a Map-entries Alias.
	 * It is expected that the key of the entries is renamed to the given Alias.
	 * 
	 * NOTE: 
	 * No type checks will be done here - it is just about setting plain String values to String parameters.
	 * 
	 * According to BUG-discussion 2202 this is done in an atomic session.
	 * @throws Exception
	 */
	public void testSetParameterValueOnMapAlias() throws Exception {
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);

		// set the Alias for all Map entries to a new value
		String[] children = session.getChildNodeNames(MAPNODE);
		Map<String, String> mapping = new HashMap<String, String>();
		for (String child : children) {
			String id = "" + session.getNodeValue( MAPNODE + "/" + child + "/InstanceId").getLong();
			connector.setParameterValue(MAPNODE + "." + id + ".Alias", "alias_" + child, TR069Connector.TR069_STRING );
			mapping.put( "alias_" + child, id );
		}
		// re-read children and check that new Alias maps to the same id as before
		String[] newChildren = session.getChildNodeNames(MAPNODE);
		// the number of children must not have changed
		assertEquals(newChildren.length, children.length);
		for (String newChild : newChildren) {
			String id = "" + session.getNodeValue( MAPNODE + "/" + newChild + "/InstanceId").getLong();
			assertEquals(mapping.get(newChild), id);
			mapping.remove(newChild);
		}
		assertEquals(0, mapping.size());
	}

	
	/**
	 * 
	 * This test checks that SetParameterValue throws a TR069Exeption with fault code 
	 * NON_WRITABLE_PARAMETER (9008), if the corresponding Dmt node has attached Metadata that does 
	 * not permit the REPLACE operation.
	 * 
	 * NOTE: 
	 * No type checks will be done here - it is just about setting plain String values to String parameters.
	 * 
	 * @throws Exception
	 */
	public void testSetParameterValueOnNonWritableNode() throws Exception {
		// setup a simple plugin with a singleton that has 2 params
		// both params are read-only (via MetaData restrictions)
		Node rootNde = new Node(null, "mapped plugin root", false, null, null );
		
		Node singletonNode = new Node(rootNde, SINGLETON, false, null, null );
		singletonNode.setMetaNode( new MetaNode(false, MetaNode.PERMANENT, DmtData.FORMAT_NODE, new int[] {MetaNode.CMD_ADD, MetaNode.CMD_GET, MetaNode.CMD_REPLACE} ));
		Node param1 = new Node( singletonNode, "name", true, new DmtData("plainname"), null );
		Node param2 = new Node( singletonNode, "description", true, new DmtData("plaindescription"), null );
		MetaNode paramMetaNode = new MetaNode(true, MetaNode.AUTOMATIC, DmtData.FORMAT_STRING, new int[] {MetaNode.CMD_GET} );
		param1.setMetaNode(paramMetaNode);
		param2.setMetaNode(paramMetaNode);
		
		TestDataPlugin plugin = new TestDataPlugin("testplugin", rootNde);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, ROOT );
		registerService(DataPlugin.class.getName(), plugin, props);

		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		try {
			connector.setParameterValue(SINGLETON + ".name", "newNameValue", TR069Connector.TR069_STRING );
			fail( "setParameterValue on a non-writable parameter must throw a TR069Exception with fault code: " + TR069Exception.NON_WRITABLE_PARAMETER);
		} catch (TR069Exception e) {
			assertEquals( "setParameterValue on a non-writable parameter correctly throws a TR069Exception with fault code: " + TR069Exception.NON_WRITABLE_PARAMETER,
					TR069Exception.NON_WRITABLE_PARAMETER, e.getFaultCode());
		}
	}
	
	/**
	 * 
	 * This test checks that SetParameterValue throws a TR069Exeption with fault code 
	 * INVALID_PARAMETER_NAME (9005) if the addressed object does not have such a parameter.   
	 * 
	 * NOTE: 
	 * No type checks will be done here - it is just about setting plain String values to String parameters.
	 * 
	 * @throws Exception
	 */
	public void testSetParameterValueOnNonExistingParameter() throws Exception {
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type

		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);

		try {
			connector.setParameterValue(SINGLETON + ".nonexistingParam", "newValue", TR069Connector.TR069_STRING );
			assertFalse( "The connector must only create missing instances of Map or List nodes on the fly.", session.isNodeUri(SINGLETON + "/nonexistingParam"));
		} catch (TR069Exception e) {
			assertEquals( "setParameterValue on a non-existing object parameter correctly throws a TR069Exception with fault code: " + TR069Exception.INVALID_PARAMETER_NAME,
					TR069Exception.INVALID_PARAMETER_NAME, e.getFaultCode());
		}
	}
	
	/************************ toUri() tests ****************************/

	/**
	 * Checks that toUri treats all pathes as relative to DmtSession.
	 * >>>
	 * The connector assumes that each parameter or object path is relative to the root of the Dmt Session.
	 * <<<
	 */
	public void testToUriAssumesRelativePathes() throws Exception {
		// register simple plugin with one list, one map and a singleton object
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type

		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		// addressing a node with relative path must work and result must be relative as well
		String uri = null;
		try {
			uri = connector.toURI(SINGLETON + ".", false);
			assertFalse("The resulting uri must be relative to the DmtSession: " + uri, uri.startsWith(ROOT));
			assertEquals(SINGLETON, uri);
		} catch (TR069Exception e) {
			fail("ToUri() must accept relative path '" + SINGLETON + ".' on session root: " + ROOT + " without an Exception");
		}

		// addressing a node with absolute path (on a session root != ".") 
		// must throw an exception, because the node can not be found relative to the session
		try {
			uri = connector.toURI(ROOT + "." + SINGLETON + ".", false);
			fail("ToUri() must not accept an absolute path '" + ROOT + "." + SINGLETON + ".' on session root: " + ROOT);
		} catch (TR069Exception e) {
			pass("ToUri() does not accept an absolute path '" + ROOT + "." + SINGLETON + ".' on session root: " + ROOT);
		}
	}
	
	/**
	 * This test performs some "friendly" tests of the toUri() method without 
	 * special encoding requirements.
	 * All tests are expected to work without exceptions.
	 * 
	 * This test is based on knowledge of the data model that the test plugin maintains.
	 * See javadoc of registerDefaultTestPlugin for description of this datamodel. 
	 * 
	 * @throws Exception 
	 */
	public void testToUriForSimpleCases() throws Exception {
		// register simple plugin with one list, one map and a singleton object
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type

		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		// test some simple path conversions that should work without exceptions 
		assertFriendlyToUri(SINGLETON + ".", SINGLETON);	
		assertFriendlyToUri(SINGLETON + ".name", SINGLETON + "/name");

		// check map ...
		assertFriendlyToUri(MAPNODE + ".", MAPNODE);
		// with instanceId
		assertFriendlyToUri(MAPNODE + ".100.", MAPNODE + "/key0" ); 
		assertFriendlyToUri(MAPNODE + ".100.name", MAPNODE + "/key0/name" ); 
		// with Alias
		assertFriendlyToUri(MAPNODE + ".[key0].", MAPNODE + "/key0" ); 
		assertFriendlyToUri(MAPNODE + ".[key0].name", MAPNODE + "/key0/name" ); 

		// check list ...
		assertFriendlyToUri(LISTNODE + ".", LISTNODE);
		// with instanceId
		assertFriendlyToUri(LISTNODE + ".100.", LISTNODE + "/0" ); 
		assertFriendlyToUri(LISTNODE + ".100.name", LISTNODE + "/0/name" );
		
	}

	/**
	 * This test checks that toUri() throws a TR069Exception of fault-code INVALID_PARAMETER_NAME
	 * if the path points to an Alias or XNumberOfEntries parameter. 
	 * 
	 * @throws Exception
	 */
	public void testToUriForSyntheticNodes() throws Exception {
		// register simple plugin with one list, one map and a singleton object
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type

		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);
	
		// try to map an Alias parameter
		String path = MAPNODE + ".100.Alias";
		try {
			connector.toURI(path, false);
			fail( "Mapping the synthetic Alias parameter must throw a TR069Exception.INVALID_PARAMETER_NAME for path: " + path );
		} catch (TR069Exception e) {
			assertEquals( "Mapping the synthetic Alias parameter must throw a TR069Exception.INVALID_PARAMETER_NAME for path: " + path,
					TR069Exception.INVALID_PARAMETER_NAME, e.getFaultCode());
		}

		// try to map a NumberOfEntries parameter on a Map
		path = MAPNODE + "NumberOfEntries";
		try {
			connector.toURI(path, false);
			fail( "Mapping a synthetic NumberOfEntries parameter must throw a TR069Exception.INVALID_PARAMETER_NAME for path: " + path );
		} catch (TR069Exception e) {
			assertEquals( "Mapping a synthetic NumberOfEntries parameter must throw a TR069Exception.INVALID_PARAMETER_NAME for path: " + path,
					TR069Exception.INVALID_PARAMETER_NAME, e.getFaultCode());
		}

		// try to map a NumberOfEntries parameter on a List
		path = LISTNODE + "NumberOfEntries";
		try {
			connector.toURI(path, false);
			fail( "Mapping a synthetic NumberOfEntries parameter must throw a TR069Exception.INVALID_PARAMETER_NAME for path: " + path );
		} catch (TR069Exception e) {
			assertEquals( "Mapping a synthetic NumberOfEntries parameter must throw a TR069Exception.INVALID_PARAMETER_NAME for path: " + path,
					TR069Exception.INVALID_PARAMETER_NAME, e.getFaultCode());
		}
	}

	/**
	 * This test checks that toUri creates missing nodes on lists and maps, 
	 * if the create parameter is set to true.
	 *  
	 * According to BUG-discussion 2202 this is done in an atomic session.
	 * @throws Exception
	 */
	public void testToUriCreatesMissingNodes() throws Exception {
		String newInstanceId = "111";
		String newAlias = "newalias";
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		// no node with such an instanceId must exist at this point
		assertInstanceIdNodeNotExists(MAPNODE, newInstanceId);
		String uri = connector.toURI(MAPNODE + "." + newInstanceId + ".name", true);
		assertTrue(session.isNodeUri(uri));
		
		// do the same sequence for a list node
		// no node with such an instanceId must exist at this point
		assertInstanceIdNodeNotExists(LISTNODE, newInstanceId);
		uri = connector.toURI(LISTNODE + "." + newInstanceId + ".name", true);
		assertInstanceIdNodeExists(LISTNODE, newInstanceId, null, null );
		assertUniqueInstanceIds(LISTNODE);
		
		// test creation of a map entry that is addressed through an Alias
		assertMapNodeNotExists(MAPNODE, newAlias);
		uri = connector.toURI(MAPNODE + ".[" + newAlias + "].name", true);
		assertTrue(session.isNodeUri(uri));
		assertMapNodeExists(MAPNODE, newAlias, null, null);
	}

	/**
	 * This test checks that toUri on invalid pathes 
	 * TR069Exception.
	 * 
	 * @throws Exception
	 */
	public void testToUriOnInvalidPathes() throws Exception {
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type
		
		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);
		
		try {
			// without trailing "."
			connector.toURI(MAPNODE, false);
			connector.toURI(LISTNODE, false);
			connector.toURI(SINGLETON, false);
			connector.toURI(MAPNODE + ".100", false);
			connector.toURI(LISTNODE + ".100", false);
			
			connector.toURI(LISTNODE + ".inexistent.", false);
			connector.toURI(MAPNODE + ".inexistent.", false);
			connector.toURI("inexistent.", false);

			connector.toURI(SINGLETON + ".inexistentParam", false);
			connector.toURI(MAPNODE + ".100.inexistentParam", false);
			connector.toURI(LISTNODE + ".100.inexistentParam", false);

			fail( "toUri() on invalid path must throw a TR069Exception.");
		} catch (TR069Exception e) {
			pass( "toUri() on invalid path throws a TR069Exception.");
			List<Integer> expectedFaultCodes = Arrays.asList(new Integer[] {9001, 9002, 9003, 9003, 9005, 9006, 9007, 9008 });
			assertTrue(expectedFaultCodes.contains( e.getFaultCode()) );
		}

		// test absolute path
		try {
			connector.setParameterValue(ROOT + "." + SINGLETON + ".name", "newName", TR069Connector.TR069_STRING);
			fail("SetParameterValue() must not accept an absolute path '" + ROOT + "." + SINGLETON + ".' on session root: " + ROOT);
		} catch (TR069Exception e) {
			pass("SetParameterValue() does not accept an absolute path '" + ROOT + "." + SINGLETON + ".' on session root: " + ROOT);
		}
	}

	/************************ toPath() tests ****************************/

	/**
	 * Checks that toPath treats all pathes as relative to DmtSession.
	 * >>>
	 * The connector assumes that each parameter or object path is relative to the root of the Dmt Session.
	 * <<<
	 */
	public void testToPathAssumesRelativePathes() throws Exception {
		// register simple plugin with one list, one map and a singleton object
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type

		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		// addressing a node with relative path must work and result must be relative as well
		String path = null;
		try {
			path = connector.toPath(SINGLETON);
			assertFalse("The resulting path must be relative to the DmtSession: " + path, path.startsWith("."));
			assertEquals(SINGLETON + ".", path);
		} catch (TR069Exception e) {
			fail("ToPath() must accept relative path '" + SINGLETON + "' on session root: " + ROOT + " without an Exception");
		}

		// addressing a node with absolute path (on a session root != ".") 
		// must throw an exception, because the node can not be found relative to the session
		try {
			path = connector.toPath(ROOT + "/" + SINGLETON);
			fail("ToPath() must not accept an absolute path '" + ROOT + "/" + SINGLETON + "' on session root: " + ROOT);
		} catch (TR069Exception e) {
			pass("ToPath() does not accept an absolute path '" + ROOT + "/" + SINGLETON + "' on session root: " + ROOT);
		}
	}
	
	/**
	 * This test performs some "friendly" tests of the toPath() method without 
	 * special encoding requirements.
	 * All tests are expected to work without exceptions.
	 * 
	 * This test is based on knowledge of the data model that the test plugin maintains.
	 * See javadoc of registerDefaultTestPlugin for description of this datamodel. 
	 * 
	 * @throws Exception 
	 */
	public void testToPathForSimpleCases() throws Exception {
		// register simple plugin with one list, one map and a singleton object
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type

		// get a session on the testplugins root
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		// test some simple path conversions that should work without exceptions 
		assertFriendlyToPath(SINGLETON, SINGLETON + ".");	
		assertFriendlyToPath(SINGLETON + "/name", SINGLETON + ".name");

		// check map ...
		assertFriendlyToPath(MAPNODE, MAPNODE + ".");
		// with instanceId
		assertFriendlyToPath(MAPNODE + "/key0", MAPNODE + ".100." ); 
		assertFriendlyToPath(MAPNODE + "/key0/name", MAPNODE + ".100.name" ); 

		// check list ...
		assertFriendlyToPath(LISTNODE, LISTNODE + ".");
		// with instanceId
		assertFriendlyToPath(LISTNODE + "/0", LISTNODE + ".100." ); 
		assertFriendlyToPath(LISTNODE + "/0/name", LISTNODE + ".100.name" );
		
	}
	// ############ UTILITIES / HELPERS ########################################## 
	

	private void assertNumberOfEntries(String uri, String prefix) throws DmtException {
		String type = session.getNodeType(uri);
		Collection<ParameterInfo> infos = connector.getParameterNames("", true);
		assertNotNull("Missing parameter names for: " + session.getRootUri(), infos);
		String paramNumberOfEntries = prefix + "NumberOfEntries";
		boolean numberOfEntriesExist = false;
		for (ParameterInfo paramInfo : infos) {
			if (paramNumberOfEntries.equals(paramInfo.getPath())) {
				numberOfEntriesExist = true;
				break;
			}
		}
		boolean checkNumberOfEntries = DmtConstants.DDF_MAP.equals(type) || DmtConstants.DDF_LIST.equals(type);
		assertEquals(paramNumberOfEntries + " is incorrectly supported.", checkNumberOfEntries, numberOfEntriesExist);
	}
	
	/**
	 * Asserts that the object at the given path has the expected Parameters.
	 * 
	 * @param expected
	 * @param path
	 * @param nextLevel
	 */
	private void assertCorrectParameterNames(List<String> expected, String path, boolean nextLevel) {
		try {
			List<String> unexpected = new ArrayList<String>();
			Collection<ParameterInfo> infos = connector.getParameterNames(path, nextLevel);
			assertNotNull(infos);
			assertEquals(expected.size(), infos.size());
			for (ParameterInfo info : infos) {
				if ( ! expected.contains(info.getPath() ))
					unexpected.add(info.getPath());
				expected.remove(info.getPath());
			}
			assertEquals("There are unexpected results in the returned ParameterNames: " + unexpected, 0, unexpected.size());
			assertEquals("Expected results are missing in the returned ParameterNames: " + expected, 0, expected.size());
		} catch (TR069Exception e) {
			fail("Unexpected TR069Exception in GetParameterNames on path '"+path+"' with nextLevel="+nextLevel+".");
		}

	}
	
	/**
	 * Obtains a list of the path names under a given path from the Dmt.
	 * If parameter nextLevel is true then only the direct children are returned.
	 * If nextLevel is false then the result is the whole subtree.
	 * 
	 * The returned pathes are TR069 pathes. Indexes/keys of lists/maps are therefore replaced 
	 * by the value of the corresponding InstanceId of the list/map entry.
	 *  
	 * For lists and maps the synthetic parameterXNumberOfEntries is added.
	 * For each entry of a map the parameter Alias is added.
	 * 
	 * @param uri ... the Dmt uri to start with
	 * @param nextLevel ... only direct children returned if true, whole subtree otherwise
	 * @return
	 */
	private void fillPathSubtree( List<String> subtree, String uri, String prefix, boolean nextLevel ) throws Exception {
		String type = session.getNodeType(uri);
		boolean uriIsMap = DmtConstants.DDF_MAP.equals(type);
		boolean uriIsList = DmtConstants.DDF_LIST.equals(type);

		if ( !nextLevel )
			subtree.add(prefix + ".");
		
		for (String child : session.getChildNodeNames(uri)) {
			if ( "InstanceId".equals(child) )
				continue;
			// if child is a list/map entry, then the name must be replaced by the corresponding InstanceId value
			String id = child;
			String instanceIdUri = uri + "/" + child + "/InstanceId";
			boolean isLeaf = session.isLeafNode(uri + "/" + child);
			if ((uriIsList || uriIsMap) && !isLeaf && (session.isNodeUri(instanceIdUri))) {
				id = String.valueOf(session.getNodeValue(instanceIdUri).getLong());
			}
			if (!nextLevel && !isLeaf) {
				if (uriIsMap) {
					// if parent is a Map, then child is a map-key and needs a
					// synthetic Alias parameter
					subtree.add(prefix + "." + id + ".Alias");
				}
				fillPathSubtree(subtree, uri + "/" + child, prefix + "." + id, nextLevel);
			} else {
				if ( isLeaf )
					subtree.add( prefix + "." + id );
				else 
					subtree.add( prefix + "." + id + ".");
			}
		}
	}
	
	/**
	 * Compares the result of toUri() for the given path with the expected result.
	 * Asserts that both are equal and that no exception is thrown.
	 * @param path
	 * @param expected
	 */
	private void assertFriendlyToUri( String path, String expected ) {
		try {
			String uri = connector.toURI(path, false);
			assertNotNull(uri);
			assertFalse( "ToUri() must not return an absolute uri: " + uri, uri.startsWith("."));
			assertEquals( expected, uri );
		} catch (TR069Exception e) {
			fail( "Unexpected TR069Exception in toUri() for parameter: " + path );
		}
	}

	/**
	 * Compares the result of toPath() for the given uri with the expected result.
	 * Asserts that both are equal and that no exception is thrown.
	 * @param uri
	 * @param expected
	 */
	private void assertFriendlyToPath( String uri, String expected ) {
		try {
			String path = connector.toPath(uri);
			assertNotNull(path);
			assertFalse( "ToPath() must not return an absolute uri: " + path, path.startsWith("."));
			assertEquals( expected, path );
		} catch (TR069Exception e) {
			fail( "Unexpected TR069Exception in toPath() for parameter: " + uri );
		}
	}
}
