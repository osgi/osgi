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
import org.osgi.test.cases.blueprint.framework.StringProperty;
import org.osgi.test.cases.blueprint.framework.TestNullValue;
import org.osgi.test.cases.blueprint.framework.TestArgument;
import org.osgi.test.cases.blueprint.framework.TestProperty;
import org.osgi.test.cases.blueprint.framework.TestStringValue;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains primitive string to type injection convertions
 *
 * @version $Revision$
 */
public class TestPrimitiveInjection extends DefaultTestBundleControl {
    private void addConstructorValueValidator(MetadataEventSet startEvents, String id, Object value, Class type) {
        startEvents.validateComponentArgument(id, "arg1", value, type);
    }

    private void addArgumentMetadataValidator(MetadataEventSet startEvents, String id, String source, Class argType, Class valueType) {
        startEvents.addValidator(new ArgumentMetadataValidator(id, new TestArgument[] {
            new StringArgument(argType, source, valueType)
        }));
    }

    private void addConstructorNullValidator(MetadataEventSet startEvents, String id, Class targetType) {
        startEvents.validateComponentArgument(id, "arg1", null, targetType);
        startEvents.addValidator(new ArgumentMetadataValidator(id, new TestArgument[] {
            new TestArgument(new TestNullValue(), targetType)
        }));
    }

