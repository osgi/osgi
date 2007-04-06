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
package org.osgi.test.cases.measurement.tbc;

import org.osgi.test.cases.util.*;
import org.osgi.util.measurement.*;

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
public class MeasurementControl extends DefaultTestBundleControl {
	private final static Unit[]	allUnits	= new Unit[] {Unit.m, Unit.s,
			Unit.kg, Unit.K, Unit.A, Unit.mol, Unit.cd, Unit.rad, Unit.m_s,
			Unit.m_s2, Unit.m2, Unit.m3, Unit.Hz, Unit.N, Unit.Pa, Unit.J,
			Unit.W, Unit.C, Unit.V, Unit.F, Unit.Ohm, Unit.S, Unit.Wb, Unit.T,
			Unit.lx, Unit.Gy, Unit.kat, Unit.unity};
	/**
	 * List of test methods used in this test case.
	 */
	static String[]				methods		= new String[] {
			"testUnitConstants", "testUnitComparison", "testStateConstructor",
			"testStateComparison", "testMeasurementConstructor",
			"testMeasurementComparison", "testMeasurementAddition",
			"testMeasurementSubstraction", "testMeasurementMultiplication",
			"testMeasurementDivision"
			};

	/**
	 * Returns the list of test methods contained in this test case.
	 * 
	 * @return list of test methods
	 */
	public String[] getMethods() {
		return methods;
	}

	/**
	 * Enumerates through all units available in <tt>Unit</tt> class.
	 */
	public void testUnitConstants() {
		log("Unit.A: " + Unit.A); // unit ampere
		log("Unit.C: " + Unit.C); // unit coulomb
		log("Unit.cd: " + Unit.cd); // unit candela
		log("Unit.F: " + Unit.F); // unit farad
		log("Unit.Gy: " + Unit.Gy); // unit gray
		log("Unit.Hz: " + Unit.Hz); // unit hertz
		log("Unit.J: " + Unit.J); // unit joule
		log("Unit.K: " + Unit.K); // unit kelvin
		log("Unit.kat: " + Unit.kat); // unit katal
		log("Unit.kg: " + Unit.kg); // unit kilogram
		log("Unit.lx: " + Unit.lx); // unit lux
		log("Unit.m: " + Unit.m); // unit meter
		log("Unit.m2: " + Unit.m2); // unit square meter
		log("Unit.m3: " + Unit.m3); // unit cubic meter
		log("Unit.m_s: " + Unit.m_s); // unit meter per second
		log("Unit.m_s2: " + Unit.m_s2); // unit meter per second squared
		log("Unit.mol: " + Unit.mol); // unit mole
		log("Unit.N: " + Unit.N); // unit newton
		log("Unit.Ohm: " + Unit.Ohm); // unit ohm
		log("Unit.Pa: " + Unit.Pa); // unit pascal
		log("Unit.rad: " + Unit.rad); // unit radians
		log("Unit.S: " + Unit.S); // unit siemens
		log("Unit.s: " + Unit.s); // unit second
		log("Unit.T: " + Unit.T); // unit tesla
		log("Unit.unity: " + Unit.unity); // no unit
		log("Unit.V: " + Unit.V); // unit volt
		log("Unit.W: " + Unit.W); // unit watt
		log("Unit.Wb: " + Unit.Wb); // unit weber
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
		State[] states = {new State(1, null), // test arg null
				new State(2, "firstState"), new State(3, null, 1000), // test
																	  // arg
																	  // null
				new State(4, "secondState", 1001)};
		// test State object contents
		for (int i = 0; i < states.length; i++) {
			log("Value of state " + i);
			log(states[i]);
		}
		// test toString value
		log("firstState.toString: " + states[1]);
		log("secondState.toString: " + states[3]);
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
		Measurement[] mesures = {new Measurement(100.0),
				new Measurement(101.0, Unit.m), new Measurement(102.0, null), // test
																			  // arg
																			  // null
				new Measurement(103.0, 1.2, Unit.lx),
				new Measurement(104.0, 1.3, null), // test arg null
				new Measurement(105.0, 1.4, Unit.kg, 1000),
				new Measurement(106.0, 1.5, null, 1001) // test arg null
		};
		// test Mesurement object contents
		for (int i = 0; i < mesures.length; i++) {
			log("Value of mesure " + i);
			log(mesures[i]);
		}
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
		// Mesures are equal
		assertTrue("Measurement.equals() is wrong", mes1.equals(mes2));
		assertEquals("Measurement.equals() is wrong", mes1.hashCode(), mes2
				.hashCode());
		// Equality with the same object
		assertTrue("Measurement.equals() is wrong", mes1.equals(mes1));
		// Mesures differents due to value
		assertTrue("Measurement.equals() is wrong", !mes1
				.equals(new Measurement(101.0, 3.0, Unit.m, 1002)));
		// Mesures differents due to error
		assertTrue("Measurement.equals() is wrong", !mes1
				.equals(new Measurement(100.0, 1.0, Unit.m, 1003)));
		// Mesures differents due to unit
		assertTrue("Measurement.equals() is wrong", !mes1
				.equals(new Measurement(100.0, 3.0, Unit.kg, 1004)));
		// Mesures differents due to incorrect class
		assertTrue("Measurement.equals() is wrong", !mes1.equals(Unit.m));
		// Comparison using CompareTo() method
		// Mesures are equals because they overlap due to errors
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
		// mes1 is less than the mesure specified as parameter
		assertTrue("Measurement.compareTo() is wrong", (mes1
				.compareTo(new Measurement(105.0, 1.9, Unit.m, 1009))) < 0);
		// mes1 is greater than the mesure specified as parameter
		assertTrue("Measurement.compareTo() is wrong", (mes1
				.compareTo(new Measurement(94.0, 2.0, Unit.m, 1010))) > 0);
		// Test ArithmeticException case when the unit is not compatible
		try {
			mes1.compareTo(new Measurement(100.0, 1.0, Unit.kg, 1004));
		}
		catch (Exception e) {
			// Check that we got the correct exception
			assertException("CompareTo an uncompatible unit",
					ArithmeticException.class, e);
		}
		// Test ClassCastException case when the object passed as parameter
		// is not a <tt>Measurement</tt> object
		try {
			mes1.compareTo(new State(100, "oneState"));
		}
		catch (Exception e) {
			// Check that we got the correct exception
			assertException("CompareTo a different class",
					ClassCastException.class, e);
		}
	}

