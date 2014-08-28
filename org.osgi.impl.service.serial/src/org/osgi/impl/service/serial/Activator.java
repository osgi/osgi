package org.osgi.impl.service.serial;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.step.TestStep;

public class Activator implements BundleActivator {

    private static BundleContext context = null;

    private ServiceRegistration testStepReg = null;

    public static BundleContext getContext() {
        return context;
    }

    public void start(BundleContext context) throws Exception {
        Activator.context = context;

        TestStep testStep = new TestStepImpl();
        Dictionary prop = new Properties();
        prop.put(Constants.SERVICE_PID, "org.osgi.service.serial");
        testStepReg = context.registerService(TestStep.class.getName(), testStep, prop);
    }

    public void stop(BundleContext context) throws Exception {
        if (testStepReg != null) {
            testStepReg.unregister();
        }

        Activator.context = null;
    }
}
