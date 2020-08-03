package org.osgi.test.cases.dmt.tc4.rfc141;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.ArrayAssert;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.GenericDataPlugin;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.Node;

public class TestBug1735_ScaffoldMetaData extends ScaffoldNodeHelper {
	
	
	/**
	 * 
	 * Checks metadata of a Scaffold node as described in RFC141
	 * (RFC141, section 6.3.1)
	 */
	public void testScaffoldMetaData() throws Exception {

		preparePlugin();
		
		String uri = "./A";
		session = dmtAdmin.getSession( uri, DmtSession.LOCK_TYPE_SHARED);
		MetaNode metaNode = session.getMetaNode(uri);
		
		assertNotNull("DmtAdmin must provide MetaData for a scaffold node!", metaNode );
		
		checkMetaNode(metaNode);

		assertEquals( "The type of a scaffold node must be: " + DmtConstants.DDF_SCAFFOLD, DmtConstants.DDF_SCAFFOLD, session.getNodeType(uri));
	}

	/**
	 * Checks for correct node type of scaffold nodes.
	 * (RFC141, section 6.3.2)
	 * 
	 * @throws Exception
	 */
	public void testScaffoldNodeType() throws Exception {
		preparePlugin();

		log( "testing node type of scaffold nodes ...");
		String uri = "./A";
		session = dmtAdmin.getSession( uri, DmtSession.LOCK_TYPE_SHARED);
		assertEquals( "Scaffold nodes must have node type: '" + DmtConstants.DDF_SCAFFOLD + "'!", session.getNodeType(uri), DmtConstants.DDF_SCAFFOLD );
	}

	
	//	- can():  CMD_GET
	//	- isLeaf(): false
	//	- getScope(): permanent (can't be changed via sessions)
	//	- getDescription(): null
	//	- getMaxOccurrence(): 1
	//	- isZeroOccurrenceAllowed(): true
	//	- getDefault(), getValidNames(), getValidValues(): null
	//	- getMax(): Double.MAX_VALUE
	//	- getMin(): Double.MIN_VALUE
	//	- getRawFormatNames(): null
	//	- getMimeTypes(): null
	//	- isValidName(): true
	//	- isValidValue(): false
	private void checkMetaNode( MetaNode metaNode ) {
		assertEquals( "Scaffold nodes must be interior nodes!", false, metaNode.isLeaf() );
		
		assertEquals( "Scaffold nodes must support the GET operation!", true, metaNode.can( MetaNode.CMD_GET ) );
		assertEquals( "Scaffold nodes must not support the ADD operation!", false, metaNode.can( MetaNode.CMD_ADD ) );
		assertEquals( "Scaffold nodes must not support the DELETE operation!", false, metaNode.can( MetaNode.CMD_DELETE ) );
		assertEquals( "Scaffold nodes must not support the EXECUTE operation!", false, metaNode.can( MetaNode.CMD_EXECUTE ) );
		assertEquals( "Scaffold nodes must not support the REPLACE operation!", false, metaNode.can( MetaNode.CMD_REPLACE ) );

		assertEquals( "Scaffold nodes must have scope PERMANENT!", MetaNode.PERMANENT, metaNode.getScope() );

		assertNull("The MetaData of scaffold nodes must not provide a description!", metaNode.getDescription() );
		
		assertEquals( "The MetaData of scaffold nodes must have max occurence of 1!", 1, metaNode.getMaxOccurrence() );

		assertEquals( "The MetaData of scaffold nodes must allow zero occurrence!", true, metaNode.isZeroOccurrenceAllowed() );

		assertNull("The MetaData of scaffold nodes must not provide a default!", metaNode.getDefault() );
		assertNull("The MetaData of scaffold nodes must not provide values for 'getValidNames()'!", metaNode.getValidNames() );
		assertNull("The MetaData of scaffold nodes must not provide values for 'getValidValues()'!", metaNode.getValidValues() );

		assertEquals( "The MetaData of scaffold nodes must allow a max value of " + Double.MAX_VALUE, Double.MAX_VALUE, metaNode.getMax(), 0 );
		assertEquals( "The MetaData of scaffold nodes must allow a min value of " + Double.MIN_VALUE, Double.MIN_VALUE, metaNode.getMin(), 0 );

		assertNull("The MetaData of scaffold nodes must not provide raw format names!", metaNode.getRawFormatNames() );
		ArrayAssert.assertEquivalenceArrays("The MetaData of scaffold nodes must not provide mime types!", 
				metaNode.getMimeTypes(), new String[]{ DmtConstants.DDF_SCAFFOLD });

		assertEquals( "The method 'isValidName()' of a scaffolds MetaData must return 'true' for all params!", true, metaNode.isValidName("any name") );
		assertEquals( "The method 'isValidValue()' of a scaffolds MetaData must return 'false' for all params!", false, metaNode.isValidValue( new DmtData("value")) );

		assertEquals( "Scaffold nodes must have the format DmtData.FORMAT_NODE: " + DmtData.FORMAT_NODE, DmtData.FORMAT_NODE, metaNode.getFormat() );
		
	}
	
	private void preparePlugin() throws Exception {
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
}
