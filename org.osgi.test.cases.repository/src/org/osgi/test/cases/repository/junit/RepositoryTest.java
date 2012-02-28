package org.osgi.test.cases.repository.junit;

import org.osgi.framework.Bundle;
import org.osgi.test.support.OSGiTestCase;

public class RepositoryTest extends OSGiTestCase {
    public void testFoo() {
        for (Bundle b : getContext().getBundles()) {
            System.out.println("**** " + b.getSymbolicName());
        }
    }

    public void testBar() {
    }
}
