package org.osgi.test.cases.remoteserviceadmin.junit;

import java.io.*;
import java.net.*;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.remoteserviceadmin.*;
import org.osgi.test.cases.remoteserviceadmin.common.*;
import org.osgi.test.support.compatibility.*;

public class ClosingTest extends DefaultTestBundleControl {
	RemoteServiceAdmin rsa;
	
	/**
	 * @see org.osgi.test.support.compatibility.DefaultTestBundleControl#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		rsa = (RemoteServiceAdmin) getService(RemoteServiceAdmin.class);
	}
	/**
	 * @see org.osgi.test.support.compatibility.DefaultTestBundleControl#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		ungetAllServices();
	}

	
	
	public void testClosing() throws Exception {
		TestService service = new TestService();
		ServiceRegistration registration = getContext().registerService(new String[]{A.class.getName(), B.class.getName()},
				                                                        service, null );
		assertNotNull(registration);

		try {
			Hashtable<String, Object> dictionary = new Hashtable<String, Object>();
			dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());

			assertFalse( busy(9000));
			Collection<ExportRegistration> exportRegistrations = rsa.exportService(registration.getReference(), dictionary);
			assertEquals(1, exportRegistrations.size());
			ExportRegistration er = exportRegistrations.iterator().next();
			er.getExportReference();
			assertTrue( busy(9000));
			
			er.close();
			assertFalse( busy(9000));
			
		} finally {
			registration.unregister();
		}
	}

	private boolean busy(int i) {
		try {
			ServerSocket s = new ServerSocket(i, 10, InetAddress.getByName("localhost"));
			s.close();
			return false;
		} catch( Throwable e) {
			return true;
		}
	}

	class TestService implements A, B, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * @see org.osgi.test.cases.remoteserviceadmin.common.A#getA()
		 */
		public String getA() {
			return "this is A";
		}

		/**
		 * @see org.osgi.test.cases.remoteserviceadmin.common.B#getB()
		 */
		public String getB() {
			return "this is B";
		}
		
	}
}
