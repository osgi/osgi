package org.osgi.test.cases.dmt.tc4.rfc141;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.MountPlugin;
import org.osgi.service.dmt.spi.MountPoint;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.GenericDataPlugin;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.Node;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

public class TestBug1732_MountPluginEventing extends
		DefaultTestBundleControl {

	DmtAdmin dmtAdmin;
	DmtSession session;
	ServiceRegistration pluginRegistration;


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
		if ( pluginRegistration != null )
			pluginRegistration.unregister();
		unregisterAllServices();
		ungetAllServices();
	}

	public void testAddedEvent() throws Exception {
		checkPostEvent(DmtConstants.EVENT_TOPIC_ADDED, false);
	}

	public void testDeletedEvent() throws Exception {
		checkPostEvent(DmtConstants.EVENT_TOPIC_DELETED, false);
	}

	public void testReplacedEvent() throws Exception {
		checkPostEvent(DmtConstants.EVENT_TOPIC_REPLACED, false);
	}

	public void testCopiedEvent() throws Exception {
		checkPostEvent(DmtConstants.EVENT_TOPIC_COPIED, true);
	}

	public void testRenamedEvent() throws Exception {
		checkPostEvent(DmtConstants.EVENT_TOPIC_RENAMED, true);
	}

	public void checkPostEvent( String topic, boolean withNewNodes ) throws Exception {

		String[] nodes = new String[] { "X", "Y"};
		String[] newNodes = new String[] { "X1", "Y1"};

		GenericDataPlugin plugin = preparePlugin( "./A" );
		Hashtable props = new Hashtable();
		if ( withNewNodes )
			props.put( EventConstants.EVENT_TOPIC, new String[] {
				DmtConstants.EVENT_TOPIC_COPIED,
				DmtConstants.EVENT_TOPIC_RENAMED
			});
		else
			props.put( EventConstants.EVENT_TOPIC, new String[] {
					DmtConstants.EVENT_TOPIC_ADDED,
					DmtConstants.EVENT_TOPIC_DELETED,
					DmtConstants.EVENT_TOPIC_REPLACED
				});

		registerService(EventHandler.class.getName(), plugin, props);

		assertNull(plugin.lastReceivedEvent);

		MountPoint mountPoint = (MountPoint) plugin.lastAddedMountPoints.get(0);
		Hashtable additionalProperties = new Hashtable();
		additionalProperties.put( "testKey1", "testValue1" );
		additionalProperties.put( "testKey2", "testValue2" );

		// try to overwrite default properties
		additionalProperties.put( DmtConstants.EVENT_PROPERTY_NODES, "faked nodes" );
		additionalProperties.put( DmtConstants.EVENT_PROPERTY_NEW_NODES, "faked newnodes" );
		additionalProperties.put( DmtConstants.EVENT_PROPERTY_SESSION_ID, "faked session.id" );

		if ( withNewNodes )
			mountPoint.postEvent(topic, nodes, newNodes, additionalProperties );
		else
			mountPoint.postEvent(topic, nodes, additionalProperties );

		Sleep.sleep(200);

		Event event = plugin.lastReceivedEvent;
		System.out.println("############# plugin: " + plugin);
		assertNotNull(event);
		assertEquals(topic, event.getTopic() );

		// check that given props are there (if they have non-conflicting names)
		assertEquals( "testValue1", event.getProperty("testKey1"));
		assertEquals( "testValue2", event.getProperty("testKey2"));

		// check that protected properties where not overwritten
		String[] eventNodes = (String[]) event.getProperty(DmtConstants.EVENT_PROPERTY_NODES);
		assertEquals(Arrays.asList(new String[] {"./A/X", "./A/Y"}), Arrays.asList( eventNodes ));

		if ( withNewNodes ) {
			String[] eventNewNodes = (String[]) event.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES);
			assertEquals(Arrays.asList(new String[] {"./A/X1", "./A/Y1"}), Arrays.asList( eventNewNodes ));
		}
		else
			assertNull(event.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));

		// sessionID must be -1 for internal events
		assertEquals("the session.id property must be -1 for internal events", Integer.valueOf(-1), event.getProperty(DmtConstants.EVENT_PROPERTY_SESSION_ID));
	}


	/**
	 * @throws Exception
	 */
	private GenericDataPlugin preparePlugin( String mountRoot )
			throws Exception {
		Node n = new Node(null, "A", "node A");
		GenericDataPlugin mountingPlugin = new GenericDataPlugin(
				"MountingPlugin", mountRoot, n);

		Dictionary props = new Hashtable();
		String[] dataRootURIs = new String[] {mountRoot};

		props.put(DataPlugin.DATA_ROOT_URIS, dataRootURIs);

		pluginRegistration = getContext().registerService(new String[] {DataPlugin.class.getName(), MountPlugin.class.getName() }, mountingPlugin, props);
		return mountingPlugin;
	}
}
