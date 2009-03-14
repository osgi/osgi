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
import org.osgi.test.cases.blueprint.components.injection.ConstructorInjection;
import org.osgi.test.cases.blueprint.components.injection.ConstructorInjectionStaticFactory;
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

//    // Basic Test
//    public void testBasic() throws Exception {
//        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
//                + "www/inner_component_basic.jar");
//        MetadataEventSet startEvents = controller.getStartEvents();
//
//        //inner component should be created
//        startEvents.addAssertion("compInnerOne", AssertionService.COMPONENT_CREATED);
//        startEvents.addAssertion("compInnerOne_1", AssertionService.COMPONENT_CREATED);
//
//        //inner component should not be created
//        startEvents.addFailureEvent(new ComponentAssertion("compInnerLazyOuter", AssertionService.COMPONENT_CREATED));
//        startEvents.addFailureEvent(new ComponentAssertion("compInnerLazyOuter_1", AssertionService.COMPONENT_CREATED));
//
//        //multi inner components
//        startEvents.addAssertion("compInnerTwo", AssertionService.COMPONENT_CREATED);
//        startEvents.addAssertion("compInnerTwo_1", AssertionService.COMPONENT_CREATED);
//        startEvents.addAssertion("compInnerTwo_2", AssertionService.COMPONENT_CREATED);
//
//        TestComponentValue testComponentValue = new TestComponentValue(new LocalComponent(InnerComponentInjection.class));
//        //Ignore ID
//        startEvents.addValidator(new ConstructorMetadataValidator(
//                "compInnerIgnoreID",
//                new TestParameter[] {
//                    new StringParameter("compInnerIgnoreID"),
//                    new TestParameter(testComponentValue) }
//        ));
//        //Ignore scope
//        startEvents.addValidator(new ConstructorMetadataValidator(
//                "compInnerIgnoreScope",
//                new TestParameter[] {
//                    new StringParameter("compInnerIgnoreScope"),
//                    new TestParameter(testComponentValue) }
//        ));
//
//        controller.run();
//    }



    // Constructor Test


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

    
    /**
     * Constructor value validator
     * @param startEvents
     * @param componentId
     * @param value         The expected inner component object
     * @param type          The expected inner component type
     */
    private void addConstructorValueValidator(MetadataEventSet startEvents, String componentId, Object value, Class type) {
        startEvents.validateComponentArgument(componentId, "arg1", value, type);
    }
    
    private void addConstructorValueTestItems(MetadataEventSet startEvents, Class innerComponentExpectedClass) throws Exception {
        ConstructorInjection ci;
        //string
        ci = new ConstructorInjection("STR1");
        this.addConstructorValueValidator(startEvents, "compInnerStringNoTyped", ci, innerComponentExpectedClass);
        ci = new ConstructorInjection("STR2");
        this.addConstructorValueValidator(startEvents, "compInnerStringBothTyped", ci, innerComponentExpectedClass);
        ci = new ConstructorInjection("STR3");
        this.addConstructorValueValidator(startEvents, "compInnerStringValueTyped", ci, innerComponentExpectedClass);
        ci = new ConstructorInjection("STR4");
        this.addConstructorValueValidator(startEvents, "compInnerStringArgTyped", ci, innerComponentExpectedClass);
        ci = new ConstructorInjection("STR5");
        this.addConstructorValueValidator(startEvents, "compInnerString", ci, innerComponentExpectedClass);
        //boolean
        ci = new ConstructorInjection(true);
        this.addConstructorValueValidator(startEvents, "compInnerPrimitiveBoolean", ci, innerComponentExpectedClass);
        ci = new ConstructorInjection(new Boolean(false));
        this.addConstructorValueValidator(startEvents, "compInnerWrapperedBoolean", ci, innerComponentExpectedClass);
        //byte
        ci = new ConstructorInjection((byte)-128);
        this.addConstructorValueValidator(startEvents, "compInnerPrimitiveByte", ci, innerComponentExpectedClass);
        ci = new ConstructorInjection(new Byte("127"));
        this.addConstructorValueValidator(startEvents, "compInnerWrapperedByte", ci, innerComponentExpectedClass);
        //double
        ci = new ConstructorInjection(new Double("4.9e-324").doubleValue());
        this.addConstructorValueValidator(startEvents, "compInnerPrimitiveDouble", ci, innerComponentExpectedClass);
        ci = new ConstructorInjection(new Double("1.7976931348623157E308"));
        this.addConstructorValueValidator(startEvents, "compInnerWrapperedDouble", ci, innerComponentExpectedClass);
        //float
        ci = new ConstructorInjection(new Float("1.4E-45").floatValue());
        this.addConstructorValueValidator(startEvents, "compInnerPrimitiveFloat", ci, innerComponentExpectedClass);
        ci = new ConstructorInjection(new Float("3.4028235E38"));
        this.addConstructorValueValidator(startEvents, "compInnerWrapperedFloat", ci, innerComponentExpectedClass);
        //int
        ci = new ConstructorInjection(-2147483648);
        this.addConstructorValueValidator(startEvents, "compInnerPrimitiveInteger", ci, innerComponentExpectedClass);
        ci = new ConstructorInjection(new Integer("2147483647"));
        this.addConstructorValueValidator(startEvents, "compInnerWrapperedInteger", ci, innerComponentExpectedClass);
        //char
        ci = new ConstructorInjection('a');
        this.addConstructorValueValidator(startEvents, "compInnerPrimitiveCharacter", ci, innerComponentExpectedClass);
        ci = new ConstructorInjection(new Character('A'));
        this.addConstructorValueValidator(startEvents, "compInnerWrapperedCharacter", ci, innerComponentExpectedClass);
        //short
        ci = new ConstructorInjection((short)-32768);
        this.addConstructorValueValidator(startEvents, "compInnerPrimitiveShort", ci, innerComponentExpectedClass);
        ci = new ConstructorInjection(new Short("32767"));
        this.addConstructorValueValidator(startEvents, "compInnerWrapperedShort", ci, innerComponentExpectedClass);
        //long
        ci = new ConstructorInjection(new Long("-9223372036854775808").longValue());
        this.addConstructorValueValidator(startEvents, "compInnerPrimitiveLong", ci, innerComponentExpectedClass);
        ci = new ConstructorInjection(new Long("9223372036854775807"));
        this.addConstructorValueValidator(startEvents, "compInnerWrapperedLong", ci, innerComponentExpectedClass);
    }
    
    /**
     * Constructor metadata validator
     * @param innerArgTargetType    <constrator-arg type="">
     * @param innerArgValueType     <value type="">
     */
    private void addConstructorMetadataValidator(MetadataEventSet startEvents, String componentId, Class innerComponentClass, 
            Class innerArgTargetType, Class innerArgValueType, String innerArgSource) {
        // metadata test
        TestComponentValue testComponentValue = new TestComponentValue(
                new LocalComponent(
                        innerComponentClass,
                        new TestParameter[] { new StringParameter(innerArgTargetType, innerArgSource, innerArgValueType) },
                        null
                )
        );
        startEvents.addValidator(
                new ConstructorMetadataValidator(
                        componentId, 
                        new TestParameter[] { new TestParameter(testComponentValue) }
                )
        );
    }
    
    private void addConstructorMetadataTestItems(MetadataEventSet startEvents, Class innerComponentExpectedClass) throws Exception {
        // string
        this.addConstructorMetadataValidator(startEvents, "compInnerStringNoTyped", innerComponentExpectedClass, null, null, "STR1");
        this.addConstructorMetadataValidator(startEvents, "compInnerStringBothTyped", innerComponentExpectedClass, String.class, String.class, "STR2");
        this.addConstructorMetadataValidator(startEvents, "compInnerStringValueTyped", innerComponentExpectedClass, null, String.class, "STR3");
        this.addConstructorMetadataValidator(startEvents, "compInnerStringArgTyped", innerComponentExpectedClass, String.class, null, "STR4");
        this.addConstructorMetadataValidator(startEvents, "compInnerString", innerComponentExpectedClass, String.class, null, "STR5");
        //boolean
        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveBoolean", innerComponentExpectedClass, Boolean.TYPE, null, "true");
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedBoolean", innerComponentExpectedClass, Boolean.class, null, "false");
        //byte
        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveByte", innerComponentExpectedClass, Byte.TYPE, null, "-128");
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedByte", innerComponentExpectedClass, Byte.class, null, "127");
        //double
        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveDouble", innerComponentExpectedClass, Double.TYPE, null, "4.9e-324");
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedDouble", innerComponentExpectedClass, Double.class, null, "1.7976931348623157E308");
        //float
        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveFloat", innerComponentExpectedClass, Float.TYPE, null, "1.4E-45");
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedFloat", innerComponentExpectedClass, Float.class, null, "3.4028235E38");
        //int
        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveInteger", innerComponentExpectedClass, Integer.TYPE, null, "-2147483648");
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedInteger", innerComponentExpectedClass, Integer.class, null, "2147483647");
        //char 
        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveCharacter", innerComponentExpectedClass, Character.TYPE, null, "a");
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedCharacter", innerComponentExpectedClass, Character.class, null, "A");
        //short
        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveShort", innerComponentExpectedClass, Short.TYPE, null, "-32768");
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedShort", innerComponentExpectedClass, Short.class, null, "32767");
        //long
        this.addConstructorMetadataValidator(startEvents, "compInnerPrimitiveLong", innerComponentExpectedClass, Long.TYPE, null, "-9223372036854775808");
        this.addConstructorMetadataValidator(startEvents, "compInnerWrapperedLong", innerComponentExpectedClass, Long.class, null, "9223372036854775807");
    }

    
    public void testConstructorInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // value test
        this.addConstructorValueTestItems(startEvents, ConstructorInjection.class);
        
        // parameter metadata test
        this.addConstructorMetadataTestItems(startEvents, ConstructorInjection.class);

        controller.run();
    }

