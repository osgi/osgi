/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.measurement.junit;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.osgi.util.measurement.Measurement;
import org.osgi.util.measurement.State;
import org.osgi.util.measurement.Unit;

/**
 * Tests Measurement utility class. It tests as well two other classes related
 * to Measurement class that is to say State and Unit.
 * 
 * <p>
 * A <tt>Measurement</tt> object is used for maintaining the tuple of value,
 * error, unit and timestamp. The value and error are represented as doubles and
 * the time is measured in milliseconds since midnight, January 1, 1970 UTC.
 * 
 * @version $$
 * @author Siemens VDO Automotive
 */
public class MeasurementControl extends TestCase {
	private final static Unit[]	allUnits	= new Unit[] {Unit.m, Unit.s,
			Unit.kg, Unit.K, Unit.A, Unit.mol, Unit.cd, Unit.rad, Unit.m_s,
			Unit.m_s2, Unit.m2, Unit.m3, Unit.Hz, Unit.N, Unit.Pa, Unit.J,
			Unit.W, Unit.C, Unit.V, Unit.F, Unit.Ohm, Unit.S, Unit.Wb, Unit.T,
			Unit.lx, Unit.Gy, Unit.kat, Unit.unity};

	/**
	 * Enumerates through all units available in <tt>Unit</tt> class.
	 */
	public void testUnitConstants() {
		assertEquals("A", Unit.A.toString()); // unit ampere
		assertEquals("C", Unit.C.toString()); // unit coulomb
		assertEquals("cd", Unit.cd.toString()); // unit candela
		assertEquals("F", Unit.F.toString()); // unit farad
		assertEquals("Gy", Unit.Gy.toString()); // unit gray
		assertEquals("Hz", Unit.Hz.toString()); // unit hertz
		assertEquals("J", Unit.J.toString()); // unit joule
		assertEquals("K", Unit.K.toString()); // unit kelvin
		assertEquals("kat", Unit.kat.toString()); // unit katal
		assertEquals("kg", Unit.kg.toString()); // unit kilogram
		assertEquals("lx", Unit.lx.toString()); // unit lux
		assertEquals("m", Unit.m.toString()); // unit meter
		assertEquals("m2", Unit.m2.toString()); // unit square meter
		assertEquals("m3", Unit.m3.toString()); // unit cubic meter
		assertEquals("m/s", Unit.m_s.toString()); // unit meter per
															// second
		assertEquals("m/s2", Unit.m_s2.toString()); // unit meter per
															// second squared
		assertEquals("mol", Unit.mol.toString()); // unit mole
		assertEquals("N", Unit.N.toString()); // unit newton
		assertEquals("Ohm", Unit.Ohm.toString()); // unit ohm
		assertEquals("Pa", Unit.Pa.toString()); // unit pascal
		assertEquals("rad", Unit.rad.toString()); // unit radians
		assertEquals("S", Unit.S.toString()); // unit siemens
		assertEquals("s", Unit.s.toString()); // unit second
		assertEquals("T", Unit.T.toString()); // unit tesla
		assertEquals("", Unit.unity.toString()); // no unit
		assertEquals("V", Unit.V.toString()); // unit volt
		assertEquals("W", Unit.W.toString()); // unit watt
		assertEquals("Wb", Unit.Wb.toString()); // unit weber
	}

	/**
	 * Tests <tt>Unit</tt> class comparison method.
	 */
	public void testUnitComparison() {
		// test equality of the same unit
		assertTrue("Unit.equals() is wrong", Unit.kg.equals(Unit.kg));
		// test none equality of different unit
		assertTrue("Unit.equals() is wrong", !Unit.kg.equals(Unit.m));
		// test none equality of object that is not a Unit
		assertTrue("Unit.equals() is wrong", !Unit.kg.equals(new State(10,
				"test")));
	}

	/**
	 * Tests all <tt>State</tt> class constructor by displaying object
	 * contents after creation.
	 */
	public void testStateConstructor() {
		// Construct a State with all possible constructors
		testStateConstructor(1, null); // test arg null
		testStateConstructor(2, "firstState");
		testStateConstructor(3, null, 1000); // test arg null
		testStateConstructor(4, "secondState", 1001);
	}

	private void testStateConstructor(int value, String name) {
		State state = new State(value, name);
		assertEquals(value, state.getValue());
		assertEquals(name, state.getName());
		assertEquals(0l, state.getTime());
	}
	private void testStateConstructor(int value, String name, long time) {
		State state = new State(value,
				name, time);
		assertEquals(value, state.getValue());
		assertEquals(name, state.getName());
		assertEquals(time, state.getTime());
	}

