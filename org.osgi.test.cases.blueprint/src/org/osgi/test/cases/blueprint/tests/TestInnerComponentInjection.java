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

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.test.cases.blueprint.components.injection.ComponentInjection;
import org.osgi.test.cases.blueprint.framework.ComponentAssertion;
import org.osgi.test.cases.blueprint.framework.ConstructorMetadataValidator;
import org.osgi.test.cases.blueprint.framework.InnerComponentValidator;
import org.osgi.test.cases.blueprint.framework.LocalComponent;
import org.osgi.test.cases.blueprint.framework.MapValueEntry;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.PropertyMetadataValidator;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.StringParameter;
import org.osgi.test.cases.blueprint.framework.StringProperty;
import org.osgi.test.cases.blueprint.framework.TestComponentValue;
import org.osgi.test.cases.blueprint.framework.TestListValue;
import org.osgi.test.cases.blueprint.framework.TestMapValue;
import org.osgi.test.cases.blueprint.framework.TestParameter;
import org.osgi.test.cases.blueprint.framework.TestProperty;
import org.osgi.test.cases.blueprint.framework.TestSetValue;
import org.osgi.test.cases.blueprint.framework.TestValue;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.StringValueDescriptor;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class TestInnerComponentInjection extends DefaultTestBundleControl {

    // Basic Test
    public void testBasic() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_basic.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        //inner component should be created
        startEvents.addAssertion("compInnerOne", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("compInnerOne_1", AssertionService.COMPONENT_CREATED);

        //inner component should not be created
        startEvents.addFailureEvent(new ComponentAssertion("compInnerLazyOuter", AssertionService.COMPONENT_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("compInnerLazyOuter_1", AssertionService.COMPONENT_CREATED));

        //multi inner components
        startEvents.addAssertion("compInnerTwo", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("compInnerTwo_1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("compInnerTwo_2", AssertionService.COMPONENT_CREATED);

        TestComponentValue testComponentValue = new TestComponentValue(new LocalComponent(ComponentTestInfo.class));
        //Ignore ID
        startEvents.addValidator(new ConstructorMetadataValidator(
                "compInnerIgnoreID",
                new TestParameter[] {
                    new StringParameter("compInnerIgnoreID"),
                    new TestParameter(testComponentValue) }
        ));
        //Ignore scope
        startEvents.addValidator(new ConstructorMetadataValidator(
                "compInnerIgnoreScope",
                new TestParameter[] {
                    new StringParameter("compInnerIgnoreScope"),
                    new TestParameter(testComponentValue) }
        ));

        controller.run();
    }



    // Constructor Test
    private void addConstructorMetadataValidator(MetadataEventSet startEvents, String componentId, Class innerComponentClass, Class innerArgType) {
        // metadata test
        TestComponentValue testComponentValue = new TestComponentValue(new LocalComponent(innerComponentClass,
                new TestParameter[] { new StringParameter(innerArgType) }, null));

        startEvents.addValidator(new ConstructorMetadataValidator(componentId, new TestParameter[] { new TestParameter(
                testComponentValue) }));
    }

    private void addConstructorArgValueValidator(MetadataEventSet startEvents, String outterComponentId, Class innerComponentClassName, Object innerArgValue, Class innerArgType ){
        // value test
        Dictionary innerComponentProps = new Hashtable();
        StringValueDescriptor svd = new StringValueDescriptor("arg1", innerArgValue, innerArgType); // the "arg1" is "inner" arg1
        innerComponentProps.put("arg1", svd); // the "arg1" is "inner" arg1

        startEvents.addValidator(new InnerComponentValidator(outterComponentId, "arg1", innerComponentClassName, innerComponentProps));  //the "arg1" is "outer" arg1

    }

    private void addConstructorTestItem(MetadataEventSet startEvents) throws Exception {


        this.addConstructorMetadataValidator(startEvents, "compInnerString", ComponentInjection.class, String.class);
        this.addConstructorArgValueValidator(startEvents, "compInnerString", ComponentTestInfo.class, new String("ABC"), String.class);

        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveBoolean", ComponentInjection.class, Boolean.TYPE);
        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveBoolean", ComponentTestInfo.class, Boolean.TRUE, Boolean.TYPE);
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedBoolean", ComponentInjection.class, Boolean.class);
        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedBoolean", ComponentTestInfo.class, Boolean.FALSE, Boolean.class);

        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveByte", ComponentInjection.class, Byte.TYPE);
        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveByte", ComponentTestInfo.class, new Byte(Byte.MIN_VALUE), Byte.TYPE);
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedByte", ComponentInjection.class, Byte.class);
        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedByte", ComponentTestInfo.class, new Byte(Byte.MAX_VALUE), Byte.class);

        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveDouble", ComponentInjection.class, Double.TYPE);
        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveDouble", ComponentTestInfo.class, new Double(Double.MIN_VALUE), Double.TYPE);
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedDouble", ComponentInjection.class, Double.class);
        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedDouble", ComponentTestInfo.class, new Double(Double.MAX_VALUE), Double.class);

        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveFloat", ComponentInjection.class, Float.TYPE);
        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveFloat", ComponentTestInfo.class, new Float(Float.MIN_VALUE), Float.TYPE);
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedFloat", ComponentInjection.class, Float.class);
        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedFloat", ComponentTestInfo.class, new Float(Float.MAX_VALUE), Float.class);

        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveInteger", ComponentInjection.class, Integer.TYPE);
        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveInteger", ComponentTestInfo.class, new Integer(Integer.MIN_VALUE), Integer.TYPE);
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedInteger", ComponentInjection.class, Integer.class);
        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedInteger", ComponentTestInfo.class, new Integer(Integer.MAX_VALUE), Integer.class);

        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveCharacter", ComponentInjection.class, Character.TYPE);
        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveCharacter", ComponentTestInfo.class, new Character(Character.MIN_VALUE), Character.TYPE);
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedCharacter", ComponentInjection.class, Character.class);
        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedCharacter", ComponentTestInfo.class, new Character(Character.MAX_VALUE), Character.class);

        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveShort", ComponentInjection.class, Short.TYPE);
        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveShort", ComponentTestInfo.class, new Short(Short.MIN_VALUE), Short.TYPE);
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedShort", ComponentInjection.class, Short.class);
        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedShort", ComponentTestInfo.class, new Short(Short.MAX_VALUE), Short.class);

        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveLong", ComponentInjection.class, Long.TYPE);
        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveLong", ComponentTestInfo.class, new Long(Long.MIN_VALUE), Long.TYPE);
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedLong", ComponentInjection.class, Long.class);
        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedLong", ComponentTestInfo.class, new Long(Long.MAX_VALUE), Long.class);
    }

    public void testConstructorInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addConstructorTestItem(startEvents);

        controller.run();
    }

    public void testInstanceFactoryConstructorInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addConstructorTestItem(startEvents);

        controller.run();
    }

    public void testStaticFactoryConstructorInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_static_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addConstructorTestItem(startEvents);

        controller.run();
    }

    // Property Test

    private void addPropertyValidator(MetadataEventSet startEvents, String componentId, Class innerComponentClass,
            Class innerProType, String innerProName) {

        // metadata test
        TestComponentValue testComponentValue = new TestComponentValue(new LocalComponent(innerComponentClass, null,
                new TestProperty[] { new StringProperty(innerProName, innerProType) }));
        startEvents.addValidator(new PropertyMetadataValidator(componentId, new TestProperty[] { new TestProperty(
                testComponentValue, "innerComponent") }));
    }

    private void addPropertyTestItem(MetadataEventSet startEvents) throws Exception {
        this.addPropertyValidator(startEvents, "compInnerString", ComponentInjection.class, null, "string");
        this.addPropertyValidator(startEvents, "compInnerPrimitiveBoolean", ComponentInjection.class, Boolean.TYPE, "primBoolean");
        this.addPropertyValidator(startEvents, "compInnerWrapperedBoolean", ComponentInjection.class, Boolean.class, "boolean");
        this.addPropertyValidator(startEvents, "compInnerPrimitiveByte", ComponentInjection.class, Byte.TYPE, "primByte");
        this.addPropertyValidator(startEvents, "compInnerWrapperedByte", ComponentInjection.class, Byte.class, "byte");
        this.addPropertyValidator(startEvents, "compInnerPrimitiveDouble", ComponentInjection.class, Double.TYPE, "primDouble");
        this.addPropertyValidator(startEvents, "compInnerWrapperedDouble", ComponentInjection.class, Double.class, "double");
        this.addPropertyValidator(startEvents, "compInnerPrimitiveFloat", ComponentInjection.class, Float.TYPE, "primFloat");
        this.addPropertyValidator(startEvents, "compInnerWrapperedFloat", ComponentInjection.class, Float.class, "float");
        this.addPropertyValidator(startEvents, "compInnerPrimitiveInteger", ComponentInjection.class, Integer.TYPE, "primInteger");
        this.addPropertyValidator(startEvents, "compInnerWrapperedInteger", ComponentInjection.class, Integer.class, "integer");
        this.addPropertyValidator(startEvents, "compInnerPrimitiveCharacter", ComponentInjection.class, Character.TYPE, "primCharacter");
        this.addPropertyValidator(startEvents, "compInnerWrapperedCharacter", ComponentInjection.class, Character.class, "character");
        this.addPropertyValidator(startEvents, "compInnerPrimitiveShort", ComponentInjection.class, Short.TYPE, "primShort");
        this.addPropertyValidator(startEvents, "compInnerWrapperedShort", ComponentInjection.class, Short.class, "short");
        this.addPropertyValidator(startEvents, "compInnerPrimitiveLong", ComponentInjection.class, Long.TYPE, "primLong");
        this.addPropertyValidator(startEvents, "compInnerWrapperedLong", ComponentInjection.class, Long.class, "long");
    }

    public void testPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropertyTestItem(startEvents);

        controller.run();
    }

    public void testInstanceFactoryPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropertyTestItem(startEvents);

        controller.run();
    }

    public void testStaticFactoryPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_static_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropertyTestItem(startEvents);

        controller.run();
    }

    // Collection Test
    public void testCollectionInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_collection_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        TestComponentValue testComponentValue = new TestComponentValue(new LocalComponent(ComponentTestInfo.class,
                new TestParameter[] { new StringParameter() }, null));

        // List
        startEvents.addValidator(new ConstructorMetadataValidator("compInnerList", new TestParameter[] { new TestParameter(
                new TestListValue(new TestValue[] { testComponentValue, testComponentValue })) }));

        // Set
        startEvents.addValidator(new ConstructorMetadataValidator("compInnerSet", new TestParameter[] { new TestParameter(
                new TestSetValue(new TestValue[] { testComponentValue, testComponentValue })) }));

        // Map
        startEvents.addValidator(new ConstructorMetadataValidator("compInnerMap", new TestParameter[] { new TestParameter(
                new TestMapValue(new MapValueEntry[] { new MapValueEntry(testComponentValue, testComponentValue),
                        new MapValueEntry(testComponentValue, testComponentValue) })) }));

        controller.run();
    }
}
