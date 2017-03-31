/*
 * Copyright (c) OSGi Alliance (2000, 2015). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.residentialmanagement;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.log.LogService;
import org.osgi.test.cases.residentialmanagement.plugins.MultiRootDataPlugin;
import org.osgi.test.cases.residentialmanagement.plugins.Node;
import org.osgi.test.support.sleep.Sleep;


/**
 * This test case checks possible operations on the Filter subtree of the RDMT
 *
 * @author Steffen Druesedow (Deutsche Telekom Laboratories)
 */
@SuppressWarnings("deprecation")
public class FilterOperationsTestCase extends RMTTestBase {

	/**
	 * Check behavior for invalid targets.
	 * Attempts to get results for invalid targets. It is expected that a
	 * DmtException is thrown - otherwise the test fails.
	 * @throws Exception
	 */
	public void testInvalidTargets() throws Exception {

		session = dmtAdmin.getSession(FILTER_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(session);

		// create filter to initialize creation of the automatic nodes
		String uri = FILTER_ROOT + "/" + "InvalidTargetsTest";
		session.createInteriorNode( uri );
		session.commit(); // TODO: This commit should not be necessary - needs check

		try {
			assertInvalidTargetRejected(uri, "A/" );		// must be absolute
			assertInvalidTargetRejected(uri, "./A" );		// must end with slash
			assertInvalidTargetRejected(uri, "./A/-/" );
			assertInvalidTargetRejected(uri, "./A/-/*/" );
			assertInvalidTargetRejected(uri, "./A/*/-/" );
		} finally {
			cleanupSearch(session, uri);
		}
	}

	/**
	 * Checks behavior for invalid filters.
	 * @throws Exception
	 */
	public void testInvalidFilters() throws Exception {
		Node root = new Node(null, "", null );
		Node n2 = new Node(root, "invalidSearch1", null ); // interior
		new Node(n2, "b1", "leaf 1");	// leaf
		new Node(n2, "b2", "leaf 2");	// leaf
		new Node(n2, "level", "1");		// leaf

		Node n3 = new Node(root, "invalidSearch2", null); // interior
		new Node(n3, "c1", "leaf 1");	// leaf
		new Node(n3, "c2", "leaf 2");	// leaf
		new Node(n3, "level", "2");		// leaf

		// mount plugin unter "./RMT/InvalidFilter"
		MultiRootDataPlugin aclPlugin = new MultiRootDataPlugin("P1", root);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, "./Test/InvalidFilter" );
		registerService(DataPlugin.class.getName(), aclPlugin, props);

		// create new filter
		session = dmtAdmin.getSession( ".", DmtSession.LOCK_TYPE_ATOMIC);
		String uri = FILTER_ROOT + "/" + "InvalidFilterTest";
		session.createInteriorNode( uri );
		session.commit();

		String[] resultUriList = null;
		String[] resultList = null; 
		try {
			session.setNodeValue(uri + "/" + TARGET, new DmtData( "./Test/InvalidFilter/*/"));
			session.setNodeValue(uri + "/" + FILTER, new DmtData("invalid-filter-expression"));
			session.commit();

			// try to access resultUriList and result --> this should cause an exception
			resultUriList = session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			resultList = session.getChildNodeNames(uri + "/" + RESULT );
			fail( "An invalid filter expression must not be accepted." );
		}
		catch (DmtException x) {
			pass( "An invalid filter expression correctly causes a DmtException." );
		}
		finally {
			assertNull("The resultUriList must be empty for this search.", resultUriList);
			assertNull("The result must be empty for this search.", resultList);
			cleanupSearch(session, uri);
		}
	}


