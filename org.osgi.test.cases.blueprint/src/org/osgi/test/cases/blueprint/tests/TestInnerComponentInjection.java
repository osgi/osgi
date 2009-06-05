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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.blueprint.reflect.BeanMetadata;

import org.osgi.test.cases.blueprint.components.injection.ConstructorInjection;
import org.osgi.test.cases.blueprint.components.injection.ConstructorInjectionStaticFactory;
import org.osgi.test.cases.blueprint.components.injection.InnerComponentInjection;
import org.osgi.test.cases.blueprint.components.injection.InnerComponentInjectionStaticFactory;
import org.osgi.test.cases.blueprint.components.injection.PropertyInjection;
import org.osgi.test.cases.blueprint.components.injection.PropertyInjectionStaticFactory;
import org.osgi.test.cases.blueprint.framework.ComponentAssertion;
import org.osgi.test.cases.blueprint.framework.ArgumentMetadataValidator;
import org.osgi.test.cases.blueprint.framework.FactoryMetadataValidator;
import org.osgi.test.cases.blueprint.framework.BeanComponent;
import org.osgi.test.cases.blueprint.framework.MapValueEntry;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.PropertyMetadataValidator;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.StringArgument;
import org.osgi.test.cases.blueprint.framework.StringProperty;
import org.osgi.test.cases.blueprint.framework.TestComponentValue;
import org.osgi.test.cases.blueprint.framework.TestListValue;
import org.osgi.test.cases.blueprint.framework.TestMapValue;
import org.osgi.test.cases.blueprint.framework.TestArgument;
import org.osgi.test.cases.blueprint.framework.TestProperty;
import org.osgi.test.cases.blueprint.framework.TestSetValue;
import org.osgi.test.cases.blueprint.framework.TestValue;
import org.osgi.test.cases.blueprint.services.AssertionService;
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

        TestComponentValue testComponentValue = new TestComponentValue(new BeanComponent(InnerComponentInjection.class,null,null,null));

        controller.run();
    }



    // Constructor Test

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
    private void addArgumentMetadataValidator(MetadataEventSet startEvents, String outerComponentId,
            Class innerComponentClass, String factoryMethodName,
            Class innerArgTargetType, Class innerArgValueType, String innerArgSource) {
        // metadata test
        TestComponentValue testComponentValue = new TestComponentValue(
                new BeanComponent(
                        null,
                        innerComponentClass,
                        factoryMethodName,
                        null,
                        null,
                        new TestArgument[] { new StringArgument(innerArgTargetType, innerArgSource, innerArgValueType) },
                        null,
 						null,
 						BeanMetadata.INITIALIZATION_EAGER,
 						BeanMetadata.SCOPE_PROTOTYPE
                )
        );
        startEvents.addValidator(
                new ArgumentMetadataValidator(
                        outerComponentId,
                        new TestArgument[] { new TestArgument(testComponentValue) }
                )
        );
    }


    private void addConstructorMetadataTestItems(MetadataEventSet startEvents, Class innerComponentExpectedClass, String factoryMethodName) throws Exception {
        // string
        this.addArgumentMetadataValidator(startEvents, "compInnerStringBothTyped", innerComponentExpectedClass, factoryMethodName, String.class, String.class, "STR2");
        this.addArgumentMetadataValidator(startEvents, "compInnerStringValueTyped", innerComponentExpectedClass, factoryMethodName, null, String.class, "STR3");
        this.addArgumentMetadataValidator(startEvents, "compInnerStringArgTyped", innerComponentExpectedClass, factoryMethodName, String.class, null, "STR4");
        this.addArgumentMetadataValidator(startEvents, "compInnerString", innerComponentExpectedClass, factoryMethodName, String.class, null, "STR5");
        //boolean
        this.addArgumentMetadataValidator(startEvents, "compInnerPrimitiveBoolean", innerComponentExpectedClass, factoryMethodName, Boolean.TYPE, null, "true");
        this.addArgumentMetadataValidator(startEvents, "compInnerWrapperedBoolean", innerComponentExpectedClass, factoryMethodName, Boolean.class, null, "false");
        //byte
        this.addArgumentMetadataValidator(startEvents, "compInnerPrimitiveByte", innerComponentExpectedClass, factoryMethodName, Byte.TYPE, null, "-128");
        this.addArgumentMetadataValidator(startEvents, "compInnerWrapperedByte", innerComponentExpectedClass, factoryMethodName, Byte.class, null, "127");
        //double
        this.addArgumentMetadataValidator(startEvents, "compInnerPrimitiveDouble", innerComponentExpectedClass, factoryMethodName, Double.TYPE, null, "4.9e-324");
        this.addArgumentMetadataValidator(startEvents, "compInnerWrapperedDouble", innerComponentExpectedClass, factoryMethodName, Double.class, null, "1.7976931348623157E308");
        //float
        this.addArgumentMetadataValidator(startEvents, "compInnerPrimitiveFloat", innerComponentExpectedClass, factoryMethodName, Float.TYPE, null, "1.4E-45");
        this.addArgumentMetadataValidator(startEvents, "compInnerWrapperedFloat", innerComponentExpectedClass, factoryMethodName, Float.class, null, "3.4028235E38");
        //int
        this.addArgumentMetadataValidator(startEvents, "compInnerPrimitiveInteger", innerComponentExpectedClass, factoryMethodName, Integer.TYPE, null, "-2147483648");
        this.addArgumentMetadataValidator(startEvents, "compInnerWrapperedInteger", innerComponentExpectedClass, factoryMethodName, Integer.class, null, "2147483647");
        //char
        this.addArgumentMetadataValidator(startEvents, "compInnerPrimitiveCharacter", innerComponentExpectedClass, factoryMethodName, Character.TYPE, null, "a");
        this.addArgumentMetadataValidator(startEvents, "compInnerWrapperedCharacter", innerComponentExpectedClass, factoryMethodName, Character.class, null, "A");
        //short
        this.addArgumentMetadataValidator(startEvents, "compInnerPrimitiveShort", innerComponentExpectedClass, factoryMethodName, Short.TYPE, null, "-32768");
        this.addArgumentMetadataValidator(startEvents, "compInnerWrapperedShort", innerComponentExpectedClass, factoryMethodName, Short.class, null, "32767");
        //long
        this.addArgumentMetadataValidator(startEvents, "compInnerPrimitiveLong", innerComponentExpectedClass, factoryMethodName, Long.TYPE, null, "-9223372036854775808");
        this.addArgumentMetadataValidator(startEvents, "compInnerWrapperedLong", innerComponentExpectedClass, factoryMethodName, Long.class, null, "9223372036854775807");
    }

    private void addFactoryMetadataValidator(MetadataEventSet startEvents, String componentId, String factoryMethodName, String staticFactoryClassName, TestValue factoryTestComponentValue) throws Exception{
        startEvents.addValidator(
                new FactoryMetadataValidator(
                        componentId,
                        factoryMethodName,
                        staticFactoryClassName,
                        factoryTestComponentValue
                )
        );
    }

    private void addFactoryMetadataTestItems(MetadataEventSet startEvents, String staticFactoryClassName, TestValue factoryTestComponentValue)throws Exception{
        this.addFactoryMetadataValidator(startEvents, "compInnerStringBothTyped", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerStringValueTyped", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerStringArgTyped", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerString", "makeInstance", staticFactoryClassName, factoryTestComponentValue);

        this.addFactoryMetadataValidator(startEvents, "compInnerPrimitiveBoolean", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerWrapperedBoolean", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerPrimitiveByte", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerWrapperedByte", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerPrimitiveDouble", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerWrapperedDouble", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerPrimitiveFloat", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerWrapperedFloat", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerPrimitiveInteger", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerWrapperedInteger", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerPrimitiveCharacter", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerWrapperedCharacter", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerPrimitiveShort", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerWrapperedShort", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerPrimitiveLong", "makeInstance", staticFactoryClassName, factoryTestComponentValue);
        this.addFactoryMetadataValidator(startEvents, "compInnerWrapperedLong", "makeInstance", staticFactoryClassName, factoryTestComponentValue);

    }

    public void testConstructorInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // value test
        this.addConstructorValueTestItems(startEvents, ConstructorInjection.class);

        // parameter metadata test
        this.addConstructorMetadataTestItems(startEvents, ConstructorInjection.class, null);

        controller.run();
    }

    public void testInstanceFactoryConstructorInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // value test
        this.addConstructorValueTestItems(startEvents, ConstructorInjection.class);

        // factory metadata test
        this.addFactoryMetadataTestItems(startEvents, null, null);

        // parameter metadata test
        // TODO: bugzilla 1230, Mismatch in constructor parameter size expected:<1> but was:<0>
        this.addConstructorMetadataTestItems(startEvents, null, "makeInstance");

        controller.run();
    }

    public void testStaticFactoryConstructorInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_static_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // value test
        this.addConstructorValueTestItems(startEvents, ConstructorInjection.class);

        // factory metadata test
        // TODO: bugzilla 1231, comp created by StaticFactory, has constructorInjMetadata, but no methodInjMetaData?
        this.addFactoryMetadataTestItems(startEvents,InnerComponentInjectionStaticFactory.class.getName(), null);

        // parameter metadata test
        // TODO: BeanComponent, wait bugzilla 1230 1231
        this.addConstructorMetadataTestItems(startEvents, ConstructorInjectionStaticFactory.class, "makeInstance");

        controller.run();
    }



    // Property Test


    /**
     * Property Value Validator
     * @param value         The expected inner component object
     * @param type          The expected inner component type
     */
    private void addPropertyValueValidator(MetadataEventSet startEvents, String componentId, String propertyName, Object value, Class type){
        startEvents.validateComponentProperty(componentId, propertyName, value, type);
    }

    private void addPropertyValueTestItems(MetadataEventSet startEvents, Class innerComponentExpectedType) throws Exception {
        PropertyInjection pi;
        //string
        pi = new PropertyInjection();
        pi.setString("ABC");
        this.addPropertyValueValidator(startEvents, "compInnerStringTyped", "innerComponent", pi, innerComponentExpectedType); //typed
        pi = new PropertyInjection();
        pi.setString("XYZ");
        this.addPropertyValueValidator(startEvents, "compInnerString", "innerComponent", pi, innerComponentExpectedType);
        //boolean
        pi = new PropertyInjection();
        pi.setPrimBoolean(true);
        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveBoolean", "innerComponent", pi, innerComponentExpectedType);
        pi = new PropertyInjection();
        pi.setBoolean(new Boolean(false));
        this.addPropertyValueValidator(startEvents, "compInnerWrapperedBoolean", "innerComponent", pi, innerComponentExpectedType);
        //byte
        pi = new PropertyInjection();
        pi.setPrimByte((byte)-128);
        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveByte", "innerComponent", pi, innerComponentExpectedType);
        pi = new PropertyInjection();
        pi.setByte(new Byte("127"));
        this.addPropertyValueValidator(startEvents, "compInnerWrapperedByte", "innerComponent", pi, innerComponentExpectedType);
        //double
        pi = new PropertyInjection();
        pi.setPrimDouble(new Double("4.9e-324").doubleValue());
        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveDouble", "innerComponent", pi, innerComponentExpectedType);
        pi = new PropertyInjection();
        pi.setDouble(new Double("1.7976931348623157E308"));
        this.addPropertyValueValidator(startEvents, "compInnerWrapperedDouble", "innerComponent", pi, innerComponentExpectedType);
        //float
        pi = new PropertyInjection();
        pi.setPrimFloat(new Float("1.4E-45").floatValue());
        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveFloat", "innerComponent", pi, innerComponentExpectedType);
        pi = new PropertyInjection();
        pi.setFloat(new Float("3.4028235E38"));
        this.addPropertyValueValidator(startEvents, "compInnerWrapperedFloat", "innerComponent", pi, innerComponentExpectedType);
        //int
        pi = new PropertyInjection();
        pi.setPrimInteger(-2147483648);
        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveInteger", "innerComponent", pi, innerComponentExpectedType);
        pi = new PropertyInjection();
        pi.setInteger(new Integer("2147483647"));
        this.addPropertyValueValidator(startEvents, "compInnerWrapperedInteger", "innerComponent", pi, innerComponentExpectedType);
        //char
        pi = new PropertyInjection();
        pi.setPrimCharacter('a');
        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveCharacter", "innerComponent", pi, innerComponentExpectedType);
        pi = new PropertyInjection();
        pi.setCharacter(new Character('A'));
        this.addPropertyValueValidator(startEvents, "compInnerWrapperedCharacter", "innerComponent", pi, innerComponentExpectedType);
        //short
        pi = new PropertyInjection();
        pi.setPrimShort((short)-32768);
        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveShort", "innerComponent", pi, innerComponentExpectedType);
        pi = new PropertyInjection();
        pi.setShort(new Short("32767"));
        this.addPropertyValueValidator(startEvents, "compInnerWrapperedShort", "innerComponent", pi, innerComponentExpectedType);
        //long
        pi = new PropertyInjection();
        pi.setPrimLong(new Long("-9223372036854775808").longValue());
        this.addPropertyValueValidator(startEvents, "compInnerPrimitiveLong", "innerComponent", pi, innerComponentExpectedType);
        pi = new PropertyInjection();
        pi.setLong(new Long("9223372036854775807"));
        this.addPropertyValueValidator(startEvents, "compInnerWrapperedLong", "innerComponent", pi, innerComponentExpectedType);
    }

    /**
     * Property metadata Validator
     * @param startEvents
     * @param outerComponentId
     * @param innerComponentSpecifiedClass
     * @param innerProName
     * @param innerProType      <value type="">
     * @param innerProSource
     */
    private void addPropertyMetadataValidator(MetadataEventSet startEvents, String outerComponentId,
            Class innerComponentSpecifiedClass, String factoryMethodName,
            String innerProName, Class innerProType, String innerProSource ) {
        TestComponentValue testComponentValue = new TestComponentValue(
                new BeanComponent(
                        null,
                        innerComponentSpecifiedClass,
                        factoryMethodName,
                        null,
                        null,
                        null,
                        new TestProperty[] { new StringProperty(innerProName, innerProType, innerProSource) },
 						null,
 						BeanMetadata.INITIALIZATION_EAGER,
 						BeanMetadata.SCOPE_PROTOTYPE
                )
        );
        startEvents.addValidator(
                new PropertyMetadataValidator(
                        outerComponentId,
                        new TestProperty[] { new TestProperty(testComponentValue, "innerComponent") }
                )
        );
    }

    private void addPropertyMetadataTestItem(MetadataEventSet startEvents, Class innerComponentSpecifiedClass, String factoryMethodName) throws Exception {
        //string
        this.addPropertyMetadataValidator(startEvents, "compInnerStringTyped", innerComponentSpecifiedClass, factoryMethodName, "string", String.class, "ABC"); //typed
        this.addPropertyMetadataValidator(startEvents, "compInnerString", innerComponentSpecifiedClass, factoryMethodName, "string", null, "XYZ");
        //boolean
        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveBoolean", innerComponentSpecifiedClass, factoryMethodName, "primBoolean", null, "true");
        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedBoolean", innerComponentSpecifiedClass, factoryMethodName, "boolean", null, "false");
        //byte
        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveByte", innerComponentSpecifiedClass, factoryMethodName, "primByte", null, "-128");
        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedByte", innerComponentSpecifiedClass, factoryMethodName, "byte", null, "127");
        //double
        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveDouble", innerComponentSpecifiedClass, factoryMethodName, "primDouble", null, "4.9e-324");
        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedDouble", innerComponentSpecifiedClass, factoryMethodName, "double", null, "1.7976931348623157E308");
        //float
        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveFloat", innerComponentSpecifiedClass, factoryMethodName, "primFloat", null,"1.4E-45");
        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedFloat", innerComponentSpecifiedClass, factoryMethodName, "float", null,"3.4028235E38");
        //int
        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveInteger", innerComponentSpecifiedClass, factoryMethodName, "primInteger", null, "-2147483648");
        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedInteger", innerComponentSpecifiedClass, factoryMethodName, "integer", null,"2147483647");
        //char
        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveCharacter", innerComponentSpecifiedClass, factoryMethodName, "primCharacter",null, "a");
        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedCharacter", innerComponentSpecifiedClass, factoryMethodName, "character",null, "A");
        //short
        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveShort", innerComponentSpecifiedClass, factoryMethodName, "primShort", null,"-32768");
        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedShort", innerComponentSpecifiedClass, factoryMethodName, "short", null, "32767");
        //long
        this.addPropertyMetadataValidator(startEvents, "compInnerPrimitiveLong", innerComponentSpecifiedClass, factoryMethodName, "primLong", null, "-9223372036854775808");
        this.addPropertyMetadataValidator(startEvents, "compInnerWrapperedLong", innerComponentSpecifiedClass, factoryMethodName, "long", null,"9223372036854775807");
    }

    public void testPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // value test
        this.addPropertyValueTestItems(startEvents, PropertyInjection.class);
        // property metadata test
        this.addPropertyMetadataTestItem(startEvents, PropertyInjection.class, null);

        controller.run();
    }

    public void testInstanceFactoryPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // value test
        this.addPropertyValueTestItems(startEvents, PropertyInjection.class);
        // property metadata test
        // TODO: BeanComponent, wait bugzilla 1230 1231,
        this.addPropertyMetadataTestItem(startEvents, null, "makeInstance");

        controller.run();
    }

    public void testStaticFactoryPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_static_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // value test
        this.addPropertyValueTestItems(startEvents, PropertyInjection.class);
        // property metadata test
        // TODO: BeanComponent, wait bugzilla 1230 1231,
        this.addPropertyMetadataTestItem(startEvents, PropertyInjectionStaticFactory.class, "makeInstance");

        controller.run();
    }


    private TestComponentValue makeTestComponentValue(Class innerComponentClass, String factoryMethodName,
            Class innerArgTargetType, Class innerArgValueType, String innerArgSource){
        return new TestComponentValue(
                new BeanComponent(
                        null,
                        innerComponentClass,
                        factoryMethodName,
                        null,
                        null,
                        new TestArgument[] { new StringArgument(innerArgTargetType, innerArgSource, innerArgValueType) },
                        null,
                        null,
 						BeanMetadata.INITIALIZATION_EAGER,
 						BeanMetadata.SCOPE_PROTOTYPE
                )
        );
    }

    // Collection Test
    public void testCollectionInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/inner_component_collection_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // List - meta
        TestComponentValue testComponentValue1 = this.makeTestComponentValue(ConstructorInjection.class, null, null, null, "compInner1");
        TestComponentValue testComponentValue2 = this.makeTestComponentValue(ConstructorInjection.class, null, null, null, "compInner2");
        startEvents.addValidator(
                new ArgumentMetadataValidator(
                        "compInnerList",
                        new TestArgument[] {
                                new TestArgument(
                                        new TestListValue(
                                                new TestValue[] { testComponentValue1, testComponentValue2 }
                                        )
                                )
                        }
                )
        );
        // List - value
        ConstructorInjection inner1 = new ConstructorInjection("compInner1");
        ConstructorInjection inner2 = new ConstructorInjection("compInner2");
        List expectedList = new ArrayList();
        expectedList.add(inner1);
        expectedList.add(inner2);
        this.addConstructorValueValidator(startEvents, "compInnerList", expectedList, List.class);




        // Set - meta
        TestComponentValue testComponentValue3 = this.makeTestComponentValue(ConstructorInjection.class, null, null, null, "compInner3");
        TestComponentValue testComponentValue4 = new TestComponentValue(
                new BeanComponent(
                        null,
                        PropertyInjection.class,
                        null,
                        null,
                        null,
                        null,
                        new TestProperty[] { new StringProperty("string", null, "compInner4") },
                     	null,
 						BeanMetadata.INITIALIZATION_EAGER,
 						BeanMetadata.SCOPE_PROTOTYPE
                )
        );
        startEvents.addValidator(
                new ArgumentMetadataValidator(
                        "compInnerSet",
                        new TestArgument[] {
                                new TestArgument(
                                        new TestSetValue(
                                                new TestValue[] { testComponentValue3, testComponentValue4 }
                                        )
                                )
                        }
                )
        );


        // Set - value
        ConstructorInjection inner3 = new ConstructorInjection("compInner3");
        PropertyInjection inner4 = new PropertyInjection();
        inner4.setString("compInner4");
        Set expectedSet = new LinkedHashSet();
        expectedSet.add(inner3);
        expectedSet.add(inner4);
        this.addConstructorValueValidator(startEvents, "compInnerSet", expectedSet, Set.class);



        // Map - meta
        TestComponentValue compInnerEntryKey1 = this.makeTestComponentValue(ConstructorInjection.class, null, null, null, "compInnerEntryKey1");
        TestComponentValue compInnerEntryValue1 = this.makeTestComponentValue(ConstructorInjection.class, null, null, null, "compInnerEntryValue1");
        TestComponentValue compInnerEntryKey2 = new TestComponentValue(
        new BeanComponent(
                null,
                PropertyInjection.class,
                null,
                null,
                null,
                null,
                new TestProperty[] { new StringProperty("string", null, "compInnerEntryKey2") },
                null,
 				BeanMetadata.INITIALIZATION_EAGER,
 				BeanMetadata.SCOPE_PROTOTYPE
                )
        );
        TestComponentValue compInnerEntryValue2 =  new TestComponentValue(
                new BeanComponent(
                        null,
                        PropertyInjection.class,
                        null,
                        null,
                        null,
                        null,
                        new TestProperty[] { new StringProperty("string", null, "compInnerEntryValue2") },
                        null,
                        BeanMetadata.INITIALIZATION_EAGER,
                        BeanMetadata.SCOPE_PROTOTYPE
                        )
                );

        startEvents.addValidator(
                new ArgumentMetadataValidator(
                        "compInnerMap",
                        new TestArgument[] {
                                new TestArgument(
                                        new TestMapValue(
                                                new MapValueEntry[] {
                                                        new MapValueEntry(compInnerEntryKey1, compInnerEntryValue1),
                                                        new MapValueEntry(compInnerEntryKey2, compInnerEntryValue2)
                                                }
                                        )
                                )
                        }
                )
        );




        // Map - value
        ConstructorInjection innerKey1 = new ConstructorInjection("compInnerEntryKey1");
        ConstructorInjection innerValue1 = new ConstructorInjection("compInnerEntryValue1");
        PropertyInjection innerKey2 = new PropertyInjection();
        innerKey2.setString("compInnerEntryKey2");
        PropertyInjection innerValue2 = new PropertyInjection();
        innerValue2.setString("compInnerEntryValue2");

        Map expectedMap = new HashMap();
        expectedMap.put(innerKey1, innerValue1);
        expectedMap.put(innerKey2, innerValue2);

        this.addConstructorValueValidator(startEvents, "compInnerMap", expectedMap, Map.class);


        controller.run();
    }
}
