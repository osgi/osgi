/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */

package org.osgi.test.cases.tracker.tb3;

import java.util.*;

import org.osgi.framework.*;

/**
   Bundle for exporting packages

   @author Ericsson Telecom AB
*/
public class TB3Activator implements BundleActivator
{
    BundleContext bc;
    TestService3 ts3;
    Properties ts3Props;
    ServiceRegistration tsr3;


    /**
       Starts the bundle.
       Installs several services later filtered by the tbc
    */
    public void start(BundleContext bc) {

        this.bc=bc;

        ts3 = new TestService3Impl();
        ts3Props = new Properties();
        ts3Props.put("name", "TestService3");
        ts3Props.put("version", new Float(1.0));
        ts3Props.put("compatible", new Float(1.0));
        ts3Props.put("description", "TestService 3");

        tsr3 = bc.registerService(TestService3.class.getName(), ts3, ts3Props);
        System.out.println("### TS3 started");
    }

    /**
       Stops the bundle.
    */
    public void stop(BundleContext bc) {
        try
        {
            tsr3.unregister();

        }
        catch (IllegalStateException e) { /* Ignore */ }

        tsr3 = null;
        ts3 = null;
    }
}