	/**
	 * checks that the Result and ResultUriList is empty, if no target it set
	 * @throws Exception
	 */
	public void testEmptyResultIfNoTarget() throws Exception {
		session = dmtAdmin.getSession(FILTER_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(session);

		// create filter to initialize creation of the automatic nodes
		String uri = FILTER_ROOT + "/" + "InvalidTargetsTest";
		session.createInteriorNode( uri );
		session.commit(); // TODO: This commit should not be necessary - needs check

		try {
			// force a search
			String[] children = session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			assertEquals("The ResultUriList must be empty if no target is set.", 0, children.length);
			children = session.getChildNodeNames(uri + "/" + RESULT );
			assertEquals("The Result must be empty if no target is set.", 0, children.length);
		}
		finally {
			cleanupSearch(session, uri);
		}

	}


	/**
	 * Checks that a search without a filter is possible and returns unfiltered results.
	 * Furthermore it is checked that all returned results point to interior nodes.
	 *
	 * The target is set to all LogEntries of the RMT.
	 * It is expected that the results hold all indexes of the log entries.
	 */
	public void testSearchWithoutFilter() throws Exception {
		// create a number of random logs to ensure that the Log part of the RMT is filled
		createRandomLogs(100);
		Sleep.sleep(DELAY);

		String uri = FILTER_ROOT + "/" + "SimpleLogSearch";
		session = dmtAdmin.getSession(RMT_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		session.createInteriorNode( uri );
		session.commit();

		try {
			// set target to all LogEntries
			session.setNodeValue(uri + "/" + TARGET, new DmtData(LOG_ROOT + "/LogEntries/*/"));
			session.commit();

			// check resultUriList
			String[] resultUriList = session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			String[] realChildren = session.getChildNodeNames(LOG_ROOT + "/" + LOG_ENTRIES );
			assertEquals("Searching without a filter must return all (unfiltered) results.", realChildren.length, resultUriList.length );
			// check that all uris point to interior nodes
			for (String resultUri : resultUriList) {
				String rUri = uri + "/" + RESULT_URI_LIST + "/" + resultUri;
				String value = session.getNodeValue(rUri).getString();
				assertFalse("The uris in the ResultUriList must point to interior nodes: " + uri + "/" + RESULT + "/" + value, session.isLeafNode(uri + "/" + RESULT + "/" + value));
			}

			// check result tree
			String[] results = session.getChildNodeNames(uri + "/" + RESULT );
			// session starts at $/Framework, so the result tree starts at "LOG" and must contain all unfiltered logEntries
			// session root is $/Framework, so the only result childNode must be "Log"
			assertEquals( "The result must only have one child for this search.", 1, results.length );
			assertEquals( "The only child node for this search must be: " + LOG, LOG, results[0]);

			results = session.getChildNodeNames(uri + "/" + RESULT + "/" + LOG );
			assertEquals( "The result must only have one child for this search.", 1, results.length );
			assertEquals( "The only child node for " + uri + "/" + RESULT + "/" + LOG + " must be: " + LOG_ENTRIES, LOG_ENTRIES, results[0]);
			results = session.getChildNodeNames(uri + "/" + RESULT + "/" + LOG + "/" + LOG_ENTRIES);
			assertEquals("Searching without a filter must return all (unfiltered) results.", realChildren.length, results.length );

		}
		finally {
			cleanupSearch(session, uri);
		}
	}

	/**
	 * Check that the result-uris are relative to current session.
	 * Search target is $/Framework/Log/LogEntries/* /
	 * The target will be set in one session that was opened on "." (root of DMT) and closed then.
	 * The results will be read in a new session on "$".
	 * It is expected that the uris in the ResultUriList are relative to "$".
	 */
	public void testResultsAreRelativeToSession() throws Exception {
		// create a number of random logs to ensure that the Log part of the RMT is filled
		createRandomLogs(10);
		Sleep.sleep(DELAY);

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_ATOMIC);
		String uri = FILTER_ROOT + "/" + "RelativeUris";
		session.createInteriorNode( uri );
		session.commit();

		try {
			// set target to all LogEntries
			session.setNodeValue(uri + "/" + TARGET, new DmtData(LOG_ROOT + "/LogEntries/*/"));
			session.commit();
			session.close();
			// create second session
			session = dmtAdmin.getSession(RMT_ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			String[] resultUriList = session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			assertTrue( "The resultUriList must not be empty for this search.", resultUriList.length > 0 );
			for (String index : resultUriList) {
				String value = session.getNodeValue(uri + "/" + RESULT_URI_LIST + "/" + index).getString();

				// session root is $, so the uris must start with "Log/LogEntries"
				assertTrue( "The result uris must be relative to current session: " + value, value.startsWith(LOG + "/" + LOG_ENTRIES ));
			}
			// the result tree must start at the session root and contain only matching nodes
			// --> for current search, it must only contain one child of name "Log"
			String[] results = session.getChildNodeNames(uri + "/" + RESULT );
			assertEquals( "The result must have exactly one child for this search.", 1, results.length );
			// session root is $, so the only result childNode must be "Log"
			assertEquals( "The only child node for this search must be: " + LOG, LOG, results[0]);
		}
		finally {
			cleanupSearch(session, uri);
		}

	}

	/**
	 * Checks that no results are returned that are part of the Filter sub-tree.
	 * Search target is $/* /* /
	 * It is expected that the ResultUriList and Result contain no uris from the "Filter" subtree.
	 */
	public void testFilterTreeExcluded() throws Exception {
		// create a number of random logs to ensure that the Log part of the RMT is filled
		createRandomLogs(10);
		Sleep.sleep(DELAY);

		String uri = FILTER_ROOT + "/" + "FilterTreeExcluded";
		session = dmtAdmin.getSession(RMT_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		session.createInteriorNode( uri );
		session.commit();

		try {
			session.setNodeValue(uri + "/" + TARGET, new DmtData(RMT_ROOT + "/*/*/"));
			session.commit();

			String[] resultUriList = session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			assertTrue( "The resultUriList must not be empty for this search.", resultUriList.length > 0 );
			for (String index : resultUriList) {
				String value = session.getNodeValue(uri + "/" + RESULT_URI_LIST + "/" + index).getString();

				// the uris must not start with "Filter"
				assertFalse( "Matches from the Filter subtree must be excluded: " + value, value.startsWith(FILTER));
			}

			// results are relative to $ --> must not start with "Filter"
			String[] results = session.getChildNodeNames(uri + "/" + RESULT );
			assertTrue( "The result tree must not be empty for this search.", results.length > 0 );
			for (String result : results)
				// there must be no child of name "Filter"
				assertFalse( "Matches from the Filter subtree must be excluded: " + result, FILTER.equals(result));

		}
		finally {
			cleanupSearch(session, uri);
		}

	}

	/**
	 * Checks that the subtrees of Result and ResultUriList can not be modified through a DmtSession.
	 *
	 * Registers a Plugin that manages a simple tree. This tree itself is writeable, i.e. nodes can be created, modified and deleted.
	 * Then it performs a simple search on this plugin and attempts several write requests to the results.
	 * Fails, if any of the attempts is permitted without an exception.
	 * @throws Exception
	 */
	public void testResultsAreReadOnly() throws Exception {
		Node root = new Node(null, "A", null );
		Node n2 = new Node(root, "B", null );
		new Node(n2, "b1", "leaf b1");	// leaf
		new Node(n2, "b2", "leaf b2");	// leaf

		Node n3 = new Node(root, "C", null);
		new Node(n3, "c1", "leaf c1");	// leaf
		new Node(n3, "c2", "leaf c2");	// leaf

		MultiRootDataPlugin plugin = new MultiRootDataPlugin("P1", root);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, "./TestPlugin" );
		registerService(DataPlugin.class.getName(), plugin, props);

		String uri = FILTER_ROOT + "/" + "FilterReadOnlyTest";
		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_ATOMIC);
		session.createInteriorNode( uri );
		session.commit();

		try {
			session.setNodeValue(uri + "/" + TARGET, new DmtData("./TestPlugin/*/"));
			session.commit();

			String[] resultUriList = session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			assertEquals("The resultUriList must contain 2 entries for this search.", 2, resultUriList.length);

			// trying to add a new entry to the resultUriList
			try {
				session.createLeafNode(uri + "/" + RESULT_URI_LIST + "/" + resultUriList.length, new DmtData("impossible"));
				fail("It must not be possible to add nodes to ResultUriList.");

			} catch (DmtException e) {
				pass("Passed: Not allowed to add nodes to ResultUriList --> OK.");
			}

			// trying to delete entries of the resultUriList
			try {
				session.deleteNode(uri + "/" + RESULT_URI_LIST + "/0");
				fail("It must not be possible to delete nodes from the ResultUriList.");
			} catch (DmtException e) {
				pass("Passed: Not allowed to delete nodes from the ResultUriList --> OK.");
			}

			// trying to modifiy entries of the resultUriList
			try {
				for (String index : resultUriList) {
					String resultUri = uri + "/" + RESULT_URI_LIST + "/" + index;
					session.setNodeValue(resultUri, new DmtData("impossible"));
					session.setDefaultNodeValue(resultUri);
					session.setNodeTitle(resultUri, "impossible");
					session.setNodeType(resultUri, "text/plain");
					session.setNodeAcl(resultUri, new Acl("Add=*&Replace=*&Get=*"));
					fail("It must not be possible to modify entries of the ResultUriList.");
				}

			} catch (DmtException e) {
				pass("Passed: Not allowed to modify entries of the ResultUriList --> OK.");
			}


			// ************ do the same tests with the Result tree
			// results are relative to "."
			String[] results = session.getChildNodeNames(uri + "/" + RESULT );
			assertEquals("The result must contain 1 child for this search.", 1, results.length);

			// trying to add new children to the result
			try {
				session.createLeafNode(uri + "/" + RESULT + "/impossible", new DmtData("impossible"));
				session.createInteriorNode(uri + "/" + RESULT + "/impossible2");
				fail("It must not be possible to add nodes to Result.");

			} catch (DmtException e) {
				pass("Passed: Not allowed to add nodes to Result --> OK.");
			}

			// trying to delete children of the result
			try {
				session.deleteNode(uri + "/" + RESULT + "/" + results[0]);
				fail("It must not be possible to delete nodes from the Result.");
			} catch (DmtException e) {
				pass("Passed: Not allowed to delete nodes in the subtree of Result --> OK.");
			}

			// trying to modifiy the Result
			try {
				for (String result : results) {
					String resultUri = uri + "/" + RESULT + "/" + result;
					session.setNodeValue(resultUri, new DmtData("impossible"));
					session.setDefaultNodeValue(resultUri);
					session.setNodeType(resultUri, "text/plain");
					session.setNodeTitle(resultUri, "impossible");
					session.setNodeAcl(resultUri, new Acl("Add=*&Replace=*&Get=*"));
					fail("It must not be possible to modify nodes in the subtree of Result.");
				}

			} catch (DmtException e) {
				pass("Passed: Not allowed to modify nodes in the subtree of Result --> OK.");
			}

		}
		finally {
			cleanupSearch(session, uri);
		}
	}


	/**
	 * Checks that only results are returned that are visible to the current session.
	 *
	 * Two plugins are registered, only one of them will be part of the session that reads
	 * the filtering results.
	 * It is expected that the results contain only nodes from the visible plugin.
	 * This test relies on the configuration of the RMT-root on "./Scaffold/RMT".
	 * @throws Exception
	 */
	public void testVisibilityBySession() throws Exception {
		Node root1 = new Node(null, "", null );
		Node n2 = new Node(root1, "B", null );
		new Node(n2, "b1", "leaf b1");	// leaf
		new Node(n2, "b2", "leaf b2");	// leaf

		Node root2 = new Node(null, "", null );
		Node n3 = new Node(root1, "C", null );
		new Node(n3, "c1", "leaf c1");	// leaf
		new Node(n3, "c2", "leaf c2");	// leaf

		// mount first plugin unter "./Scaffold/visible" --> sibling of the RMT root node
		MultiRootDataPlugin visiblePlugin = new MultiRootDataPlugin("P1", root1);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, "./Scaffold/visible" );
		registerService(DataPlugin.class.getName(), visiblePlugin, props);

		// mount second plugin unter "./outside/invisible"
		MultiRootDataPlugin invisiblePlugin = new MultiRootDataPlugin("P2", root2);
		props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, "./outside/invisible" );
		registerService(DataPlugin.class.getName(), invisiblePlugin, props);

