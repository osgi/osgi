package org.osgi.test.cases.dmt.tc4.rfc141;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.GenericDataPlugin;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.Node;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class tests ... TODO
 * @author steffen
 *
 */
public class TestBug1746_UriProperties extends DefaultTestBundleControl {

	static final String PROP_MAX_SEGMENT_NAME_LENGTH = "org.osgi.dmtree.max.segment.name.length";
	static final String PROP_MAX_URI_LENGTH = "org.osgi.dmtree.max.uri.length";
	static final String PROP_MAX_URI_SEGMENTS = "org.osgi.dmtree.max.uri.segments";

	static final int MAX_SEGMENT_LENGTH = 33;
	static final int MAX_URI_LENGTH = 129;
	static final int MAX_URI_SEGMENTS = 21;

	DmtAdmin dmtAdmin;
	DmtSession session;
	GenericDataPlugin dataPlugin;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");
		dmtAdmin = getService(DmtAdmin.class);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("tearing down");
		if (session != null && session.getState() == DmtSession.STATE_OPEN)
			session.close();
		unregisterAllServices();
		ungetAllServices();
	}


	/**
	 * tests whether the property values are read correctly from the system
	 * props
	 */
	public void testPropertyValues() throws Exception {
		// properties have been set via bnd-file
		// TODO (S. Druesedow) fix implementation because Uri length limits are removed (see bug 2144)
//		assertEquals(Uri.getMaxSegmentNameLength(), MAX_SEGMENT_LENGTH);
//		assertEquals(Uri.getMaxUriLength(), MAX_URI_LENGTH);
//		assertEquals(Uri.getMaxUriSegments(), MAX_URI_SEGMENTS);
	}

	/**
	 * tests the correct handling of the system property
	 * "org.osgi.dmtree.max.segment.name.length"
	 * 
	 * It performs following checks:
	 * - attempts to open a session with a URI that contains a too long segment
	 * - attempts to invoke isLeafNode on a wrong URI in an already opened session  
	 * 
	 * @throws Exception
	 */
// This test is no longer valid for DMT Admin V2.0.
//	public void testMaxSegmentNameLength() throws Exception {
//		String rootSegment = "A";
//		// define a segment that is longer than MAX_SEGMENT_LENGTH
//		String segment1 = "A123456789012345678901234567890123456";
//		String longUri = "./" + rootSegment + "/" + segment1;
//
//		registerLongSegmentPlugin(rootSegment, segment1);
//
//		log("testing long segment name in DmtAdmin.getSession()");
//		try {
//			session = dmtAdmin.getSession(longUri, DmtSession.LOCK_TYPE_SHARED);
//
//			fail("DmtAdmin must not accept URI's with segment names "
//					+ "longer than length defined in system property: "
//					+ PROP_MAX_SEGMENT_NAME_LENGTH);
//		} catch (DmtException e) {
//			assertEquals(
//					"DmtAdmin does not accept URI's with segment names longer than "
//							+ "length defined in system property: "
//							+ PROP_MAX_SEGMENT_NAME_LENGTH,
//					DmtException.URI_TOO_LONG, e.getCode());
//		}
//
//		log("testing long segment name in opened session");
//		try {
////			registerLongSegmentPlugin(rootSegment, segment1);
//			session = dmtAdmin.getSession("./" + rootSegment,
//					DmtSession.LOCK_TYPE_SHARED);
//			assertEquals(false, session.isNodeUri(longUri));
//
//			session.isLeafNode(longUri);
//
//			fail("DmtAdmin must not accept URI's with segment names "
//					+ "longer than length defined in system property: "
//					+ PROP_MAX_SEGMENT_NAME_LENGTH);
//		} catch (DmtException e) {
//			assertEquals(
//					"DmtAdmin does not accept URI's with segment names longer than "
//							+ "length defined in system property: "
//							+ PROP_MAX_SEGMENT_NAME_LENGTH,
//					DmtException.URI_TOO_LONG, e.getCode());
//		}
//	}

	/**
	 * tests the correct handling of the system property
	 * "org.osgi.dmtree.max.uri.length"
	 * 
	 * @throws Exception
	 */
