package org.osgi.test.cases.dmt.tc4.rfc141;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.GenericDataPlugin;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.Node;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public abstract class ScaffoldNodeHelper extends DefaultTestBundleControl{

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
		System.out.println( "tearing down");
		if ( session != null && session.getState() == DmtSession.STATE_OPEN )
			session.close();
		unregisterAllServices();
		ungetAllServices();
	}
	

	void prepareScaffoldPlugin() throws Exception {
		String mountRoot = "./A/B";

		Node n3 = new Node(null, "B", "node B");
		@SuppressWarnings("unused")
		Node n4 = new Node(n3, "C", "node C");
		dataPlugin = new GenericDataPlugin("P1", mountRoot, n3);
		
		Dictionary<String,Object> props = new Hashtable<>();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] {mountRoot});
		
		registerService(DataPlugin.class.getName(), dataPlugin, props );

		// at this point "./A" must be a scaffold node that it maintained by the DmtAdmin
	}
	
	void assertStringArrayEquals( String[] expected, String[] actual ) throws Exception {
		assertNotNull(expected);
		assertNotNull(actual);
		assertEquals(expected.length, actual.length );
		for (int i = 0; i < actual.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}

}
