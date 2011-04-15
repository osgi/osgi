package org.osgi.test.cases.dmt.tc4.rfc141.atomic;

import info.dmtree.Acl;
import info.dmtree.DmtAdmin;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.DmtConstants;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.GenericDataPlugin;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.MetaNode;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.Node;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class TestAtomicSessionEvents extends
		DefaultTestBundleControl implements EventHandler {

	static int numEvents;
	static long sleepDelay = 200;
	
	DmtAdmin dmtAdmin;
	DmtSession session;
	Vector events;
	boolean logEvents;

	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");
		numEvents = 0;
		events = new Vector();
		dmtAdmin = (DmtAdmin) getService(DmtAdmin.class);
		Hashtable props = new Hashtable();
		props.put( EventConstants.EVENT_TOPIC, new String[] { 
				"info/dmtree/DmtEvent/ADDED", 
				"info/dmtree/DmtEvent/DELETED", 
				"info/dmtree/DmtEvent/REPLACED",
				"info/dmtree/DmtEvent/RENAMED",
				"info/dmtree/DmtEvent/COPIED"
				});
		registerService(EventHandler.class.getName(), this, props );
		logEvents = true;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("tearing down");
		closeSession();
		unregisterAllServices();
		ungetAllServices();
		this.events = null;
	}

	/**
	 * - initially ./A sub-tree is empty
	 * - add ./A/B - leaf node with value "a.b"
	 * - replace node ./A/B node value with "a.bbb"
	 * - expected events:
	 * info/dmtree/dmt/DmtEvent/ADDED {
  	 * nodes = [./A/B],
  	 * session.id = 5
	 * }
	 * The node is added with the value "a.bbb".
	 * 
	 * @throws Exception
	 */
	public void testAtomicEvents1() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.createLeafNode( "./A/B", new DmtData("a.b") );
		session.setNodeValue( "./A/B", new DmtData("a.bbb"));
		session.commit();
		Thread.sleep(300);
		
		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_ADDED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(1, nodes.length );
		assertEquals("./A/B", nodes[0]);
	}

	/**
	 * - initially ./A sub-tree contains ./A/B with value "a.b"
	 * - replace node ./A/B node value with "a.bbb"
	 * - replace node ./A/B node value with "a.bbbbbb"
	 * - expected events:
	 * info/dmtree/dmt/DmtEvent/REPLACED {
  	 * nodes = [./A/B],
  	 * session.id = 5
	 * }
	 * The node value is replaced with "a.bbbbbb".
	 * 
	 * @throws Exception
	 */
	public void testAtomicEvents2() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "B", "a.b");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.setNodeValue( "./A/B", new DmtData("a.bbb"));
		session.setNodeValue( "./A/B", new DmtData("a.bbbbbb"));
		session.commit();
		Thread.sleep(300);

		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_REPLACED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(1, nodes.length );
		assertEquals("./A/B", nodes[0]);
	}

	/**
	 * - initially ./A sub-tree contains ./A/B with value "a.b"
	 * - delete ./A/B
	 * - add ./A/B with value "a.b"
	 * No event, the value is same.
	 * 
	 * @throws Exception
	 */
	public void testAtomicEvents3() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "B", "a.b");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.deleteNode( "./A/B" );
		session.createLeafNode( "./A/B", new DmtData("a.b") );
		session.commit();
		assertEquals(events.size(), 0 );
	}

	/**
	 * -- initially ./A sub-tree contains ./A/B with value "a.b"
	 * - delete ./A/B
	 * - add ./A/B with value "a.bb"
	 * info/dmtree/dmt/DmtEvent/REPLACED {
  	 * nodes = [./A/B],
  	 * session.id = 5
	 * }
	 * The node value is replaced with "a.bb".
	 * 
	 * @throws Exception
	 */
	public void testAtomicEvents4() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "B", "a.b");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.deleteNode( "./A/B" );
		session.createLeafNode( "./A/B", new DmtData("a.bb") );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_REPLACED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(1, nodes.length );
		assertEquals("./A/B", nodes[0]);
	}

	/**
	 * - initially ./A sub-tree contains the internal ./A/D and leaf ./A/D/E with
	 * value "a.d.e"
	 * - delete on ./A/D
	 * - add ./A/D
	 * info/dmtree/dmt/DmtEvent/DELETED {
  	 * nodes = [./A/D/E],
  	 * session.id = 5
	 * }
	 * Only the child is removed.
	 * 
	 * @throws Exception
	 */
	public void testAtomicEvents5() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.deleteNode( "./A/D" );
		session.createInteriorNode( "./A/D" );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_DELETED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(1, nodes.length );
		assertEquals("./A/D/E", nodes[0]);
	}

	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 *   -F
	 *    -G -> a.d.f.g
	 * THEN: 
	 * - delete on ./A/D
	 * - add ./A/D
	 * expected events:
	 * info/dmtree/dmt/DmtEvent/DELETED {
  	 * nodes = [./A/D/E, ./A/D/F],
  	 * session.id = 5
	 * }
	 * Only the child is removed.
	 * 
	 * @throws Exception
	 */
	public void testAtomicEvents6() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		Node n4 = new Node(n2, "F", null );	// interior child
		Node n5 = new Node(n4, "G", "a.d.f.g");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.deleteNode( "./A/D" );
		session.createInteriorNode( "./A/D" );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_DELETED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(2, nodes.length );
		assertEquals("./A/D/E", nodes[0]);
		assertEquals("./A/D/F", nodes[1]);
	}

	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 * 
	 * THEN:
	 * 	setNodeTitle on ./A/D/E 
	 * 
	 * expected events:
	 * info/dmtree/dmt/DmtEvent/REPLACED {
  	 * nodes = [./A/D/E],
  	 * session.id = 5
	 * }
	 * Only the child is removed.
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnTitleChange() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		Node n4 = new Node(n2, "F", null );	// interior child
		Node n5 = new Node(n4, "G", "a.d.f.g");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.setNodeTitle("./A/D/E", "new title" );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_REPLACED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(1, nodes.length );
		assertEquals("./A/D/E", nodes[0]);
	}

	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 * 
	 * THEN:
	 * 	setNodeType on ./A/D/E 
	 * 
	 * expected events:
	 * info/dmtree/dmt/DmtEvent/REPLACED {
  	 * nodes = [./A/D/E],
  	 * session.id = 5
	 * }
	 * Only the child is removed.
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnTypeChange() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		Node n4 = new Node(n2, "F", null );	// interior child
		Node n5 = new Node(n4, "G", "a.d.f.g");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.setNodeType("./A/D/E", "new type" );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_REPLACED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(1, nodes.length );
		assertEquals("./A/D/E", nodes[0]);
	}

	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 * 
	 * THEN:
	 * 	setNodeAcl on ./A/D/E 
	 * 
	 * expected events:
	 * info/dmtree/dmt/DmtEvent/REPLACED {
  	 * nodes = [./A/D/E],
  	 * session.id = 5
	 * }
	 * Only the child is removed.
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnAclChange() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		Node n4 = new Node(n2, "F", null );	// interior child
		Node n5 = new Node(n4, "G", "a.d.f.g");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.setNodeAcl("./A/D/E", new Acl("Add=*&Get=*&Replace=*") );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_REPLACED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(1, nodes.length );
		assertEquals("./A/D/E", nodes[0]);
	}

	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 * 
	 * THEN:
	 * 	renameNode on ./A/D/E to new name F 
	 * 	renameNode on ./A/D/F to new name G 
	 * 	renameNode on ./A/D/G to new name H 
	 * 	renameNode on ./A/D/H to new name I 
	 * 
	 * expected events:
	 * info/dmtree/dmt/DmtEvent/RENAMED {
  	 * nodes = [./A/D/E],
  	 * newnodes = [./A/D/G],
  	 * session.id = 5
	 * }
	 * Only the child is removed.
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnRenameOfLeafNode1() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.renameNode( "./A/D/E", "F" );
		session.renameNode( "./A/D/F", "G" );
		session.renameNode( "./A/D/G", "H" );
		session.renameNode( "./A/D/H", "I" );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_RENAMED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(1, nodes.length );
		assertEquals("./A/D/E", nodes[0]);
		String[] newnodes = (String[]) event.getProperty("newnodes");
		assertEquals(1, newnodes.length );
		assertEquals("./A/D/I", newnodes[0]);
	}

	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 * 
	 * THEN:
	 * 	renameNode on ./A/D/E to new name F 
	 * 	renameNode on ./A/D/F to new name G 
	 * 	renameNode on ./A/D/G to new name H 
	 * 	renameNode on ./A/D/H to new name I 
	 * 	renameNode on ./A/D/I back to original name E 
	 * 
	 * expected events: none
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnRenameOfLeafNode2() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.renameNode( "./A/D/E", "F" );
		session.renameNode( "./A/D/F", "G" );
		session.renameNode( "./A/D/G", "H" );
		session.renameNode( "./A/D/H", "I" );
		session.renameNode( "./A/D/I", "E" );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(events.size(), 0 );
	}

	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 * 
	 * THEN:
	 * 	renameNode on ./A/D to new name X 
	 * 	renameNode on ./A/X to new name Z 
	 * 	renameNode on ./A/Z to new name Y 
	 * 
	 * expected events:
	 * info/dmtree/dmt/DmtEvent/RENAMED {
  	 * nodes = [./A/D],
  	 * newnodes = [./A/Y],
  	 * session.id = 5
	 * }
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnRenameOfInteriorNode1() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.renameNode( "./A/D", "X" );
		session.renameNode( "./A/X", "Z" );
		session.renameNode( "./A/Z", "Y" );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_RENAMED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(1, nodes.length );
		assertEquals("./A/D", nodes[0]);
		String[] newnodes = (String[]) event.getProperty("newnodes");
		assertEquals(1, newnodes.length );
		assertEquals("./A/Y", newnodes[0]);
	}

	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 * 
	 * THEN:
	 * 	renameNode on ./A/D to new name X 
	 * 	renameNode on ./A/X to new name Z 
	 * 	renameNode on ./A/Z to new name Y 
	 * 	renameNode on ./A/Y back to original name D
	 * 
	 * expected events: none
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnRenameOfInteriorNode2() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.renameNode( "./A/D", "X" );
		session.renameNode( "./A/X", "Z" );
		session.renameNode( "./A/Z", "Y" );
		session.renameNode( "./A/Y", "D" );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(this.events.size(), 0);
	}

	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 *   -F -> a.d.f
	 * 
	 * THEN:
	 * 	setNodeValue on ./A/D --> a.d 
	 * 
	 * expected events:
	 * info/dmtree/dmt/DmtEvent/REPLACED {
  	 * nodes = [./A/D/E,./A/D/F],
  	 * session.id = 5
	 * }
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnSetValueInteriorNode() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		Node n4 = new Node(n2, "F", "a.d.f");
		Node n5 = new Node(n2, "G", null);
		Node n6 = new Node(n5, "H", ".a.d.g.h" );
