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
import org.osgi.test.cases.blueprint.components.injection.InnerComponentInjection;
import org.osgi.test.cases.blueprint.components.injection.PropertyInjection;
import org.osgi.test.cases.blueprint.components.injection.PropertyInjectionInstanceFactory;
import org.osgi.test.cases.blueprint.components.injection.PropertyInjectionStaticFactory;
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

        TestComponentValue testComponentValue = new TestComponentValue(new LocalComponent(InnerComponentInjection.class));
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



//    // Constructor Test
//    private void addConstructorMetadataValidator(MetadataEventSet startEvents, String componentId, Class innerComponentClass, Class innerArgType) {
//        // metadata test
//        TestComponentValue testComponentValue = new TestComponentValue(new LocalComponent(innerComponentClass,
//                new TestParameter[] { new StringParameter(innerArgType) }, null));
//
//        startEvents.addValidator(new ConstructorMetadataValidator(componentId, new TestParameter[] { new TestParameter(
//                testComponentValue) }));
//    }
//
//    private void addConstructorArgValueValidator(MetadataEventSet startEvents, String outterComponentId, Class innerComponentClassName, Object innerArgValue, Class innerArgType ){
//        // value test
//        Dictionary innerComponentProps = new Hashtable();
//        StringValueDescriptor svd = new StringValueDescriptor("arg1", innerArgValue, innerArgType); // the "arg1" is "inner" arg1
//        innerComponentProps.put("arg1", svd); // the "arg1" is "inner" arg1
//
//        startEvents.addValidator(new InnerComponentValidator(outterComponentId, "arg1", innerComponentClassName, innerComponentProps));  //the "arg1" is "outer" arg1
//
//    }
//
//    private void addConstructorTestItem(MetadataEventSet startEvents) throws Exception {
//
//
//        this.addConstructorMetadataValidator(startEvents, "compInnerString", ComponentInjection.class, String.class);
//        this.addConstructorArgValueValidator(startEvents, "compInnerString", ComponentTestInfo.class, new String("ABC"), String.class);
//
//        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveBoolean", ComponentInjection.class, Boolean.TYPE);
//        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveBoolean", ComponentTestInfo.class, Boolean.TRUE, Boolean.TYPE);
//        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedBoolean", ComponentInjection.class, Boolean.class);
//        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedBoolean", ComponentTestInfo.class, Boolean.FALSE, Boolean.class);
//
//        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveByte", ComponentInjection.class, Byte.TYPE);
//        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveByte", ComponentTestInfo.class, new Byte(Byte.MIN_VALUE), Byte.TYPE);
//        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedByte", ComponentInjection.class, Byte.class);
//        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedByte", ComponentTestInfo.class, new Byte(Byte.MAX_VALUE), Byte.class);
//
//        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveDouble", ComponentInjection.class, Double.TYPE);
//        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveDouble", ComponentTestInfo.class, new Double(Double.MIN_VALUE), Double.TYPE);
//        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedDouble", ComponentInjection.class, Double.class);
//        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedDouble", ComponentTestInfo.class, new Double(Double.MAX_VALUE), Double.class);
//
//        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveFloat", ComponentInjection.class, Float.TYPE);
//        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveFloat", ComponentTestInfo.class, new Float(Float.MIN_VALUE), Float.TYPE);
//        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedFloat", ComponentInjection.class, Float.class);
//        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedFloat", ComponentTestInfo.class, new Float(Float.MAX_VALUE), Float.class);
//
//        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveInteger", ComponentInjection.class, Integer.TYPE);
//        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveInteger", ComponentTestInfo.class, new Integer(Integer.MIN_VALUE), Integer.TYPE);
//        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedInteger", ComponentInjection.class, Integer.class);
//        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedInteger", ComponentTestInfo.class, new Integer(Integer.MAX_VALUE), Integer.class);
//
//        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveCharacter", ComponentInjection.class, Character.TYPE);
//        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveCharacter", ComponentTestInfo.class, new Character(Character.MIN_VALUE), Character.TYPE);
//        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedCharacter", ComponentInjection.class, Character.class);
//        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedCharacter", ComponentTestInfo.class, new Character(Character.MAX_VALUE), Character.class);
//
//        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveShort", ComponentInjection.class, Short.TYPE);
//        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveShort", ComponentTestInfo.class, new Short(Short.MIN_VALUE), Short.TYPE);
//        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedShort", ComponentInjection.class, Short.class);
//        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedShort", ComponentTestInfo.class, new Short(Short.MAX_VALUE), Short.class);
//
//        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveLong", ComponentInjection.class, Long.TYPE);
//        this.addConstructorArgValueValidator(startEvents, "compInnerPrimitiveLong", ComponentTestInfo.class, new Long(Long.MIN_VALUE), Long.TYPE);
//        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedLong", ComponentInjection.class, Long.class);
//        this.addConstructorArgValueValidator(startEvents, "compInnerWrapperedLong", ComponentTestInfo.class, new Long(Long.MAX_VALUE), Long.class);
//    }
//
//    public void testConstructorInjection() throws Exception {
//        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
//                + "www/inner_component_constructor_injection.jar");
//        MetadataEventSet startEvents = controller.getStartEvents();
//
//        this.addConstructorTestItem(startEvents);
//
//        controller.run();
//    }
//
//    public void testInstanceFactoryConstructorInjection() throws Exception {
//        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
//                + "www/inner_component_factory_constructor_injection.jar");
//        MetadataEventSet startEvents = controller.getStartEvents();
//
//        this.addConstructorTestItem(startEvents);
//
//        controller.run();
//    }
//
//    public void testStaticFactoryConstructorInjection() throws Exception {
//        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
//                + "www/inner_component_static_factory_constructor_injection.jar");
//        MetadataEventSet startEvents = controller.getStartEvents();
//
//        this.addConstructorTestItem(startEvents);
//
//        controller.run();
//    }

    // Property Test

    private void addPropertyValidator(MetadataEventSet startEvents, String componentId, Class innerComponentClass, Object innerComponent,
            String innerProName, Class innerProType, String innerProSource ) {
        // value test
        // note: innerComponentClass may be not same with innerComponent.getClass, when use factory injection.
        startEvents.validateComponentProperty(componentId, "innerComponent", innerComponent, innerComponent.getClass());

        // metadata test
        TestComponentValue testComponentValue = new TestComponentValue(
                new LocalComponent(
                        innerComponentClass, 
                        null,
                        new TestProperty[] { new StringProperty(innerProName, innerProType, innerProSource) }
                )
        );
        
        startEvents.addValidator(
                new PropertyMetadataValidator(
                        componentId, 
                        new TestProperty[] { new TestProperty(testComponentValue, "innerComponent") }
                )
        );
    }

    private void addPropertyTestItem(MetadataEventSet startEvents, Class innerComponentClass) throws Exception {
        PropertyInjection pi;
        //string
        pi = new PropertyInjection();
        pi.setString("ABC");
        this.addPropertyValidator(startEvents, "compInnerStringTyped", innerComponentClass, pi, "string", String.class, "ABC"); //typed
        pi = new PropertyInjection();
        pi.setString("XYZ");
        this.addPropertyValidator(startEvents, "compInnerString", innerComponentClass, pi, "string", null, "XYZ");
        //boolean
        pi = new PropertyInjection();
        pi.setPrimBoolean(true);
        this.addPropertyValidator(startEvents, "compInnerPrimitiveBoolean", innerComponentClass, pi, "primBoolean", null, "true");
        pi = new PropertyInjection();
        pi.setBoolean(new Boolean(false));
        this.addPropertyValidator(startEvents, "compInnerWrapperedBoolean", innerComponentClass, pi, "boolean", null, "false");
        //byte
        pi = new PropertyInjection();
        pi.setPrimByte((byte)-128);
        this.addPropertyValidator(startEvents, "compInnerPrimitiveByte", innerComponentClass, pi, "primByte", null, "-128");
        pi = new PropertyInjection();
        pi.setByte(new Byte("127"));
        this.addPropertyValidator(startEvents, "compInnerWrapperedByte", innerComponentClass, pi, "byte", null, "127");
        //double
        pi = new PropertyInjection();
        pi.setPrimDouble(new Double("4.9e-324").doubleValue());
        this.addPropertyValidator(startEvents, "compInnerPrimitiveDouble", innerComponentClass, pi, "primDouble", null, "4.9e-324");
        pi = new PropertyInjection();
        pi.setDouble(new Double("1.7976931348623157E308"));
        this.addPropertyValidator(startEvents, "compInnerWrapperedDouble", innerComponentClass, pi, "double", null, "1.7976931348623157E308");
        //float
        pi = new PropertyInjection();
        pi.setPrimFloat(new Float("1.4E-45").floatValue());
        this.addPropertyValidator(startEvents, "compInnerPrimitiveFloat", innerComponentClass, pi, "primFloat", null,"1.4E-45");
        pi = new PropertyInjection();
        pi.setFloat(new Float("3.4028235E38"));
        this.addPropertyValidator(startEvents, "compInnerWrapperedFloat", innerComponentClass, pi, "float", null,"3.4028235E38");
        //int
        pi = new PropertyInjection();
        pi.setPrimInteger(-2147483648);
        this.addPropertyValidator(startEvents, "compInnerPrimitiveInteger", innerComponentClass, pi, "primInteger", null, "-2147483648");
        pi = new PropertyInjection();
        pi.setInteger(new Integer("2147483647"));
        this.addPropertyValidator(startEvents, "compInnerWrapperedInteger", innerComponentClass, pi, "integer", null,"2147483647");
        //char
        pi = new PropertyInjection();
        pi.setPrimCharacter('a');
        this.addPropertyValidator(startEvents, "compInnerPrimitiveCharacter", innerComponentClass, pi, "primCharacter",null, "a");
        pi = new PropertyInjection();
        pi.setCharacter(new Character('A'));
        this.addPropertyValidator(startEvents, "compInnerWrapperedCharacter", innerComponentClass, pi, "character",null, "A");
        //short
        pi = new PropertyInjection();
        pi.setPrimShort((short)-32768);
        this.addPropertyValidator(startEvents, "compInnerPrimitiveShort", innerComponentClass, pi, "primShort", null,"-32768");
        pi = new PropertyInjection();
        pi.setShort(new Short("32767"));
        this.addPropertyValidator(startEvents, "compInnerWrapperedShort", innerComponentClass, pi, "short", null, "32767");
        //long
        pi = new PropertyInjection();
        pi.setPrimLong(new Long("-9223372036854775808").longValue());
        this.addPropertyValidator(startEvents, "compInnerPrimitiveLong", innerComponentClass, pi, "primLong", null, "-9223372036854775808");
        pi = new PropertyInjection();
        pi.setLong(new Long("9223372036854775807"));
        this.addPropertyValidator(startEvents, "compInnerWrapperedLong", innerComponentClass, pi, "long", null,"9223372036854775807");
    }

    public void testPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropertyTestItem(startEvents, PropertyInjection.class);

        controller.run();
    }

    public void testInstanceFactoryPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropertyTestItem(startEvents, null);

        controller.run();
    }

    public void testStaticFactoryPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_static_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropertyTestItem(startEvents, PropertyInjectionStaticFactory.class);

        controller.run();
    }

//    // Collection Test
//    public void testCollectionInjection() throws Exception {
//        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
//                + "www/inner_component_collection_injection.jar");
//        MetadataEventSet startEvents = controller.getStartEvents();
//
//        TestComponentValue testComponentValue = new TestComponentValue(new LocalComponent(ComponentTestInfo.class,
//                new TestParameter[] { new StringParameter() }, null));
//
//        // List
//        startEvents.addValidator(new ConstructorMetadataValidator("compInnerList", new TestParameter[] { new TestParameter(
//                new TestListValue(new TestValue[] { testComponentValue, testComponentValue })) }));
//
//        // Set
//        startEvents.addValidator(new ConstructorMetadataValidator("compInnerSet", new TestParameter[] { new TestParameter(
//                new TestSetValue(new TestValue[] { testComponentValue, testComponentValue })) }));
//
//        // Map
//        startEvents.addValidator(new ConstructorMetadataValidator("compInnerMap", new TestParameter[] { new TestParameter(
//                new TestMapValue(new MapValueEntry[] { new MapValueEntry(testComponentValue, testComponentValue),
//                        new MapValueEntry(testComponentValue, testComponentValue) })) }));
//
//        controller.run();
//    }
}
