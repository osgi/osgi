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

import org.osgi.test.cases.blueprint.framework.ArgumentMetadataValidator;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.PropertyMetadataValidator;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.StringArgument;
import org.osgi.test.cases.blueprint.framework.TestArgument;
import org.osgi.test.cases.blueprint.framework.TestProperty;
import org.osgi.test.cases.blueprint.framework.TestStringValue;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class TestCustomConverter extends DefaultTestBundleControl {

    private void addConstructorValidator(MetadataEventSet startEvents, String id, Object value, Class type) {
        startEvents.validateComponentArgument(id, "arg1", value, type);
        startEvents.addValidator(new ArgumentMetadataValidator(id, new TestArgument[] {
        // all tests that can use this convenience method have a string argument as the first arg
                new StringArgument(String.class, id), new StringArgument(type) }));
    }

    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName,
            Object propertyValue, Class type) {
        // a "" string value
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, type);
        startEvents.addValidator(new PropertyMetadataValidator(compName, new TestProperty[] {
            new TestProperty(new TestStringValue(type), propertyName) }));
    }

    public void testCustomTypeInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer() + "www/converter_custom_type_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // constructor
        this.addConstructorValidator(startEvents, "compAsia_cnst", new org.osgi.test.cases.blueprint.components.injection.AsianRegionCode("CN+86"), org.osgi.test.cases.blueprint.components.injection.AsianRegionCode.class);
        this.addConstructorValidator(startEvents, "compImplicit_cnst", new org.osgi.test.cases.blueprint.components.injection.AsianRegionCode("CN+86"), org.osgi.test.cases.blueprint.components.injection.AsianRegionCode.class);
        // property
        this.addPropertyValidator(startEvents, "compAsia_prpt", "regionCode", new org.osgi.test.cases.blueprint.components.injection.AsianRegionCode("CN+86"), org.osgi.test.cases.blueprint.components.injection.AsianRegionCode.class);
        this.addPropertyValidator(startEvents, "compImplicit_prpt", "regionCode", new org.osgi.test.cases.blueprint.components.injection.AsianRegionCode("CN+86"), org.osgi.test.cases.blueprint.components.injection.AsianRegionCode.class);

        controller.run();
    }

    public void testMultiRegisteredConverter() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer() + "www/converter_multi_registered_converter.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // constructor
        this.addConstructorValidator(startEvents, "comp_cnst", new org.osgi.test.cases.blueprint.components.injection.RegionCode("CN+86"), org.osgi.test.cases.blueprint.components.injection.RegionCode.class);
        this.addConstructorValidator(startEvents, "compAsia_cnst", new org.osgi.test.cases.blueprint.components.injection.AsianRegionCode("CN+86"), org.osgi.test.cases.blueprint.components.injection.AsianRegionCode.class);

        controller.run();
    }

    public void testCustomBooleanConverter() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer() + "www/converter_custom_boolean_converter.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // constructor
        startEvents.validateComponentArgument("compCustomBoolean_cnst", "arg1", Boolean.TRUE, Boolean.class);
        startEvents.addValidator(new ArgumentMetadataValidator("compCustomBoolean_cnst", new TestArgument[] {
                new StringArgument(Boolean.class) }));
        // property
        this.addPropertyValidator(startEvents, "compCustomBoolean_prpt", "boolean", Boolean.FALSE, Boolean.class);

        controller.run();
    }

    public void testSubclassConverterInstead() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer() + "www/converter_subclass_converter_instead.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // constructor
        this.addConstructorValidator(startEvents, "comp_cnst", new org.osgi.test.cases.blueprint.components.injection.AsianRegionCode("CN+86"), org.osgi.test.cases.blueprint.components.injection.AsianRegionCode.class);
        // property
        this.addPropertyValidator(startEvents, "comp_prpt", "regionCode", new org.osgi.test.cases.blueprint.components.injection.AsianRegionCode("CN+86"), org.osgi.test.cases.blueprint.components.injection.AsianRegionCode.class);

        controller.run();
    }

    public void testConverterCalled() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer() + "www/converter_called.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        startEvents.addAssertion("compEuropean_cnst", AssertionService.METHOD_CALLED);

        controller.run();
    }

    public void testConversionServiceInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/conversion_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // this will validate that the property got converted to a Boolean properly
        this.addPropertyValidator(startEvents, "ConversionInjection", "conversion", Boolean.TRUE, null);

        controller.run();
    }

    public void testConversionServiceInjectionOverride() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/conversion_injection_override.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // this will validate that the property got converted to a Boolean properly
        // using the custom Boolean converter
        this.addPropertyValidator(startEvents, "ConversionInjection", "conversion", Boolean.TRUE, null);

        controller.run();
    }
}