	/**
	 * Tests value, unit and error computations for the add() method.
	 */
	public void testMeasurementAddition() {
		// Test the use of the three different methods to perform an addition
		testMeasurementComputation("add");
		// Test <tt>Unit</tt> compatibility while performing an addition.
		// An incompatibility generates an <tt>ArithmeticException</tt>
		testUnitCompatibility("add");
		// Test error computation while performing an addition
		testErrorComputation("add");
	}

	/**
	 * Tests value, unit and error computations for the sub() method.
	 */
	public void testMeasurementSubstraction() {
		// Test the use of the three different methods to perform a substraction
		testMeasurementComputation("sub");
		// Test <tt>Unit</tt> compatibility while performing a substraction.
		// An incompatibility generates an <tt>ArithmeticException</tt>
		testUnitCompatibility("sub");
		// Test error computation while performing a substraction
		testErrorComputation("sub");
	}

	/**
	 * Tests value, unit and error computations for the mul() method.
	 */
	public void testMeasurementMultiplication() {
		// Test the use of the three different methods to perform a
		// multiplication
		testMeasurementComputation("mul");
		// Test <tt>Unit</tt> compatibility while performing a multiplication.
		// An incompatibility generates an <tt>ArithmeticException</tt>
		testUnitCompatibility("mul");
		// Test <tt>Unit</tt> computation while performing a multiplication
		// For the exponent test the <tt>Measurement</tt> class can support
		// from -64 up to +63.
		testUnitComputation("mul", 63);
		// Test error computation while performing a multiplication
		testErrorComputation("mul");
	}

