package org.osgi.test.cases.repository.junit;

import java.util.Arrays;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.OSGiTestCase;

public class RepositoryTest extends OSGiTestCase {
    public void testFooXX() throws Exception {
        for (Bundle b : getContext().getBundles()) {
            System.err.println("@@@@ " + b.getSymbolicName());
        }

        for (ServiceReference<?> sr : getContext().getAllServiceReferences(null, null)) {
            System.err.println("**** " + Arrays.toString((String []) sr.getProperty(Constants.OBJECTCLASS)));
        }
    }

    public void testBar() {
    }
}
