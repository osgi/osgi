package org.osgi.test.cases.dmt.tc4.tbc;

import info.dmtree.DmtAdmin;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.dmt.tc4.tb1.Activator;
import org.osgi.test.support.OSGiTestCase;

/**
 * tests simple mapping cases
 * 
 * @author steffen
 *
 */
public class TestJohnAndAndreas extends OSGiTestCase {
	BundleContext context = FrameworkUtil.getBundle(TestJohnAndAndreas.class).getBundleContext();
	DmtAdmin dmtAdmin;
	DmtSession session;

	Bundle andreas = null;
	Activator john = new Activator();
	
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up ...");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("tearing down ...");
		
		if ( andreas != null ) {
			System.out.println( "stopping andreas");
			andreas.stop();
			Thread.sleep(200);
		}
		if ( john != null ) {
			System.out.println( "stopping john ");
			john.unregister();
			john.stop(context);
			Thread.sleep(200);
		}

		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			if ( bundles[i].getLocation().indexOf( "org.osgi.impl.service.dmt.jar" ) != -1 ) {
				System.out.println( "restarting dmt-admin bundle" );
				bundles[i].stop();
				Thread.sleep(500);
				bundles[i].start();
			}
		}
	}

	public void testAndreasBeforeJohn() throws Exception {
		System.out.println("Hello");
		
		System.out.println( "initially the tree is empty");
		listTree();

		System.out.println();
		System.out.println( "starting Andreas's bundle... ");
		andreas = install("tb1.jar");
		andreas.start();
		
		listTree();

		System.out.println( "starting John's bundle... ");
		john.start(context);
		Thread.sleep(200);
		
		listTree();
		
		System.out.println( "stopping John's bundle... ");
		john.stop(context);
		Thread.sleep(200);
		listTree();
	}

	public void testJohnBeforeAndreas() throws Exception {
		System.out.println("Hello");
		
		System.out.println( "initially the tree is empty");
		listTree();

		System.out.println( "starting John's bundle... ");
		BundleActivator john = new Activator();
		john.start(context);
		
		listTree();

		System.out.println();
		System.out.println( "starting Andreas's bundle... ");
		Bundle bundle = install("tb1.jar");
		bundle.start();
		
		listTree();
	}

	private void listTree() throws DmtException {
		System.out.println( "### listing tree ...");
		ServiceReference ref = context.getServiceReference(DmtAdmin.class.getName());
		DmtAdmin dmtAdmin = (DmtAdmin) context.getService(ref);
		String uri = ".";
		DmtSession session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_SHARED);
		listChildren(session, uri);
		session.close();
		context.ungetService(ref);
	}
	
	private void listChildren( DmtSession session, String uri ) throws DmtException {
		String[] children = session.getChildNodeNames(uri);
		for (int i = 0; i < children.length; i++) {
			String node = uri + "/" + children[i];
			System.out.println( "session: " + session.getRootUri() + " --> " + node);
			if (! session.isLeafNode(node))
				listChildren(session, node );
		}
	}
}
