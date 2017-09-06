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
import org.osgi.test.cases.blueprint.framework.TestArgument;
import org.osgi.test.cases.blueprint.framework.TestNullValue;
import org.osgi.test.cases.blueprint.framework.TestProperty;
import org.osgi.test.cases.blueprint.framework.TestStringValue;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains primitive string to type injection convertions
 *
 * @author $Id$
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
        startEvents.addAssertion("comp1", AssertionService.BEAN_CREATED);
        // no argument validator
        startEvents.addValidator(new ArgumentMetadataValidator("compNoArg"));

        // single string argument
        startEvents.validateComponentArgument("compOneArg", "arg1", "compOneArg");
        // just a single argument, no explicit typing
        startEvents.addValidator(new ArgumentMetadataValidator("compOneArg", new TestArgument[] {
            new StringArgument(String.class, "compOneArg")
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
        addConstructorValueValidator(startEvents, "compPrimByteZero", Byte.valueOf((byte)0), Byte.TYPE);
        addConstructorValueValidator(startEvents, "compPrimByteMax", Byte.valueOf(Byte.MAX_VALUE), Byte.TYPE);
        addConstructorValueValidator(startEvents, "compPrimByteMin", Byte.valueOf(Byte.MIN_VALUE), Byte.TYPE);

        // Wrappered byte tests
        addConstructorValueValidator(startEvents, "compWrapperedByteZero", Byte.valueOf((byte)0), Byte.class);
        addConstructorValueValidator(startEvents, "compWrapperedByteMax", Byte.valueOf(Byte.MAX_VALUE), Byte.class);
        addConstructorValueValidator(startEvents, "compWrapperedByteMin", Byte.valueOf(Byte.MIN_VALUE), Byte.class);
        addConstructorNullValidator(startEvents, "compWrapperedByteNull", Byte.class);

        // Primitive short tests
        addConstructorValueValidator(startEvents, "compPrimShortZero", Short.valueOf((short)0), Short.TYPE);
        addConstructorValueValidator(startEvents, "compPrimShortMax", Short.valueOf(Short.MAX_VALUE), Short.TYPE);

        // Wrappered short tests
        addConstructorValueValidator(startEvents, "compWrapperedShortZero", Short.valueOf((short)0), Short.class);
        addConstructorValueValidator(startEvents, "compWrapperedShortMax", Short.valueOf(Short.MAX_VALUE), Short.class);
        addConstructorNullValidator(startEvents, "compWrapperedShortNull", Short.class);

        // Primitive integer tests
        addConstructorValueValidator(startEvents, "compPrimIntegerZero", Integer.valueOf(0), Integer.TYPE);
        addConstructorValueValidator(startEvents, "compPrimIntegerMax", Integer.valueOf(Integer.MAX_VALUE), Integer.TYPE);
        addConstructorValueValidator(startEvents, "compPrimIntegerMin", Integer.valueOf(Integer.MIN_VALUE), Integer.TYPE);

        // Wrappered integer tests
        addConstructorValueValidator(startEvents, "compWrapperedIntegerZero", Integer.valueOf(0), Integer.class);
        addConstructorValueValidator(startEvents, "compWrapperedIntegerMax", Integer.valueOf(Integer.MAX_VALUE), Integer.class);
        addConstructorValueValidator(startEvents, "compWrapperedIntegerMin", Integer.valueOf(Integer.MIN_VALUE), Integer.class);
        addConstructorNullValidator(startEvents, "compWrapperedIntegerNull", Integer.class);

        // Primitive long tests
        addConstructorValueValidator(startEvents, "compPrimLongZero", Long.valueOf(0), Long.TYPE);
        addConstructorValueValidator(startEvents, "compPrimLongMax", Long.valueOf(Long.MAX_VALUE), Long.TYPE);
        addConstructorValueValidator(startEvents, "compPrimLongMin", Long.valueOf(Long.MIN_VALUE), Long.TYPE);

        // Wrappered long tests
        addConstructorValueValidator(startEvents, "compWrapperedLongZero", Long.valueOf(0), Long.class);
        addConstructorValueValidator(startEvents, "compWrapperedLongMax", Long.valueOf(Long.MAX_VALUE), Long.class);
        addConstructorValueValidator(startEvents, "compWrapperedLongMin", Long.valueOf(Long.MIN_VALUE), Long.class);
        addConstructorNullValidator(startEvents, "compWrapperedLongNull", Long.class);

        // Primitive character tests.  We validate the metadata because of the special processing that
        // character values receive
        addConstructorValueValidator(startEvents, "compPrimCharacterZero", Character.valueOf('\0'), Character.TYPE);
        // type is specified on the constructor arg, nothing on the value element
        addArgumentMetadataValidator(startEvents, "compPrimCharacterZero", "\\u0000", Character.TYPE, null);
        addConstructorValueValidator(startEvents, "compPrimCharacterMax", Character.valueOf(Character.MAX_VALUE), Character.TYPE);
        // type is specified on the constructor arg, nothing on the value element
        addArgumentMetadataValidator(startEvents, "compPrimCharacterMax", "\\uffff", Character.TYPE, null);

        addConstructorValueValidator(startEvents, "compPrimCharacterA", Character.valueOf('A'), Character.TYPE);

        // Wrappered character tests
        addConstructorValueValidator(startEvents, "compWrapperedCharacterZero", Character.valueOf('\0'), Character.class);
        addConstructorValueValidator(startEvents, "compWrapperedCharacterMax", Character.valueOf(Character.MAX_VALUE), Character.class);
        addConstructorValueValidator(startEvents, "compWrapperedCharacterA", Character.valueOf('A'), Character.class);
        addConstructorNullValidator(startEvents, "compWrapperedCharacterNull", Character.class);

        // Primitive double tests
        addConstructorValueValidator(startEvents, "compPrimDoubleZero", Double.valueOf(0), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleMinusZero", Double.valueOf("-0.0"), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleMax", Double.valueOf(Double.MAX_VALUE), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleMin", Double.valueOf(Double.MIN_VALUE), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleDSuffix", Double.valueOf(1.1), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubledSuffix", Double.valueOf(1.1), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleFSuffix", Double.valueOf(1.1), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoublefSuffix", Double.valueOf(1.1e1), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleNaN", Double.valueOf(Double.NaN), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleInfinity", Double.valueOf(Double.POSITIVE_INFINITY), Double.TYPE);
        addConstructorValueValidator(startEvents, "compPrimDoubleNegInfinity", Double.valueOf(Double.NEGATIVE_INFINITY), Double.TYPE);

        // Wrappered double tests
        addConstructorValueValidator(startEvents, "compWrapperedDoubleZero", Double.valueOf(0), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleMinusZero", Double.valueOf("-0.0"), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleMax", Double.valueOf(Double.MAX_VALUE), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleMin", Double.valueOf(Double.MIN_VALUE), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleDSuffix", Double.valueOf(1.1), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubledSuffix", Double.valueOf(1.1), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleFSuffix", Double.valueOf(1.1), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoublefSuffix", Double.valueOf(1.1e1), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleNaN", Double.valueOf(Double.NaN), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleInfinity", Double.valueOf(Double.POSITIVE_INFINITY), Double.class);
        addConstructorValueValidator(startEvents, "compWrapperedDoubleNegInfinity", Double.valueOf(Double.NEGATIVE_INFINITY), Double.class);
        addConstructorNullValidator(startEvents, "compWrapperedDoubleNull", Double.class);

        // Primitive float tests
        addConstructorValueValidator(startEvents, "compPrimFloatZero", Float.valueOf(0), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatMinusZero", Float.valueOf("-0.0"), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatMax", Float.valueOf(Float.MAX_VALUE), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatMin", Float.valueOf(Float.MIN_VALUE), Float.TYPE);
		addConstructorValueValidator(startEvents, "compPrimFloatDSuffix",
				Float.valueOf(1.1f), Float.TYPE);
		addConstructorValueValidator(startEvents, "compPrimFloatdSuffix",
				Float.valueOf(1.1f), Float.TYPE);
		addConstructorValueValidator(startEvents, "compPrimFloatFSuffix",
				Float.valueOf(1.1f), Float.TYPE);
		addConstructorValueValidator(startEvents, "compPrimFloatfSuffix",
				Float.valueOf(1.1e1f), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatNaN", Float.valueOf(Float.NaN), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatInfinity", Float.valueOf(Float.POSITIVE_INFINITY), Float.TYPE);
        addConstructorValueValidator(startEvents, "compPrimFloatNegInfinity", Float.valueOf(Float.NEGATIVE_INFINITY), Float.TYPE);

        // Wrappered float tests
        addConstructorValueValidator(startEvents, "compWrapperedFloatZero", Float.valueOf(0), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatMinusZero", Float.valueOf("-0.0"), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatMax", Float.valueOf(Float.MAX_VALUE), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatMin", Float.valueOf(Float.MIN_VALUE), Float.class);
		addConstructorValueValidator(startEvents, "compWrapperedFloatDSuffix",
				Float.valueOf(1.1f), Float.class);
		addConstructorValueValidator(startEvents, "compWrapperedFloatdSuffix",
				Float.valueOf(1.1f), Float.class);
		addConstructorValueValidator(startEvents, "compWrapperedFloatFSuffix",
				Float.valueOf(1.1f), Float.class);
		addConstructorValueValidator(startEvents, "compWrapperedFloatfSuffix",
				Float.valueOf(1.1e1f), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatNaN", Float.valueOf(Float.NaN), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatInfinity", Float.valueOf(Float.POSITIVE_INFINITY), Float.class);
        addConstructorValueValidator(startEvents, "compWrapperedFloatNegInfinity", Float.valueOf(Float.NEGATIVE_INFINITY), Float.class);
        addConstructorNullValidator(startEvents, "compWrapperedFloatNull", Float.class);

        // now the inferred type tests
        addConstructorValueValidator(startEvents, "constructorPrimitiveBoolean", Boolean.TRUE, Boolean.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedBoolean", Boolean.FALSE, Boolean.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveByte", Byte.valueOf((byte)123), Byte.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedByte", Byte.valueOf((byte)100), Byte.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveDouble", Double.valueOf(1.1), Double.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedDouble", Double.valueOf(1.2), Double.class);
		addConstructorValueValidator(startEvents, "constructorPrimitiveFloat",
				Float.valueOf(1.3f), Float.TYPE);
		addConstructorValueValidator(startEvents, "constructorWrapperedFloat",
				Float.valueOf(1.4f), Float.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveInteger", Integer.valueOf(12), Integer.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedInteger", Integer.valueOf(13), Integer.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveCharacter", Character.valueOf('a'), Character.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedCharacter", Character.valueOf('b'), Character.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveShort", Short.valueOf((short)15), Short.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedShort", Short.valueOf((short)16), Short.class);
        addConstructorValueValidator(startEvents, "constructorPrimitiveLong", Long.valueOf(17), Long.TYPE);
        addConstructorValueValidator(startEvents, "constructorWrapperedLong", Long.valueOf(18), Long.class);
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
        addPropertyValidator(startEvents, "compPrimByteZero", "primByte", "0", Byte.valueOf((byte)0), Byte.TYPE);
        addPropertyValidator(startEvents, "compPrimByteMax", "primByte", "127", Byte.valueOf(Byte.MAX_VALUE), Byte.TYPE);
        addPropertyValidator(startEvents, "compPrimByteMin", "primByte", "-128", Byte.valueOf(Byte.MIN_VALUE), Byte.TYPE);

        // Wrappered byte tests
        addPropertyValidator(startEvents, "compWrapperedByteZero", "byte", "0", Byte.valueOf((byte)0), Byte.class);
        addPropertyValidator(startEvents, "compWrapperedByteMax", "byte", "127", Byte.valueOf(Byte.MAX_VALUE), Byte.class);
        addPropertyValidator(startEvents, "compWrapperedByteMin", "byte", "-128", Byte.valueOf(Byte.MIN_VALUE), Byte.class);
        addPropertyNullValidator(startEvents, "compWrapperedByteNull", "byte", Byte.class);

        // Primitive short tests
        addPropertyValidator(startEvents, "compPrimShortZero", "primShort", "0", Short.valueOf((short)0), Short.TYPE);
        addPropertyValidator(startEvents, "compPrimShortMax", "primShort", "32767", Short.valueOf(Short.MAX_VALUE), Short.TYPE);
        addPropertyValidator(startEvents, "compPrimShortMin", "primShort", "-32768", Short.valueOf(Short.MIN_VALUE), Short.TYPE);

        // Wrappered short tests
        addPropertyValidator(startEvents, "compWrapperedShortZero", "short", "0", Short.valueOf((short)0), Short.class);
        addPropertyValidator(startEvents, "compWrapperedShortMax", "short", "32767", Short.valueOf(Short.MAX_VALUE), Short.class);
        addPropertyValidator(startEvents, "compWrapperedShortMin", "short", "-32768", Short.valueOf(Short.MIN_VALUE), Short.class);
        addPropertyNullValidator(startEvents, "compWrapperedShortNull", "short", Short.class);

        // Primitive integer tests
        addPropertyValidator(startEvents, "compPrimIntegerZero", "primInteger", "0", Integer.valueOf(0), Integer.TYPE);
        addPropertyValidator(startEvents, "compPrimIntegerMax", "primInteger", "2147483647", Integer.valueOf(Integer.MAX_VALUE), Integer.TYPE);
        addPropertyValidator(startEvents, "compPrimIntegerMin", "primInteger", "-2147483648", Integer.valueOf(Integer.MIN_VALUE), Integer.TYPE);

        // Wrappered integer tests
        addPropertyValidator(startEvents, "compWrapperedIntegerZero", "integer", "0", Integer.valueOf(0), Integer.class);
        addPropertyValidator(startEvents, "compWrapperedIntegerMax", "integer", "2147483647", Integer.valueOf(Integer.MAX_VALUE), Integer.class);
        addPropertyValidator(startEvents, "compWrapperedIntegerMin", "integer", "-2147483648", Integer.valueOf(Integer.MIN_VALUE), Integer.class);
        addPropertyNullValidator(startEvents, "compWrapperedIntegerNull", "integer", Integer.class);

        // Primitive long tests
        addPropertyValidator(startEvents, "compPrimLongZero", "primLong", "0", Long.valueOf(0), Long.TYPE);
        addPropertyValidator(startEvents, "compPrimLongMax", "primLong", "9223372036854775807", Long.valueOf(Long.MAX_VALUE), Long.TYPE);
        addPropertyValidator(startEvents, "compPrimLongMin", "primLong", "-9223372036854775808", Long.valueOf(Long.MIN_VALUE), Long.TYPE);

        // Wrappered long tests
        addPropertyValidator(startEvents, "compWrapperedLongZero", "long", "0", Long.valueOf(0), Long.class);
        addPropertyValidator(startEvents, "compWrapperedLongMax", "long", "9223372036854775807", Long.valueOf(Long.MAX_VALUE), Long.class);
        addPropertyValidator(startEvents, "compWrapperedLongMin", "long", "-9223372036854775808", Long.valueOf(Long.MIN_VALUE), Long.class);
        addPropertyNullValidator(startEvents, "compWrapperedLongNull", "long", Long.class);

        // Primitive character tests
        addPropertyValidator(startEvents, "compPrimCharacterZero", "primCharacter", "\\u0000", Character.valueOf('\0'), Character.TYPE);
        addPropertyValidator(startEvents, "compPrimCharacterMax", "primCharacter", "\\uffff", Character.valueOf(Character.MAX_VALUE), Character.TYPE);
        addPropertyValidator(startEvents, "compPrimCharacterA", "primCharacter", "A", Character.valueOf('A'), Character.TYPE);

        // Wrappered character tests
        addPropertyValidator(startEvents, "compWrapperedCharacterZero", "character", "\\u0000", Character.valueOf('\0'), Character.class);
        addPropertyValidator(startEvents, "compWrapperedCharacterMax", "character", "\\uffff", Character.valueOf(Character.MAX_VALUE), Character.class);
        addPropertyValidator(startEvents, "compWrapperedCharacterA", "character", "A", Character.valueOf('A'), Character.class);
        addPropertyNullValidator(startEvents, "compWrapperedCharacterNull", "character", Character.class);

        // Primitive double tests
        addPropertyValidator(startEvents, "compPrimDoubleZero", "primDouble", "0", Double.valueOf(0), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleMinusZero", "primDouble", "-0.0", Double.valueOf("-0.0"), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleMax", "primDouble", "1.7976931348623157E308", Double.valueOf(Double.MAX_VALUE), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleMin", "primDouble", " 4.9e-324 ", Double.valueOf(Double.MIN_VALUE), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleDSuffix", "primDouble", "1.1D ", Double.valueOf(1.1), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubledSuffix", "primDouble", " 1.1d", Double.valueOf(1.1), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleFSuffix", "primDouble", " 1.1F", Double.valueOf(1.1), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoublefSuffix", "primDouble", "1.1e1f", Double.valueOf(1.1e1), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleNaN", "primDouble", "NaN", Double.valueOf(Double.NaN), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleInfinity", "primDouble", "Infinity", Double.valueOf(Double.POSITIVE_INFINITY), Double.TYPE);
        addPropertyValidator(startEvents, "compPrimDoubleNegInfinity", "primDouble", "-Infinity", Double.valueOf(Double.NEGATIVE_INFINITY), Double.TYPE);

        // Wrappered double tests
        addPropertyValidator(startEvents, "compWrapperedDoubleZero", "double", "0", Double.valueOf(0), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleMinusZero", "double", "-0.0", Double.valueOf("-0.0"), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleMax", "double", "1.7976931348623157E308", Double.valueOf(Double.MAX_VALUE), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleMin", "double", " 4.9e-324 ", Double.valueOf(Double.MIN_VALUE), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleDSuffix", "double", "1.1D ", Double.valueOf(1.1), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubledSuffix", "double", " 1.1d", Double.valueOf(1.1), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleFSuffix", "double", " 1.1F", Double.valueOf(1.1), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoublefSuffix", "double", "1.1e1f", Double.valueOf(1.1e1), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleNaN", "double", "NaN", Double.valueOf(Double.NaN), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleInfinity", "double", "Infinity", Double.valueOf(Double.POSITIVE_INFINITY), Double.class);
        addPropertyValidator(startEvents, "compWrapperedDoubleNegInfinity", "double", "-Infinity", Double.valueOf(Double.NEGATIVE_INFINITY), Double.class);
        addPropertyNullValidator(startEvents, "compWrapperedDoubleNull", "double", Double.class);

        // Primitive float tests
        addPropertyValidator(startEvents, "compPrimFloatZero", "primFloat", "0", Float.valueOf(0), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatMinusZero", "primFloat", "-0.0", Float.valueOf("-0.0"), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatMax", "primFloat", "3.4028235E38", Float.valueOf(Float.MAX_VALUE), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatMin", "primFloat", " 1.4E-45 ", Float.valueOf(Float.MIN_VALUE), Float.TYPE);
		addPropertyValidator(startEvents, "compPrimFloatDSuffix", "primFloat",
				"1.1D ", Float.valueOf(1.1f), Float.TYPE);
		addPropertyValidator(startEvents, "compPrimFloatdSuffix", "primFloat",
				" 1.1d", Float.valueOf(1.1f), Float.TYPE);
		addPropertyValidator(startEvents, "compPrimFloatFSuffix", "primFloat",
				" 1.1F", Float.valueOf(1.1f), Float.TYPE);
		addPropertyValidator(startEvents, "compPrimFloatfSuffix", "primFloat",
				"1.1e1f", Float.valueOf(1.1e1f), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatNaN", "primFloat", "NaN", Float.valueOf(Float.NaN), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatInfinity", "primFloat", "Infinity", Float.valueOf(Float.POSITIVE_INFINITY), Float.TYPE);
        addPropertyValidator(startEvents, "compPrimFloatNegInfinity", "primFloat", "-Infinity", Float.valueOf(Float.NEGATIVE_INFINITY), Float.TYPE);

        // Wrappered float tests
        addPropertyValidator(startEvents, "compWrapperedFloatZero", "float", "0", Float.valueOf(0), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatMinusZero", "float", "-0.0", Float.valueOf("-0.0"), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatMax", "float", "3.4028235E38", Float.valueOf(Float.MAX_VALUE), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatMin", "float", " 1.4E-45 ", Float.valueOf(Float.MIN_VALUE), Float.class);
		addPropertyValidator(startEvents, "compWrapperedFloatDSuffix", "float",
				"1.1D ", Float.valueOf(1.1f), Float.class);
		addPropertyValidator(startEvents, "compWrapperedFloatdSuffix", "float",
				" 1.1d", Float.valueOf(1.1f), Float.class);
		addPropertyValidator(startEvents, "compWrapperedFloatFSuffix", "float",
				" 1.1F", Float.valueOf(1.1f), Float.class);
		addPropertyValidator(startEvents, "compWrapperedFloatfSuffix", "float",
				"1.1e1f", Float.valueOf(1.1e1f), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatNaN", "float", "NaN", Float.valueOf(Float.NaN), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatInfinity", "float", "Infinity", Float.valueOf(Float.POSITIVE_INFINITY), Float.class);
        addPropertyValidator(startEvents, "compWrapperedFloatNegInfinity", "float", "-Infinity", Float.valueOf(Float.NEGATIVE_INFINITY), Float.class);
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

    /*
     * Tests boxing/unboxing behavior of primitive type values
     */
    public void testPropertyBoxing() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/primitive_unboxing_injection.jar");

        MetadataEventSet startEvents = controller.getStartEvents();

        addPropertyValidator(startEvents, "primBoolean", "primBoolean", "true", Boolean.TRUE, Boolean.TYPE, Boolean.class);
        addPropertyValidator(startEvents, "wrapperedBoolean", "boolean","false", Boolean.FALSE, Boolean.class, Boolean.TYPE);

        addPropertyValidator(startEvents, "primByte", "primByte", "1", Byte.valueOf((byte)1), Byte.TYPE, Byte.class);
        addPropertyValidator(startEvents, "wrapperedByte", "byte","2", Byte.valueOf((byte)2), Byte.class, Byte.TYPE);

        addPropertyValidator(startEvents, "primShort", "primShort", "1", Short.valueOf((short)1), Short.TYPE, Short.class);
        addPropertyValidator(startEvents, "wrapperedShort", "short","2", Short.valueOf((short)2), Short.class, Short.TYPE);

        addPropertyValidator(startEvents, "primDouble", "primDouble", "1", Double.valueOf(1.0), Double.TYPE, Double.class);
        addPropertyValidator(startEvents, "wrapperedDouble", "double","2", Double.valueOf(2.0), Double.class, Double.TYPE);

		addPropertyValidator(startEvents, "primFloat", "primFloat", "1",
				Float.valueOf(1.0f), Float.TYPE, Float.class);
		addPropertyValidator(startEvents, "wrapperedFloat", "float", "2",
				Float.valueOf(2.0f), Float.class, Float.TYPE);

        addPropertyValidator(startEvents, "primInteger", "primInteger", "1", Integer.valueOf(1), Integer.TYPE, Integer.class);
        addPropertyValidator(startEvents, "wrapperedInteger", "integer","2", Integer.valueOf(2), Integer.class, Integer.TYPE);

        addPropertyValidator(startEvents, "primCharacter", "primCharacter", "1", Character.valueOf('1'), Character.TYPE, Character.class);
        addPropertyValidator(startEvents, "wrapperedCharacter", "character","2", Character.valueOf('2'), Character.class, Character.TYPE);
        controller.run();
    }
}
