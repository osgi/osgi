/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.blueprint.tests;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.blueprint.context.ModuleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection;
import org.osgi.test.cases.blueprint.framework.AdminPropertiesAdder;
import org.osgi.test.cases.blueprint.framework.AdminPropertiesRemover;
import org.osgi.test.cases.blueprint.framework.AdminPropertiesUpdater;
import org.osgi.test.cases.blueprint.framework.BlueprintEvent;
import org.osgi.test.cases.blueprint.framework.EventSet;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.PropertyMetadataValidator;
import org.osgi.test.cases.blueprint.framework.ServiceComponentExistValidator;
import org.osgi.test.cases.blueprint.framework.ServiceExistValidator;
import org.osgi.test.cases.blueprint.framework.ServiceManagerRegister;
import org.osgi.test.cases.blueprint.framework.ServiceManagerUnregister;
import org.osgi.test.cases.blueprint.framework.ServiceRegistrationValidator;
import org.osgi.test.cases.blueprint.framework.ServiceTestEvent;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.StringProperty;
import org.osgi.test.cases.blueprint.framework.TestProperty;
import org.osgi.test.cases.blueprint.framework.ThreePhaseTestController;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.ConfigurationManager;
import org.osgi.test.cases.blueprint.services.ManagedConfigurationFactory;
import org.osgi.test.cases.blueprint.services.TestGoodService;
import org.osgi.test.cases.blueprint.services.TestGoodServiceSubclass;
import org.osgi.test.cases.blueprint.services.TestServiceDynamicsInterface;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class TestConfigAdminServiceSupport extends DefaultTestBundleControl {

    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName, Object propertyValue, Class type) {
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, type);
        //startEvents.addValidator(new PropertyMetadataValidator(compName, new TestProperty[] { new StringProperty(propertyName, type.getName()) }));
    }


    public void testPlaceholderPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_configuration_objects.jar",
                getWebServer()+"www/placeholder_property_injection.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

        this.addPropertyValidator(startEventSet, "compString", "string", "abc", String.class);
        this.addPropertyValidator(startEventSet, "compStringPart", "string", "abcxyz", String.class);
        this.addPropertyValidator(startEventSet, "compStringEmpty", "string", "", String.class);
        this.addPropertyValidator(startEventSet, "compStringEmptyPart", "string", "xyz", String.class);
        this.addPropertyValidator(startEventSet, "compStringNull", "string", "", String.class);
        this.addPropertyValidator(startEventSet, "compStringNullPart", "string", "xyzxyz", String.class);

        this.addPropertyValidator(startEventSet, "compWrapperBoolean", "boolean", Boolean.TRUE, Boolean.class);
        this.addPropertyValidator(startEventSet, "compWrapperByte", "byte", new Byte((byte)3), Byte.class);
        this.addPropertyValidator(startEventSet, "compWrapperCharacter", "char", new Character((char)4), Character.class);
        this.addPropertyValidator(startEventSet, "compWrapperInteger", "int", new Integer(5), Integer.class);
        this.addPropertyValidator(startEventSet, "compWrapperShort", "short", new Short((short)6), Short.class);
        this.addPropertyValidator(startEventSet, "compWrapperLong", "long", new Long(7), Long.class);
        this.addPropertyValidator(startEventSet, "compWrapperDouble", "double", new Double(8.0), Double.class);
        this.addPropertyValidator(startEventSet, "compWrapperFloat", "float", new Float(9.0), Float.class);

        controller.run();
    }

    public void testPlaceholderPrefixAndSuffix() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_configuration_objects.jar",
                getWebServer()+"www/placeholder_prefix_and_suffix.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

        this.addPropertyValidator(startEventSet, "compString1", "string", "abc", String.class);
        this.addPropertyValidator(startEventSet, "compString2", "string", "abc", String.class);

        controller.run();
    }

    public void testPlaceholderDefaultProperties() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_configuration_objects.jar",
                getWebServer()+"www/placeholder_default_properties.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

        this.addPropertyValidator(startEventSet, "compString1", "string", "def", String.class);
        this.addPropertyValidator(startEventSet, "compString2", "string", "def", String.class);
        this.addPropertyValidator(startEventSet, "compString3", "string", "def", String.class);

        controller.run();
    }

    public void testPlaceholderDeclarationScope() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_configuration_objects.jar",
                getWebServer()+"www/placeholder_declaration_scope.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

        this.addPropertyValidator(startEventSet, "compString", "string", "abc", String.class);

        controller.run();
    }

    // Section 5.7.2
    public void testServicePropertiesEvaluation() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_configuration_objects.jar",
                getWebServer()+"www/service_properties_evaluation.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

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

        controller.run();
    }

    private ConfigurationManager retrieveConfigurationManager(BundleContext bundleContext) throws Exception{
        ServiceReference ref = bundleContext.getServiceReference("org.osgi.test.cases.blueprint.services.ConfigurationManager");
        if (ref == null) {
            fail("No Configuration Manager Service found!");
        }
        return (ConfigurationManager)bundleContext.getService(ref);

    }

    public void testServicePropertiesAutoUpdate() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_configuration_objects.jar",
                getWebServer()+"www/service_properties_auto_update.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

        ConfigurationManager cm = this.retrieveConfigurationManager(getContext());

        Properties initialProps = new Properties();
        initialProps.put("str", "abc");
        Properties updatedProps = new Properties();
        updatedProps.put("str", "xyz");

        // when the service registered, then add a test event listener to update properties
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED",
                new Class[] {TestGoodServiceSubclass.class},
                null,
                new AdminPropertiesUpdater(cm, "org.osgi.test.cases.blueprint.components.cmsupport.config1", updatedProps)
        ));

        // Validate the service property is changed to "xyz"
        // IIUC, we need not do anything to ensure the value has been updated here,
        // because the TestPhase won't validate any result until all expected event received and processed.
        startEventSet.addValidator(new ServiceRegistrationValidator(TestGoodService.class, "compGoodService", null, initialProps));
        startEventSet.addValidator(new ServiceRegistrationValidator(TestGoodServiceSubclass.class, "compGoodServiceSubclass", null, updatedProps));

        controller.run();
    }

    // Section 5.7.3
    public void testComponentPropertiesEvaluation() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_configuration_objects.jar",
                getWebServer()+"www/component_properties_evaluation.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

        this.addPropertyValidator(startEventSet, "comp1", "string", "abc", String.class);
        this.addPropertyValidator(startEventSet, "comp1", "boolean", Boolean.TRUE, Boolean.class);
        this.addPropertyValidator(startEventSet, "comp1", "byte", new Byte((byte)3), Byte.class);
        this.addPropertyValidator(startEventSet, "comp1", "char", new Character((char)4), Character.class);
        this.addPropertyValidator(startEventSet, "comp1", "int", new Integer(5), Integer.class);
        this.addPropertyValidator(startEventSet, "comp1", "short", new Short((short)6), Short.class);
        this.addPropertyValidator(startEventSet, "comp1", "long", new Long(7), Long.class);
        this.addPropertyValidator(startEventSet, "comp1", "double", new Double(8.0), Double.class);
        this.addPropertyValidator(startEventSet, "comp1", "float", new Float(9.0), Float.class);

        //test the config-properties take precedence
        this.addPropertyValidator(startEventSet, "comp2", "string", "abc", String.class);
        this.addPropertyValidator(startEventSet, "comp2", "boolean", Boolean.TRUE, Boolean.class);
        this.addPropertyValidator(startEventSet, "comp2", "byte", new Byte((byte)3), Byte.class);
        this.addPropertyValidator(startEventSet, "comp2", "char", new Character((char)4), Character.class);
        this.addPropertyValidator(startEventSet, "comp2", "int", new Integer(5), Integer.class);
        this.addPropertyValidator(startEventSet, "comp2", "short", new Short((short)6), Short.class);
        this.addPropertyValidator(startEventSet, "comp2", "long", new Long(7), Long.class);
        this.addPropertyValidator(startEventSet, "comp2", "double", new Double(8.0), Double.class);
        this.addPropertyValidator(startEventSet, "comp2", "float", new Float(9.0), Float.class);

        controller.run();

    }

    public void testComponentPropertiesAutoUpdate() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_configuration_objects.jar",
                getWebServer()+"www/component_properties_auto_update.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

        ConfigurationManager cm = this.retrieveConfigurationManager(getContext());

        Hashtable initialTableItems = new Hashtable();
        initialTableItems.put("string", "abc");
        initialTableItems.put("boolean", "true");

        Properties updatedTableItems = new Properties();
        updatedTableItems.put("string", "xyz");
        updatedTableItems.put("boolean", "false");

        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        startEventSet.removeEvent(new BlueprintEvent("CREATED"));

        // Ok, when the CREATED event is triggered, update the configuration object
        startEventSet.addEvent(new BlueprintEvent("CREATED", null, new AdminPropertiesUpdater(cm, "org.osgi.test.cases.blueprint.components.cmsupport.config2", updatedTableItems)));

        //compStrategyDefault
        this.addPropertyValidator(startEventSet, "compStrategyDefault", "string", "abc", String.class);
        this.addPropertyValidator(startEventSet, "compStrategyDefault", "boolean", Boolean.TRUE, Boolean.class);

        //compStrategyNone
        this.addPropertyValidator(startEventSet, "compStrategyNone", "string", "abc", String.class);
        this.addPropertyValidator(startEventSet, "compStrategyNone", "boolean", Boolean.TRUE, Boolean.class);

        //compStrategyComponent
        startEventSet.addAssertion("compStrategyComponent", AssertionService.METHOD_CALLED);

        //compStrategyContainer
        this.addPropertyValidator(startEventSet, "compStrategyNone", "string", "xyz", String.class);
        this.addPropertyValidator(startEventSet, "compStrategyNone", "boolean", Boolean.FALSE, Boolean.class);

        controller.run();

    }


    //section 5.7.5
    public void testDirectAccessManagedService() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_configuration_objects.jar",
                getWebServer()+"www/direct_access_managed_service.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

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
                getWebServer()+"www/create_factory_configuration_objects.jar",
                getWebServer()+"www/direct_access_managed_service_factory.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

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
                getWebServer()+"www/create_factory_configuration_objects.jar",
                getWebServer()+"www/factory_attribute_autoexport_all.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", new Class[]{ManagedComponentInjection.class, BaseTestComponent.class, ComponentTestInfo.class}));
        controller.run();

    }


    public void testFactoryAttributeAutoExportClass() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_factory_configuration_objects.jar",
                getWebServer()+"www/factory_attribute_autoexport_class.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", new Class[]{ManagedComponentInjection.class, BaseTestComponent.class}));  //?
        controller.run();
    }

    public void testFactoryAttributeAutoExportInterfaces() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_factory_configuration_objects.jar",
                getWebServer()+"www/factory_attribute_autoexport_interfaces.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", new Class[]{ComponentTestInfo.class}));
        controller.run();
    }

    public void testFactoryNestedInterfaces() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_factory_configuration_objects.jar",
                getWebServer()+"www/factory_attribute_autoexport_class.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", new Class[]{ManagedComponentInjection.class, BaseTestComponent.class}));  //?
        controller.run();
    }

    public void testFactoryNestedRegListener() throws Exception {

    }

    public void testFactoryServicePropertiesEvaluation() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_factory_configuration_objects.jar",
                getWebServer()+"www/factory_service_properties_evaluation.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

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
                getWebServer()+"www/create_factory_configuration_objects.jar",
                getWebServer()+"www/factory_service_properties_auto_update.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

        // 1. change the configuration objects
        ConfigurationManager cm = this.retrieveConfigurationManager(getContext());
        Dictionary updatedProps = new Hashtable();
        updatedProps.put("string", "xyz");
        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        startEventSet.removeEvent(new BlueprintEvent("CREATED"));
        // Ok, when the CREATED event is triggered, update the configuration object
        startEventSet.addEvent(new BlueprintEvent("CREATED", null, new AdminPropertiesUpdater(cm, "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1", updatedProps)));
        
        // 2. validate Service Exist
        startEventSet.addValidator(new ServiceExistValidator("org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection", updatedProps));

        controller.run();

    }

    public void testFactoryComponentPropertiesEvaluation() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_factory_configuration_objects.jar",
                getWebServer()+"www/factory_component_properties_evaluation.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

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
    
    public void testFactoryComponentPropertiesAutoUpdate_ContainerManaged() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_factory_configuration_objects.jar",
                getWebServer()+"www/factory_component_properties_auto_update_cont.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

        // 1. Change the configuration objects
        ConfigurationManager cm = this.retrieveConfigurationManager(getContext());
        Dictionary updatedProps = new Hashtable();
        updatedProps.put("string", "xyz");
        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        startEventSet.removeEvent(new BlueprintEvent("CREATED"));
        // Ok, when the CREATED event is triggered, update the configuration object
        startEventSet.addEvent(new BlueprintEvent("CREATED", null, new AdminPropertiesUpdater(cm, "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1", updatedProps)));
        
        // 2. Validate component exist
        ServiceComponentExistValidator v = null;
        v = new ServiceComponentExistValidator("org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection");
        v.addValue("string", "xyz", String.class);
        startEventSet.addValidator(v);

        controller.run();

    }
    
    public void testFactoryComponentPropertiesAutoUpdate_ComponentManaged() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_factory_configuration_objects.jar",
                getWebServer()+"www/factory_component_properties_auto_update_comp.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

        // 1. Change the configuration objects
        ConfigurationManager cm = this.retrieveConfigurationManager(getContext());
        Dictionary updatedProps = new Hashtable();
        updatedProps.put("string", "xyz");
        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        startEventSet.removeEvent(new BlueprintEvent("CREATED"));
        // Ok, when the CREATED event is triggered, update the configuration object
        startEventSet.addEvent(new BlueprintEvent("CREATED", null, new AdminPropertiesUpdater(cm, "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1", updatedProps)));
        
        // 2. Assert component method called
        startEventSet.addAssertion("managedComp", AssertionService.METHOD_CALLED);

        controller.run();

    }
    
    public void testFactoryComponentPropertiesAutoUpdate_None() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_factory_configuration_objects.jar",
                getWebServer()+"www/factory_component_properties_auto_update_none.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

        // 1. Change the configuration objects
        ConfigurationManager cm = this.retrieveConfigurationManager(getContext());
        Dictionary updatedProps = new Hashtable();
        updatedProps.put("string", "xyz");
        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        startEventSet.removeEvent(new BlueprintEvent("CREATED"));
        // Ok, when the CREATED event is triggered, update the configuration object
        startEventSet.addEvent(new BlueprintEvent("CREATED", null, new AdminPropertiesUpdater(cm, "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1", updatedProps)));
        
        
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
                getWebServer()+"www/create_factory_configuration_objects.jar",
                getWebServer()+"www/factory_service_lifecycle.jar");

        //start
        MetadataEventSet startEventSet = controller.getStartEvents(1);
        //configfactory1 has 2 configuration objects, so receive 2 registered event
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", "org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection"));
        startEventSet.addEvent(new ServiceTestEvent("REGISTERED", "org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection"));

        //middle
        MetadataEventSet middleEventSet = controller.getMiddleEvents(1);
        ConfigurationManager cm = this.retrieveConfigurationManager(getContext());
        Dictionary newProps = new Hashtable();
        newProps.put("string", "cde");
        newProps.put("boolean", new Boolean(true));
        // add 1 configuration to the configfactory1
        middleEventSet.addInitializer(new AdminPropertiesAdder(cm, "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1", newProps));
        middleEventSet.addEvent(new ServiceTestEvent("REGISTERED", new String[] {"org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection"}, null,
                new AdminPropertiesRemover(cm, "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1")));
        
        // receive 3 unregistering event
        middleEventSet.addEvent(new ServiceTestEvent("UNREGISTERING", "org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection"));
        middleEventSet.addEvent(new ServiceTestEvent("UNREGISTERING", "org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection"));
        middleEventSet.addEvent(new ServiceTestEvent("UNREGISTERING", "org.osgi.test.cases.blueprint.components.cmsupport.ManagedComponentInjection"));

        controller.run();

    }

    public void testFactoryManagedComponentInitAndDestroy_ConfigDeleted() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_configuration_objects.jar",
                getWebServer()+"www/factory_managed_component_init_and_destroy.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);

        // init method called
        startEventSet.addPropertyAssertion("managedComp", AssertionService.METHOD_CALLED, "ManagedComponentInjection_onInit");


        ConfigurationManager cm = this.retrieveConfigurationManager(getContext());

        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        startEventSet.removeEvent(new BlueprintEvent("CREATED"));

        // Ok, when the CREATED event is triggered, update the configuration object
        startEventSet.addEvent(new BlueprintEvent("CREATED", null, new AdminPropertiesRemover(cm, "org.osgi.test.cases.blueprint.components.cmsupport.configfactory1")));

        // destroy method called
        startEventSet.addPropertyAssertion("managedComp", AssertionService.METHOD_CALLED, "ManagedComponentInjection_onDestroy_ConfigDeleted");

        controller.run();

    }

    public void testFactoryManagedComponentInitAndDestroy_BundleStopping() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/create_configuration_objects.jar",
                getWebServer()+"www/factory_managed_component_init_and_destroy.jar");

        MetadataEventSet startEventSet = controller.getStartEvents(1);
        // init method called
        startEventSet.addPropertyAssertion("managedComp", AssertionService.METHOD_CALLED, "ManagedComponentInjection_onInit");


        EventSet stopEventSet = controller.getStopEvents(1);
        // destroy method called
        stopEventSet.addPropertyAssertion("managedComp", AssertionService.METHOD_CALLED, "ManagedComponentInjection_onDestroy_BundleStopping");

        controller.run();

    }

}