	/**
	 * Tests <tt>State</tt> class comparison methods.
	 */
	public void testStateComparison() {
		// Construct several states to compare
		State refState = new State(100, "firstState", 1000);
		State identicalState = new State(100, "firstState", 1001);
		State nullName = new State(100, null, 1002);
		State differentValue = new State(101, "firstState", 1003);
		State differentName = new State(100, "secondState", 1004);
		State differentState = new State(101, "secondState", 1005);
		// Test equality cases
		// with the same object
		assertTrue("State.equals() is wrong", refState.equals(refState));
		// with the same value and name
		assertTrue("State.equals() is wrong", refState.equals(identicalState));
		assertEquals("State.equals() is wrong", refState.hashCode(),
				identicalState.hashCode());
		// Test none equality cases
		// with different name
		assertTrue("State.equals() is wrong", !refState.equals(differentName));
		// with different value
		assertTrue("State.equals() is wrong", !refState.equals(differentValue));
		// with different name and value
		assertTrue("State.equals() is wrong", !refState.equals(differentState));
		// with a none State object
		assertTrue("State.equals() is wrong", !refState.equals(Unit.m));
		// with a null name
		assertTrue("State.equals() is wrong", !nullName.equals(refState));
	}

	/**
	 * Tests <tt>Measurement</tt> class constructors by displaying object
	 * contents after creation.
	 */
	public void testMeasurementConstructor() {
		// Construct a Measurement with all possible constructors
		testMeasurementConstructor(100.0);
		testMeasurementConstructor(101.0, Unit.m);
		testMeasurementConstructor(102.0, null); 
		testMeasurementConstructor(103.0, 1.2, Unit.lx);
		testMeasurementConstructor(104.0, 1.3, null); // test arg null
		testMeasurementConstructor(105.0, 1.4, Unit.kg, 1000);
		testMeasurementConstructor(106.0, 1.5, null, 1001); // test arg null
	}
	
	private void testMeasurementConstructor(double value) {
		Measurement measurement = new Measurement(value);
		assertEquals(value, measurement.getValue(), 0.0d);
		assertEquals(0.0d, measurement.getError(), 0.0d);
		assertEquals(Unit.unity, measurement.getUnit());
		assertEquals(0l, measurement.getTime());
	}

	private void testMeasurementConstructor(double value, Unit unit) {
		Measurement measurement = new Measurement(value, unit);
		if (unit == null) {
			unit = Unit.unity;
		}
		assertEquals(value, measurement.getValue(), 0.0d);
		assertEquals(0.0d, measurement.getError(), 0.0d);
		assertEquals(unit, measurement.getUnit());
		assertEquals(0l, measurement.getTime());
	}

	private void testMeasurementConstructor(double value, double error,
			Unit unit) {
		Measurement measurement = new Measurement(value, error, unit);
		if (unit == null) {
			unit = Unit.unity;
		}
		assertEquals(value, measurement.getValue(), 0.0d);
		assertEquals(error, measurement.getError(), 0.0d);
		assertEquals(unit, measurement.getUnit());
		assertEquals(0l, measurement.getTime());
	}
	
	private void testMeasurementConstructor(double value, double error,
			Unit unit, long time) {
		Measurement measurement = new Measurement(value, error, unit, time);
		if (unit == null) {
			unit = Unit.unity;
		}
		assertEquals(value, measurement.getValue(), 0.0d);
		assertEquals(error, measurement.getError(), 0.0d);
		assertEquals(unit, measurement.getUnit());
		assertEquals(time, measurement.getTime());
	}
	