	/**
	 * Tests value, unit and error computations for the sub() method.
	 */
	public void testMeasurementDivision() {
		// Test the use of the three different methods to perform a division
		testMeasurementComputation("div");
		// Test <tt>Unit</tt> compatibility while performing a division.
		// An incompatibility generates an <tt>ArithmeticException</tt>.
		testUnitCompatibility("div");
		// Test <tt>Unit</tt> computation while performing a division
		// For the exponent test the <tt>Measurement</tt> class can support
		// from -64 up to +63
		testUnitComputation("div", 64);
		// Test error computation while performing a division
		testErrorComputation("div");
	}

	/**
	 * Tests the three differents methods to compute <tt>Measurement</tt>
	 * object. The first one has the value (double) as parameter The second one
	 * has the value (double) and the unit (<tt>Unit</tt>) as parameters The
	 * third one has another <tt>Measurement</tt> object as parameter The
	 * fourth method is testing the operation with a 0 value.
	 * 
	 * @param operation The operation to perform
	 */
	private void testMeasurementComputation(String operation) {
		Class[] paramClass = {double.class, Unit.class};
		Object[] paramObject = {new Double(100.0), Unit.kg};
		MethodCall[] methods = {
				new MethodCall(operation, double.class, new Double(100.0)),
				new MethodCall(operation, paramClass, paramObject),
				new MethodCall(operation, Measurement.class, new Measurement(
						100.0, 1.0, Unit.kg, 1000)),
				new MethodCall(operation, Measurement.class, new Measurement(
						0.0, 1.0, Unit.kg, 1001))};
		// Contsruction of Measurement objects
		Measurement calcMes = null;
		// Call all different methods declared in methods array
		for (int i = 0; i < methods.length; i++) {
			try {
				calcMes = (Measurement) methods[i].invoke(new Measurement(
						100.0, 1.0, Unit.kg, 1002));
			}
			catch (Throwable e) {
				// Check that we got the correct exception
				assertException(methods[i].getName(),
						ArithmeticException.class, e);
			}
			// Log the content of the object calculated
			log("mesure content after " + methods[i].getName());
			log(calcMes);
		}
	}

	/**
	 * Tests unit compatibility while performing one of the four available
	 * operations.
	 * 
	 * The ouput is displayed in two differents lists: the list of compatible
	 * unit and the list of incompatible unit. The operation with an
	 * incompatible unit generates an <tt>ArithmeticException</tt>
	 * 
	 * @param operation The operation to perform
	 */
	private void testUnitCompatibility(String operation) {
		Measurement refMes, calcMes;
		MethodCall method;
		String message;
		int k, l;
		Unit[] compatibleUnit = new Unit[allUnits.length];
		Unit[] incompatibleUnit = new Unit[allUnits.length];
		// Iterate through all units to check unit compatibility
		for (int i = 0; i < allUnits.length; i++) {
			// Create method to call
			method = new MethodCall(operation, Measurement.class,
					new Measurement(100.0, 1.0, allUnits[i], 1000 + i));
			// Iterate through all units
			k = 0;
			l = 0;
			for (int j = 0; j < allUnits.length; j++) {
				try {
					calcMes = (Measurement) method.invoke(new Measurement(
							100.0, 1.0, allUnits[j], 1000 + j));
					compatibleUnit[k++] = allUnits[j];
				}
				catch (Throwable e) {
					// Check that we got the correct exception
					assertException(method.getName() + " " + allUnits[i]
							+ " to " + allUnits[j], ArithmeticException.class,
							e);
					incompatibleUnit[l++] = allUnits[j];
				}
			}
			// Log the final message for compatible unit
			if (k > 0) {
				message = operation + " " + allUnits[i] + " to [";
				for (int m = 0; m < k; m++) {
					message += compatibleUnit[m];
					if (m + 1 != k)
						message += ",";
				}
				message += "] can be done";
				log(message);
			}
			// Log the final message for incompatible unit
			if (l > 0) {
				message = operation + " " + allUnits[i] + " to [";
				for (int m = 0; m < l; m++) {
					message += incompatibleUnit[m];
					if (m + 1 != l)
						message += ",";
				}
				message += "] cannot be done";
				log(message);
			}
		}
	}

