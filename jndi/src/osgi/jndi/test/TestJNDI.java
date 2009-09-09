package osgi.jndi.test;

import java.util.*;

import javax.naming.*;
import javax.naming.spi.*;

import junit.framework.*;

import org.osgi.framework.*;
import org.osgi.service.jndi.*;
import org.osgi.util.tracker.*;

public class TestJNDI extends TestCase {
	BundleContext	context;

	final InitialContextFactory fc1 = new InitialContextFactory() {
		public Context getInitialContext(Hashtable< ? , ? > environment)
				throws NamingException {
			return new DummyContext("1");
		}
	};
	
	public void setBundleContext(BundleContext c) throws Exception {
		this.context = c;
	}

	public void testSimple() throws Exception {
		// Use tracker ... DS or BP too much for a test
		ServiceTracker tracker = new ServiceTracker(context, JNDI.class.getName(), null);
		tracker.open();		
		JNDI jndi = (JNDI) tracker.waitForService(10000);

		// Try without setting up factory
		try {
			Context context = new InitialContext();
			context.lookup("1-test");
			fail("Should have had an exception");
		} catch( NoInitialContextException nice) {
			// OK
		}
		
		// Simple standard case
		JNDIRegistration r = jndi.pushInitialContextFactory(fc1);
		try {
			Context context = new InitialContext();
			assertEquals( "1-test", context.lookup("test"));
		}
		finally {
			r.close();
		}

		// Try without after closing registration
		try {
			Context context = new InitialContext();
			context.lookup("1-test");
			fail("Should have had an exception");
		} catch( NoInitialContextException nice) {
			// OK
		}
		
	}

}