//    public void testInstanceFactoryConstructorInjection() throws Exception {
//        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
//                + "www/inner_component_factory_constructor_injection.jar");
//        MetadataEventSet startEvents = controller.getStartEvents();
//
//        // value test
//        this.addConstructorValueTestItems(startEvents, ConstructorInjection.class);
//        
//        // parameter metadata test
//        this.addConstructorMetadataTestItems(startEvents, null);
//
//
//        controller.run();
//    }
//
//    public void testStaticFactoryConstructorInjection() throws Exception {
//        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
//                + "www/inner_component_static_factory_constructor_injection.jar");
//        MetadataEventSet startEvents = controller.getStartEvents();
//
//        // value test
//        this.addConstructorValueTestItems(startEvents, ConstructorInjection.class);
//
//        // parameter metadata test
//        this.addConstructorMetadataTestItems(startEvents, ConstructorInjectionStaticFactory.class);
//        
//        controller.run();
//    }

    // Property Test

//
//    /**
//     * Property Value Validator
//     * @param value         The expected inner component object
//     * @param type          The expected inner component type
//     */
//    private void addPropertyValueValidator(MetadataEventSet startEvents, String componentId, String propertyName, Object value, Class type){
//        startEvents.validateComponentProperty(componentId, propertyName, value, type);
//    }
//    
//    private void addPropertyValueTestItems(MetadataEventSet startEvents, Class innerComponentExpectedType) throws Exception {
//        PropertyInjection pi;
//        //string
//        pi = new PropertyInjection();
//        pi.setString("ABC");
//        this.addPropertyValueValidator(startEvents, "compInnerStringTyped", "innerComponent", pi, innerComponentExpectedType); //typed
//        pi = new PropertyInjection();
//        pi.setString("XYZ");
//        this.addPropertyValueValidator(startEvents, "compInnerString", "innerComponent", pi, innerComponentExpectedType);
//        //boolean
//        pi = new PropertyInjection();
//        pi.setPrimBoolean(true);
//        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveBoolean", "innerComponent", pi, innerComponentExpectedType);
//        pi = new PropertyInjection();
//        pi.setBoolean(new Boolean(false));
//        this.addPropertyValueValidator(startEvents, "compInnerWrapperedBoolean", "innerComponent", pi, innerComponentExpectedType);
//        //byte
//        pi = new PropertyInjection();
//        pi.setPrimByte((byte)-128);
//        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveByte", "innerComponent", pi, innerComponentExpectedType);
//        pi = new PropertyInjection();
//        pi.setByte(new Byte("127"));
//        this.addPropertyValueValidator(startEvents, "compInnerWrapperedByte", "innerComponent", pi, innerComponentExpectedType);
//        //double
//        pi = new PropertyInjection();
//        pi.setPrimDouble(new Double("4.9e-324").doubleValue());
//        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveDouble", "innerComponent", pi, innerComponentExpectedType);
//        pi = new PropertyInjection();
//        pi.setDouble(new Double("1.7976931348623157E308"));
//        this.addPropertyValueValidator(startEvents, "compInnerWrapperedDouble", "innerComponent", pi, innerComponentExpectedType);
//        //float
//        pi = new PropertyInjection();
//        pi.setPrimFloat(new Float("1.4E-45").floatValue());
//        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveFloat", "innerComponent", pi, innerComponentExpectedType);
//        pi = new PropertyInjection();
//        pi.setFloat(new Float("3.4028235E38"));
//        this.addPropertyValueValidator(startEvents, "compInnerWrapperedFloat", "innerComponent", pi, innerComponentExpectedType);
//        //int
//        pi = new PropertyInjection();
//        pi.setPrimInteger(-2147483648);
//        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveInteger", "innerComponent", pi, innerComponentExpectedType);
//        pi = new PropertyInjection();
//        pi.setInteger(new Integer("2147483647"));
//        this.addPropertyValueValidator(startEvents, "compInnerWrapperedInteger", "innerComponent", pi, innerComponentExpectedType);
//        //char
//        pi = new PropertyInjection();
//        pi.setPrimCharacter('a');
//        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveCharacter", "innerComponent", pi, innerComponentExpectedType);
//        pi = new PropertyInjection();
//        pi.setCharacter(new Character('A'));
//        this.addPropertyValueValidator(startEvents, "compInnerWrapperedCharacter", "innerComponent", pi, innerComponentExpectedType);
//        //short
//        pi = new PropertyInjection();
//        pi.setPrimShort((short)-32768);
//        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveShort", "innerComponent", pi, innerComponentExpectedType);
//        pi = new PropertyInjection();
//        pi.setShort(new Short("32767"));
//        this.addPropertyValueValidator(startEvents, "compInnerWrapperedShort", "innerComponent", pi, innerComponentExpectedType);
//        //long
//        pi = new PropertyInjection();
//        pi.setPrimLong(new Long("-9223372036854775808").longValue());
//        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveLong", "innerComponent", pi, innerComponentExpectedType);
//        pi = new PropertyInjection();
//        pi.setLong(new Long("9223372036854775807"));
//        this.addPropertyValueValidator(startEvents, "compInnerWrapperedLong", "innerComponent", pi, innerComponentExpectedType);
//    }
//    
//    /**
//     * Property metadata Validator
//     * @param startEvents
//     * @param componentId
//     * @param innerComponentSpecifiedClass
//     * @param innerProName
//     * @param innerProType      <value type="">
//     * @param innerProSource
//     */
//    private void addPropertyMetadataValidator(MetadataEventSet startEvents, String componentId, Class innerComponentSpecifiedClass,
//            String innerProName, Class innerProType, String innerProSource ) {
//        TestComponentValue testComponentValue = new TestComponentValue(
//                new LocalComponent(
//                        innerComponentSpecifiedClass, 
//                        null,
//                        new TestProperty[] { new StringProperty(innerProName, innerProType, innerProSource) }
//                )
//        );
//        startEvents.addValidator(
//                new PropertyMetadataValidator(
//                        componentId, 
//                        new TestProperty[] { new TestProperty(testComponentValue, "innerComponent") }
//                )
//        );
//    }
//    
//    private void addPropertyMetadataTestItem(MetadataEventSet startEvents, Class innerComponentSpecifiedClass) throws Exception {
//        PropertyInjection pi;
//        //string
//        this.addPropertyMetadataValidator(startEvents, "compInnerStringTyped", innerComponentSpecifiedClass, "string", String.class, "ABC"); //typed
//        this.addPropertyMetadataValidator(startEvents, "compInnerString", innerComponentSpecifiedClass, "string", null, "XYZ");
//        //boolean
//        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveBoolean", innerComponentSpecifiedClass, "primBoolean", null, "true");
//        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedBoolean", innerComponentSpecifiedClass, "boolean", null, "false");
//        //byte
//        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveByte", innerComponentSpecifiedClass, "primByte", null, "-128");
//        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedByte", innerComponentSpecifiedClass, "byte", null, "127");
//        //double
//        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveDouble", innerComponentSpecifiedClass, "primDouble", null, "4.9e-324");
//        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedDouble", innerComponentSpecifiedClass, "double", null, "1.7976931348623157E308");
//        //float
//        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveFloat", innerComponentSpecifiedClass, "primFloat", null,"1.4E-45");
//        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedFloat", innerComponentSpecifiedClass, "float", null,"3.4028235E38");
//        //int
//        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveInteger", innerComponentSpecifiedClass, "primInteger", null, "-2147483648");
//        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedInteger", innerComponentSpecifiedClass, "integer", null,"2147483647");
//        //char
//        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveCharacter", innerComponentSpecifiedClass, "primCharacter",null, "a");
//        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedCharacter", innerComponentSpecifiedClass, "character",null, "A");
//        //short
//        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveShort", innerComponentSpecifiedClass, "primShort", null,"-32768");
//        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedShort", innerComponentSpecifiedClass, "short", null, "32767");
//        //long
//        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveLong", innerComponentSpecifiedClass, "primLong", null, "-9223372036854775808");
//        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedLong", innerComponentSpecifiedClass, "long", null,"9223372036854775807");
//    }
//
//    public void testPropertyInjection() throws Exception {
//        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
//                + "www/inner_component_property_injection.jar");
//        MetadataEventSet startEvents = controller.getStartEvents();
//
//        this.addPropertyValueTestItems(startEvents, PropertyInjection.class);
//        this.addPropertyMetadataTestItem(startEvents, PropertyInjection.class);
//
//        controller.run();
//    }
//
//    public void testInstanceFactoryPropertyInjection() throws Exception {
//        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
//                + "www/inner_component_factory_property_injection.jar");
//        MetadataEventSet startEvents = controller.getStartEvents();
//        
//        this.addPropertyValueTestItems(startEvents, PropertyInjection.class);
//        this.addPropertyMetadataTestItem(startEvents, null);
//
//        controller.run();
//    }
//
//    public void testStaticFactoryPropertyInjection() throws Exception {
//        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
//                + "www/inner_component_static_factory_property_injection.jar");
//        MetadataEventSet startEvents = controller.getStartEvents();
//
//        this.addPropertyValueTestItems(startEvents, PropertyInjection.class);
//        this.addPropertyMetadataTestItem(startEvents, PropertyInjectionStaticFactory.class);
//
//        controller.run();
//    }

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
