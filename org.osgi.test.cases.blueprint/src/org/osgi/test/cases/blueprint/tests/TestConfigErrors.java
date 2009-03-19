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

import org.osgi.test.cases.blueprint.framework.EventSet;
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
}
