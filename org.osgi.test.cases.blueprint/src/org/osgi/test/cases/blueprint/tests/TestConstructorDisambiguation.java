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

import org.osgi.test.cases.blueprint.components.injection.AsianRegionCode;
import org.osgi.test.cases.blueprint.components.injection.EuropeanRegionCode;
import org.osgi.test.cases.blueprint.components.injection.RegionCode;
import org.osgi.test.cases.blueprint.framework.ArgumentMetadataValidator;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.PropertyMetadataValidator;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.StandardErrorTestController;
import org.osgi.test.cases.blueprint.framework.StringArgument;
import org.osgi.test.cases.blueprint.framework.StringProperty;
import org.osgi.test.cases.blueprint.framework.TestNullValue;
import org.osgi.test.cases.blueprint.framework.TestArgument;
import org.osgi.test.cases.blueprint.framework.TestProperty;
import org.osgi.test.cases.blueprint.framework.TestStringValue;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains tests specificcally for testing signature disambiguation
 * scenarios
 *
 * @version $Id$
 */
public class TestConstructorDisambiguation extends DefaultTestBundleControl {
    /*
     * Tests the error condition where there are not enough arguments to match a constructor
     */
    public void testConstructorShortArgs() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_constructor_cardinality_short.jar");
        controller.run();
    }

    /*
     * Tests the error condition where there are too many arguments to match a constructor
     */
    public void testConstructorLongArgs() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_constructor_cardinality_long.jar");
        controller.run();
    }

    /*
     * Tests the error condition where there are not enough arguments to match a static factory method
     */
    public void testStaticFactoryShortArgs() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_static_constructor_cardinality_short.jar");
        controller.run();
    }

    /*
     * Tests the error condition where there are too many arguments to match a static factory method
     */
    public void testStaticFactoryLongArgs() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_static_constructor_cardinality_long.jar");
        controller.run();
    }

    /*
     * Tests the error condition where there are not enough arguments to match an instance factory method
     */
    public void testInstanceFactoryShortArgs() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_instance_constructor_cardinality_short.jar");
        controller.run();
    }

    /*
     * Tests the error condition where there are too many arguments to match an instance factory method
     */
    public void testInstanceFactoryLongArgs() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_instance_constructor_cardinality_long.jar");
        controller.run();
    }


    /**
     * Add a validator for a set of disambiguation tests.  This
     * will add the validator for the direct, static factory,
     * and instance factory variations of a test.
     *
     * @param startEvents
     *               The event set we're working with
     *
     * @param id     The unique identifying portion of the bean id.
     * @param value  The expected injected value.
     * @param type   The expected value type that identifies the constructor
     *               method used.
     */
    private void addConstructorValueValidator(MetadataEventSet startEvents, String id, Object value, Class type) {
        // each test has 3 different construction variations
        startEvents.validateComponentArgument(id + "Arg", "arg1", value, type);
        startEvents.validateComponentArgument(id + "ArgStatic", "arg1", value, type);
        startEvents.validateComponentArgument(id + "ArgInstance", "arg1", value, type);
    }


    /*
     * Tests the signature disambiguation using the assignability tests criteria (no type conversion)
     */
    public void testStringTypeConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/constructor_type_assignability.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        addConstructorValueValidator(startEvents, "string", "XYZ", String.class);
        addConstructorValueValidator(startEvents, "int", new Integer(1), Integer.TYPE);
        addConstructorValueValidator(startEvents, "integer", new Integer(1), Integer.TYPE);
        addConstructorValueValidator(startEvents, "regionCode", new RegionCode("RC+40"), RegionCode.class);
        addConstructorValueValidator(startEvents, "europeanRegionCode", new EuropeanRegionCode("UK+32"), RegionCode.class);
        addConstructorValueValidator(startEvents, "asianRegionCode", new AsianRegionCode("CN+86"), AsianRegionCode.class);
        addConstructorValueValidator(startEvents, "asianRegionCodeSuper", new AsianRegionCode("CN+86"), RegionCode.class);
        addConstructorValueValidator(startEvents, "primToWrapper", Boolean.TRUE, Boolean.class);
        addConstructorValueValidator(startEvents, "wrapperToWrapper", Boolean.TRUE, Boolean.class);
        addConstructorValueValidator(startEvents, "wrapperToPrim", Boolean.TRUE, Boolean.TYPE);
        addConstructorValueValidator(startEvents, "primToPrim", Boolean.TRUE, Boolean.TYPE);

        controller.run();
    }


    /*
     * Tests for ambiguities where the source is a wrapper class and wrapper and primitive both exist
     */
    public void testWrapperPrimitiveAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_wrapper_primitive_ambiguity.jar");
        controller.run();
    }

    public void testInstanceWrapperPrimitiveAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_instance_wrapper_primitive_ambiguity.jar");
        controller.run();
    }

    public void testStaticWrapperPrimitiveAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_static_wrapper_primitive_ambiguity.jar");
        controller.run();
    }


    /*
     * Tests for ambiguities where the source is a primitive and wrapper and primitive both exist
     */
    public void testPrimitiveWrapperAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_primitive_wrapper_ambiguity.jar");
        controller.run();
    }

    public void testInstancePrimitiveWrapperAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_instance_primitive_wrapper_ambiguity.jar");
        controller.run();
    }

    public void testStaticPrimitiveWrapperAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_static_primitive_wrapper_ambiguity.jar");
        controller.run();
    }


    /*
     * Tests for ambiguities where there is an assignability relationship between the source and multiple targets
     */
    public void testAssignabilityAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_constructor_assignability_ambiguity.jar");
        controller.run();
    }

    public void testInstanceAssignabilityAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_instance_constructor_assignability_ambiguity.jar");
        controller.run();
    }

    public void testStaticAssignabilityAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_static_constructor_assignability_ambiguity.jar");
        controller.run();
    }


    /*
     * Tests for ambiguities where there is a converison compatibility relationship between the source and multiple targets
     */
    public void testStringConversionAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_converted_primitive_wrapper_ambiguity.jar");
        controller.run();
    }

    public void testInstanceStringConversionAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_instance_converted_primitive_wrapper_ambiguity.jar");
        controller.run();
    }

    public void testStaticStringConversionAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_static_converted_primitive_wrapper_ambiguity.jar");
        controller.run();
    }


    /*
     * Tests for ambiguities where there is a converison compatibility relationship between the source and multiple targets
     */
    public void testCollectionConversionAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_converted_collection_ambiguity.jar");
        controller.run();
    }

    public void testInstanceCollectionConversionAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_instance_converted_collection_ambiguity.jar");
        controller.run();
    }

    public void testStaticCollectionConversionAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_static_converted_collection_ambiguity.jar");
        controller.run();
    }


    /*
     * Tests for ambiguities where there is a converison compatibility relationship between the source and multiple targets
     */
    public void testMapConversionAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_converted_map_ambiguity.jar");
        controller.run();
    }

    public void testInstanceMapConversionAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_instance_converted_map_ambiguity.jar");
        controller.run();
    }

    public void testStaticMapConversionAmbiguity() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_static_converted_map_ambiguity.jar");
        controller.run();
    }
}
