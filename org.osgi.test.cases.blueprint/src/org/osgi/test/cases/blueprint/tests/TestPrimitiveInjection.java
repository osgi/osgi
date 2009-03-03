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

import org.osgi.service.blueprint.reflect.NullValue;
import org.osgi.test.cases.blueprint.framework.ConstructorMetadataValidator;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.PropertyMetadataValidator;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.StringParameter;
import org.osgi.test.cases.blueprint.framework.StringProperty;
import org.osgi.test.cases.blueprint.framework.TestNullValue;
import org.osgi.test.cases.blueprint.framework.TestParameter;
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
    private void addConstructorValidator(MetadataEventSet startEvents, String id, Object value, Class type) {
        startEvents.validateComponentArgument(id, "arg1", value, type);
    }

    private void addConstructorMetadataValidator(MetadataEventSet startEvents, String id, String source, Class argType, Class valueType) {
        startEvents.addValidator(new ConstructorMetadataValidator(id, new TestParameter[] {
            new StringParameter(argType, source, valueType)
        }));
    }

    private void addConstructorValidator(MetadataEventSet startEvents, String id, Class targetType) {
        startEvents.validateComponentArgument(id, "arg1", null, targetType);
        startEvents.addValidator(new ConstructorMetadataValidator(id, new TestParameter[] {
            new TestParameter(new TestNullValue())
        }));
    }

    public void addConstructorTests(StandardTestController controller) {
        MetadataEventSet startEvents = controller.getStartEvents();
        // no argument constructor...just gets created
        // this uses the default component name
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        // no argument validator
        startEvents.addValidator(new ConstructorMetadataValidator("compNoArg"));

        // single string argument
        startEvents.validateComponentArgument("compOneArg", "arg1", "compOneArg");
        // just a single argument, no explicit typing
        startEvents.addValidator(new ConstructorMetadataValidator("compOneArg", new TestParameter[] {
            new StringParameter("compOneArg")
        }));
        // Two String arguments with implicit order
        startEvents.validateComponentArgument("compImplicitString", "arg1", "compImplicitString");
        startEvents.validateComponentArgument("compImplicitString", "arg2", "ABC");
        startEvents.addValidator(new ConstructorMetadataValidator("compImplicitString", new TestParameter[] {
            new StringParameter("compImplicitString"),
            new StringParameter("ABC")
        }));

        // Two String arguments with explicit order
        startEvents.validateComponentArgument("compIndexedString", "arg1", "compIndexedString");
        startEvents.validateComponentArgument("compIndexedString", "arg2", "ABC");
        startEvents.addValidator(new ConstructorMetadataValidator("compIndexedString", new TestParameter[] {
            new TestParameter(new TestStringValue("compIndexedString"), 0),
            new TestParameter(new TestStringValue("ABC"), 1)
        }));

        // Three string arguments with different types in correct argument order
        startEvents.validateComponentArgument("compThreeArgOverride", "arg1", "compThreeArgOverride");
        // expect the second argument to match with the explicit string null argument
        startEvents.validateComponentArgument("compThreeArgOverride", "arg2", null, String.class);
        // The boolean type argument can only match the last one (Object)
        startEvents.validateComponentArgument("compThreeArgOverride", "arg3", Boolean.TRUE, Boolean.class);
        startEvents.addValidator(new ConstructorMetadataValidator("compThreeArgOverride", new TestParameter[] {
            new StringParameter("compThreeArgOverride"),
            new TestParameter(new TestNullValue(), String.class),
            new StringParameter(Boolean.class, "true")
        }));

        // Three string arguments with different types using different argument order that requires
        // type-matching rules to be applied.
        startEvents.validateComponentArgument("compThreeArgImplicit", "arg1", "compThreeArgImplicit");
        // expect the second argument to match with the explicit string null argument
        startEvents.validateComponentArgument("compThreeArgImplicit", "arg2", null, String.class);
        // The boolean type argument can only match the last one (Object)
        startEvents.validateComponentArgument("compThreeArgImplicit", "arg3", "ABC");
        startEvents.addValidator(new ConstructorMetadataValidator("compIndexedString", new TestParameter[] {
            new StringParameter("compThreeArgImplicit"),
            new TestParameter(new TestNullValue()),
            new StringParameter("ABC")
        }));

        // Null string valued argument
        startEvents.validateComponentArgument("compZeroLengthString", "arg2", "");
        startEvents.addValidator(new ConstructorMetadataValidator("compZeroLengthString", new TestParameter[] {
            new TestParameter(new TestStringValue("compZeroLengthString"), 0),
            new TestParameter(new TestStringValue(""), 1)
        }));

        // null string valued argument
        startEvents.validateComponentArgument("compNullString", "arg2", null, String.class);
        startEvents.addValidator(new ConstructorMetadataValidator("compNullString", new TestParameter[] {
            new TestParameter(new TestStringValue("compNullString"), 0),
            new TestParameter(new TestNullValue(), 1)
        }));

        // from this point on, we're only testing the value of a single argument


        // Primitive boolean tests
        addConstructorValidator(startEvents, "compPrimBooleanYes", Boolean.TRUE, Boolean.TYPE);
        // type is specified on the constructor arg, nothing on the value element
        addConstructorMetadataValidator(startEvents, "compPrimBooleanYes", "yes", Boolean.TYPE, null);

        addConstructorValidator(startEvents, "compPrimBooleanTrue", Boolean.TRUE, Boolean.TYPE);
        // type and value specified on the constructor arg.  The type should stick with the constructor
        addConstructorMetadataValidator(startEvents, "compPrimBooleanTrue", "true", Boolean.TYPE, null);

        addConstructorValidator(startEvents, "compPrimBooleanOn", Boolean.TRUE, Boolean.TYPE);
        // type specified on both the constructor argument and the value.
        addConstructorMetadataValidator(startEvents, "compPrimBooleanOn", "on", Boolean.TYPE, Boolean.TYPE);
        // this one is slightly different.  The type is on the <value> tag.
        addConstructorValidator(startEvents, "compPrimBooleanNo", Boolean.FALSE, Boolean.TYPE);
        // type specified on the <value> element, not on the constructor arg
        addConstructorMetadataValidator(startEvents, "compPrimBooleanNo", "no", null, Boolean.TYPE);

        addConstructorValidator(startEvents, "compPrimBooleanFalse", Boolean.FALSE, Boolean.TYPE);
        addConstructorValidator(startEvents, "compPrimBooleanOff", Boolean.FALSE, Boolean.TYPE);

        // Wrappered boolean tests
        addConstructorValidator(startEvents, "compWrapperedBooleanYes", Boolean.TRUE, Boolean.class);
        addConstructorValidator(startEvents, "compWrapperedBooleanTrue", Boolean.TRUE, Boolean.class);
        addConstructorValidator(startEvents, "compWrapperedBooleanOn", Boolean.TRUE, Boolean.class);
        addConstructorValidator(startEvents, "compWrapperedBooleanNo", Boolean.FALSE, Boolean.class);
        addConstructorValidator(startEvents, "compWrapperedBooleanFalse", Boolean.FALSE, Boolean.class);
        addConstructorValidator(startEvents, "compWrapperedBooleanOff", Boolean.FALSE, Boolean.class);
        addConstructorValidator(startEvents, "compWrapperedBooleanNull", Boolean.class);

        // Primitive byte tests
        addConstructorValidator(startEvents, "compPrimByteZero", new Byte((byte)0), Byte.TYPE);
        addConstructorValidator(startEvents, "compPrimByteMax", new Byte(Byte.MAX_VALUE), Byte.TYPE);
        addConstructorValidator(startEvents, "compPrimByteMin", new Byte(Byte.MIN_VALUE), Byte.TYPE);

        // Wrappered byte tests
        addConstructorValidator(startEvents, "compWrapperedByteZero", new Byte((byte)0), Byte.class);
        addConstructorValidator(startEvents, "compWrapperedByteMax", new Byte(Byte.MAX_VALUE), Byte.class);
        addConstructorValidator(startEvents, "compWrapperedByteMin", new Byte(Byte.MIN_VALUE), Byte.class);
        addConstructorValidator(startEvents, "compWrapperedByteNull", Byte.class);

        // Primitive short tests
        addConstructorValidator(startEvents, "compPrimShortZero", new Short((short)0), Short.TYPE);
        addConstructorValidator(startEvents, "compPrimShortMax", new Short(Short.MAX_VALUE), Short.TYPE);

        // Wrappered short tests
        addConstructorValidator(startEvents, "compWrapperedShortZero", new Short((short)0), Short.class);
        addConstructorValidator(startEvents, "compWrapperedShortMax", new Short(Short.MAX_VALUE), Short.class);
        addConstructorValidator(startEvents, "compWrapperedShortNull", Short.class);

        // Primitive integer tests
        addConstructorValidator(startEvents, "compPrimIntegerZero", new Integer(0), Integer.TYPE);
        addConstructorValidator(startEvents, "compPrimIntegerMax", new Integer(Integer.MAX_VALUE), Integer.TYPE);
        addConstructorValidator(startEvents, "compPrimIntegerMin", new Integer(Integer.MIN_VALUE), Integer.TYPE);

        // Wrappered integer tests
        addConstructorValidator(startEvents, "compWrapperedIntegerZero", new Integer(0), Integer.class);
        addConstructorValidator(startEvents, "compWrapperedIntegerMax", new Integer(Integer.MAX_VALUE), Integer.class);
        addConstructorValidator(startEvents, "compWrapperedIntegerMin", new Integer(Integer.MIN_VALUE), Integer.class);
        addConstructorValidator(startEvents, "compWrapperedIntegerNull", Integer.class);

        // Primitive long tests
        addConstructorValidator(startEvents, "compPrimLongZero", new Long(0), Long.TYPE);
        addConstructorValidator(startEvents, "compPrimLongMax", new Long(Long.MAX_VALUE), Long.TYPE);
        addConstructorValidator(startEvents, "compPrimLongMin", new Long(Long.MIN_VALUE), Long.TYPE);

        // Wrappered long tests
        addConstructorValidator(startEvents, "compWrapperedLongZero", new Long(0), Long.class);
        addConstructorValidator(startEvents, "compWrapperedLongMax", new Long(Long.MAX_VALUE), Long.class);
        addConstructorValidator(startEvents, "compWrapperedLongMin", new Long(Long.MIN_VALUE), Long.class);
        addConstructorValidator(startEvents, "compWrapperedLongNull", Long.class);

        // Primitive character tests.  We validate the metadata because of the special processing that
        // character values receive
        addConstructorValidator(startEvents, "compPrimCharacterZero", new Character('\0'), Character.TYPE);
        // type is specified on the constructor arg, nothing on the value element
        addConstructorMetadataValidator(startEvents, "compPrimCharacterZero", "\\u0000", Character.TYPE, null);
        addConstructorValidator(startEvents, "compPrimCharacterMax", new Character(Character.MAX_VALUE), Character.TYPE);
        // type is specified on the constructor arg, nothing on the value element
        addConstructorMetadataValidator(startEvents, "compPrimCharacterMax", "\\uffff", Character.TYPE, null);

        addConstructorValidator(startEvents, "compPrimCharacterA", new Character('A'), Character.TYPE);

        // Wrappered character tests
        addConstructorValidator(startEvents, "compWrapperedCharacterZero", new Character('\0'), Character.class);
        addConstructorValidator(startEvents, "compWrapperedCharacterMax", new Character(Character.MAX_VALUE), Character.class);
        addConstructorValidator(startEvents, "compWrapperedCharacterA", new Character('A'), Character.class);
        addConstructorValidator(startEvents, "compWrapperedCharacterNull", Character.class);

        // Primitive double tests
        addConstructorValidator(startEvents, "compPrimDoubleZero", new Double(0), Double.TYPE);
        addConstructorValidator(startEvents, "compPrimDoubleMinusZero", new Double("-0.0"), Double.TYPE);
        addConstructorValidator(startEvents, "compPrimDoubleMax", new Double(Double.MAX_VALUE), Double.TYPE);
        addConstructorValidator(startEvents, "compPrimDoubleMin", new Double(Double.MIN_VALUE), Double.TYPE);
        addConstructorValidator(startEvents, "compPrimDoubleDSuffix", new Double(1.1), Double.TYPE);
        addConstructorValidator(startEvents, "compPrimDoubledSuffix", new Double(1.1), Double.TYPE);
        addConstructorValidator(startEvents, "compPrimDoubleFSuffix", new Double(1.1), Double.TYPE);
        addConstructorValidator(startEvents, "compPrimDoublefSuffix", new Double(1.1e1), Double.TYPE);
        addConstructorValidator(startEvents, "compPrimDoubleNaN", new Double(Double.NaN), Double.TYPE);
        addConstructorValidator(startEvents, "compPrimDoubleInfinity", new Double(Double.POSITIVE_INFINITY), Double.TYPE);
        addConstructorValidator(startEvents, "compPrimDoubleNegInfinity", new Double(Double.NEGATIVE_INFINITY), Double.TYPE);

        // Wrappered double tests
        addConstructorValidator(startEvents, "compWrapperedDoubleZero", new Double(0), Double.class);
        addConstructorValidator(startEvents, "compWrapperedDoubleMinusZero", new Double("-0.0"), Double.class);
        addConstructorValidator(startEvents, "compWrapperedDoubleMax", new Double(Double.MAX_VALUE), Double.class);
        addConstructorValidator(startEvents, "compWrapperedDoubleMin", new Double(Double.MIN_VALUE), Double.class);
        addConstructorValidator(startEvents, "compWrapperedDoubleDSuffix", new Double(1.1), Double.class);
        addConstructorValidator(startEvents, "compWrapperedDoubledSuffix", new Double(1.1), Double.class);
        addConstructorValidator(startEvents, "compWrapperedDoubleFSuffix", new Double(1.1), Double.class);
        addConstructorValidator(startEvents, "compWrapperedDoublefSuffix", new Double(1.1e1), Double.class);
        addConstructorValidator(startEvents, "compWrapperedDoubleNaN", new Double(Double.NaN), Double.class);
        addConstructorValidator(startEvents, "compWrapperedDoubleInfinity", new Double(Double.POSITIVE_INFINITY), Double.class);
        addConstructorValidator(startEvents, "compWrapperedDoubleNegInfinity", new Double(Double.NEGATIVE_INFINITY), Double.class);
        addConstructorValidator(startEvents, "compWrapperedDoubleNull", Double.class);

        // Primitive float tests
        addConstructorValidator(startEvents, "compPrimFloatZero", new Float(0), Float.TYPE);
        addConstructorValidator(startEvents, "compPrimFloatMinusZero", new Float("-0.0"), Float.TYPE);
        addConstructorValidator(startEvents, "compPrimFloatMax", new Float(Float.MAX_VALUE), Float.TYPE);
        addConstructorValidator(startEvents, "compPrimFloatMin", new Float(Float.MIN_VALUE), Float.TYPE);
        addConstructorValidator(startEvents, "compPrimFloatDSuffix", new Float(1.1), Float.TYPE);
        addConstructorValidator(startEvents, "compPrimFloatdSuffix", new Float(1.1), Float.TYPE);
        addConstructorValidator(startEvents, "compPrimFloatFSuffix", new Float(1.1), Float.TYPE);
        addConstructorValidator(startEvents, "compPrimFloatfSuffix", new Float(1.1e1), Float.TYPE);
        addConstructorValidator(startEvents, "compPrimFloatNaN", new Float(Float.NaN), Float.TYPE);
        addConstructorValidator(startEvents, "compPrimFloatInfinity", new Float(Float.POSITIVE_INFINITY), Float.TYPE);
        addConstructorValidator(startEvents, "compPrimFloatNegInfinity", new Float(Float.NEGATIVE_INFINITY), Float.TYPE);

        // Wrappered float tests
        addConstructorValidator(startEvents, "compWrapperedFloatZero", new Float(0), Float.class);
        addConstructorValidator(startEvents, "compWrapperedFloatMinusZero", new Float("-0.0"), Float.class);
        addConstructorValidator(startEvents, "compWrapperedFloatMax", new Float(Float.MAX_VALUE), Float.class);
        addConstructorValidator(startEvents, "compWrapperedFloatMin", new Float(Float.MIN_VALUE), Float.class);
        addConstructorValidator(startEvents, "compWrapperedFloatDSuffix", new Float(1.1), Float.class);
        addConstructorValidator(startEvents, "compWrapperedFloatdSuffix", new Float(1.1), Float.class);
        addConstructorValidator(startEvents, "compWrapperedFloatFSuffix", new Float(1.1), Float.class);
        addConstructorValidator(startEvents, "compWrapperedFloatfSuffix", new Float(1.1e1), Float.class);
        addConstructorValidator(startEvents, "compWrapperedFloatNaN", new Float(Float.NaN), Float.class);
        addConstructorValidator(startEvents, "compWrapperedFloatInfinity", new Float(Float.POSITIVE_INFINITY), Float.class);
        addConstructorValidator(startEvents, "compWrapperedFloatNegInfinity", new Float(Float.NEGATIVE_INFINITY), Float.class);
        addConstructorValidator(startEvents, "compWrapperedFloatNull", Float.class);

        // now the inferred type tests
        addConstructorValidator(startEvents, "constructorPrimitiveBoolean", Boolean.TRUE, Boolean.TYPE);
        addConstructorValidator(startEvents, "constructorWrapperedBoolean", Boolean.FALSE, Boolean.class);
        addConstructorValidator(startEvents, "constructorPrimitiveByte", new Byte((byte)123), Byte.TYPE);
        addConstructorValidator(startEvents, "constructorWrapperedByte", new Byte((byte)100), Byte.class);
        addConstructorValidator(startEvents, "constructorPrimitiveDouble", new Double(1.1), Double.TYPE);
        addConstructorValidator(startEvents, "constructorWrapperedDouble", new Double(1.2), Double.class);
        addConstructorValidator(startEvents, "constructorPrimitiveFloat", new Float(1.3), Float.TYPE);
        addConstructorValidator(startEvents, "constructorWrapperedFloat", new Float(1.4), Float.class);
        addConstructorValidator(startEvents, "constructorPrimitiveInteger", new Integer(12), Integer.TYPE);
        addConstructorValidator(startEvents, "constructorWrapperedInteger", new Integer(13), Integer.class);
        addConstructorValidator(startEvents, "constructorPrimitiveCharacter", new Character('a'), Character.TYPE);
        addConstructorValidator(startEvents, "constructorWrapperedCharacter", new Character('b'), Character.class);
        addConstructorValidator(startEvents, "constructorPrimitiveShort", new Short((short)15), Short.TYPE);
        addConstructorValidator(startEvents, "constructorWrapperedShort", new Short((short)16), Short.class);
        addConstructorValidator(startEvents, "constructorPrimitiveLong", new Long((long)17), Long.TYPE);
        addConstructorValidator(startEvents, "constructorWrapperedLong", new Long((long)18), Long.class);
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

    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName, String source, Object propertyValue, Class type, Class valueType) {
        // a "" string value
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, type);
        startEvents.addValidator(new PropertyMetadataValidator(compName, new StringProperty(propertyName, valueType, source)));
    }

    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName, String source, Object propertyValue, Class type) {
        // a "" string value
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, type);
        startEvents.addValidator(new PropertyMetadataValidator(compName, new StringProperty(propertyName, null, source)));
    }

    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName, Class type) {
        // a "" string value
        startEvents.validateComponentProperty(compName, propertyName, null, type);
        startEvents.addValidator(new PropertyMetadataValidator(compName, new TestProperty(new TestNullValue(), propertyName)));
    }

    public void addPropertyTests(StandardTestController controller) {
        MetadataEventSet startEvents = controller.getStartEvents();
        // no argument constructor...just gets created

        // single string property
        addPropertyValidator(startEvents, "compImplicitString", "string", "ABC", "ABC", String.class);

        // a "" string value
        addPropertyValidator(startEvents, "compZeroLengthString", "string", "", "", String.class);

        // a null value
        // this requires a little special handling
        addPropertyValidator(startEvents, "compNullString", "string", String.class);

        // Primitive boolean tests
        addPropertyValidator(startEvents, "compPrimBooleanYes", "primBoolean", "yes", Boolean.TRUE, Boolean.TYPE, Boolean.TYPE);
        addPropertyValidator(startEvents, "compPrimBooleanTrue", "primBoolean", "true", Boolean.TRUE, Boolean.TYPE, Boolean.TYPE);
        addPropertyValidator(startEvents, "compPrimBooleanOn", "primBoolean", "on", Boolean.TRUE, Boolean.TYPE);
        addPropertyValidator(startEvents, "compPrimBooleanNo", "primBoolean", "no", Boolean.FALSE, Boolean.TYPE);
        addPropertyValidator(startEvents, "compPrimBooleanFalse", "primBoolean", "false", Boolean.FALSE, Boolean.TYPE);
        addPropertyValidator(startEvents, "compPrimBooleanOff", "primBoolean", "off", Boolean.FALSE, Boolean.TYPE);

        // Wrappered boolean tests
        addPropertyValidator(startEvents, "compWrapperedBooleanYes", "boolean","yes", Boolean.TRUE, Boolean.class);
        addPropertyValidator(startEvents, "compWrapperedBooleanTrue", "boolean", "true", Boolean.TRUE, Boolean.class);
        addPropertyValidator(startEvents, "compWrapperedBooleanOn", "boolean", "on", Boolean.TRUE, Boolean.class);
        addPropertyValidator(startEvents, "compWrapperedBooleanNo", "boolean", "no", Boolean.FALSE, Boolean.class);
        addPropertyValidator(startEvents, "compWrapperedBooleanFalse", "boolean", "false", Boolean.FALSE, Boolean.class);
        addPropertyValidator(startEvents, "compWrapperedBooleanOff", "boolean", "off", Boolean.FALSE, Boolean.class);
        addPropertyValidator(startEvents, "compWrapperedBooleanNull", "boolean", Boolean.class);

        // Primitive byte tests
        addPropertyValidator(startEvents, "compPrimByteZero", "primByte", "0", new Byte((byte)0), Byte.TYPE);
        addPropertyValidator(startEvents, "compPrimByteMax", "primByte", "127", new Byte(Byte.MAX_VALUE), Byte.TYPE);
        addPropertyValidator(startEvents, "compPrimByteMin", "primByte", "-128", new Byte(Byte.MIN_VALUE), Byte.TYPE);

        // Wrappered byte tests
        addPropertyValidator(startEvents, "compWrapperedByteZero", "byte", "0", new Byte((byte)0), Byte.class);
        addPropertyValidator(startEvents, "compWrapperedByteMax", "byte", "127", new Byte(Byte.MAX_VALUE), Byte.class);
        addPropertyValidator(startEvents, "compWrapperedByteMin", "byte", "-128", new Byte(Byte.MIN_VALUE), Byte.class);
        addPropertyValidator(startEvents, "compWrapperedByteNull", "byte", Byte.class);

        // Primitive short tests
        addPropertyValidator(startEvents, "compPrimShortZero", "primShort", "0", new Short((short)0), Short.TYPE);
        addPropertyValidator(startEvents, "compPrimShortMax", "primShort", "32767", new Short(Short.MAX_VALUE), Short.TYPE);
        addPropertyValidator(startEvents, "compPrimShortMin", "primShort", "-32768", new Short(Short.MIN_VALUE), Short.TYPE);

        // Wrappered short tests
        addPropertyValidator(startEvents, "compWrapperedShortZero", "short", "0", new Short((short)0), Short.class);
        addPropertyValidator(startEvents, "compWrapperedShortMax", "short", "32767", new Short(Short.MAX_VALUE), Short.class);
        addPropertyValidator(startEvents, "compWrapperedShortMin", "short", "-32768", new Short(Short.MIN_VALUE), Short.class);
        addPropertyValidator(startEvents, "compWrapperedShortNull", "short", Short.class);

        // Primitive integer tests
        addPropertyValidator(startEvents, "compPrimIntegerZero", "primInteger", "0", new Integer(0), Integer.TYPE);
        addPropertyValidator(startEvents, "compPrimIntegerMax", "primInteger", "2147483647", new Integer(Integer.MAX_VALUE), Integer.TYPE);
        addPropertyValidator(startEvents, "compPrimIntegerMin", "primInteger", "-2147483648", new Integer(Integer.MIN_VALUE), Integer.TYPE);

        // Wrappered integer tests
        addPropertyValidator(startEvents, "compWrapperedIntegerZero", "integer", "0", new Integer(0), Integer.class);
        addPropertyValidator(startEvents, "compWrapperedIntegerMax", "integer", "2147483647", new Integer(Integer.MAX_VALUE), Integer.class);
        addPropertyValidator(startEvents, "compWrapperedIntegerMin", "integer", "-2147483648", new Integer(Integer.MIN_VALUE), Integer.class);
        addPropertyValidator(startEvents, "compWrapperedIntegerNull", "integer", Integer.class);

        // Primitive long tests
        addPropertyValidator(startEvents, "compPrimLongZero", "primLong", "0", new Long(0), Long.TYPE);
        addPropertyValidator(startEvents, "compPrimLongMax", "primLong", "9223372036854775807", new Long(Long.MAX_VALUE), Long.TYPE);
        addPropertyValidator(startEvents, "compPrimLongMin", "primLong", "-9223372036854775808", new Long(Long.MIN_VALUE), Long.TYPE);

        // Wrappered long tests
        addPropertyValidator(startEvents, "compWrapperedLongZero", "long", "0", new Long(0), Long.class);
        addPropertyValidator(startEvents, "compWrapperedLongMax", "long", "9223372036854775807", new Long(Long.MAX_VALUE), Long.class);
        addPropertyValidator(startEvents, "compWrapperedLongMin", "long", "-9223372036854775808", new Long(Long.MIN_VALUE), Long.class);
        addPropertyValidator(startEvents, "compWrapperedLongNull", "long", Long.class);

        // Primitive character tests
        addPropertyValidator(startEvents, "compPrimCharacterZero", "primCharacter", "\\u0000", new Character('\0'), Character.TYPE);
        addPropertyValidator(startEvents, "compPrimCharacterMax", "primCharacter", "\\uffff", new Character(Character.MAX_VALUE), Character.TYPE);
        addPropertyValidator(startEvents, "compPrimCharacterA", "primCharacter", "A", new Character('A'), Character.TYPE);

        // Wrappered character tests
        addPropertyValidator(startEvents, "compWrapperedCharacterZero", "character", "\\u0000", new Character('\0'), Character.class);
        addPropertyValidator(startEvents, "compWrapperedCharacterMax", "character", "\\uffff", new Character(Character.MAX_VALUE), Character.class);
        addPropertyValidator(startEvents, "compWrapperedCharacterA", "character", "A", new Character('A'), Character.class);
        addPropertyValidator(startEvents, "compWrapperedCharacterNull", "character", Character.class);

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
        addPropertyValidator(startEvents, "compWrapperedDoubleNull", "double", Double.class);

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
        addPropertyValidator(startEvents, "compWrapperedFloatNull", "float", Float.class);
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
