package org.osgi.test.cases.dmt.tc4.rfc141;

import info.dmtree.DmtAdmin;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.Uri;
import info.dmtree.spi.DataPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.test.cases.dmt.tc4.rfc141.plugins.GenericDataPlugin;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.Node;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class TestBug1746_UriProperties extends DefaultTestBundleControl {

	static final int MAX_SEGMENT_LENGTH = 33;
	static final int MAX_URI_LENGTH = 129;
	static final int MAX_URI_SEGMENTS = 21;

	DmtAdmin dmtAdmin;
	DmtSession session;
	GenericDataPlugin dataPlugin;

	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");
		dmtAdmin = (DmtAdmin) getService(DmtAdmin.class);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("tearing down");
		if (session != null && session.getState() == DmtSession.STATE_OPEN)
			session.close();
		unregisterAllServices();
		ungetAllServices();
	}

	/**
	 * tests existence of specified constants for String literals in
	 * info.dmtree.Uri
	 */
	public void testNewUriConstants() throws Exception {
		assertConstant("org.osgi.dmtree.max.segment.name.length", "MAX_SEGMENT_NAME_LENGTH", Uri.class);
		assertConstant("org.osgi.dmtree.max.uri.length", "MAX_URI_LENGTH", Uri.class);
		assertConstant("org.osgi.dmtree.max.uri.segments", "MAX_URI_SEGMENTS", Uri.class);
	}

	/**
	 * tests whether the property values are read correctly from the system
	 * props
	 */
	public void testPropertyValues() throws Exception {
		// properties have been set via bnd-file
		assertEquals(Uri.getMaxSegmentNameLength(), 33);
		assertEquals(Uri.getMaxUriLength(), 129);
		assertEquals(Uri.getMaxUriSegments(), 21);
	}

	/**
	 * tests the correct handling of the system property
	 * "org.osgi.dmtree.max.segment.name.length"
	 * 
	 * @throws Exception
	 */
	public void testMaxSegmentNameLength() throws Exception {
		String rootSegment = "A";
		String segment1 = "A123456789012345678901234567890123456";
		String longUri = "./" + rootSegment + "/" + segment1;

		registerLongSegmentPlugin(rootSegment, segment1);

		log("testing long segment name in DmtAdmin.getSession()");
		try {
			session = dmtAdmin.getSession(longUri, DmtSession.LOCK_TYPE_SHARED);

			fail("DmtAdmin must not accept URI's with segment names "
					+ "longer than length defined in system property: "
					+ Uri.MAX_SEGMENT_NAME_LENGTH);
		} catch (DmtException e) {
			assertEquals(
					"DmtAdmin does not accept URI's with segment names longer than "
							+ "length defined in system property: "
							+ Uri.MAX_SEGMENT_NAME_LENGTH,
					DmtException.URI_TOO_LONG, e.getCode());
		}

		log("testing long segment name in already opened session");
		try {
			registerLongSegmentPlugin(rootSegment, segment1);
			session = dmtAdmin.getSession("./" + rootSegment,
					DmtSession.LOCK_TYPE_SHARED);
			assertEquals(false, session.isNodeUri(longUri));

			session.isLeafNode(longUri);

			fail("DmtAdmin must not accept URI's with segment names "
					+ "longer than length defined in system property: "
					+ Uri.MAX_SEGMENT_NAME_LENGTH);
		} catch (DmtException e) {
			assertEquals(
					"DmtAdmin does not accept URI's with segment names longer than "
							+ "length defined in system property: "
							+ Uri.MAX_SEGMENT_NAME_LENGTH,
					DmtException.URI_TOO_LONG, e.getCode());
		}
	}

	/**
	 * tests the correct handling of the system property
	 * "org.osgi.dmtree.max.uri.length"
	 * 
	 * @throws Exception
	 */
	public void testMaxUriLength() throws Exception {
		String nodeName = "A123456789012345678901234567890";
		String root = "./" + nodeName;
		String longUri = "./" + nodeName + "/" + nodeName + "/" + nodeName
				+ "/" + nodeName + "/" + nodeName;

		registerLongUriPlugin(nodeName, nodeName);

		log("testing long uri in DmtAdmin.getSession()");
		try {
			session = dmtAdmin.getSession(longUri, DmtSession.LOCK_TYPE_SHARED);

			fail("DmtAdmin must not accept URI's which are "
					+ "longer than length defined in system property: "
					+ Uri.MAX_URI_LENGTH);
		} catch (DmtException e) {
			e.printStackTrace();
			assertEquals(
					"DmtAdmin does not accept URI's which are longer than the "
							+ "length defined in system property: "
							+ Uri.MAX_URI_LENGTH, DmtException.URI_TOO_LONG,
					e.getCode());
		} catch (IllegalArgumentException e) {
			pass("DmtAdmin does not accept URI's with segment names longer than "
					+ "length defined in system property: "
					+ Uri.MAX_SEGMENT_NAME_LENGTH);
		}

		log("testing long uri in already openend session");
		try {
			session = dmtAdmin.getSession(root, DmtSession.LOCK_TYPE_SHARED);
			assertEquals(false, session.isNodeUri(longUri));
			session.isLeafNode(longUri);

			fail("DmtAdmin must not accept URI's which are "
					+ "longer than length defined in system property: "
					+ Uri.MAX_URI_LENGTH);
		} catch (DmtException e) {
			assertEquals(
					"DmtAdmin does not accept URI's which are longer than the "
							+ "length defined in system property: "
							+ Uri.MAX_URI_LENGTH, DmtException.URI_TOO_LONG,
					e.getCode());
		} catch (IllegalArgumentException e) {
			pass("DmtAdmin does not accept URI's with segment names longer than "
					+ "length defined in system property: "
					+ Uri.MAX_SEGMENT_NAME_LENGTH);
		}
	}

	/**
	 * tests the correct handling of the system property
	 * "org.osgi.dmtree.max.uri.segments"
	 * 
	 * @throws Exception
	 */
	public void testMaxUriSegments() throws Exception {
		String segment = "A";

		log( "trying to register plugin to a Uri with too many segments");
		String longUri = registerMaxSegmentPlugin(segment, 22, 2 );
		
		log("testing uri with too many segments in DmtAdmin.getSession()");
		try {
			session = dmtAdmin.getSession(longUri, DmtSession.LOCK_TYPE_SHARED);

			fail("DmtAdmin must not accept URI's with a number of segments "
					+ "higher than defined in system property: "
					+ Uri.MAX_URI_SEGMENTS);
		} catch (DmtException e) {
			e.printStackTrace();
			assertEquals(
					"DmtAdmin does not accept URI's which a number of segments higher than "
							+ "defined in system property: "
							+ Uri.MAX_URI_SEGMENTS, DmtException.URI_TOO_LONG,
					e.getCode());
		} catch (IllegalArgumentException e) {
			pass("DmtAdmin does not accept URI's with a number of segments "
					+ "higher than defined in system property: "
					+ Uri.MAX_URI_SEGMENTS);
		}

		
		log("testing uri with too many segments in already opened session");
		try {
			session = dmtAdmin.getSession("./A/A/A", DmtSession.LOCK_TYPE_SHARED);
			// isNodeUri must return false
			assertEquals( false, session.isNodeUri(longUri));
			// isLeafNode must throw an Exception
			session.isLeafNode(longUri);

			fail("DmtAdmin must not accept URI's with a number of segments "
					+ "higher than defined in system property: "
					+ Uri.MAX_URI_SEGMENTS);
		} catch (DmtException e) {
			e.printStackTrace();
			assertEquals(
					"DmtAdmin does not accept URI's which a number of segments higher than "
							+ "defined in system property: "
							+ Uri.MAX_URI_SEGMENTS, DmtException.URI_TOO_LONG,
					e.getCode());
		} catch (IllegalArgumentException e) {
			pass("DmtAdmin does not accept URI's with a number of segments "
					+ "higher than defined in system property: "
					+ Uri.MAX_URI_SEGMENTS);
		}
	}

	
	private void registerLongSegmentPlugin(String rootSegment, String segment1)
			throws Exception {
		String mountRoot = "./" + rootSegment;
		Node n2 = new Node(null, rootSegment, "rootSegment");
		Node n3 = new Node(n2, segment1, "segment1");
		dataPlugin = new GenericDataPlugin("P1", mountRoot, n2);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS,
				new String[] {mountRoot});

		registerService(DataPlugin.class.getName(), dataPlugin, props);
	}

	private void registerLongUriPlugin(String rootSegment, String segment)
			throws Exception {
		String mountRoot = "./" + rootSegment;
		Node n2 = new Node(null, rootSegment, "rootSegment");
		Node n3 = new Node(n2, segment, "segment1");
		Node n4 = new Node(n3, segment, "segment2");
		Node n5 = new Node(n4, segment, "segment3");
		Node n6 = new Node(n5, segment, "segment4");
		Node n7 = new Node(n6, segment, "segment5");
		dataPlugin = new GenericDataPlugin("P1", mountRoot, n2);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS,
				new String[] { mountRoot });

		registerService(DataPlugin.class.getName(), dataPlugin, props);
	}

	private String registerMaxSegmentPlugin(String segmentName, int maxSegments, int rootLevel)
			throws Exception {
		String mountRoot = "./" + segmentName;
		
		Node first = new Node(null, segmentName, "node0" );
		Node parent = first;
		String pluginRoot = ".";
		String wholeUri = pluginRoot;
		for (int i = 0; i < maxSegments; i++) {
			Node n = new Node( parent, segmentName, "node" + (i+1));
			parent = n;
			wholeUri+= "/" + segmentName;
			if ( i < rootLevel )
				pluginRoot+= "/" + segmentName;
		}
		dataPlugin = new GenericDataPlugin("P1", mountRoot, first);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS,
				new String[] { pluginRoot });

		registerService(DataPlugin.class.getName(), dataPlugin, props);
		return wholeUri;
	}

}
