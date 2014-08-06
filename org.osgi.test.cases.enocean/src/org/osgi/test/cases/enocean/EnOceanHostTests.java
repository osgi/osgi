
package org.osgi.test.cases.enocean;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanHost;

/**
 * Tests related to the EnOceanHost interface.
 */
public class EnOceanHostTests extends EnOceanTestCase {

	/**
	 * Test that at least one EnOceanHost service is registered when an
	 * implementation of the EnOcean specification is running.
	 */
	public void testEnOceanHostServiceAvailability() {
		try {
			ServiceReference[] srs = getContext().getAllServiceReferences(EnOceanHost.class.getName(), null);
			assertNotNull("Test failed: no EnOceanHost service has been found in the OSGi service registry.", srs);
			log("DEBUG: The test found " + srs.length + " EnOceanHost service(s).");
		} catch (InvalidSyntaxException e) {
			log("Test failed: no EnOceanHost service has been found in the OSGi service registry.");
			e.printStackTrace();
			fail();
		}
	}

}
