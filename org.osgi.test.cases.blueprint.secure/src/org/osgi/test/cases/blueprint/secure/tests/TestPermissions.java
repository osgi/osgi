/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.blueprint.secure.tests;

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.osgi.test.cases.blueprint.components.serviceimport.ServiceTwoListener;
import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.*;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This performs some basic permissoions test to ensure the extender bundle
 * doesn't extend its own permissions to the bundle
 *
 * @version $Revision$
 */
public class TestPermissions extends DefaultTestBundleControl {
    /**
     * Tests that the BlueprintContainer cannot be created if the
     * bundle does not have register permission for the BlueprintContainer
     *
     * @exception Exception
     */
	public void testNoBlueprintPermission() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/blueprint_denied.jar");
        controller.run();
    }

    /**
     * Tests that the BlueprintContainer cannot be created if the
     * bundle cannot register its exported services.
     *
     * @exception Exception
     */
	public void testNoExportPermission() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/ServiceOne_export_denied.jar");
        controller.run();
    }

    /**
     * Tests that the BlueprintContainer cannot be created if the
     * bundle cannot import its required services.
     *
     * @exception Exception
     */
	public void testNoImportPermission() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/ServiceOne_import_denied.jar");
        controller.run();
    }


    /**
     * Tests that the BlueprintContainer functions correctly if it has
     * export and import permissions.
     *
     * @exception Exception
     */
	public void testImportExportPermission() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/ServiceOne_export_denied.jar");
        controller.run();
    }


    /**
     * Tests that with the appropriate import/export permissions, the containers
     * function the same as when run without security.  This is a subset of
     * the non-secure test of the same name.
     */
    public void testSingleInterfaceExport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_import.jar",
                getWebServer()+"www/ServiceOne_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // the is the component creation
        exportStartEvents.addAssertion("ServiceOne", AssertionService.BEAN_CREATED);
        // validate that the service has been registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne"));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // the is the component creation
        exportStopEvents.addAssertion("ServiceOne", AssertionService.BEAN_DESTROY_METHOD);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, null));

        controller.run();
    }
}
