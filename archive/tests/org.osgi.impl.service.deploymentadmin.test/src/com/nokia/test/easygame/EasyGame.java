package com.nokia.test.easygame;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class EasyGame implements BundleActivator {

    public void start(BundleContext context) throws Exception {
        context.getDataFile("x.x");
    }

    public void stop(BundleContext context) throws Exception {
    }

}