	/**
	 * Tests error computation while performing one of the four operations
	 * available in <tt>Measurement</tt> class.
	 * 
	 * Error calculations must therefore adhere to the rules listed in the table
	 * below. In this table, Ea is the absolute positive error in a value a and
	 * Eb is the absolute positive error in a value b. c is a constant double
	 * value without error.
	 * 
	 * Calculation Function Error ----------- -------- ----- a x b
	 * mul(Measurement) |Ea x b| + |a x Eb| a / b div(Measurement) (|Ea x b| +
	 * |a x Eb|) / b^2 a + b add(Measurement) Ea + Eb a - b sub(Measurement) Ea +
	 * Eb a x c mul(double) |Ea x c| a / c div(double) |Ea / c| a + c
	 * add(double) Ea a - c sub(double) Ea
	 * 
	 * @param operation The operation to perform
	 */
	private void testErrorComputation(String operation) {
		MethodCall[] methods = {
				new MethodCall(operation, double.class, new Double(-100.0)),
				new MethodCall(operation, Measurement.class, new Measurement(
						200.0, -1.1, Unit.kg, 1000))};
		Measurement calcMes = null;
		// Call all different methods declared in methods array
		for (int i = 0; i < methods.length; i++) {
			try {
				calcMes = (Measurement) methods[i].invoke(new Measurement(
						300.0, 1.2, Unit.kg, 1001));
			}
			catch (Throwable e) {
				// Check that we got the correct exception
				assertException(methods[i].getName(),
						ArithmeticException.class, e);
			}
			// Log the error computed
			log("Error computed by " + methods[i].getName() + " = "
					+ calcMes.getError());
		}
	}

	/**
	 * Tests unit computation while performing one of the four operations
	 * available on <tt>Measurement</tt> class.
	 * 
	 * The first part of this method is performing operation recursively on the
	 * same unit in order to test exponent. The second part is performing
	 * operation recursively with all units.
	 * 
	 * @param operation The operation to perform
	 * @param exponentLimit Maximum exponent on the same unit.
	 */
	private void testUnitComputation(String operation, int exponentLimit) {
		Measurement calcMes = null;
		MethodCall method = null;
		String message;
		// Perform the same operation on the same object with the same unit
		// until exponentLimit is reached with a SUI base unit (kg)
		calcMes = new Measurement(100.0, 1.0, Unit.unity, 1000);
		method = new MethodCall(operation, Measurement.class, new Measurement(
				100.0, 1.0, Unit.kg, 1000));
		for (int j = 0; j < exponentLimit; j++) {
			try {
				// Log the unit computed
				message = operation + " kg to " + calcMes.getUnit();
				calcMes = (Measurement) method.invoke(calcMes);
				message += " = ";
				message += calcMes.getUnit();
				log(message);
			}
			catch (Throwable e) {
				// Check that we got the correct exception
				assertException(method.getName(), ArithmeticException.class, e);
			}
		}
		// Perform the same operation recursively on the same object with
		// all units
		calcMes = new Measurement(100.0, 1.0, Unit.unity, 1000);
		for (int i = 0; i < allUnits.length; i++) {
			try {
				// Log the unit computed
				message = operation + " " + allUnits[i] + " to "
						+ calcMes.getUnit();
				method = new MethodCall(operation, Measurement.class,
						new Measurement(100.0, 1.0, allUnits[i], 1000));
				calcMes = (Measurement) method.invoke(calcMes);
				message += " = ";
				message += calcMes.getUnit();
				log(message);
			}
			catch (Throwable e) {
				// Check that we got the correct exception
				assertException(method.getName(), ArithmeticException.class, e);
			}
		}
	}


	/**
	 * Logs the content of a <tt>Measurement</tt> object.
	 * 
	 * @param mes Measurement to dump
	 */
	private void log(Measurement mes) {
		if (mes != null) {
			log("value: " + mes.getValue());
			log("error: " + mes.getError());
			log("unit: " + mes.getUnit());
			log("time: " + mes.getTime());
		}
	}

	/**
	 * Logs the content of a <tt>State</tt> object.
	 * 
	 * @param state State to dump
	 */
	private void log(State state) {
		if (state != null) {
			log("value: " + state.getValue());
			log("name: " + state.getName());
			log("time: " + state.getTime());
		}
	}

	/**
	 * Overridden Logger method that logs a comment when things passes.
	 */
	public void pass(String message) {
	}
}