	/**
	 * Tests <tt>Measurement</tt> class comparison methods. There are two ways
	 * to compare two <tt>Measurement</tt> objects by using equals() or
	 * compareTo() methods.
	 * 
	 * The compareTo() method is using the following rules: it returns a
	 * negative integer, zero, or a positive integer if the this object is less
	 * than, equal to, or grater than the specified object.
	 * 
	 * compareTo() returns zero if there is some x such that getValue() -
	 * getError() <= x <= getValue() + getError()
	 */
	public void testMeasurementComparison() {
		Measurement mes1 = new Measurement(100.0, 3.0, Unit.m, 1000);
		Measurement mes2 = new Measurement(100.0, 3.0, Unit.m, 1001);
		// Comparison using equals() method
		// Measures are equal
		assertTrue("Measurement.equals() is wrong", mes1.equals(mes2));
		assertEquals("Measurement.equals() is wrong", mes1.hashCode(), mes2
				.hashCode());
		// Equality with the same object
		assertTrue("Measurement.equals() is wrong", mes1.equals(mes1));
		// Measures different due to value
		assertTrue("Measurement.equals() is wrong", !mes1
				.equals(new Measurement(101.0, 3.0, Unit.m, 1002)));
		// Measures different due to error
		assertTrue("Measurement.equals() is wrong", !mes1
				.equals(new Measurement(100.0, 1.0, Unit.m, 1003)));
		// Measures different due to unit
		assertTrue("Measurement.equals() is wrong", !mes1
				.equals(new Measurement(100.0, 3.0, Unit.kg, 1004)));
		// Measures different due to incorrect class
		assertTrue("Measurement.equals() is wrong", !mes1.equals(Unit.m));
		// Comparison using CompareTo() method
		// Measures are equals because they overlap due to errors
		assertTrue("Measurement.compareTo() is wrong",
				(mes1.compareTo(mes1)) == 0);
		assertTrue("Measurement.compareTo() is wrong", (mes1
				.compareTo(new Measurement(100.0, 1.0, Unit.m, 1005))) == 0);
		assertTrue("Measurement.compareTo() is wrong", (mes1
				.compareTo(new Measurement(100.0, 3.0, Unit.m, 1006))) == 0);
		assertTrue("Measurement.compareTo() is wrong", (mes1
				.compareTo(new Measurement(95.0, 2.0, Unit.m, 1007))) == 0);
		assertTrue("Measurement.compareTo() is wrong", (mes1
				.compareTo(new Measurement(104.0, 2.0, Unit.m, 1008))) == 0);
		// mes1 is less than the measure specified as parameter
		assertTrue("Measurement.compareTo() is wrong", (mes1
				.compareTo(new Measurement(105.0, 1.9, Unit.m, 1009))) < 0);
		// mes1 is greater than the measure specified as parameter
		assertTrue("Measurement.compareTo() is wrong", (mes1
				.compareTo(new Measurement(94.0, 2.0, Unit.m, 1010))) > 0);
		// Test ArithmeticException case when the unit is not compatible
		try {
			mes1.compareTo(new Measurement(100.0, 1.0, Unit.kg, 1004));
		}
		catch (ArithmeticException e) {
			// expected
		}
		catch (Exception e) {
			fail("CompareTo an uncompatible unit", e);
		}
		// Test ClassCastException case when the object passed as parameter
		// is not a <tt>Measurement</tt> object
		try {
			mes1.compareTo(new State(100, "oneState"));
		}
		catch (ClassCastException e) {
			// expected
		}
		catch (Exception e) {
			// Check that we got the correct exception
			fail("CompareTo a different class", e);
		}
	}

	/**
	 * Tests value, unit and error computations for the add() method.
	 */
	public void testMeasurementAddition() {
		// Test the use of the three different methods to perform an addition
		Measurement receiver = new Measurement(100.0d, 1.0d, Unit.kg);
		Measurement expected = new Measurement(200.0d, 1.0d, Unit.kg);
		assertEquals(expected, receiver.add(100.0d));
		assertEquals(expected, receiver.add(100.0d, Unit.kg));
		assertEquals(expected, receiver.add(new Measurement(100.0d, Unit.kg)));
		
		expected = new Measurement(200.0d, 2.0d, Unit.kg);
		assertEquals(expected, receiver.add(new Measurement(100.0d, 1.0d,
				Unit.kg)));
		
		// Test <tt>Unit</tt> compatibility while performing an addition.
		// An incompatibility generates an <tt>ArithmeticException</tt>
		final int l = allUnits.length;
		for (int i = 0; i < l; i++) {
			Measurement outer = new Measurement(100.0d, 1.0d, allUnits[i],
					1000 + i);
			for (int j = 0; j < l; j++) {
				Measurement inner = new Measurement(100.0d, 1.0d, allUnits[j],
						2000 + j);
				try {
					outer.add(inner);
					if (i != j) {
						fail("add different unit");
					}
				}
				catch (ArithmeticException e) {
					if (i == j) {
						fail("add same unit",e);
					}
				}
				catch (Exception e) {
					fail("invalid exception", e);
				}
			}
		}
	}

