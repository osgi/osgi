package org.osgi.test.cases.repository.junit;

import org.osgi.framework.Bundle;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class RepositoryTest extends DefaultTestBundleControl {
    public void testFoo() {
        for (Bundle b : getContext().getBundles()) {
            System.out.println("**** " + b.getSymbolicName());
        }
    }

    public void testBar() {
    }
}
