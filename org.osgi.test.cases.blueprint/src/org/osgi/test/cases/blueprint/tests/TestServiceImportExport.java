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

package org.osgi.test.cases.blueprint.tests;

import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.ServiceFactory;

import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.ServiceMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;
import org.osgi.test.cases.blueprint.components.serviceimport.ServiceOneListener;
import org.osgi.test.cases.blueprint.components.serviceimport.ServiceTwoListener;
import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.*;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class tests interactions between service import and export.
 * Since these operations are intertwined, most of the tests
 * involve loading multiple bundles.  One bundle will export services,
 * the other will import them.
 *
 * @version $Revision$
 */
public class TestServiceImportExport extends DefaultTestBundleControl {
    /*
     * Just a simple export/import for a service with an unsatisfied dependency.  According to
     * section 121.6.11, the manager must call the listener with the initial registration state.
     * In this test, there will be two services, one that is registered at activation and one
     * unregistered.
     */
    public void testRegistrationListenerInitialState() throws Exception {
        // We only do the export and then shut this back down again.  That will
        // cause the events of interests to be fired.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/registration_listener_initial_state.jar");
        // We're really only interesting the listener events, but we'll take a look
        // at the metadata as well
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        Hashtable activeProps = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        activeProps.put("service.interface.name", TestServiceOne.class.getName());
        activeProps.put("service.component.name", "ServiceOneActive");
        exportStartEvents.addEvent(new ComponentAssertion("registeredListener", AssertionService.SERVICE_REGISTERED, activeProps));

        Hashtable inactiveProps = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        inactiveProps.put("service.interface.name", TestServiceOne.class.getName());
        inactiveProps.put("service.component.name", "ServiceOneInactive");
        // this service should get an unregistered call
        exportStartEvents.addEvent(new ComponentAssertion("unregisteredListener", AssertionService.SERVICE_UNREGISTERED, inactiveProps));
        // we'll fail this if we see a registered event
        exportStartEvents.addFailureEvent(new ComponentAssertion("unregisteredListener", AssertionService.SERVICE_REGISTERED, inactiveProps));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        exportStopEvents.addEvent(new ComponentAssertion("registeredListener", AssertionService.SERVICE_UNREGISTERED, activeProps));
        // and we should not see a second unregistered event for this
        exportStopEvents.addFailureEvent(new ComponentAssertion("unregisteredListener", AssertionService.SERVICE_UNREGISTERED, inactiveProps));

        controller.run();
    }
}
