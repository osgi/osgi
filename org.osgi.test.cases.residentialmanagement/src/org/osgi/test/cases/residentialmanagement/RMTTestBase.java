package org.osgi.test.cases.residentialmanagement;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.log.LogLevel;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;
import org.osgi.service.log.admin.LoggerAdmin;
import org.osgi.service.log.admin.LoggerContext;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

@SuppressWarnings("deprecation")
public abstract class RMTTestBase extends DefaultTestBundleControl implements
		RMTConstants {

	static final int DELAY; 
	ServiceReference<LoggerAdmin>	loggerAdminReference;
	LoggerAdmin						loggerAdmin;
	LoggerContext					rootContext;
	Map<String,LogLevel>			rootLogLevels;
	DmtAdmin dmtAdmin;
	DmtSession session;
	
	Bundle testBundle1 = null;
	Bundle testBundle2 = null;
	Bundle testBundle3 = null;
	Bundle testBundle4 = null;
	Bundle testBundle5 = null;
	Bundle testBundle6 = null;
	LogService log;
	
	private static Set<String> operations;

	static {
		operations = new HashSet<String>();
		operations.add("A");
		operations.add("G");
		operations.add("R");
		operations.add("D");
		// what about EXECUTE?
//		operations.add("E");
		
		int delay = 500;
		try {
			delay = Integer.parseInt(OSGiTestCaseProperties
					.getProperty("org.osgi.test.cases.rmt.delay"));
		} catch (Exception e) {
			System.out.println("System property 'org.osgi.test.cases.rmt.delay' not set or invalid.");
		}
		DELAY = delay;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		loggerAdminReference = getContext()
				.getServiceReference(LoggerAdmin.class);
		if (loggerAdminReference != null) {
			loggerAdmin = getContext().getService(loggerAdminReference);

			// save off original log levels
			rootContext = loggerAdmin.getLoggerContext(null);
			rootLogLevels = rootContext.getLogLevels();
			// enable all log levels for all loggers by default
			rootContext.setLogLevels(Collections
					.singletonMap(Logger.ROOT_LOGGER_NAME, LogLevel.TRACE));
		}
		System.out.println("setting up");
		dmtAdmin = getService(DmtAdmin.class);
	}


	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("tearing down");
		if (this.testBundle1 != null
				&& testBundle1.getState() != Bundle.UNINSTALLED)
			this.testBundle1.uninstall();
		this.testBundle1 = null;
		if (this.testBundle2 != null
				&& testBundle2.getState() != Bundle.UNINSTALLED)
			this.testBundle2.uninstall();
		this.testBundle2 = null;
		if (this.testBundle3 != null
				&& testBundle3.getState() != Bundle.UNINSTALLED)
			this.testBundle3.uninstall();
		this.testBundle3 = null;
		if (this.testBundle4 != null
				&& testBundle4.getState() != Bundle.UNINSTALLED)
			this.testBundle4.uninstall();
		this.testBundle4 = null;
		if (this.testBundle5 != null
				&& testBundle5.getState() != Bundle.UNINSTALLED)
			this.testBundle5.uninstall();
		this.testBundle5 = null;
		if (this.testBundle6 != null
				&& testBundle6.getState() != Bundle.UNINSTALLED)
			this.testBundle6.uninstall();
		this.testBundle6 = null;

		if (session != null && session.getState() == DmtSession.STATE_OPEN)
			session.close();
		unregisterAllServices();
		ungetAllServices();

		if (rootContext != null) {
			rootContext.setLogLevels(rootLogLevels);
		}
	}

	
	// -----Utilities-----
	Bundle installAndStartBundle(String location) throws IOException,
			BundleException {
		URL url = getContext().getBundle().getResource(location);
		InputStream is = url.openStream();
		Bundle bundle = getContext().installBundle(location, is);
		bundle.start();
		is.close();
		return bundle;
	}

	/**
	 * takes the nodes uri and checks meta-data against the formal descriptions from the TreeSummary 
	 * @param uri
	 * @param can ... encoded String from the Tree summary
	 * @param cardinality
	 * @param scope
	 * @throws Exception
	 */
	void assertMetaData( String uri, boolean isLeaf, String canStr, String cardinality, int scope, int format) throws Exception {
		MetaNode metaNode = session.getMetaNode(uri);
		assertNotNull("The metadata for " + uri + " must not be null!", metaNode);
		
		assertEquals( uri + " must " + (isLeaf ? "":" not ") + " be a leaf node", isLeaf, metaNode.isLeaf() );
		assertOperations(uri, metaNode, canStr);
		assertCardinality(uri, metaNode, cardinality);

		assertEquals( uri + " must have scope: " + scope, scope, metaNode.getScope() );
		assertEquals( "The MetaData of " + uri + " has a wrong data format.", format, metaNode.getFormat() );
	}
	
	
	String getBundleStateString( int state ) {
		switch (state) {
		case Bundle.ACTIVE:
			return "ACTIVE";
		case Bundle.INSTALLED:
			return "INSTALLED";
		case Bundle.RESOLVED:
			return "RESOLVED";
		case Bundle.STARTING:
			return "STARTING";
		case Bundle.STOPPING:
			return "STOPPING";
		case Bundle.UNINSTALLED:
			return "UNINSTALLED";
		}
		return null;
	}
	
	void dumpTree(DmtSession session, String uri ) throws Exception {
		System.out.println( uri );
		if ( session.isLeafNode(uri) )
			System.out.println( ">>>" + session.getNodeValue(uri));
		else {
			String[] children = session.getChildNodeNames(uri);
			for (String child : children)
				dumpTree(session, uri + "/" + child );
		}
	}

	/**
	 * returns the pathes of all file entries in the given bundle recursively
	 * directory entries are filtered out
	 * @param bundle
	 * @return
	 */
	Set<String> getBundleEntries( Bundle bundle, boolean encode ) {
		Set<String> entries = new HashSet<String>();
		addBundleEntryFolder(entries, bundle, "", encode);
		return entries;
	}

	static final String	LOG_TEST_MESSAGE_PREFIX	= "Log-Test Message";
	void createRandomLogs(int max) throws Exception {
		log = getService(LogService.class);
		// add a number of random logs
		for (int i = 0; i < max; i++) {
			// random log-level
			int level = (int) (Math.random() * LogService.LOG_DEBUG) + 1;
			if ( level == LogService.LOG_ERROR )
				log.log(level, LOG_TEST_MESSAGE_PREFIX + i,
						new RuntimeException("Log-Test Exception: " + i));
			else 
				log.log(level, LOG_TEST_MESSAGE_PREFIX + i);
			Sleep.sleep(10);
		}
	}

	/**
	 * compares the keySets of both maps as well as values that are of simple java type in the 
	 * "expected" map. The "real" map only holds String representations of values, which are unpredictable
	 * for more complex types like Arrays etc. 
	 * @param expected
	 * @param real
	 * @return true, if keySets and String representations of simple java values are equal, false otherwise
	 */
	boolean equalMapContent( Map<String, Object> expected, Map<String, ?> real ) {
		if ( expected == null || real == null )
			return false;
		if ( expected.size() != real.size() )
			return false;
		if ( ! expected.keySet().equals(real.keySet()) )
			return false;
		for (String key : expected.keySet()) {
			Object v1 = expected.get(key);
			Object v2 = real.get(key);
			// only compare the value, if it is of simple type that has a known String representation
			if ( v1 instanceof String || 
				 v1 instanceof Integer ||
				 v1 instanceof Long ||
				 v1 instanceof Boolean ||
				 v1 instanceof Float ) {
				if ( ! ("" + v1).equals( "" + v2) )
					return false;
			}
		}
		return true;
	}
	
	String stripWhitespaces( String s ) {
		char[] chars = s.toCharArray();
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			if ( ! Character.isWhitespace(chars[i]))
				b.append(chars[i]);
		}
		return b.toString();
	}

	private void addBundleEntryFolder(Set<String> results, Bundle bundle, String folder, boolean encode ) {
		Enumeration<String> paths = bundle.getEntryPaths(folder);
		if (null == paths) {
			//Only entries that have content will be returned, that is, empty directories in the Bundle’s archive are not returned.
			return;
		}
		while (paths.hasMoreElements()) {
			// filter out directories
			String path = paths.nextElement();
			if ( path.endsWith("/"))
				addBundleEntryFolder(results, bundle, path, encode );
			else {
				if ( encode )
					results.add(Uri.encode(path));
				else 
					results.add(path);
			}
		}
	}
	
	/**
	 * asserts that the metanode reports support for the specified operations
	 * @param uri ... the uri that the metadata belongs to
	 * @param metanode ... the metanode to be checked
	 * @param canStr ... a string defining the assumed capabilities of the node (eg. "_GR_" or "AGRD")
	 */
	private void assertOperations( final String uri, final MetaNode metaNode, final String canStr ) {
		for (int i = 0; i < canStr.length(); i++) {
			boolean should = operations.contains( canStr.substring(i,i+1) );
			// switch cases are in the order of the encoded actions in canStr ("AGRD")
			// TODO: what about EXECUTE ?
			switch (i) {
			case 0:
				assertEquals( uri + " must " + (should ? "":" not ") + " support ADD operation!", should, metaNode.can( MetaNode.CMD_ADD ) );
				break;
			case 1:
				assertEquals( uri + " must " + (should ? "":" not ") + " support GET operation!", should, metaNode.can( MetaNode.CMD_GET ) );
				break;
			case 2:
				assertEquals( uri + " must " + (should ? "":" not ") + " support REPLACE operation!", should, metaNode.can( MetaNode.CMD_REPLACE ) );
				break;
			case 3:
				assertEquals( uri + " must " + (should ? "":" not ") + " support DELETE operation!", should, metaNode.can( MetaNode.CMD_DELETE ) );
				break;
	
			default:
				break;
			}
		}
	}
	
	/**
	 * asserts that the metanode reports the correct cardinality
	 * @param uri ... the uri that the metadata belongs to
	 * @param metaNode ... the metanode to be checked
	 * @param cardinality ... a String defining the specified cardinalities (e.g. "0,1", "0..*" or "1")
	 */
	private void assertCardinality(final String uri, final MetaNode metaNode, String cardinality ) {
		// check first character
		boolean zeroAllowed = "0".equals(cardinality.substring(0,1));
		// check last character
		long max = "*".equals(cardinality.substring(cardinality.length()-1,cardinality.length())) ? Integer.MAX_VALUE : 1;
		assertEquals( "The MetaData of " + uri + " provides wrong value for 'zero occurrence allowed'!", zeroAllowed, metaNode.isZeroOccurrenceAllowed() );
		assertTrue( "The MetaData of " + uri + " provides wrong value for max occurence.", metaNode.getMaxOccurrence() > 0 && metaNode.getMaxOccurrence() <= max );
	}




}
