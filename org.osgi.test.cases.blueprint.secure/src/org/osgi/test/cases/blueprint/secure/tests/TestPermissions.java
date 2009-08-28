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
     * A managed bundle must have register permission for BlueprintContainer
     * or the container creation must fail.  This is not to be registered
     * using the extender's permissions.
     *
     * @exception Exception
     */
    public void testNoBlueprintPermission() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/blueprint_denied.jar");
        // this is to be an error per Bugzilla 1387
        controller.run();
    }

    /**
     * Tests that trying to use java.lang.System.exit() as a factory
     * method will fail because of a security exception.
     *
     * @exception Exception
     */
    public void testNoSystemPermission() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/system_bean.jar");
        controller.run();
    }


    /**
     * The bean class is imported from a bundle with AllPermissions, however,
     * the call to the constructor should be made as if it was coming from
     * code within the bundle and should die with a SecurityException
     *
     * @exception Exception
     */
    public void testBeanConstructorPermission() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/bean_constructor.jar");
        controller.run();
    }


    /**
     * The bean class is imported from a bundle with AllPermissions, however,
     * the call to the factory method should be made as if it was coming from
     * code within the bundle and should die with a SecurityException
     *
     * @exception Exception
     */
    public void testBeanInstanceFactoryPermission() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/bean_instance_factory.jar");
        controller.run();
    }


    /**
     * The bean class is imported from a bundle with AllPermissions, however,
     * the call to the constructor should be made as if it was coming from
     * code within the bundle and should die with a SecurityException
     *
     * @exception Exception
     */
    public void testBeanStaticFactoryPermission() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/bean_static_factory.jar");
        controller.run();
    }


    /**
     * The bean class is imported from a bundle with AllPermissions, however,
     * the call to the init-method should be made as if it was coming from
     * code within the bundle and should die with a SecurityException
     *
     * @exception Exception
     */
    public void testBeanInitMethodPermission() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/bean_init_method.jar");
        controller.run();
    }


    /**
     * The bean class is imported from a bundle with AllPermissions, however,
     * the call to the destroy-method should be made as if it was coming from
     * code within the bundle and should die with a SecurityException
     *
     * @exception Exception
     */
    public void testBeanDestroyMethodPermission() throws Exception {
        // NOTE:  exceptions from the destroy method are just swallowed, so this is
        // not an error test.  If the security exception is not thrown, there
        // will be an ASSERTION_FAILURE raised that will cause the test failure.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/bean_destroy_method.jar");
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
     * bundle cannot import its required services.  This bundle
     * has a short grace period timeout specified, in case the
     * implementation handles the security failure as if it was a
     * service unavailable situation.  Either way, there should be
     * a creation failure.
     *
     * @exception Exception
     */
    public void testNoImportPermission() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/ServiceOne_import_denied.jar");
        controller.addSetupBundle(getWebServer()+"www/ServiceOne_export.jar");
        controller.run();
    }

    /**
     * The listener code is imported from a bundle with AllPermissions.  However,
     * the listener callback needs to be made using the bundle's access control context,
     * so a security exception should result.  Exceptions in listener methods are not
     * causes for container failures, so we're relying on an ASSERTION_FAILURE event
     * to indicate this didn't work.
     *
     * @exception Exception
     */
    public void testBindMethodPermission() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/bind_method.jar");
        controller.addSetupBundle(getWebServer()+"www/ServiceOne_export.jar");
        controller.run();
    }

    /**
     * The listener code is imported from a bundle with AllPermissions.  However,
     * the listener callback needs to be made using the bundle's access control context,
     * so a security exception should result.  Exceptions in listener methods are not
     * causes for container failures, so we're relying on an ASSERTION_FAILURE event
     * to indicate this didn't work.
     *
     * @exception Exception
     */
    public void testUnbindMethodPermission() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/unbind_method.jar");
        controller.addSetupBundle(getWebServer()+"www/ServiceOne_export.jar");
        controller.run();
    }


    /**
     * The listener code is imported from a bundle with AllPermissions.  However,
     * the listener callback needs to be made using the bundle's access control context,
     * so a security exception should result.  Exceptions in listener methods are not
     * causes for container failures, so we're relying on an ASSERTION_FAILURE event
     * to indicate this didn't work.
     *
     * @exception Exception
     */
    public void testRegistrationMethodPermission() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/registered_method.jar");
        controller.run();
    }


    /**
     * The listener code is imported from a bundle with AllPermissions.  However,
     * the listener callback needs to be made using the bundle's access control context,
     * so a security exception should result.  Exceptions in listener methods are not
     * causes for container failures, so we're relying on an ASSERTION_FAILURE event
     * to indicate this didn't work.
     *
     * @exception Exception
     */
    public void testUnegistrationMethodPermission() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/unregistered_method.jar");
        controller.run();
    }


    /**
     * The type converter de is imported from a bundle with AllPermissions.  However,
     * the converter method calls need to be made using the bundle's access control context,
     * so a security exception should result.  Exceptions in listener methods are not
     * causes for container failures, so we're relying on an ASSERTION_FAILURE event
     * to indicate this didn't work.
     *
     * @exception Exception
     */
    public void testCanConvertPermission() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/converter_canConvert.jar");
        controller.run();
    }


    /**
     * The type converter de is imported from a bundle with AllPermissions.  However,
     * the converter method calls need to be made using the bundle's access control context,
     * so a security exception should result.  Exceptions in listener methods are not
     * causes for container failures, so we're relying on an ASSERTION_FAILURE event
     * to indicate this didn't work.
     *
     * @exception Exception
     */
    public void testConvertPermission() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/converter_convert.jar");
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
