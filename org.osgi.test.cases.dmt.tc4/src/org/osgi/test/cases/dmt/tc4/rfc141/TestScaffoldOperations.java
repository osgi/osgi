package org.osgi.test.cases.dmt.tc4.rfc141;

import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;

/**
 * Scaffold nodes must support all operations of info.dmtree.spi.ReadableDataSession.
 * 
 * @author steffen
 *
 */
public class TestScaffoldOperations extends ScaffoldNodeHelper {
	
	/**
	 * 
	 * Checks metadata of a Scaffold node as described in RFC141
	 * (RFC141, section 6.3.1)
	 */
	public void testScaffoldNodeOperations() throws Exception {

		prepareScaffoldPlugin();
		
		String uri = "./A";
		session = dmtAdmin.getSession( uri, DmtSession.LOCK_TYPE_SHARED);
		
		// should provide correct array of node names
		getChildNodeNamesOperation(session, uri, new String[]{"B"} );
		
		// should provide non-null MetaNode
		getMetaNodeOperation(session, uri);
		
		// should throw DmtException of type COMMAND_NOT_ALLOWED
		getNodeSizeOperation(session, uri);
		
		getNodeTimeStampOperation(session, uri);
	}
	
	/**
	 * checks that the getChildNodeNames operation is supported and provides valid data
	 * @param session
	 * @param uri
	 * @param expected
	 * @throws Exception
	 */
	private void getChildNodeNamesOperation( DmtSession session, String uri, String[] expected ) throws Exception {
		String[] childNodeNames = session.getChildNodeNames(uri);
		assertStringArrayEquals(expected, childNodeNames);
	}
	
	/**
	 * checks that a Metanode can be obtained from the session.
	 * Does not check the content of the MetaData, because this has it's own testcase.
	 * @param session
	 * @param uri
	 * @throws Exception
	 */
	private void getMetaNodeOperation( DmtSession session, String uri ) throws Exception {
		MetaNode metaNode = session.getMetaNode(uri);
		assertNotNull("The DmtAdmin must provide a MetaNode for scaffold nodes!", metaNode);
	}

	/**
	 * checks that the DmtAdmin throws a DmtExeption of type COMMAND_NOT_ALLOWED, if getNodeSize is invoked on a scaffold node
	 * @param session
	 * @param uri
	 * @throws Exception
	 */
	private void getNodeSizeOperation( DmtSession session, String uri ) throws Exception {
		try {
			session.getNodeSize(uri);
			fail( "The DmtAdmin must throw a DmtException of type COMMAND_NOT_ALLOWED for getNodeSize() on Scaffold nodes." );
		} catch (DmtException e) {
			pass( "The DmtAdmin correctly throws a DmtException of type COMMAND_NOT_ALLOWED for getNodeSize() on Scaffold nodes." );
		} 
	}

	/**
	 * checks that the DmtAdmin throws a DmtExeption of type FEATURE_NOT_SUPPORTED, if getNodeTimestamp is invoked on a scaffold node
	 * @param session
	 * @param uri
	 * @throws Exception
	 */
	private void getNodeTimeStampOperation( DmtSession session, String uri ) throws Exception {
		try {
			session.getNodeTimestamp(uri);
			fail( "The DmtAdmin must throw a DmtException of type COMMAND_NOT_ALLOWED for getNodeSize() on Scaffold nodes." );
		} catch (DmtException e) {
			pass( "The DmtAdmin correctly throws a DmtException of type COMMAND_NOT_ALLOWED for getNodeSize() on Scaffold nodes." );
		} 
	}
}