	/**
	 * Tests value, unit and error computations for the sub() method.
	 */
	public void testMeasurementSubstraction() {
		// Test the use of the three different methods to perform a substraction
		Measurement receiver = new Measurement(100.0d, 1.0d, Unit.kg);
		Measurement expected = new Measurement(0.0d, 1.0d, Unit.kg);
		assertEquals(expected, receiver.sub(100.0d));
		assertEquals(expected, receiver.sub(100.0d, Unit.kg));
		assertEquals(expected, receiver.sub(new Measurement(100.0d, Unit.kg)));
		
		expected = new Measurement(0.0d, 2.0d, Unit.kg);
		assertEquals(expected, receiver.sub(new Measurement(100.0d, 1.0d,
				Unit.kg)));
		
		// Test <tt>Unit</tt> compatibility while performing a substraction.
		// An incompatibility generates an <tt>ArithmeticException</tt>
		final int l = allUnits.length;
		for (int i = 0; i < l; i++) {
			Measurement outer = new Measurement(100.0d, 1.0d, allUnits[i],
					1000 + i);
			for (int j = 0; j < l; j++) {
				Measurement inner = new Measurement(100.0d, 1.0d, allUnits[j],
						2000 + j);
				try {
					outer.sub(inner);
					if (i != j) {
						fail("sub different unit");
					}
				}
				catch (ArithmeticException e) {
					if (i == j) {
						fail("sub same unit", e);
					}
				}
				catch (Exception e) {
					fail("invalid exception", e);
				}
			}
		}
	}

	/**
	 * Tests value, unit and error computations for the mul() method.
	 */
	public void testMeasurementMultiplication() {
		// Test the use of the three different methods to perform a
		// multiplication
		Measurement receiver = new Measurement(100.0d, 1.0d, Unit.kg);
		Unit kg2 = receiver.mul(1.0d, Unit.kg).getUnit();
		assertEquals("kg^2", kg2.toString());
		
		Measurement expected = new Measurement(-10000.0d, 100.0d, Unit.kg);
		assertEquals(expected, receiver.mul(-100.0d));
		
		expected = new Measurement(-10000.0d, 100.0d, kg2);
		assertEquals(expected, receiver.mul(-100.0d, Unit.kg));
		assertEquals(expected, receiver.mul(new Measurement(-100.0d, Unit.kg)));

		expected = new Measurement(-10000.0d, 300.0d, kg2);
		assertEquals(expected, receiver.mul(new Measurement(-100.0d, 2.0d,
				Unit.kg)));
		
		// Test <tt>Unit</tt> compatibility while performing a multiplication.
		// An incompatibility generates an <tt>ArithmeticException</tt>
		final int l = allUnits.length;
		for (int i = 0; i < l; i++) {
			Measurement outer = new Measurement(100.0d, 1.0d, allUnits[i],
					1000 + i);
			for (int j = 0; j < l; j++) {
				Measurement inner = new Measurement(100.0d, 1.0d, allUnits[j],
						2000 + j);
				try {
					outer.mul(inner);
				}
				catch (Exception e) {
					fail("invalid exception", e);
				}
			}
		}
	}

	/**
	 * Tests value, unit and error computations for the sub() method.
	 */
	public void testMeasurementDivision() {
		// Test the use of the three different methods to perform a division
		Measurement receiver = new Measurement(200.0d, 1.0d, Unit.kg);
		Measurement expected = new Measurement(-1.0d, 0.005d, Unit.kg);
		assertEquals(expected, receiver.div(-200.0d));
		
		expected = new Measurement(-1.0d, 0.005d, Unit.unity);
		assertEquals(expected, receiver.div(-200.0d, Unit.kg));
		assertEquals(expected, receiver.div(new Measurement(-200.0d, Unit.kg)));

		expected = new Measurement(-1.0d, 0.015d, Unit.unity);
		assertEquals(expected, receiver.div(new Measurement(-200.0d, 2.0d,
				Unit.kg)));
		
		// Test <tt>Unit</tt> compatibility while performing a division.
		// An incompatibility generates an <tt>ArithmeticException</tt>.
		final int l = allUnits.length;
		for (int i = 0; i < l; i++) {
			Measurement outer = new Measurement(100.0d, 1.0d, allUnits[i],
					1000 + i);
			for (int j = 0; j < l; j++) {
				Measurement inner = new Measurement(100.0d, 1.0d, allUnits[j],
						2000 + j);
				try {
					outer.div(inner);
				}
				catch (Exception e) {
					fail("invalid exception", e);
				}
			}
		}
	}

	/**
	 * Fail with cause t.
	 * 
	 * @param message Failure message.
	 * @param t Cause of the failure.
	 */
	public static void fail(String message, Throwable t) {
		AssertionFailedError e = new AssertionFailedError(message + ": "
				+ t.getMessage());
		e.initCause(t);
		throw e;
	}
}