    public void addConstructorTests(StandardTestController controller) {
        MetadataEventSet startEvents = controller.getStartEvents();
        // no argument constructor...just gets created
        // this uses the default component name
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        // no argument validator
        startEvents.addValidator(new ArgumentMetadataValidator("compNoArg"));

        // single string argument
        startEvents.validateComponentArgument("compOneArg", "arg1", "compOneArg");
        // just a single argument, no explicit typing
        startEvents.addValidator(new ArgumentMetadataValidator("compOneArg", new TestArgument[] {
            new StringArgument("compOneArg")
        }));
        // Two String arguments with implicit order
        startEvents.validateComponentArgument("compImplicitString", "arg1", "compImplicitString");
        startEvents.validateComponentArgument("compImplicitString", "arg2", "ABC");
        startEvents.addValidator(new ArgumentMetadataValidator("compImplicitString", new TestArgument[] {
            new StringArgument("compImplicitString"),
            new StringArgument("ABC")
        }));

        // Two String arguments with explicit order
        // TODO:  Bugzilla 1163 -- these may need adjusting once 1163 is resolved.  The arguments are supposed to be
        // in index order, not specification order
        startEvents.validateComponentArgument("compIndexedString", "arg1", "compIndexedString");
        startEvents.validateComponentArgument("compIndexedString", "arg2", "ABC");
        startEvents.addValidator(new ArgumentMetadataValidator("compIndexedString", new TestArgument[] {
            new TestArgument(new TestStringValue("ABC"), 1),
            new TestArgument(new TestStringValue("compIndexedString"), 0)
        }));

        // Three string arguments with different types in correct argument order
        startEvents.validateComponentArgument("compThreeArgOverride", "arg1", "compThreeArgOverride");
        // expect the second argument to match with the explicit string null argument
        startEvents.validateComponentArgument("compThreeArgOverride", "arg2", null, String.class);
        // The boolean type argument can only match the last one (Object)
        startEvents.validateComponentArgument("compThreeArgOverride", "arg3", Boolean.TRUE, Boolean.class);
        startEvents.addValidator(new ArgumentMetadataValidator("compThreeArgOverride", new TestArgument[] {
            new StringArgument(String.class, "compThreeArgOverride"),
            // TODO:  The order may also need adjusting here.
            new StringArgument(Object.class, "true", Boolean.class),
            new TestArgument(new TestNullValue(), String.class)
        }));

        // Three string arguments with different types using different argument order that requires
        // type-matching rules to be applied.
        startEvents.validateComponentArgument("compThreeArgImplicit", "arg1", "compThreeArgImplicit");
        // expect the second argument to match with the explicit string null argument
        startEvents.validateComponentArgument("compThreeArgImplicit", "arg2", null, String.class);
        // The boolean type argument can only match the last one (Object)
        startEvents.validateComponentArgument("compThreeArgImplicit", "arg3", "ABC");
        startEvents.addValidator(new ArgumentMetadataValidator("compThreeArgImplicit", new TestArgument[] {
            new StringArgument("compThreeArgImplicit"),
            new TestArgument(new TestNullValue()),
            new StringArgument("ABC")
        }));

        // Null string valued argument
        startEvents.validateComponentArgument("compZeroLengthString1", "arg2", "");
        startEvents.addValidator(new ArgumentMetadataValidator("compZeroLengthString1", new TestArgument[] {
            // TODO:  Bugzilla 1163 -- these may need adjusting once 1163 is resolved.  The arguments are supposed to be
            // in index order, not specification order
            new TestArgument(new TestStringValue(""), 1),
            new TestArgument(new TestStringValue("compZeroLengthString1"), 0)
        }));

        // Null string valued argument
        startEvents.validateComponentArgument("compZeroLengthString2", "arg2", "");
        startEvents.addValidator(new ArgumentMetadataValidator("compZeroLengthString2", new TestArgument[] {
            // TODO:  Bugzilla 1163 -- these may need adjusting once 1163 is resolved.  The arguments are supposed to be
            // in index order, not specification order
            new TestArgument(new TestStringValue(""), 1),
            new TestArgument(new TestStringValue("compZeroLengthString2"), 0)
        }));

        // Null string valued argument
        startEvents.validateComponentArgument("compZeroLengthString3", "arg2", "");
        startEvents.addValidator(new ArgumentMetadataValidator("compZeroLengthString3", new TestArgument[] {
            // TODO:  Bugzilla 1163 -- these may need adjusting once 1163 is resolved.  The arguments are supposed to be
            // in index order, not specification order
            new TestArgument(new TestStringValue(""), 1),
            new TestArgument(new TestStringValue("compZeroLengthString3"), 0)
        }));

        // null string valued argument
        startEvents.validateComponentArgument("compNullString", "arg2", null, String.class);
        startEvents.addValidator(new ArgumentMetadataValidator("compNullString", new TestArgument[] {
            // TODO:  Bugzilla 1163 -- these may need adjusting once 1163 is resolved.  The arguments are supposed to be
            // in index order, not specification order
            new TestArgument(new TestNullValue(), 1),
            new TestArgument(new TestStringValue("compNullString"), 0)
        }));

        // from this point on, we're only testing the value of a single argument


        // Primitive boolean tests
        addConstructorValueValidator(startEvents, "compPrimBooleanYes", Boolean.TRUE, Boolean.TYPE);
        // type is specified on the constructor arg, nothing on the value element
        addArgumentMetadataValidator(startEvents, "compPrimBooleanYes", "yes", Boolean.TYPE, null);

        addConstructorValueValidator(startEvents, "compPrimBooleanTrue", Boolean.TRUE, Boolean.TYPE);
        // type and value specified on the constructor arg.  The type should stick with the constructor
        addArgumentMetadataValidator(startEvents, "compPrimBooleanTrue", "true", Boolean.TYPE, null);

        addConstructorValueValidator(startEvents, "compPrimBooleanOn", Boolean.TRUE, Boolean.TYPE);
        // type specified on both the constructor argument and the value.
        addArgumentMetadataValidator(startEvents, "compPrimBooleanOn", "on", Boolean.TYPE, Boolean.TYPE);
        // this one is slightly different.  The type is on the <value> tag.
        addConstructorValueValidator(startEvents, "compPrimBooleanNo", Boolean.FALSE, Boolean.TYPE);
        // type specified on the <value> element, not on the constructor arg
        addArgumentMetadataValidator(startEvents, "compPrimBooleanNo", "no", null, Boolean.TYPE);

        addConstructorValueValidator(startEvents, "compPrimBooleanFalse", Boolean.FALSE, Boolean.TYPE);
        addConstructorValueValidator(startEvents, "compPrimBooleanOff", Boolean.FALSE, Boolean.TYPE);

        // Wrappered boolean tests
        addConstructorValueValidator(startEvents, "compWrapperedBooleanYes", Boolean.TRUE, Boolean.class);
        addConstructorValueValidator(startEvents, "compWrapperedBooleanTrue", Boolean.TRUE, Boolean.class);
        addConstructorValueValidator(startEvents, "compWrapperedBooleanOn", Boolean.TRUE, Boolean.class);
        addConstructorValueValidator(startEvents, "compWrapperedBooleanNo", Boolean.FALSE, Boolean.class);
        addConstructorValueValidator(startEvents, "compWrapperedBooleanFalse", Boolean.FALSE, Boolean.class);
        addConstructorValueValidator(startEvents, "compWrapperedBooleanOff", Boolean.FALSE, Boolean.class);
        addConstructorNullValidator(startEvents, "compWrapperedBooleanNull", Boolean.class);

        // Primitive byte tests
        addConstructorValueValidator(startEvents, "compPrimByteZero", new Byte((byte)0), Byte.TYPE);
        addConstructorValueValidator(startEvents, "compPrimByteMax", new Byte(Byte.MAX_VALUE), Byte.TYPE);
        addConstructorValueValidator(startEvents, "compPrimByteMin", new Byte(Byte.MIN_VALUE), Byte.TYPE);

        // Wrappered byte tests
        addConstructorValueValidator(startEvents, "compWrapperedByteZero", new Byte((byte)0), Byte.class);
        addConstructorValueValidator(startEvents, "compWrapperedByteMax", new Byte(Byte.MAX_VALUE), Byte.class);
        addConstructorValueValidator(startEvents, "compWrapperedByteMin", new Byte(Byte.MIN_VALUE), Byte.class);
        addConstructorNullValidator(startEvents, "compWrapperedByteNull", Byte.class);

        // Primitive short tests
        addConstructorValueValidator(startEvents, "compPrimShortZero", new Short((short)0), Short.TYPE);
        addConstructorValueValidator(startEvents, "compPrimShortMax", new Short(Short.MAX_VALUE), Short.TYPE);

        // Wrappered short tests
        addConstructorValueValidator(startEvents, "compWrapperedShortZero", new Short((short)0), Short.class);
        addConstructorValueValidator(startEvents, "compWrapperedShortMax", new Short(Short.MAX_VALUE), Short.class);
        addConstructorNullValidator(startEvents, "compWrapperedShortNull", Short.class);

        // Primitive integer tests
        addConstructorValueValidator(startEvents, "compPrimIntegerZero", new Integer(0), Integer.TYPE);
        addConstructorValueValidator(startEvents, "compPrimIntegerMax", new Integer(Integer.MAX_VALUE), Integer.TYPE);
        addConstructorValueValidator(startEvents, "compPrimIntegerMin", new Integer(Integer.MIN_VALUE), Integer.TYPE);

        // Wrappered integer tests
        addConstructorValueValidator(startEvents, "compWrapperedIntegerZero", new Integer(0), Integer.class);
        addConstructorValueValidator(startEvents, "compWrapperedIntegerMax", new Integer(Integer.MAX_VALUE), Integer.class);
        addConstructorValueValidator(startEvents, "compWrapperedIntegerMin", new Integer(Integer.MIN_VALUE), Integer.class);
        addConstructorNullValidator(startEvents, "compWrapperedIntegerNull", Integer.class);

        // Primitive long tests
        addConstructorValueValidator(startEvents, "compPrimLongZero", new Long(0), Long.TYPE);
        addConstructorValueValidator(startEvents, "compPrimLongMax", new Long(Long.MAX_VALUE), Long.TYPE);
        addConstructorValueValidator(startEvents, "compPrimLongMin", new Long(Long.MIN_VALUE), Long.TYPE);

        // Wrappered long tests
        addConstructorValueValidator(startEvents, "compWrapperedLongZero", new Long(0), Long.class);
        addConstructorValueValidator(startEvents, "compWrapperedLongMax", new Long(Long.MAX_VALUE), Long.class);
        addConstructorValueValidator(startEvents, "compWrapperedLongMin", new Long(Long.MIN_VALUE), Long.class);
        addConstructorNullValidator(startEvents, "compWrapperedLongNull", Long.class);

        // Primitive character tests.  We validate the metadata because of the special processing that
        // character values receive
        addConstructorValueValidator(startEvents, "compPrimCharacterZero", new Character('\0'), Character.TYPE);
        // type is specified on the constructor arg, nothing on the value element
        addArgumentMetadataValidator(startEvents, "compPrimCharacterZero", "\\u0000", Character.TYPE, null);
        addConstructorValueValidator(startEvents, "compPrimCharacterMax", new Character(Character.MAX_VALUE), Character.TYPE);
        // type is specified on the constructor arg, nothing on the value element
        addArgumentMetadataValidator(startEvents, "compPrimCharacterMax", "\\uffff", Character.TYPE, null);

        addConstructorValueValidator(startEvents, "compPrimCharacterA", new Character('A'), Character.TYPE);

        // Wrappered character tests
        addConstructorValueValidator(startEvents, "compWrapperedCharacterZero", new Character('\0'), Character.class);
        addConstructorValueValidator(startEvents, "compWrapperedCharacterMax", new Character(Character.MAX_VALUE), Character.class);
        addConstructorValueValidator(startEvents, "compWrapperedCharacterA", new Character('A'), Character.class);
        addConstructorNullValidator(startEvents, "compWrapperedCharacterNull", Character.class);

        // Primitive double tests
        addConstructorValueValidator(startEvents, "compPrimDoubleZero", new Double(0), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleMinusZero", new Double("-0.0"), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleMax", new Double(Double.MAX_VALUE), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleMin", new Double(Double.MIN_VALUE), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleDSuffix", new Double(1.1), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubledSuffix", new Double(1.1), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleFSuffix", new Double(1.1), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoublefSuffix", new Double(1.1e1), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleNaN", new Double(Double.NaN), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleInfinity", new Double(Double.POSITIVE_INFINITY), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleNegInfinity", new Double(Double.NEGATIVE_INFINITY), Double.TYPE);

        // Wrappered double tests
        addConstructorValueValidator(startEvents, "compWrapperedDoubleZero", new Double(0), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleMinusZero", new Double("-0.0"), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleMax", new Double(Double.MAX_VALUE), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleMin", new Double(Double.MIN_VALUE), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleDSuffix", new Double(1.1), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubledSuffix", new Double(1.1), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleFSuffix", new Double(1.1), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoublefSuffix", new Double(1.1e1), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleNaN", new Double(Double.NaN), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleInfinity", new Double(Double.POSITIVE_INFINITY), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleNegInfinity", new Double(Double.NEGATIVE_INFINITY), Double.class);
        addConstructorNullValidator(startEvents, "compWrapperedDoubleNull", Double.class);

        // Primitive float tests
        addConstructorValueValidator(startEvents, "compPrimFloatZero", new Float(0), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatMinusZero", new Float("-0.0"), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatMax", new Float(Float.MAX_VALUE), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatMin", new Float(Float.MIN_VALUE), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatDSuffix", new Float(1.1), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatdSuffix", new Float(1.1), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatFSuffix", new Float(1.1), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatfSuffix", new Float(1.1e1), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatNaN", new Float(Float.NaN), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatInfinity", new Float(Float.POSITIVE_INFINITY), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatNegInfinity", new Float(Float.NEGATIVE_INFINITY), Float.TYPE);

        // Wrappered float tests
        addConstructorValueValidator(startEvents, "compWrapperedFloatZero", new Float(0), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatMinusZero", new Float("-0.0"), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatMax", new Float(Float.MAX_VALUE), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatMin", new Float(Float.MIN_VALUE), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatDSuffix", new Float(1.1), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatdSuffix", new Float(1.1), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatFSuffix", new Float(1.1), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatfSuffix", new Float(1.1e1), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatNaN", new Float(Float.NaN), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatInfinity", new Float(Float.POSITIVE_INFINITY), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatNegInfinity", new Float(Float.NEGATIVE_INFINITY), Float.class);
        addConstructorNullValidator(startEvents, "compWrapperedFloatNull", Float.class);

        // now the inferred type tests
        addConstructorValueValidator(startEvents, "constructorPrimitiveBoolean", Boolean.TRUE, Boolean.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedBoolean", Boolean.FALSE, Boolean.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveByte", new Byte((byte)123), Byte.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedByte", new Byte((byte)100), Byte.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveDouble", new Double(1.1), Double.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedDouble", new Double(1.2), Double.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveFloat", new Float(1.3), Float.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedFloat", new Float(1.4), Float.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveInteger", new Integer(12), Integer.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedInteger", new Integer(13), Integer.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveCharacter", new Character('a'), Character.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedCharacter", new Character('b'), Character.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveShort", new Short((short)15), Short.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedShort", new Short((short)16), Short.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveLong", new Long((long)17), Long.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedLong", new Long((long)18), Long.class);
    }


	/*
	 * Tests most of the string-to-type constructor conversion and injection.
	 */
	public void testStringTypeConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/primitive_constructor_injection.jar");
        // we have multiple versions of the same tests with different creations.  The same
        // validators are used across the board
        addConstructorTests(controller);
        controller.run();
    }