		String uri = FILTER_ROOT + "/" + "VisibilityTest";
		session = dmtAdmin.getSession("./Scaffold", DmtSession.LOCK_TYPE_ATOMIC);
		session.createInteriorNode( uri );
		session.commit();

		try {
			// this target covers both plugins, but only one of them is part of the session
			session.setNodeValue(uri + "/" + TARGET, new DmtData("./*/*/"));
			session.commit();

			Set<String> expected = new HashSet<String>();
			Set<String> wrong = new HashSet<String>();
			expected.add( "visible" );
			expected.add( "RMT" );
			String[] resultUriList = session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			for (String index : resultUriList) {
				String value = session.getNodeValue(uri + "/" + RESULT_URI_LIST + "/" + index).getString();
				if ( ! expected.contains(value))
					wrong.add(value);
				expected.remove(value);
			}
			assertEquals("The resultUriList contains unexpected entries: " + wrong, 0, wrong.size() );
			assertEquals("Expected entries are missing in the in the resultUri list: " + expected, 0, expected.size() );

			expected = new HashSet<String>();
			wrong = new HashSet<String>();
			expected.add( "visible" );
			expected.add( "RMT" );
			String[] results = session.getChildNodeNames(uri + "/" + RESULT );
			for (String result : results) {
				if ( ! expected.contains(result))
					wrong.add(result);
				expected.remove(result);
			}
			assertEquals("The result tree contains unexpected nodes: " + wrong, 0, wrong.size() );
			assertEquals("Expected nodes are missing in the in the result tree: " + expected, 0, expected.size() );
		}
		finally {
			cleanupSearch(session, uri);
		}

	}

	/**
	 * Checks that the result only returns nodes that the caller (the session) has access to.
	 *
	 * In order to test this, a plugin is registered and a limiting ACL is set to parts of it's subtree.
	 * Then a session is created with a given principal and the search is triggered.
	 * It is expected that only the parts of the plugins subtree are returned where the caller
	 * (the given principal) has "Get" permission for.
	 * @throws Exception
	 */
	public void testVisibilityByACL() throws Exception {
		Node root = new Node(null, "", null );
		Node n2 = new Node(root, "visible", null ); // interior
		new Node(n2, "b1", "leaf b1");	// leaf
		new Node(n2, "b2", "leaf b2");	// leaf

		Node n3 = new Node(root, "invisible", null); // interior
		new Node(n3, "c1", "leaf c1");	// leaf
		new Node(n3, "c2", "leaf c2");	// leaf

		// mount first plugin unter "./Test/ACLCheck"
		MultiRootDataPlugin aclPlugin = new MultiRootDataPlugin("P1", root);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, "./Test/ACLCheck" );
		registerService(DataPlugin.class.getName(), aclPlugin, props);

		// create a session to limit GET - ACL for one interior child node to a non-existing principal
		session = dmtAdmin.getSession( "./Test", DmtSession.LOCK_TYPE_ATOMIC);
		session.setNodeAcl("./Test/ACLCheck/invisible", new Acl("Get=highlyUnlikelyPrincipal"));
		session.close();

		// create new filter
		session = dmtAdmin.getSession( FILTER_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		String uri = FILTER_ROOT + "/" + "AclVisibilityTest";
		session.createInteriorNode( uri );
		session.commit();

		try {
			// this target should cover all interior nodes of the registered aclPlugin
			session.setNodeValue(uri + "/" + TARGET, new DmtData("./Test/ACLCheck/*/"));
			session.commit();
			session.close();

			// create a session with a principal that does not match the configured one
			session = dmtAdmin.getSession( "admin", ".", DmtSession.LOCK_TYPE_ATOMIC);

			// results must not contain the "invisible" node, because of the ACL restriction
			String[] resultUriList = session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			assertEquals("The resultUriList must contain 1 entry for this search.", 1, resultUriList.length);
			String value = session.getNodeValue(uri + "/" + RESULT_URI_LIST + "/0").getString();
			assertEquals( "Only the visible node must be in the resultUri list.", "Test/ACLCheck/visible", value );

			String[] results = session.getChildNodeNames(uri + "/" + RESULT );
			assertEquals("The result must contain 1 entry for this search.", 1, results.length);
			assertEquals( "Only the visible node must be in the resultUri list.", "Test", results[0] );
		}
		finally {
			cleanupSearch(session, uri);
		}
	}

	/**
	 * Checks that the result only returns nodes that match the configured filter.
	 * The search is performed against "primitive" nodes, i.e. not list nodes.
	 *
	 * A plugin is registered with some child nodes, a search is created with a target and a filter,
	 * that must limit the result set.
	 * It is expected that only the parts of the plugins subtree are returned that match the filter.
	 * @throws Exception
	 */
	public void testFilterAgainstPrimitiveNodes() throws Exception {
		Node root = new Node(null, "", null );
		Node n2 = new Node(root, "visible", null ); // interior
		new Node(n2, "b1", "leaf 1");	// leaf
		new Node(n2, "b2", "leaf 2");	// leaf
		new Node(n2, "level", "1");	// leaf

		Node n3 = new Node(root, "invisible", null); // interior
		new Node(n3, "c1", "leaf 1");	// leaf
		new Node(n3, "c2", "leaf 2");	// leaf
		new Node(n3, "level", "2");	// leaf

		// mount plugin unter "./RMT/SimpleFilter"
		MultiRootDataPlugin aclPlugin = new MultiRootDataPlugin("P1", root);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, "./Test/SimpleFilter" );
		registerService(DataPlugin.class.getName(), aclPlugin, props);

		// create new filter
		session = dmtAdmin.getSession( ".", DmtSession.LOCK_TYPE_ATOMIC);
		String uri = FILTER_ROOT + "/" + "SimpleFilterTest";
		session.createInteriorNode( uri );
		session.commit();

		try {
			// this target should cover all interior nodes of the registered aclPlugin
			session.setNodeValue(uri + "/" + TARGET, new DmtData( "./Test/SimpleFilter/*/"));
			session.setNodeValue(uri + "/" + FILTER, new DmtData("(level=1)"));
			session.commit();

			// results must not contain the "invisible" node, because of the Filter restriction
			String[] resultUriList = session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			assertEquals("The resultUriList must contain 1 entry for this search.", 1, resultUriList.length);
			String value = session.getNodeValue(uri + "/" + RESULT_URI_LIST + "/0").getString();
			assertEquals( "Only the visible node must be in the resultUri list.", "Test/SimpleFilter/visible", value );

			String visibleUri = uri + "/" + RESULT + "/Test/SimpleFilter/visible";
			String invisibleUri = uri + "/" + RESULT + "/Test/SimpleFilter/invisible";
			assertTrue( "This node must be part of the Result tree: " + visibleUri, session.isNodeUri(visibleUri) );
			assertFalse( "This node must not be part of the Result tree: " + invisibleUri, session.isNodeUri(invisibleUri) );
		}
		finally {
			cleanupSearch(session, uri);
		}
	}

	/**
	 * Checks that the result only returns nodes that match the configured filter.
	 * The search is performed against list nodes of the RDMT Log tree.
	 *
	 * It is expected that only the parts of the plugins subtree are returned that match the filter.
	 * @throws Exception
	 */
	public void testFilterAgainstListNodes() throws Exception {
		// create a number of random logs to ensure that the Log part of the RMT is filled
		createRandomLogs(10);
		// make sure that there is at least one DEBUG log
		getService(LogService.class).log(LogService.LOG_DEBUG, "a debug message");
		Sleep.sleep(DELAY);

		// create new filter
		session = dmtAdmin.getSession( RMT_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		String uri = FILTER_ROOT + "/" + "ListNodeFilter";
		session.createInteriorNode( uri );
		session.commit();

		try {
			// set target to LogEntries and filter to DEBUG logs only
			session.setNodeValue(uri + "/" + TARGET, new DmtData( LOG_ROOT + "/" + LOG_ENTRIES + "/*/"));
			session.setNodeValue(uri + "/" + FILTER, new DmtData("(" + LEVEL + "=" + LogService.LOG_DEBUG + ")"));
			session.commit();

			// get real logs from Log tree
			Set<String> expectedLogs = new HashSet<String>();
			Set<String> expectedIndexes = new HashSet<String>();
			Set<String> wrong = new HashSet<String>();
			String[] realLogs = session.getChildNodeNames(LOG_ROOT + "/" + LOG_ENTRIES);
			for (String index : realLogs) {
				if ( LogService.LOG_DEBUG == session.getNodeValue(LOG_ROOT + "/" + LOG_ENTRIES + "/" + index + "/" + LEVEL).getInt()) {
					// add index with expected prefix
					expectedLogs.add(LOG + "/" + LOG_ENTRIES + "/" + index);
					expectedIndexes.add( "" + index);
				}
			}

			// compare resultUriList with indexes of real debug logs
			String[] resultUriList = session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			for (String index : resultUriList) {
				String value = session.getNodeValue(uri + "/" + RESULT_URI_LIST + "/" + index).getString();
				if ( ! expectedLogs.contains(value))
					wrong.add(value);
				expectedLogs.remove(value);
			}
			assertEquals("The resultUriList contains unexpected entries: " + wrong, 0, wrong.size() );
			assertEquals("Expected entries are missing in the in the resultUri list: " + expectedLogs, 0, expectedLogs.size() );

			String[] results = session.getChildNodeNames(uri + "/" + RESULT + "/" + LOG + "/" + LOG_ENTRIES );
			assertTrue( "The result must have children under: " + uri + "/" + RESULT + "/" + LOG + "/" + LOG_ENTRIES, results.length > 0);
			wrong.clear();
			assertEquals("The result must contain " + expectedIndexes.size() + " entries for this search.", expectedIndexes.size(), results.length);
			for (String result : results) {
				if ( ! expectedIndexes.contains(result))
					wrong.add(result);
				expectedIndexes.remove(result);
			}
			assertEquals("The result tree contains unexpected log entries: " + wrong, 0, wrong.size() );
			assertEquals("Expected entries are missing in the in the result tree: " + expectedIndexes, 0, expectedIndexes.size() );
		}
		finally {
			cleanupSearch(session, uri);
		}
	}


	/**
	 * Checks that the number of entries in resultUriList can be limited
	 *
	 * The test is performed against the LogEntries in the RMT
	 *
	 * @throws Exception
	 */
	public void testLimit() throws Exception {
		int limit = 10;
		// create a number of random logs to ensure that the Log part of the RMT is filled
		createRandomLogs(100);
		Sleep.sleep(DELAY);

		String uri = FILTER_ROOT + "/LimitedLogSearch";
		session = dmtAdmin.getSession(RMT_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		session.createInteriorNode( uri );
		session.commit();

		try {
			String limitUri = uri + '/' + LIMIT;
			assertEquals("The limit default value is not correct.", -1, session.getNodeValue(limitUri).getInt());
			// set target to all LogEntries
			session.setNodeValue(uri + '/' + TARGET, new DmtData(LOG_ROOT + "/LogEntries/*/"));
			session.setNodeValue(limitUri, new DmtData(limit));
			session.commit();

			// check resultUriList
			String[] resultUriList = session.getChildNodeNames(uri + '/' + RESULT_URI_LIST);
			assertEquals( "The resultUriList must have exactly "+ limit +" entries for this limited search.", limit, resultUriList.length );

			// check results
			String[] results = session.getChildNodeNames(uri + '/' + RESULT + '/' + LOG + '/' + LOG_ENTRIES);
			assertTrue("The result must have children under: " + uri + '/' + RESULT + '/' + LOG + '/' + LOG_ENTRIES, results.length > 0);
			assertEquals( "The result must have exactly "+ limit +" matching entries for this limited search.", limit, results.length );
		}
		finally {
			cleanupSearch(session, uri);
		}
	}

	/**
	 * Checks that the number of entries in resultUriList can be limited if the search
	 * is performed with a filter.
	 *
	 * The test is performed against the LogEntries in the RDMT
	 * @throws Exception
	 */
	public void testLimitWithFilter() throws Exception {
		int limit = 10;
		// create a number of random logs to ensure that the Log part of the RMT is filled
		createRandomLogs(100);
		// make sure that there are at least limit+1 DEBUG logs
		LogService logger = getService(LogService.class);
		for (int i = 0; i < limit+1; i++)
			logger.log(LogService.LOG_DEBUG, "debug message " + i);
		Sleep.sleep(DELAY);

		String uri = FILTER_ROOT + "/" + "LimitedLogSearchWitFilter";
		session = dmtAdmin.getSession(RMT_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		session.createInteriorNode( uri );
		session.commit();

		try {
			// set target to all LogEntries
			session.setNodeValue(uri + "/" + TARGET, new DmtData(LOG_ROOT + "/LogEntries/*/"));
			session.setNodeValue(uri + "/" + FILTER, new DmtData("(" + LEVEL + "=" + LogService.LOG_DEBUG + ")"));
			session.setNodeValue(uri + "/" + LIMIT, new DmtData(limit));
			session.commit();

			// check resultUriList
			String[] resultUriList = session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			assertEquals( "The resultUriList must have exactly "+ limit +" entries for this limited search.", limit, resultUriList.length );

			// check results
			String[] results = session.getChildNodeNames(uri + "/" + RESULT + "/" + LOG + "/" + LOG_ENTRIES );
			assertTrue( "The result must have children under: " + uri + "/" + RESULT + "/" + LOG + "/" + LOG_ENTRIES, results.length > 0);
			assertEquals( "The result must have exactly "+ limit +" matching entries for this limited search.", limit, results.length );
		}
		finally {
			cleanupSearch(session, uri);
		}
	}

	// ******** Utilities ************************

	private void assertInvalidTargetRejected( String uri, String target ) throws Exception {
		try {
			session.setNodeValue(uri + "/" + TARGET, new DmtData(target ));
			session.commit(); // TODO: This commit should not be necessary - needs check
			// force a search
			session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			fail( "The target \"" + target + "\" must not be accepted." );
		} catch (DmtException e) {
			pass( "Success: Target \"" + target + "\" was correctly rejected." );
		}
	}

	private void cleanupSearch( DmtSession session, String searchUri ) {
		try {
			session.deleteNode(searchUri);
			if ( session.getLockType() == DmtSession.LOCK_TYPE_ATOMIC )
				session.commit();
		} catch (DmtException e) {
			log("Error while cleaning up search tree: " + searchUri);
		}
	}
}
