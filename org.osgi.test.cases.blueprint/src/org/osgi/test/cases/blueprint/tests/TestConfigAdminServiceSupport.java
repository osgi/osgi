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

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection;
import org.osgi.test.cases.blueprint.framework.AdminPropertiesAdder;
import org.osgi.test.cases.blueprint.framework.AdminPropertiesRemover;
import org.osgi.test.cases.blueprint.framework.AdminPropertiesUpdater;
import org.osgi.test.cases.blueprint.framework.BlueprintAdminEvent;
import org.osgi.test.cases.blueprint.framework.ComponentAssertion;
import org.osgi.test.cases.blueprint.framework.EventSet;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.ServiceComponentExistValidator;
import org.osgi.test.cases.blueprint.framework.ServiceExistValidator;
import org.osgi.test.cases.blueprint.framework.ServiceRegistrationValidator;
import org.osgi.test.cases.blueprint.framework.ServiceTestEvent;
import org.osgi.test.cases.blueprint.framework.StandardErrorTestController;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.ThreePhaseTestController;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.TestGoodService;
import org.osgi.test.cases.blueprint.services.TestGoodServiceSubclass;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class TestConfigAdminServiceSupport extends DefaultTestBundleControl {

    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName, Object propertyValue, Class type) {
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, type);
        //startEvents.addValidator(new PropertyMetadataValidator(compName, new TestProperty[] { new StringProperty(propertyName, type.getName()) }));
    }


    public void testPlaceholderPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/placeholder_property_injection.jar");

        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        this.addPropertyValidator(startEventSet, "compString", "string", "abc", String.class);
        this.addPropertyValidator(startEventSet, "compStringPart", "string", "abcxyz", String.class);
        this.addPropertyValidator(startEventSet, "compStringEmpty", "string", "", String.class);
        this.addPropertyValidator(startEventSet, "compStringEmptyPart", "string", "xyz", String.class);

        this.addPropertyValidator(startEventSet, "compWrapperBoolean", "boolean", Boolean.TRUE, Boolean.class);
        this.addPropertyValidator(startEventSet, "compWrapperByte", "byte", new Byte((byte)3), Byte.class);
        this.addPropertyValidator(startEventSet, "compWrapperCharacter", "character", new Character((char)4), Character.class);
        this.addPropertyValidator(startEventSet, "compWrapperInteger", "integer", new Integer(5), Integer.class);
        this.addPropertyValidator(startEventSet, "compWrapperShort", "short", new Short((short)6), Short.class);
        this.addPropertyValidator(startEventSet, "compWrapperLong", "long", new Long(7), Long.class);
        this.addPropertyValidator(startEventSet, "compWrapperDouble", "double", new Double(8.0), Double.class);
        this.addPropertyValidator(startEventSet, "compWrapperFloat", "float", new Float(9.0), Float.class);

        controller.run();
    }


    /**
     * test error conditions when config admin service is not available
     */
    public void testMissingConfigAdminService() throws Exception {
        ServiceReference configAdminService = getContext().getServiceReference("org.osgi.service.cm.ConfigurationAdmin");
        Bundle adminBundle = configAdminService.getBundle();
        // stop the config admin service before attempting to run the same test as above...this should
        // fail this time.
        adminBundle.stop();

        try {
            // this should just be the standard error set
            StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/placeholder_property_injection.jar");
            // this insures we don't have errors resulting from missing stuff
            controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
            controller.run();
        } finally {
            // restart this service before continuing with the tests
            adminBundle.start();
        }
    }




    /**
     * invalid pid on the property placeholder
     */
    public void testBadPlaceholderPid() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_placeholder_bad_pid.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }

    public void testPlaceholderPrefixAndSuffix() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/placeholder_prefix_and_suffix.jar");
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        this.addPropertyValidator(startEventSet, "compString1", "string", "abc", String.class);
        this.addPropertyValidator(startEventSet, "compString2", "string", "abc", String.class);

        controller.run();
    }

    /**
     * multiple placeholders using default prefix
     */
    public void testDefaultPrefixConflict() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_default_prefix_conflict.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }

    /**
     * multiple placeholders using same explicit prefix
     */
    public void testExplicitPrefixConflict() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_explicit_prefix_conflict.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }

    public void testPlaceholderDefaultProperties() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/placeholder_default_properties.jar");
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        this.addPropertyValidator(startEventSet, "compString1", "string", "def", String.class);
        this.addPropertyValidator(startEventSet, "compString2", "string", "def", String.class);
        this.addPropertyValidator(startEventSet, "compString3", "string", "def", String.class);

        controller.run();
    }

    /**
     * missing placeholder config value should force an error
     */
    public void testMissingPlaceholderDefault() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_missing_placeholder_default.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }

    public void testPlaceholderDeclarationScope() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/placeholder_declaration_scope.jar");
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        this.addPropertyValidator(startEventSet, "compString", "string", "abc", String.class);

        controller.run();
    }

    // Section 5.7.2
    public void testServicePropertiesEvaluation() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/service_properties_evaluation.jar");
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        // publish one
        Hashtable expectedProps = new Hashtable();
        expectedProps.put("str", "abc");
        startEventSet.addValidator(new ServiceRegistrationValidator(TestGoodService.class, "compGoodService", null, expectedProps));

        // publish all the properties
        expectedProps = new Hashtable();
        expectedProps.put("str", "abc");
        expectedProps.put("str_empty", "");
        expectedProps.put("str_boolean", "true");
        expectedProps.put("str_byte", "3");
        expectedProps.put("str_char", "4");
        expectedProps.put("str_int", "5");
        expectedProps.put("str_short", "6");
        expectedProps.put("str_long", "7");
        expectedProps.put("str_double", "8.0");
        expectedProps.put("str_float", "9.0");
        startEventSet.addValidator(new ServiceRegistrationValidator(TestGoodServiceSubclass.class, "compGoodServiceSubclass", null, expectedProps));

        expectedProps = new Hashtable(expectedProps);
        // this property comes from explicitly specifying the entry in addition to referencing the pid
        expectedProps.put("extra", "abc");
        startEventSet.addValidator(new ServiceRegistrationValidator(TestGoodServiceSubclass.class, "compGoodServiceSubclass", null, expectedProps));

        expectedProps = new Hashtable(expectedProps);
        // this property come a second config-admin property set
        expectedProps.put("configId", "config4");
        // we don't use the explicit one
        expectedProps.remove("extra");
        startEventSet.addValidator(new ServiceRegistrationValidator(TestGoodServiceSubclass.class, "compGoodServiceSubclass", null, expectedProps));

        controller.run();
    }



    public void testServicePropertiesAutoUpdate() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/service_properties_auto_update.jar");
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        Properties initialProps = new Properties();
        initialProps.put("str", "abc");
        Properties updatedProps = new Properties();
        updatedProps.put("str", "xyz");

        // when the service registered, then add a test event listener to update properties
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED",
                new Class[] {TestGoodServiceSubclass.class},
                null,
                new AdminPropertiesUpdater(getContext(), "org.osgi.test.cases.blueprint.services.ConfigurationManager", "org.osgi.test.cases.blueprint.components.cmsupport.config1", updatedProps)
        ));

        // the update above will result in a service MODIFIED event...we'll wait until that occurs before
        // running the validators
        startEventSet.addServiceEvent("MODIFIED", TestGoodServiceSubclass.class);

        // Validate the service property is changed to "xyz"
        startEventSet.addValidator(new ServiceRegistrationValidator(TestGoodService.class, "compGoodService", null, initialProps));
        startEventSet.addValidator(new ServiceRegistrationValidator(TestGoodServiceSubclass.class, "compGoodServiceSubclass", null, updatedProps));

        controller.run();
    }

    /**
     * invalid pid on the cm-properties tag
     */
    public void testBadCmPropertiesPid() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_properties_bad_pid.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }

    // Section 5.7.3
    public void testComponentPropertiesEvaluation() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/component_properties_evaluation.jar");
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        this.addPropertyValidator(startEventSet, "comp1", "string", "abc", String.class);
        this.addPropertyValidator(startEventSet, "comp1", "boolean", Boolean.TRUE, Boolean.class);
        this.addPropertyValidator(startEventSet, "comp1", "byte", new Byte((byte)3), Byte.class);
        this.addPropertyValidator(startEventSet, "comp1", "character", new Character((char)4), Character.class);
        this.addPropertyValidator(startEventSet, "comp1", "integer", new Integer(5), Integer.class);
        this.addPropertyValidator(startEventSet, "comp1", "short", new Short((short)6), Short.class);
        this.addPropertyValidator(startEventSet, "comp1", "long", new Long(7), Long.class);
        this.addPropertyValidator(startEventSet, "comp1", "double", new Double(8.0), Double.class);
        this.addPropertyValidator(startEventSet, "comp1", "float", new Float(9.0), Float.class);

        //test the config-properties take precedence
        this.addPropertyValidator(startEventSet, "comp2", "string", "abc", String.class);
        this.addPropertyValidator(startEventSet, "comp2", "boolean", Boolean.TRUE, Boolean.class);
        this.addPropertyValidator(startEventSet, "comp2", "byte", new Byte((byte)3), Byte.class);
        this.addPropertyValidator(startEventSet, "comp2", "character", new Character((char)4), Character.class);
        this.addPropertyValidator(startEventSet, "comp2", "integer", new Integer(5), Integer.class);
        this.addPropertyValidator(startEventSet, "comp2", "short", new Short((short)6), Short.class);
        this.addPropertyValidator(startEventSet, "comp2", "long", new Long(7), Long.class);
        this.addPropertyValidator(startEventSet, "comp2", "double", new Double(8.0), Double.class);
        this.addPropertyValidator(startEventSet, "comp2", "float", new Float(9.0), Float.class);

        controller.run();

    }

    /**
     * invalid pid on the managed-properties tag
     */
    public void testBadManagedPropertiesPid() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_managed_properties_bad_pid.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }

    public void testComponentPropertiesAutoUpdate() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/component_properties_auto_update.jar");
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        Hashtable initialTableItems = new Hashtable();
        initialTableItems.put("string", "abc");
        initialTableItems.put("boolean", "true");

        Properties updatedTableItems = new Properties();
        updatedTableItems.put("string", "xyz");
        updatedTableItems.put("boolean", "false");

        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        startEventSet.removeEvent(new BlueprintAdminEvent("CREATED"));

        // Ok, when the CREATED event is triggered, update the configuration object
        startEventSet.addEvent(new BlueprintAdminEvent("CREATED", null, new AdminPropertiesUpdater(getContext(), "org.osgi.test.cases.blueprint.services.ConfigurationManager",  "org.osgi.test.cases.blueprint.components.cmsupport.config2", updatedTableItems)));

        //compStrategyDefault
        this.addPropertyValidator(startEventSet, "compStrategyDefault", "string", "abc", String.class);
        this.addPropertyValidator(startEventSet, "compStrategyDefault", "boolean", Boolean.TRUE, Boolean.class);

        //compStrategyNone
        this.addPropertyValidator(startEventSet, "compStrategyNone", "string", "abc", String.class);
        this.addPropertyValidator(startEventSet, "compStrategyNone", "boolean", Boolean.TRUE, Boolean.class);

        //compStrategyComponent
        startEventSet.addAssertion("compStrategyComponent", AssertionService.METHOD_CALLED);

        //compStrategyContainer
        this.addPropertyValidator(startEventSet, "compStrategyContainer", "string", "xyz", String.class);
        this.addPropertyValidator(startEventSet, "compStrategyContainer", "boolean", Boolean.FALSE, Boolean.class);

        controller.run();

    }

    /**
     * invalid pid on the managed-properties tag
     */
    public void testComponentMissingUpdateMethod() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_missing_update_method.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }

    /**
     * invalid pid on the managed-properties tag
     */
    public void testComponentBadUpdateMethod() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_bad_update_method.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }


    //section 5.7.5
    public void testDirectAccessManagedService() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/direct_access_managed_service.jar");
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        Hashtable serviceProps = null;

        // compTestManagedService
        serviceProps = new Hashtable();
        serviceProps.put("service.pid", "org.osgi.test.cases.blueprint.components.cmsupport.config1");
        startEventSet.addValidator(new ServiceRegistrationValidator(org.osgi.service.cm.ManagedService.class, "compTestManagedService", null, serviceProps));

        startEventSet.addPropertyAssertion("compTestManagedService", AssertionService.METHOD_CALLED, "TestManagedService_updated");

        controller.run();
    }

    public void testDirectAccessManagedServiceFactory() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/direct_access_managed_service_factory.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        Hashtable serviceProps = null;

        // compTestManagedServiceFactory
        serviceProps = new Hashtable();
        serviceProps.put("service.pid", "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1");
        startEventSet.addValidator(new ServiceRegistrationValidator(org.osgi.service.cm.ManagedServiceFactory.class, "compTestManagedServiceFactory", null, serviceProps));

        startEventSet.addPropertyAssertion("compTestManagedServiceFactory", AssertionService.METHOD_CALLED, "TestManagedServiceFactory_updated_0");
        startEventSet.addPropertyAssertion("compTestManagedServiceFactory", AssertionService.METHOD_CALLED, "TestManagedServiceFactory_updated_1");


        controller.run();
    }


    //Section 5.7.4

    public void testFactoryAttributeAutoExportAll() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/factory_attribute_autoexport_all.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", new Class[]{ManagedComponentInjection.class, BaseTestComponent.class, ComponentTestInfo.class}));
        controller.run();

    }


    public void testFactoryAttributeAutoExportClass() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/factory_attribute_autoexport_class.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", new Class[]{ManagedComponentInjection.class, BaseTestComponent.class}));  //?
        controller.run();
    }

    public void testFactoryAttributeAutoExportInterfaces() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/factory_attribute_autoexport_interfaces.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", new Class[]{ComponentTestInfo.class}));
        controller.run();
    }

    public void testFactoryNestedInterfaces() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/factory_attribute_autoexport_class.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", new Class[]{ManagedComponentInjection.class, BaseTestComponent.class}));  //?
        controller.run();
    }


    public void testFactoryServicePropertiesEvaluation() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/factory_service_properties_evaluation.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        HashMap expectedProps;

        expectedProps = new HashMap();
        expectedProps.put("string", "abc");
        expectedProps.put("boolean", new Boolean(true));
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", "org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection", expectedProps));
        //startEventSet.addValidator(new ServiceExistValidator("org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection", expectedProps));

        expectedProps = new HashMap();
        expectedProps.put("string", "bcd");
        expectedProps.put("boolean", new Boolean(false));
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", "org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection", expectedProps));
        //startEventSet.addValidator(new ServiceExistValidator("org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection", expectedProps));

        controller.run();

    }

    public void testFactoryServicePropertiesAutoUpdate() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/factory_service_properties_auto_update.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        // 1. change the configuration objects
        Dictionary updatedProps = new Hashtable();
        updatedProps.put("string", "xyz");
        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        startEventSet.removeEvent(new BlueprintAdminEvent("CREATED"));
        // Ok, when the CREATED event is triggered, update the configuration object
        startEventSet.addEvent(new BlueprintAdminEvent("CREATED", null, new AdminPropertiesUpdater(getContext(), "org.osgi.test.cases.blueprint.services.ConfigurationManager", "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1", updatedProps)));

        // 2. validate Service Exist
        startEventSet.addValidator(new ServiceExistValidator("org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection", updatedProps));

        controller.run();

    }

    public void testFactoryComponentPropertiesEvaluation() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/factory_component_properties_evaluation.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        ServiceComponentExistValidator v = null;

        v = new ServiceComponentExistValidator("org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection");
        v.addValue("string", "abc", String.class);
        v.addValue("boolean", new Boolean("true"), Boolean.class);
        startEventSet.addValidator(v);

        // and create a second set of properties to validate the listener call backs
        Hashtable props1 = new Hashtable();
        props1.put("string", "abc");
        props1.put("boolean", new Boolean("true"));

        startEventSet.addEvent(new ComponentAssertion("ManagedComponentListener", AssertionService.SERVICE_REGISTERED, props1));

        v = new ServiceComponentExistValidator("org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection");
        v.addValue("string", "bcd", String.class);
        v.addValue("boolean", new Boolean("false"), Boolean.class);
        startEventSet.addValidator(v);

        // and create a second set of properties to validate the listener call backs
        Hashtable props2 = new Hashtable();
        props2.put("string", "bcd");
        props2.put("boolean", new Boolean("false"));

        startEventSet.addEvent(new ComponentAssertion("ManagedComponentListener", AssertionService.SERVICE_REGISTERED, props2));

        // now some expected termination stuff
        EventSet stopEventSet = controller.getStopEvents();
        stopEventSet.addEvent(new ComponentAssertion("ManagedComponentListener", AssertionService.SERVICE_UNREGISTERED, props1));
        stopEventSet.addEvent(new ComponentAssertion("ManagedComponentListener", AssertionService.SERVICE_UNREGISTERED, props2));

        controller.run();

    }

    public void testFactoryComponentPropertiesAutoUpdate_ContainerManaged() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/factory_component_properties_auto_update_cont.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        // 1. Change the configuration objects
        Dictionary updatedProps = new Hashtable();
        updatedProps.put("string", "xyz");
        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        startEventSet.removeEvent(new BlueprintAdminEvent("CREATED"));
        // Ok, when the CREATED event is triggered, update the configuration object
        startEventSet.addEvent(new BlueprintAdminEvent("CREATED", null, new AdminPropertiesUpdater(getContext(), "org.osgi.test.cases.blueprint.services.ConfigurationManager", "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1", updatedProps)));

        // 2. Validate component exist
        ServiceComponentExistValidator v = null;
        v = new ServiceComponentExistValidator("org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection");
        v.addValue("string", "xyz", String.class);
        startEventSet.addValidator(v);

        controller.run();

    }

    public void testFactoryComponentPropertiesAutoUpdate_ComponentManaged() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/factory_component_properties_auto_update_comp.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        // 1. Change the configuration objects
        Dictionary updatedProps = new Hashtable();
        updatedProps.put("string", "xyz");
        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        startEventSet.removeEvent(new BlueprintAdminEvent("CREATED"));
        // Ok, when the CREATED event is triggered, update the configuration object
        startEventSet.addEvent(new BlueprintAdminEvent("CREATED", null, new AdminPropertiesUpdater(getContext(), "org.osgi.test.cases.blueprint.services.ConfigurationManager", "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1", updatedProps)));

        // 2. Assert component method called
        startEventSet.addAssertion("managedComp", AssertionService.METHOD_CALLED);

        controller.run();

    }

    public void testFactoryComponentPropertiesAutoUpdate_None() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/factory_component_properties_auto_update_none.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        // 1. Change the configuration objects
        Dictionary updatedProps = new Hashtable();
        updatedProps.put("string", "xyz");
        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        startEventSet.removeEvent(new BlueprintAdminEvent("CREATED"));
        // Ok, when the CREATED event is triggered, update the configuration object
        startEventSet.addEvent(new BlueprintAdminEvent("CREATED", null, new AdminPropertiesUpdater(getContext(), "org.osgi.test.cases.blueprint.services.ConfigurationManager", "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1", updatedProps)));


        // 2. no component changed
        ServiceComponentExistValidator v = null;

        v = new ServiceComponentExistValidator("org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection");
        v.addValue("string", "abc", String.class);
        v.addValue("boolean", new Boolean("true"), Boolean.class);
        startEventSet.addValidator(v);

        v = new ServiceComponentExistValidator("org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection");
        v.addValue("string", "bcd", String.class);
        v.addValue("boolean", new Boolean("false"), Boolean.class);
        startEventSet.addValidator(v);


        controller.run();

    }


    public void testFactoryServiceLifecycle() throws Exception {
        ThreePhaseTestController controller = new ThreePhaseTestController(getContext(),
                getWebServer()+"www/factory_service_lifecycle.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        //start
        MetadataEventSet startEventSet = controller.getStartEvents(0);
        //configfactory1 has 2 configuration objects, so receive 2 registered event
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", "org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection"));
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", "org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection"));

        //middle
        MetadataEventSet middleEventSet = controller.getMiddleEvents(0);
        Dictionary newProps = new Hashtable();
        newProps.put("string", "cde");
        newProps.put("boolean", new Boolean(true));

        // add 1 configuration to the configfactory1
        middleEventSet.addInitializer(new AdminPropertiesAdder(getContext(), "org.osgi.test.cases.blueprint.services.ConfigurationManager",  "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1", newProps));
        middleEventSet.addEvent(new ServiceTestEvent("REGISTERED", new String[] {"org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection"}, null,
                new AdminPropertiesRemover(getContext(), "org.osgi.test.cases.blueprint.services.ConfigurationManager",  "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1")));

        // after remove the properties, we should receive 3 unregistering event
        middleEventSet.addEvent(new ServiceTestEvent("UNREGISTERING", "org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection"));
        middleEventSet.addEvent(new ServiceTestEvent("UNREGISTERING", "org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection"));
        middleEventSet.addEvent(new ServiceTestEvent("UNREGISTERING", "org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection"));

        controller.run();

    }

    public void testFactoryManagedComponentInitAndDestroy_ConfigDeleted() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/factory_managed_component_init_and_destroy.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);

        // init method called
        startEventSet.addPropertyAssertion("managedComp", AssertionService.METHOD_CALLED, "ManagedComponentInjection_onInit");



        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        startEventSet.removeEvent(new BlueprintAdminEvent("CREATED"));

        // Ok, when the CREATED event is triggered, update the configuration object
        startEventSet.addEvent(new BlueprintAdminEvent("CREATED", null, new AdminPropertiesRemover(getContext(), "org.osgi.test.cases.blueprint.services.ConfigurationManager", "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1")));

        // destroy method called
        startEventSet.addPropertyAssertion("managedComp", AssertionService.METHOD_CALLED, "ManagedComponentInjection_onDestroy_ConfigDeleted");

        controller.run();

    }

    public void testFactoryManagedComponentInitAndDestroy_BundleStopping() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/factory_managed_component_init_and_destroy.jar");
        controller.addSetupBundle(getWebServer()+"www/create_factory_configuration_objects.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(0);
        // init method called
        startEventSet.addPropertyAssertion("managedComp", AssertionService.METHOD_CALLED, "ManagedComponentInjection_onInit");


        EventSet stopEventSet = controller.getStopEvents(0);
        // destroy method called
        stopEventSet.addPropertyAssertion("managedComp", AssertionService.METHOD_CALLED, "ManagedComponentInjection_onDestroy_BundleStopping");

        controller.run();

    }

    /**
     * tests a bad component name on a managed-service-factory listener
     */
    public void testManagedServiceListenerBadComponent() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_managed_service_listener_bad_component.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }

    /**
     * tests a bad register method name on a managed-service-factory listener
     */
    public void testManagedServiceListenerBadRegister() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_managed_service_listener_bad_register.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }

    /**
     * tests a bad unregister method name on a managed-service-factory listener
     */
    public void testManagedServiceListenerBadUnregister() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_managed_service_listener_bad_unregister.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }

    /**
     * tests an omitted component name on a managed-service-factory listener
     */
    public void testManagedServiceListenerNoComponent() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_managed_service_listener_no_component.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }

    /**
     * tests omitted method names on a managed-service-factory listener
     */
    public void testManagedServiceListenerNoMethods() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_managed_service_listener_no_methods.jar");
        // this insures we don't have errors resulting from missing stuff
        controller.addSetupBundle(getWebServer()+"www/create_configuration_objects.jar");
        controller.run();
    }

}