	/*
	 * Tests most of the string-to-type constructor conversion and injection.
	 */
	public void testStaticFactoryStringTypeConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/primitive_static_factory_constructor_injection.jar");
        // we have multiple versions of the same tests with different creations.  The same
        // validators are used across the board
        addConstructorTests(controller);
        controller.run();
    }


	/*
	 * Tests most of the string-to-type constructor conversion and injection.
	 */
	public void testInstanceFactoryStringTypeConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/primitive_factory_constructor_injection.jar");
        // we have multiple versions of the same tests with different creations.  The same
        // validators are used across the board
        addConstructorTests(controller);
        controller.run();
    }

	/**
	 * Property Validator
	 * @param startEvents
	 * @param compName
	 * @param propertyName
	 * @param source
	 * @param propertyValue
	 * @param type             property object's real type: primitive or wrapper
	 * @param valueType        <value type="">
	 */
    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName, String source, Object propertyValue, Class type, Class valueType) {
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, type);
        startEvents.addValidator(new PropertyMetadataValidator(compName, new StringProperty(propertyName, valueType, source)));
    }

    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName, String source, Object propertyValue, Class type) {
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, type);
        startEvents.addValidator(new PropertyMetadataValidator(compName, new StringProperty(propertyName, null, source)));
    }

    private void addPropertyNullValidator(MetadataEventSet startEvents, String compName, String propertyName, Class type) {
        startEvents.validateComponentProperty(compName, propertyName, null, type);
        startEvents.addValidator(new PropertyMetadataValidator(compName, new TestProperty(new TestNullValue(), propertyName)));
    }

    public void addPropertyTests(StandardTestController controller) {
        MetadataEventSet startEvents = controller.getStartEvents();
        // no argument constructor...just gets created

        // single string property
        addPropertyValidator(startEvents, "compImplicitString", "string", "ABC", "ABC", String.class);

        // a "" string value
        addPropertyValidator(startEvents, "compZeroLengthString1", "string", "", "", String.class);
        addPropertyValidator(startEvents, "compZeroLengthString2", "string", "", "", String.class);
        addPropertyValidator(startEvents, "compZeroLengthString3", "string", "", "", String.class);

        // a null value
        // this requires a little special handling
        addPropertyNullValidator(startEvents, "compNullString", "string", String.class);

        // Primitive boolean tests
        addPropertyValidator(startEvents, "compPrimBooleanTrue", "primBoolean", "true", Boolean.TRUE, Boolean.TYPE, Boolean.TYPE);  // type specified
        addPropertyValidator(startEvents, "compPrimBooleanYes", "primBoolean", "yes", Boolean.TRUE, Boolean.TYPE);
        addPropertyValidator(startEvents, "compPrimBooleanOn", "primBoolean", "on", Boolean.TRUE, Boolean.TYPE);
        addPropertyValidator(startEvents, "compPrimBooleanFalse", "primBoolean", "false", Boolean.FALSE, Boolean.TYPE);
        addPropertyValidator(startEvents, "compPrimBooleanNo", "primBoolean", "no", Boolean.FALSE, Boolean.TYPE);
        addPropertyValidator(startEvents, "compPrimBooleanOff", "primBoolean", "off", Boolean.FALSE, Boolean.TYPE);

        // Wrappered boolean tests
        addPropertyValidator(startEvents, "compWrapperedBooleanYes", "boolean","yes", Boolean.TRUE, Boolean.class);
        addPropertyValidator(startEvents, "compWrapperedBooleanTrue", "boolean", "true", Boolean.TRUE, Boolean.class);
        addPropertyValidator(startEvents, "compWrapperedBooleanOn", "boolean", "on", Boolean.TRUE, Boolean.class);
        addPropertyValidator(startEvents, "compWrapperedBooleanNo", "boolean", "no", Boolean.FALSE, Boolean.class);
        addPropertyValidator(startEvents, "compWrapperedBooleanFalse", "boolean", "false", Boolean.FALSE, Boolean.class);
        addPropertyValidator(startEvents, "compWrapperedBooleanOff", "boolean", "off", Boolean.FALSE, Boolean.class);
        addPropertyNullValidator(startEvents, "compWrapperedBooleanNull", "boolean", Boolean.class);

        // Primitive byte tests
        addPropertyValidator(startEvents, "compPrimByteZero", "primByte", "0", new Byte((byte)0), Byte.TYPE);
        addPropertyValidator(startEvents, "compPrimByteMax", "primByte", "127", new Byte(Byte.MAX_VALUE), Byte.TYPE);
        addPropertyValidator(startEvents, "compPrimByteMin", "primByte", "-128", new Byte(Byte.MIN_VALUE), Byte.TYPE);

        // Wrappered byte tests
        addPropertyValidator(startEvents, "compWrapperedByteZero", "byte", "0", new Byte((byte)0), Byte.class);
        addPropertyValidator(startEvents, "compWrapperedByteMax", "byte", "127", new Byte(Byte.MAX_VALUE), Byte.class);
        addPropertyValidator(startEvents, "compWrapperedByteMin", "byte", "-128", new Byte(Byte.MIN_VALUE), Byte.class);
        addPropertyNullValidator(startEvents, "compWrapperedByteNull", "byte", Byte.class);

        // Primitive short tests
        addPropertyValidator(startEvents, "compPrimShortZero", "primShort", "0", new Short((short)0), Short.TYPE);
        addPropertyValidator(startEvents, "compPrimShortMax", "primShort", "32767", new Short(Short.MAX_VALUE), Short.TYPE);
        addPropertyValidator(startEvents, "compPrimShortMin", "primShort", "-32768", new Short(Short.MIN_VALUE), Short.TYPE);

        // Wrappered short tests
        addPropertyValidator(startEvents, "compWrapperedShortZero", "short", "0", new Short((short)0), Short.class);
        addPropertyValidator(startEvents, "compWrapperedShortMax", "short", "32767", new Short(Short.MAX_VALUE), Short.class);
        addPropertyValidator(startEvents, "compWrapperedShortMin", "short", "-32768", new Short(Short.MIN_VALUE), Short.class);
        addPropertyNullValidator(startEvents, "compWrapperedShortNull", "short", Short.class);

        // Primitive integer tests
        addPropertyValidator(startEvents, "compPrimIntegerZero", "primInteger", "0", new Integer(0), Integer.TYPE);
        addPropertyValidator(startEvents, "compPrimIntegerMax", "primInteger", "2147483647", new Integer(Integer.MAX_VALUE), Integer.TYPE);
        addPropertyValidator(startEvents, "compPrimIntegerMin", "primInteger", "-2147483648", new Integer(Integer.MIN_VALUE), Integer.TYPE);

        // Wrappered integer tests
        addPropertyValidator(startEvents, "compWrapperedIntegerZero", "integer", "0", new Integer(0), Integer.class);
        addPropertyValidator(startEvents, "compWrapperedIntegerMax", "integer", "2147483647", new Integer(Integer.MAX_VALUE), Integer.class);
        addPropertyValidator(startEvents, "compWrapperedIntegerMin", "integer", "-2147483648", new Integer(Integer.MIN_VALUE), Integer.class);
        addPropertyNullValidator(startEvents, "compWrapperedIntegerNull", "integer", Integer.class);

        // Primitive long tests
        addPropertyValidator(startEvents, "compPrimLongZero", "primLong", "0", new Long(0), Long.TYPE);
        addPropertyValidator(startEvents, "compPrimLongMax", "primLong", "9223372036854775807", new Long(Long.MAX_VALUE), Long.TYPE);
        addPropertyValidator(startEvents, "compPrimLongMin", "primLong", "-9223372036854775808", new Long(Long.MIN_VALUE), Long.TYPE);

        // Wrappered long tests
        addPropertyValidator(startEvents, "compWrapperedLongZero", "long", "0", new Long(0), Long.class);
        addPropertyValidator(startEvents, "compWrapperedLongMax", "long", "9223372036854775807", new Long(Long.MAX_VALUE), Long.class);
        addPropertyValidator(startEvents, "compWrapperedLongMin", "long", "-9223372036854775808", new Long(Long.MIN_VALUE), Long.class);
        addPropertyNullValidator(startEvents, "compWrapperedLongNull", "long", Long.class);

        // Primitive character tests
        addPropertyValidator(startEvents, "compPrimCharacterZero", "primCharacter", "\\u0000", new Character('\0'), Character.TYPE);
        addPropertyValidator(startEvents, "compPrimCharacterMax", "primCharacter", "\\uffff", new Character(Character.MAX_VALUE), Character.TYPE);
        addPropertyValidator(startEvents, "compPrimCharacterA", "primCharacter", "A", new Character('A'), Character.TYPE);

        // Wrappered character tests
        addPropertyValidator(startEvents, "compWrapperedCharacterZero", "character", "\\u0000", new Character('\0'), Character.class);
        addPropertyValidator(startEvents, "compWrapperedCharacterMax", "character", "\\uffff", new Character(Character.MAX_VALUE), Character.class);
        addPropertyValidator(startEvents, "compWrapperedCharacterA", "character", "A", new Character('A'), Character.class);
        addPropertyNullValidator(startEvents, "compWrapperedCharacterNull", "character", Character.class);

        // Primitive double tests
        addPropertyValidator(startEvents, "compPrimDoubleZero", "primDouble", "0", new Double(0), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleMinusZero", "primDouble", "-0.0", new Double("-0.0"), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleMax", "primDouble", "1.7976931348623157E308", new Double(Double.MAX_VALUE), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleMin", "primDouble", " 4.9e-324 ", new Double(Double.MIN_VALUE), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleDSuffix", "primDouble", "1.1D ", new Double(1.1), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubledSuffix", "primDouble", " 1.1d", new Double(1.1), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleFSuffix", "primDouble", " 1.1F", new Double(1.1), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoublefSuffix", "primDouble", "1.1e1f", new Double(1.1e1), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleNaN", "primDouble", "NaN", new Double(Double.NaN), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleInfinity", "primDouble", "Infinity", new Double(Double.POSITIVE_INFINITY), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleNegInfinity", "primDouble", "-Infinity", new Double(Double.NEGATIVE_INFINITY), Double.TYPE);

        // Wrappered double tests
        addPropertyValidator(startEvents, "compWrapperedDoubleZero", "double", "0", new Double(0), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleMinusZero", "double", "-0.0", new Double("-0.0"), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleMax", "double", "1.7976931348623157E308", new Double(Double.MAX_VALUE), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleMin", "double", " 4.9e-324 ", new Double(Double.MIN_VALUE), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleDSuffix", "double", "1.1D ", new Double(1.1), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubledSuffix", "double", " 1.1d", new Double(1.1), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleFSuffix", "double", " 1.1F", new Double(1.1), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoublefSuffix", "double", "1.1e1f", new Double(1.1e1), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleNaN", "double", "NaN", new Double(Double.NaN), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleInfinity", "double", "Infinity", new Double(Double.POSITIVE_INFINITY), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleNegInfinity", "double", "-Infinity", new Double(Double.NEGATIVE_INFINITY), Double.class);
        addPropertyNullValidator(startEvents, "compWrapperedDoubleNull", "double", Double.class);

        // Primitive float tests
        addPropertyValidator(startEvents, "compPrimFloatZero", "primFloat", "0", new Float(0), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatMinusZero", "primFloat", "-0.0", new Float("-0.0"), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatMax", "primFloat", "3.4028235E38", new Float(Float.MAX_VALUE), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatMin", "primFloat", " 1.4E-45 ", new Float(Float.MIN_VALUE), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatDSuffix", "primFloat", "1.1D ", new Float(1.1), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatdSuffix", "primFloat", " 1.1d", new Float(1.1), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatFSuffix", "primFloat", " 1.1F", new Float(1.1), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatfSuffix", "primFloat", "1.1e1f", new Float(1.1e1), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatNaN", "primFloat", "NaN", new Float(Float.NaN), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatInfinity", "primFloat", "Infinity", new Float(Float.POSITIVE_INFINITY), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatNegInfinity", "primFloat", "-Infinity", new Float(Float.NEGATIVE_INFINITY), Float.TYPE);

        // Wrappered float tests
        addPropertyValidator(startEvents, "compWrapperedFloatZero", "float", "0", new Float(0), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatMinusZero", "float", "-0.0", new Float("-0.0"), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatMax", "float", "3.4028235E38", new Float(Float.MAX_VALUE), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatMin", "float", " 1.4E-45 ", new Float(Float.MIN_VALUE), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatDSuffix", "float", "1.1D ", new Float(1.1), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatdSuffix", "float", " 1.1d", new Float(1.1), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatFSuffix", "float", " 1.1F", new Float(1.1), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatfSuffix", "float", "1.1e1f", new Float(1.1e1), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatNaN", "float", "NaN", new Float(Float.NaN), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatInfinity", "float", "Infinity", new Float(Float.POSITIVE_INFINITY), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatNegInfinity", "float", "-Infinity", new Float(Float.NEGATIVE_INFINITY), Float.class);
        addPropertyNullValidator(startEvents, "compWrapperedFloatNull", "float", Float.class);
    }

    /*
     * Tests most of the string-to-type constructor conversion and injection.
     */
    public void testStringTypeProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/primitive_property_injection.jar");
        // single version of these tests too
        addPropertyTests(controller);
        controller.run();
    }

    /*
     * Tests most of the string-to-type constructor conversion and injection.
     */
    public void testStaticFactoryStringTypeProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/primitive_static_factory_property_injection.jar");
        // single version of these tests too
        addPropertyTests(controller);
        controller.run();
    }

    /*
     * Tests most of the string-to-type constructor conversion and injection.
     */
    public void testInstanceFactoryStringTypeProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/primitive_factory_property_injection.jar");
        // single version of these tests too
        addPropertyTests(controller);
        controller.run();
    }
}
