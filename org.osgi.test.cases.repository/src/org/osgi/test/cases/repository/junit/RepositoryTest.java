package org.osgi.test.cases.repository.junit;

import java.util.Arrays;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class RepositoryTest extends DefaultTestBundleControl {
    public void testFooXX() throws Exception {
        //Bundle tb1 = installBundle(getWebServer() + "xmlresources.tb1.jar", false);

        for (Bundle b : getContext().getBundles()) {
            System.err.println("@@@@ " + b.getSymbolicName() + "#" + b.getLocation());
        }

        for (ServiceReference<?> sr : getContext().getAllServiceReferences(null, null)) {
            System.err.println("**** " + Arrays.toString((String []) sr.getProperty(Constants.OBJECTCLASS)));
        }

        /*
        Bundle tb1 = getContext().
        Enumeration<URL> entries = tb1.findEntries("/", "*.xml", true);
        while (entries.hasMoreElements()) {
            System.err.println("$$$ " + entries.nextElement());
        }
        uninstallBundle(tb1);
        */
    }

    public void testBar() {
    }
}