// This test is no longer valid for DMT Admin V2.0.
//	public void testMaxUriLength() throws Exception {
//		String nodeName = "A123456789012345678901234567890";
//		String root = "./" + nodeName;
//		String longUri = "./" + nodeName + "/" + nodeName + "/" + nodeName
//				+ "/" + nodeName + "/" + nodeName;
//
//		registerLongUriPlugin(nodeName, nodeName);
//
//		log("testing long uri in DmtAdmin.getSession()");
//		try {
//			session = dmtAdmin.getSession(longUri, DmtSession.LOCK_TYPE_SHARED);
//
//			fail("DmtAdmin must not accept URI's which are "
//					+ "longer than length defined in system property: "
//					+ PROP_MAX_URI_LENGTH);
//		} catch (DmtException e) {
//			e.printStackTrace();
//			assertEquals(
//					"DmtAdmin does not accept URI's which are longer than the "
//							+ "length defined in system property: "
//							+ PROP_MAX_URI_LENGTH, DmtException.URI_TOO_LONG,
//					e.getCode());
//		} catch (IllegalArgumentException e) {
//			pass("DmtAdmin does not accept URI's with segment names longer than "
//					+ "length defined in system property: "
//					+ PROP_MAX_SEGMENT_NAME_LENGTH);
//		}
//
//		log("testing long uri in already openend session");
//		try {
//			session = dmtAdmin.getSession(root, DmtSession.LOCK_TYPE_SHARED);
//			assertEquals(false, session.isNodeUri(longUri));
//			session.isLeafNode(longUri);
//
//			fail("DmtAdmin must not accept URI's which are "
//					+ "longer than length defined in system property: "
//					+ PROP_MAX_URI_LENGTH);
//		} catch (DmtException e) {
//			assertEquals(
//					"DmtAdmin does not accept URI's which are longer than the "
//							+ "length defined in system property: "
//							+ PROP_MAX_URI_LENGTH, DmtException.URI_TOO_LONG,
//					e.getCode());
//		} catch (IllegalArgumentException e) {
//			pass("DmtAdmin does not accept URI's with segment names longer than "
//					+ "length defined in system property: "
//					+ PROP_MAX_SEGMENT_NAME_LENGTH);
//		}
//	}

	/**
	 * tests the correct handling of the system property
	 * "org.osgi.dmtree.max.uri.segments"
	 * 
	 * @throws Exception
	 */
// This test is no longer valid for DMT Admin V2.0.
//	public void testMaxUriSegments() throws Exception {
//		String segment = "A";
//
//		log( "trying to register plugin to a Uri with too many segments");
//		String longUri = registerMaxSegmentPlugin(segment, 22, 2 );
//		
//		log("testing uri with too many segments in DmtAdmin.getSession()");
//		try {
//			session = dmtAdmin.getSession(longUri, DmtSession.LOCK_TYPE_SHARED);
//
//			fail("DmtAdmin must not accept URI's with a number of segments "
//					+ "higher than defined in system property: "
//					+ PROP_MAX_URI_SEGMENTS);
//		} catch (DmtException e) {
//			e.printStackTrace();
//			assertEquals(
//					"DmtAdmin does not accept URI's which a number of segments higher than "
//							+ "defined in system property: "
//							+ PROP_MAX_URI_SEGMENTS, DmtException.URI_TOO_LONG,
//					e.getCode());
//		} catch (IllegalArgumentException e) {
//			pass("DmtAdmin does not accept URI's with a number of segments "
//					+ "higher than defined in system property: "
//					+ PROP_MAX_URI_SEGMENTS);
//		}
//
//		
//		log("testing uri with too many segments in already opened session");
//		try {
//			session = dmtAdmin.getSession("./A/A/A", DmtSession.LOCK_TYPE_SHARED);
//			// isNodeUri must return false
//			assertEquals( false, session.isNodeUri(longUri));
//			// isLeafNode must throw an Exception
//			session.isLeafNode(longUri);
//
//			fail("DmtAdmin must not accept URI's with a number of segments "
//					+ "higher than defined in system property: "
//					+ PROP_MAX_URI_SEGMENTS);
//		} catch (DmtException e) {
//			e.printStackTrace();
//			assertEquals(
//					"DmtAdmin does not accept URI's which a number of segments higher than "
//							+ "defined in system property: "
//							+ PROP_MAX_URI_SEGMENTS, DmtException.URI_TOO_LONG,
//					e.getCode());
//		} catch (IllegalArgumentException e) {
//			pass("DmtAdmin does not accept URI's with a number of segments "
//					+ "higher than defined in system property: "
//					+ PROP_MAX_URI_SEGMENTS);
//		}
//	}

	
@SuppressWarnings("unused")
	private void registerLongSegmentPlugin(String rootSegment, String segment1)
			throws Exception {
		String mountRoot = "./" + rootSegment;
		Node n2 = new Node(null, rootSegment, "rootSegment");
		Node n3 = new Node(n2, segment1, "segment1");
		dataPlugin = new GenericDataPlugin("P1", mountRoot, n2);

		Dictionary<String,Object> props = new Hashtable<>();
		props.put(DataPlugin.DATA_ROOT_URIS,
				new String[] {mountRoot});

		registerService(DataPlugin.class.getName(), dataPlugin, props);
	}

	@SuppressWarnings("unused")
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

		Dictionary<String,Object> props = new Hashtable<>();
		props.put(DataPlugin.DATA_ROOT_URIS,
				new String[] { mountRoot });

		registerService(DataPlugin.class.getName(), dataPlugin, props);
	}

	@SuppressWarnings("unused")
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

		Dictionary<String,Object> props = new Hashtable<>();
		props.put(DataPlugin.DATA_ROOT_URIS,
				new String[] { pluginRoot });

		registerService(DataPlugin.class.getName(), dataPlugin, props);
		return wholeUri;
	}

}
