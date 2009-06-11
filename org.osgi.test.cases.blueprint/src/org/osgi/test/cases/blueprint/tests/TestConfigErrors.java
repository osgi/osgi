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

import org.osgi.test.cases.blueprint.framework.EventSet;
import org.osgi.test.cases.blueprint.framework.GetComponentExceptionValidator;
import org.osgi.test.cases.blueprint.framework.StandardErrorTestController;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains tests related to ModuleContext creation errors.
 *
 * @author left
 * @version $Revision$
 */
public class TestConfigErrors extends DefaultTestBundleControl {
    /*
     * Tests an explicitly specified config file on the header that does not exist.
     */
    public void testNoConfigFile() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_no_config_file.jar");
        controller.run();
    }

    /*
     * Tests multiple explicitly specified config files, but one is missing
     */
    public void testMissingConfigFile() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_missing_config_file.jar");
        controller.run();
    }

    /*
     * Tests a duplicate component name in the config file
     */
    public void testDuplicateComponentName() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_duplicate_name.jar");
        controller.run();
    }

    /*
     * Tests a config file with a bad class name.
     */
    public void testMissingComponentClass() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_missing_component_class.jar");
        controller.run();
    }

    /*
     * Tests a config file where no class name has been specified for a component.
     */
    public void testNoComponentClass() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_no_component_class.jar");
        controller.run();
    }

    /*
     * Tests a config file with a bad factory class name.
     */
    public void testStaticFactoryMissingClass() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_static_factory_missing_class.jar");
        controller.run();
    }

    /*
     * Tests a config file with a bad factory method name.
     */
    public void testStaticFactoryMissingMethod() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_static_factory_missing_method.jar");
        controller.run();
    }

    /*
     * Tests a config file with a factory that does not have public access
     */
    public void testStaticFactoryNonPublicClass() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_static_factory_non_public_class.jar");
        controller.run();
    }

    /*
     * Tests a config file with a factory that does not have public access
     */
    public void testStaticFactoryNonPublicMethod() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_static_factory_non_public_class.jar");
        controller.run();
    }

    /*
     * Tests a config file with a non-static factory method name.
     */
    public void testStaticFactoryNonStaticMethod() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_static_factory_nonstatic_method.jar");
        controller.run();
    }

    /*
     * Tests a config file where no class name has been specified for a component.
     */
    public void testStaticFactoryNoClass() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_static_factory_no_class.jar");
        controller.run();
    }

    /*
     * Tests a config file with a bad method name.
     */
    public void testInstanceFactoryMissingMethod() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_instance_factory_missing_method.jar");
        controller.run();
    }

    /*
     * Tests a config file where a factory component has been specified, but no method.
     */
    public void testInstanceFactoryNoMethod() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_instance_factory_no_method.jar");
        controller.run();
    }

    /*
     * Tests a config file where a factory component has been specified, but it does not exist.
     */
    public void testStaticFactoryNoComponent() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_instance_factory_no_component.jar");
        controller.run();
    }

    /*
     * Tests an exception thrown from a constructor call
     */
    public void testConstructorException() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_constructor_exception.jar");
        controller.run();
    }

    /*
     * Tests an exception thrown from a constructor call triggered by a getComponent() call
     */
    public void testLazyConstructorException() throws Exception {
        // this should just be the standard error set
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/error_lazy_constructor_exception.jar");

        // We should get the error triggered when we request the component
        EventSet startEvents = controller.getStartEvents();
        startEvents.addValidator(new GetComponentExceptionValidator("ConstructorException"));
        controller.run();
    }

    /*
     * Tests an exception thrown from an instance factory call
     */
    public void testInstanceFactoryException() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_instance_factory_exception.jar");
        controller.run();
    }

    /*
     * Tests an exception thrown from a static factory call
     */
    public void testStaticFactoryException() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_static_factory_exception.jar");
        controller.run();
    }

    /*
     * Tests an exception thrown from a property injection call
     */
    public void testPropertyException() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_property_exception.jar");
        controller.run();
    }

    /*
     * Tests an exception thrown from an init-method call
     */
    public void testInitMethodException() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_init_exception.jar");
        controller.run();
    }

    /**
     * Tests an exception thrown from a destroy-method call.  This is not completely
     * an error test, as this needs to show that an exception from one destroy does not
     * terminate the shutdown chain.
     */
    public void testDestroyMethodException() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/error_destroy_exception.jar");

        // all of the interesting bits happen during the shutdown phase
        EventSet stopEvents = controller.getStopEvents();
        // we still call this, and it will let us know it got there
        stopEvents.addAssertion("DestroyException", AssertionService.COMPONENT_DESTROY_METHOD);
        // this one will happen after the exception
        stopEvents.addAssertion("GoodDestroy", AssertionService.COMPONENT_DESTROY_METHOD);
        controller.run();
    }

    /**
     * Tests constructor argument mismatch, where the constructor has few
     * arguments than specified on the component.
     */
    public void testNoConstructorMatch() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_no_constructor_match.jar");
        controller.run();
    }

    /**
     * Tests constructor type argument mismatch.  A typed value does not match
     * the constructor requirements.
     */
    public void testConstructorTypeMismatch() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_constructor_type_mismatch.jar");
        controller.run();
    }

    /**
     * Tests constructor type argument where the specified class cannot be found.
     */
    public void testConstructorInvalidType() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_constructor_invalid_type.jar");
        controller.run();
    }

    /**
     * Using a non-public constructor for a <bean> component.
     */
    public void testConstructorNonPrivate() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_constructor_non_public.jar");
        controller.run();
    }

    /**
     * Tests constructor type argument where there is a conversion error.
     */
    public void testConversionError() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_conversion_error.jar");
        controller.run();
    }

    /**
     * Tests injecting a type that's incompatible with the target.
     */
    public void testIncompatibleType() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_incompatible_type.jar");
        controller.run();
    }

    /**
     * Tests an invalid type processed by the ConversionService.
     */
    public void testConversionServiceError() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_conversion_service.jar");
        controller.run();
    }

    /**
     * Tests an invalid type processed by the ConversionService using a type Converter.
     */
    public void testConversionServiceOverrideError() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_conversion_service_override.jar");
        controller.run();
    }

    /**
     * Tests an invalid type processed by a type Converter.
     */
    public void testTypeConverterError() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_type_converter.jar");
        controller.run();
    }

    /**
     * Tests a reference to an undefined component
     */
    public void testMissingReferenceError() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_missing_reference.jar");
        controller.run();
    }

    /**
     * Tests an <idref> to an undefined component
     */
    public void testMissingIdrefError() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_missing_idref_reference.jar");
        controller.run();
    }

    /**
     * Tests a reference to an undefined component
     */
    public void testCircularReferenceError() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_circular_reference.jar");
        controller.run();
    }

    /**
     * Tests injecting a null value into a primitive type
     */
    public void testPrimitiveNull() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_primitive_null.jar");
        controller.run();
    }

    /**
     * Tests the target property does not exist
     */
    public void testMissingProperty() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_missing_property.jar");
        controller.run();
    }

    /**
     * Tests the checking for multple setters for a name
     */
    public void testAmbiguousProperty() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_ambiguous_property.jar");
        controller.run();
    }

    /**
     * Tests the checking for protected properties
     */
    public void testProtectedProperty() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_protected_property.jar");
        controller.run();
    }

    /**
     * Tests the checking for private properties
     */
    public void testPrivateProperty() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_private_property.jar");
        controller.run();
    }

    /**
     * Tests a bad bean pattern, with the getter and setter not in agreement.
     */
    public void testBadProperty() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_bad_property.jar");
        controller.run();
    }

    /**
     * Tests a non-complete set of constructor indexes
     */
    public void testSkippedIndex() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_constructor_skipped_index.jar");
        controller.run();
    }

    /**
     * Tests a duplicate index in the constructor argument set
     */
    public void testDuplicateIndex() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_constructor_duplicate_index.jar");
        controller.run();
    }

    /**
     * Tests index positions specified on only some of the constructor arguments
     */
    public void testPartialIndex() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_constructor_partial_index.jar");
        controller.run();
    }

    /**
     * Badly named init method
     */
    public void testInitNoMethod() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_init_no_method.jar");
        controller.run();
    }

    /**
     * Badly named destroy method
     */
    public void testDestroyNoMethod() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_destroy_no_method.jar");
        controller.run();
    }

    /**
     * Badly named init method
     */
    public void testInitBadMethod() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_init_bad_method.jar");
        controller.run();
    }

    /**
     * Badly named destroy method
     */
    public void testDestroyBadMethod() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_destroy_bad_method.jar");
        controller.run();
    }

    /**
     * Bad component reference on service export
     */
    public void testServiceBadComponent() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_bad_component.jar");
        controller.run();
    }

    /**
     * Bad interface name on service export
     */
    public void testServiceBadInterface() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_bad_interface.jar");
        controller.run();
    }

    /**
     * No component for a service export
     */
    public void testServiceNoComponent() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_no_component.jar");
        controller.run();
    }

    /**
     * No interface for a service export
     */
    public void testServiceNoInterface() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_no_interface.jar");
        controller.run();
    }

    /**
     * Wrong interface for component on a service export
     */
    public void testServiceWrongInterface() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_wrong_interface.jar");
        controller.run();
    }

    /**
     * Wrong both interface attribute and interfaces element specified
     */
    public void testServiceDupInterface() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_dup_interface.jar");
        controller.run();
    }

    /**
     * Bad component name on listener
     */
    public void testServiceListenerBadComponent() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_listener_bad_component.jar");
        controller.run();
    }

    /**
     * Bad register method signature
     */
    public void testServiceListenerBadRegister() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_listener_bad_register.jar");
        controller.run();
    }

    /**
     * Bad unregister method signature
     */
    public void testServiceListenerBadUnregister() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_listener_bad_unregister.jar");
        controller.run();
    }

    /**
     * No listener component specified
     */
    public void testServiceListenerNoComponent() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_listener_no_component.jar");
        controller.run();
    }

    /**
     * No listener methods specified
     */
    public void testServiceListenerNoMethods() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_listener_no_methods.jar");
        controller.run();
    }

    /**
     * No register method found
     */
    public void testServiceListenerNoRegister() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_listener_no_register.jar");
        controller.run();
    }

    /**
     * No unregister method found
     */
    public void testServiceListenerNoUnregister() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_listener_no_unregister.jar");
        controller.run();
    }

    /**
     * Bad component reference in a <component> depends-on
     */
    public void testComponentBadDependson() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_component_bad_dependson.jar");
        controller.run();
    }

    /**
     * Bad component reference in a <service> depends-on
     */
    public void testServiceBadDependson() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_bad_dependson.jar");
        controller.run();
    }

    /**
     * Missing interface spec on <reference>
     */
    public void testReferenceNoInterface() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_reference_no_interface.jar");
        controller.run();
    }

    /**
     * Bad interface class name specified on <reference>
     */
    public void testReferenceBadInterface() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_reference_bad_interface.jar");
        controller.run();
    }

    /**
     * Missing interface spec on <ref-list>
     */
    public void testRefListNoInterface() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_ref_list_no_interface.jar");
        controller.run();
    }

    /**
     * Bad interface class name specified on <ref-list>
     */
    public void testRefListBadInterface() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_ref_list_bad_interface.jar");
        controller.run();
    }

    /**
     * Bad component name on listener
     */
    public void testReferenceListenerBadComponent() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_reference_listener_bad_component.jar");
        controller.run();
    }

    /**
     * Bad bind method signature
     */
    public void testReferenceListenerBadbind() throws Exception {
        // this should just be the standard error set,
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_reference_listener_bad_bind.jar");
        // this allows the references to be resolvable to trigger the listener errors
        controller.addSetupBundle(getWebServer()+"www/ServiceOne_export.jar");
        controller.run();
    }

    /**
     * Bad unbind method signature
     */
    public void testReferenceListenerBadUnbind() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_reference_listener_bad_unbind.jar");
        // this allows the references to be resolvable to trigger the listener errors
        controller.addSetupBundle(getWebServer()+"www/ServiceOne_export.jar");
        controller.run();
    }

    /**
     * No listener component specified
     */
    public void testReferenceListenerNoComponent() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_reference_listener_no_component.jar");
        controller.run();
    }

    /**
     * No listener methods specified
     */
    public void testReferenceListenerNoMethods() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_reference_listener_no_methods.jar");
        controller.run();
    }

    /**
     * No bind method found
     */
    public void testReferenceListenerNobind() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_reference_listener_no_bind.jar");
        // this allows the references to be resolvable to trigger the listener errors
        controller.addSetupBundle(getWebServer()+"www/ServiceOne_export.jar");
        controller.run();
    }

    /**
     * No unbind method found
     */
    public void testReferenceListenerNoUnbind() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_reference_listener_no_unbind.jar");
        // this allows the references to be resolvable to trigger the listener errors
        controller.addSetupBundle(getWebServer()+"www/ServiceOne_export.jar");
        controller.run();
    }

    /**
     * Missing components in a map key-ref element
     */
    public void testMapBadKeyRef() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_map_bad_key_ref.jar");
        controller.run();
    }

    /**
     * Missing components in a map value-ref element
     */
    public void testMapBadValueRef() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_map_bad_value_ref.jar");
        controller.run();
    }

    /**
     * Missing class for a value-type
     */
    public void testMapBadValueType() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_map_bad_value_type.jar");
        controller.run();
    }

    /**
     * Missing class for a key-type
     */
    public void testMapBadKeyType() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_map_bad_key_type.jar");
        controller.run();
    }

    /**
     * Duplicate ref values specified
     */
    public void testMapDupValueRef() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_map_dup_value_ref.jar");
        controller.run();
    }

    /**
     * Duplicate key ref specified
     */
    public void testMapDupKeyRef() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_map_dup_key_ref.jar");
        controller.run();
    }

    /**
     * Duplicate key forms specified
     */
    public void testMapDupKey() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_map_dup_key.jar");
        controller.run();
    }

    /**
     * Duplicate value forms specified
     */
    public void testMapDupValue() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_map_dup_value.jar");
        controller.run();
    }

    /**
     * Missing components in a list value-ref element
     */
    public void testListBadValueRef() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_list_bad_value_ref.jar");
        controller.run();
    }

    /**
     * Missing components in a set value-ref element
     */
    public void testSetBadValueRef() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_set_bad_value_ref.jar");
        controller.run();
    }

    /**
     * type converter doesn't implement correct interface
     */
    public void testConverterWrongType() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_converter_wrong_type.jar");
        controller.run();
    }

    /**
     * ambiguous constructor argument
     */
    public void testAmbiguousConstructor() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_ambiguous_constructor.jar");
        controller.run();
    }

    /**
     * ambiguous factory constructor argument
     */
    public void testAmbiguousFactoryConstructor() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_ambiguous_factory_constructor.jar");
        controller.run();
    }

    /**
     * ambiguous static factory constructor argument
     */
    public void testAmbiguousStaticFactoryConstructor() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_ambiguous_static_factory_constructor.jar");
        controller.run();
    }

    /**
     * id specified on inner bean
     */
    public void testInnerBeanId() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_inner_bean_id.jar");
        controller.run();
    }

    /**
     * initialization specified on inner bean
     */
    public void testInnerBeanInitialization() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_inner_bean_lazy.jar");
        controller.run();
    }

    /**
     * destroy-method specified on inner bean
     */
    public void testInnerBeanDestroy() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_inner_bean_destroy.jar");
        controller.run();
    }

    /**
     * id attribute on an inline service
     */
    public void testInlineServiceId() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_inline_service_id.jar");
        controller.run();
    }

    /**
     * id attribute on an inline reference
     */
    public void testInlineReferenceId() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_inline_reference_id.jar");
        controller.addSetupBundle(getWebServer()+"www/ServiceOne_export.jar");
        controller.run();
    }

    /**
     * id attribute on an inline ref-list reference
     */
    public void testInlineRefListId() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_inline_ref_list_id.jar");
        controller.addSetupBundle(getWebServer()+"www/ServiceOne_export.jar");
        controller.run();
    }

    /**
     * bad component name in a depends-on construct
     */
    public void testReferenceBadDependsOn() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_reference_bad_dependson.jar");
        controller.addSetupBundle(getWebServer()+"www/ServiceOne_export.jar");
        controller.run();
    }

    /**
     * bad component name in a depends-on construct
     */
    public void testRefListBadDependsOn() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_ref_list_bad_dependson.jar");
        controller.addSetupBundle(getWebServer()+"www/managed_service_export.jar");
        controller.run();
    }


    /**
     * test a wild card specification on a Blueprint-Bundle header is an error
     */
    public void testBlueprintBundleWildcard() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_config_wildcard.jar");
        controller.run();
    }


    /**
     * test a <service> element as the target of a service export
     */
    public void testServiceServiceTarget() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_service_target.jar");
        controller.run();
    }


    /**
     * test a <ref-list> element as the target of a service export
     */
    public void testServiceRefListTarget() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_service_ref_list_target.jar");
        controller.run();
    }


    /**
     * test a <service> element as the target of a listener element
     */
    public void testListenerServiceTarget() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_listener_service_target.jar");
        controller.run();
    }


    /**
     * test a <ref-list> element as the target of a listener element
     */
    public void testListenerRefListTarget() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_listener_ref_list_target.jar");
        controller.run();
    }


    /**
     * test a <service> element as the target of a registration listener element
     */
    public void testRegistrationListenerServiceTarget() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_registration_listener_service_target.jar");
        controller.run();
    }


    /**
     * test a <ref-list> element as the target of a registration-listener element
     */
    public void testRegistrationListenerRefListTarget() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_registration_listener_ref_list_target.jar");
        controller.run();
    }


    /**
     * attempting to import a concrete class as a service reference
     */
    public void testConcreteClassImport() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/GoodService_import.jar");
        controller.addSetupBundle(getWebServer()+"www/GoodService_export.jar");
        controller.run();
    }
}
