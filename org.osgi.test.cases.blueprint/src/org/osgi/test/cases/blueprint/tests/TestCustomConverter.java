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
import org.osgi.test.cases.blueprint.components.injection.RegionCode;
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
        startEvents.validateComponentArgument("compAsia_cnst", "arg2", new AsianRegionCode("CN+86"), AsianRegionCode.class);
        startEvents.addValidator(new ArgumentMetadataValidator("compAsia_cnst", new TestArgument[] {
                new StringArgument("compAsia_cnst"), 
                new TestArgument(new TestStringValue(AsianRegionCode.class, "CN+86")) }));

        startEvents.validateComponentArgument("compImplicit_cnst", "arg2", new AsianRegionCode("CN+86"), AsianRegionCode.class);
        startEvents.addValidator(new ArgumentMetadataValidator("compImplicit_cnst", new TestArgument[] {
                new StringArgument("compImplicit_cnst"), 
                new StringArgument("CN+86") }));
                
        // property
        startEvents.validateComponentProperty("compAsia_prpt", "regionCode", new AsianRegionCode("CN+86"), AsianRegionCode.class);
        startEvents.addValidator(new PropertyMetadataValidator("compAsia_prpt", new TestProperty[] {
            new TestProperty(new TestStringValue(AsianRegionCode.class, "CN+86"), "regionCode") }));
        
        startEvents.validateComponentProperty("compImplicit_prpt", "regionCode", new AsianRegionCode("CN+86"), AsianRegionCode.class);
        startEvents.addValidator(new PropertyMetadataValidator("compImplicit_prpt", new TestProperty[] {
            new TestProperty(new TestStringValue("CN+86"), "regionCode") }));
        
        controller.run();
    }

    public void testMultiRegisteredConverter() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer() + "www/converter_multi_registered_converter.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // constructor
        startEvents.validateComponentArgument("comp_cnst", "arg2", new RegionCode("CN+86"), RegionCode.class);
        startEvents.addValidator(new ArgumentMetadataValidator("comp_cnst", new TestArgument[] {
                new StringArgument("comp_cnst"), 
                new StringArgument(RegionCode.class, "CN+86") }));
        
        startEvents.validateComponentArgument("compAsia_cnst", "arg2", new AsianRegionCode("CN+86"), AsianRegionCode.class);
        startEvents.addValidator(new ArgumentMetadataValidator("compAsia_cnst", new TestArgument[] {
                new StringArgument("compAsia_cnst"), 
                new StringArgument(AsianRegionCode.class, "CN+86") }));

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

    /**
     * Same as the test above, but the type converter is an
     * imported service.
     *
     * @exception Exception
     */
    public void testServiceBooleanConverter() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer() + "www/converter_custom_service_converter.jar",
            getWebServer() + "www/converter_type_converter_service.jar");
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
        startEvents.validateComponentArgument("comp_cnst", "arg2", new AsianRegionCode("CN+86"), AsianRegionCode.class);
        startEvents.addValidator(new ArgumentMetadataValidator("comp_cnst", new TestArgument[] {
                new StringArgument("comp_cnst"), 
                new StringArgument(RegionCode.class, "CN+86") }));

        // property
        startEvents.validateComponentProperty("comp_prpt", "regionCode", new AsianRegionCode("CN+86"), AsianRegionCode.class);
        startEvents.addValidator(new PropertyMetadataValidator("comp_prpt", new TestProperty[] {
            new TestProperty(new TestStringValue(RegionCode.class, "CN+86"), "regionCode") }));

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