//		int[] operations = new int[] {MetaNode.CMD_REPLACE, MetaNode.CMD_GET};
//		MetaNode metaNode = new MetaNode(false, info.dmtree.MetaNode.DYNAMIC, DmtData.FORMAT_NODE, operations );
//		n2.setMetaNode(metaNode);
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		DmtData newData = new DmtData((Object)"a.d");
		System.out.println( "format: " + newData.getFormatName() );
		session.setNodeValue( "./A/D", newData );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_REPLACED, event.getTopic());
		assertNull(event.getProperty("newnodes"));
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(2, nodes.length );
		assertEquals("./A/D/E", nodes[0]);
		assertEquals("./A/D/F", nodes[1]);
	}

	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 * 
	 * THEN:
	 * 	renameNode on ./A/D/E to new name F 
	 * 	renameNode on ./A/D/F to new name G 
	 * 	deleteNode ./A/D/G 
	 * 
	 * expected events:
	 * info/dmtree/dmt/DmtEvent/DELETED {
  	 * nodes = [./A/D/E],
  	 * session.id = 5
	 * }
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnRenameAndDelete() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.renameNode( "./A/D/E", "F" );
		session.renameNode( "./A/D/F", "G" );
		session.deleteNode( "./A/D/G" );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_DELETED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(1, nodes.length );
		assertEquals("./A/D/E", nodes[0]);
	}

	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 * 
	 * THEN:
	 * 	renameNode on ./A/D/E to new name F 
	 * 	renameNode on ./A/D/F to new name G 
	 * 	deleteNode ./A/D/G 
	 * 	addNode ./A/D/E with same value as original node (a.d.e)
	 * 
	 * expected events: none
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnRenameDeleteAdd() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.renameNode( "./A/D/E", "F" );
		session.renameNode( "./A/D/F", "G" );
		session.deleteNode( "./A/D/G" );
		session.createLeafNode("./A/D/E", new DmtData( "a.d.e") );
		session.commit();
		Thread.sleep(sleepDelay);
		
		assertEquals(events.size(), 0 );
	}


	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 * 
	 * THEN:
	 * 	renameNode on ./A/D/E to new name F 
	 * 	renameNode on ./A/D/F to new name G 
	 * 	deleteNode ./A/D/G 
	 * 	addNode ./A/D/E with value that differs from original node (a.d.e --> x.y.z)
	 * 	renameNode on ./A/D/E to new name X 
	 * 	deleteNode ./A/D/X 
	 * 	addNode ./A/D/E with same value as original node (a.d.e)
	 *  setNodeValue ./A/D/E --> x.y.z
	 * 
	 * expected events: none
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnRenameDeleteAddReplace() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		// disable event-logging for this setup 
		logEvents = false;
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.setNodeTitle("./A/D/E", "a.d.e");
		session.commit();
		Thread.sleep(sleepDelay);

		assertEquals(events.size(), 0 );
		logEvents = true;
		
		session.renameNode( "./A/D/E", "F" );
		session.renameNode( "./A/D/F", "G" );
		session.deleteNode( "./A/D/G" );
		session.createLeafNode("./A/D/E", new DmtData( "x.y.z") );
		session.renameNode( "./A/D/E", "X" );
		session.deleteNode( "./A/D/X" );
		session.createLeafNode("./A/D/E", new DmtData( "a.d.x") );
		session.setNodeValue("./A/D/E", new DmtData("a.d.e"));
		session.setNodeTitle("./A/D/E", "a.d.e");
		session.commit();
		Thread.sleep(sleepDelay);

		assertEquals(events.size(), 0 );
	}
	
	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 *   -F -> a.d.f
	 * 
	 * THEN:
	 *  copyNode ./A/D --> ./A/DNew
	 * 
	 * expected events:
	 * info/dmtree/DmtEvent/COPIED {
	 * newnodes = [./A/DNew]
	 * nodes = [./A/D]
	 * session.id = 5
	 * }
	 * @throws Exception
	 */
	public void testAtomicEventsOnCopy() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		Node n4 = new Node(n2, "F", "a.d.f");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.copy("./A/D", "./A/DNew", true);
		session.commit();
		Thread.sleep(sleepDelay);

		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_COPIED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(1, nodes.length );
		assertEquals("./A/D", nodes[0]);
		String[] newnodes = (String[]) event.getProperty("newnodes");
		assertEquals(1, newnodes.length );
		assertEquals("./A/DNew", newnodes[0]);
	}

	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 *   -F -> a.d.f
	 * 
	 * THEN:
	 *  copyNode ./A/D --> ./A/DNew
	 *  deleteNode ./A/DNew
	 * 
	 * expected events: none
	 * because no effective change against initial situation
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnCopyDelete() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		Node n4 = new Node(n2, "F", "a.d.f");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.copy("./A/D", "./A/DNew", true);
		session.deleteNode("./A/DNew");
		session.commit();
		Thread.sleep(sleepDelay);

		assertEquals(events.size(), 0 );
	}
	
	/**
	 * initially: 
	 * -A
	 *  -D
	 *   -E -> a.d.e
	 *   -F -> a.d.f
	 * 
	 * THEN:
	 *  copyNode ./A/D --> ./A/DNew
	 *  deleteNode ./A/DNew
	 *  addNode  ./A/DNew with value a.dnew.value
	 * 
	 * expected events: none
	 * info/dmtree/DmtEvent/ADDED {
	 * nodes = [./A/D]
	 * session.id = 5
	 * }
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnCopyDeleteAdd() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		Node n2 = new Node(n, "D", null);
		Node n3 = new Node(n2, "E", "a.d.e");
		Node n4 = new Node(n2, "F", "a.d.f");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.copy("./A/D", "./A/DNew", true);
		session.deleteNode("./A/DNew");
		session.createLeafNode("./A/DNew", new DmtData( "a.dnew.value"));
		session.setNodeValue("./A/DNew", new DmtData( ".axy"));
		session.commit();
		Thread.sleep(sleepDelay);

		assertEquals(this.events.size(), 1);
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_ADDED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(1, nodes.length );
		assertEquals("./A/DNew", nodes[0]);
		assertNull( event.getProperty("newnodes"));
	}

	/**
	 * initially: 
	 * -A
	 * 
	 * THEN:
	 *  createInteriorNode ./A/D
	 *  createLeafNode ./A/D/E --> a.d.e
	 *  createLeafNode ./A/D/F --> a.d.f
	 *  copyNode ./A/D --> ./A/DNew
	 * 
	 * expected events: none
	 * info/dmtree/DmtEvent/ADDED {
	 * nodes = [./A/D,./A/D/E,./A/D/F,./A/DNew,./A/DNew/E,./A/DNew/F]
	 * session.id = 5
	 * }
	 * 
	 * @throws Exception
	 */
	public void testAtomicEventsOnAddCopy() throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", null);
		GenericDataPlugin mountedPlugin = new GenericDataPlugin("P1", mountRoot, n);

		Dictionary props = new Hashtable();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
		registerService(DataPlugin.class.getName(), mountedPlugin, props);
		
		session = dmtAdmin.getSession(mountRoot, DmtSession.LOCK_TYPE_ATOMIC);
		session.createInteriorNode("./A/D", null);
		session.createLeafNode("./A/D/E", new DmtData( "a.d.e"));
		session.createLeafNode("./A/D/F", new DmtData( "a.d.f"));
		session.copy("./A/D", "./A/DNew", true);
		session.commit();
		Thread.sleep(sleepDelay);

		assertEquals(this.events.size(), 1);
		
		Event event = (Event) this.events.get(0);
		assertEquals(DmtConstants.EVENT_TOPIC_ADDED, event.getTopic());
		String[] nodes = (String[]) event.getProperty("nodes");
		assertEquals(6, nodes.length );
		assertEquals("./A/D", nodes[0]);
		assertEquals("./A/D/E", nodes[1]);
		assertEquals("./A/D/F", nodes[2]);
		assertEquals("./A/DNew", nodes[3]);
		assertEquals("./A/DNew/E", nodes[4]);
		assertEquals("./A/DNew/F", nodes[5]);
		assertNull(event.getProperty("newnodes"));
		
	}


	
	private void closeSession() throws DmtException {
		if ( session != null && DmtSession.STATE_OPEN == session.getState())
			session.close();
	}

	public void handleEvent(Event event) {
		if ( !logEvents )
			return;
		System.out.println( "event: " + numEvents);
		System.out.println(event2String( event ) );
		numEvents++;
		this.events.add(event);
	}

	private String event2String( Event event ) {
		if ( event == null ) return "";
		String lf = System.getProperty( "line.separator" );
		String result = "*** Incoming event: topic = " + event.getTopic() + lf;
		String[] props = event.getPropertyNames();
		for (int i = 0; i < props.length; i++) {
			Object value = event.getProperty(props[i]);
			if ( value instanceof String[] )
				value = Arrays.asList((String[])value);
			result+= props[i] + " = " + value + lf;
		}
		return result;
	} 
	
	private void resetEvents() {
		this.events.clear();
		this.events = new Vector();
	}

}
