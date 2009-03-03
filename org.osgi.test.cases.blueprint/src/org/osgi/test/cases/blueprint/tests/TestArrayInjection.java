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

import org.osgi.service.blueprint.reflect.Value;
import org.osgi.test.cases.blueprint.framework.ConstructorMetadataValidator;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.PropertyMetadataValidator;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.StringParameter;
import org.osgi.test.cases.blueprint.framework.TestListValue;
import org.osgi.test.cases.blueprint.framework.TestParameter;
import org.osgi.test.cases.blueprint.framework.TestProperty;
import org.osgi.test.cases.blueprint.framework.TestSetValue;
import org.osgi.test.cases.blueprint.framework.TestStringValue;
import org.osgi.test.cases.blueprint.framework.TestValue;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains primitive string to type injection convertions
 *
 * @version $Revision$
 */
public class TestArrayInjection extends DefaultTestBundleControl {
    private void addConstructorValidator(MetadataEventSet startEvents, String id, Object value) {
        startEvents.validateComponentArgument(id, "arg1", value, value.getClass());
    }

    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName, Object propertyValue) {
        // a "" string value
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, propertyValue.getClass());
    }

	public void testArrayInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/array_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty list
        addConstructorValidator(startEvents, "compEmptyListArgument", new int[0]);
        // validate the metadata for this one too
        startEvents.addValidator(new ConstructorMetadataValidator("compEmptyListArgument", new TestParameter(
            new TestListValue(new TestValue[0]))));
        addConstructorValidator(startEvents, "compEmptySetArgument", new int[0]);
        // validate the metadata for this one too
        startEvents.addValidator(new ConstructorMetadataValidator("compEmptySetArgument", new TestParameter(
            new TestSetValue(new TestValue[0]))));
        // simple list of ints
        addConstructorValidator(startEvents, "compListArgument", new int[] {1234, 5678});
        // validate the metadata for this one too
        startEvents.addValidator(new ConstructorMetadataValidator("compListArgument", new TestParameter(
            new TestListValue(new TestValue[] { new TestStringValue("1234"), new TestStringValue("5678") }))));
        // NB, Sets tend to be one item because you can't depend on order of final array
        addConstructorValidator(startEvents, "compSetArgument", new int[] {1234});
        // validate the metadata for this one too
        startEvents.addValidator(new ConstructorMetadataValidator("compSetArgument", new TestParameter(
            new TestListValue(new TestValue[] { new TestStringValue("1234") }))));

        // boolean conversions
        addConstructorValidator(startEvents, "compListPrimBoolean", new boolean[] {true, true, true, false, false, false});
        addConstructorValidator(startEvents, "compSetPrimBoolean", new boolean[] {true, true, true});
        addConstructorValidator(startEvents, "compListWrapperedBoolean", new Boolean[] {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE});
        addConstructorValidator(startEvents, "compSetWrapperedBoolean", new Boolean[] {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE});
        addConstructorValidator(startEvents, "compPrimBooleanEmptyList", new boolean[] {});
        addConstructorValidator(startEvents, "compPrimBooleanEmptySet", new boolean[] {});

        // byte conversion
        addConstructorValidator(startEvents, "compListPrimByte", new byte[] {0, Byte.MAX_VALUE, Byte.MIN_VALUE});
        addConstructorValidator(startEvents, "compSetPrimByte", new byte[] {123});
        addConstructorValidator(startEvents, "compListWrapperedByte", new Byte[] {new Byte((byte)0), new Byte(Byte.MAX_VALUE), new Byte(Byte.MIN_VALUE)});
        addConstructorValidator(startEvents, "compSetWrapperedByte", new Byte[] {new Byte((byte)123)});

        // double conversion
        addConstructorValidator(startEvents, "compListPrimDouble", new double[] {0, Double.MAX_VALUE, Double.MIN_VALUE, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY});
        addConstructorValidator(startEvents, "compSetPrimDouble", new double[] {123});
        addConstructorValidator(startEvents, "compListWrapperedDouble", new Double[] {new Double(0), new Double(Double.MAX_VALUE), new Double(Double.MIN_VALUE), new Double(Double.POSITIVE_INFINITY), new Double(Double.NEGATIVE_INFINITY)});
        addConstructorValidator(startEvents, "compSetWrapperedDouble", new Double[] {new Double(123)});

        // float conversion
        addConstructorValidator(startEvents, "compListPrimFloat", new float[] {0, Float.MAX_VALUE, Float.MIN_VALUE, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY});
        addConstructorValidator(startEvents, "compSetPrimFloat", new float[] {123});
        addConstructorValidator(startEvents, "compListWrapperedFloat", new Float[] {new Float(0), new Float(Float.MAX_VALUE), new Float(Float.MIN_VALUE), new Float(Float.POSITIVE_INFINITY), new Float(Float.NEGATIVE_INFINITY)});
        addConstructorValidator(startEvents, "compSetWrapperedFloat", new Float[] {new Float(123)});

        // int conversion
        addConstructorValidator(startEvents, "compListPrimInt", new int[] {0, Integer.MAX_VALUE, Integer.MIN_VALUE});
        addConstructorValidator(startEvents, "compSetPrimInt", new int[] {123});
        addConstructorValidator(startEvents, "compListWrapperedInt", new Integer[] {new Integer(0), new Integer(Integer.MAX_VALUE), new Integer(Integer.MIN_VALUE)});
        addConstructorValidator(startEvents, "compSetWrapperedInt", new Integer[] {new Integer(123)});

        // char conversion
        addConstructorValidator(startEvents, "compListPrimChar", new char[] {0, Character.MAX_VALUE});
        addConstructorValidator(startEvents, "compSetPrimChar", new char[] {123});
        addConstructorValidator(startEvents, "compListWrapperedChar", new Character[] {new Character((char)0), new Character(Character.MAX_VALUE)});
        addConstructorValidator(startEvents, "compSetWrapperedChar", new Character[] {new Character((char)123)});

        // short conversion
        addConstructorValidator(startEvents, "compListPrimShort", new short[] {0, Short.MAX_VALUE, Short.MIN_VALUE});
        addConstructorValidator(startEvents, "compSetPrimShort", new short[] {123});
        addConstructorValidator(startEvents, "compListWrapperedShort", new Short[] {new Short((short)0), new Short(Short.MAX_VALUE), new Short(Short.MIN_VALUE)});
        addConstructorValidator(startEvents, "compSetWrapperedShort", new Short[] {new Short((short)123)});

        // long conversion
        addConstructorValidator(startEvents, "compListPrimLong", new long[] {0, Long.MAX_VALUE, Long.MIN_VALUE});
        addConstructorValidator(startEvents, "compSetPrimLong", new long[] {123});
        addConstructorValidator(startEvents, "compListWrapperedLong", new Long[] {new Long(0), new Long(Long.MAX_VALUE), new Long(Long.MIN_VALUE)});
        addConstructorValidator(startEvents, "compSetWrapperedLong", new Long[] {new Long(123)});

        controller.run();
    }
}
