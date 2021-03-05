/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.dal;

import junit.framework.TestCase;
import org.osgi.service.dal.SIUnits;

/**
 * Validates the values of {@link SIUnits} constants.
 */
public final class SIUnitsTest extends TestCase {

	/**
	 * Tests SI base unit symbols.
	 */
	public void testSIBaseUnits() {
		assertEquals("Meter symbol is not correct.", "m", SIUnits.METER);
		assertEquals("Kilogram symbol is not correct.", "kg", SIUnits.KILOGRAM);
		assertEquals("Second symbol is not correct.", "s", SIUnits.SECOND);
		assertEquals("Ampere symbol is not correct.", "A", SIUnits.AMPERE);
		assertEquals("Kelvin symbol is not correct.", "\u212A", SIUnits.KELVIN);
		assertEquals("Mole symbol is not correct.", "mol", SIUnits.MOLE);
		assertEquals("Candela symbol is not correct.", "cd", SIUnits.CANDELA);
	}

	/**
	 * Tests derived units in the SI expressed in terms of base units.
	 */
	public void testSIDerivedUnits() {
		assertEquals("Square meter symbol is not correct.", SIUnits.METER + "\u00B2", SIUnits.SQUARE_METER);
		assertEquals("Cubic meter symbol is not correct.", SIUnits.METER + "\u00B3", SIUnits.CUBIC_METER);
		assertEquals("Meter per second symbol is not correct.",
				SIUnits.METER + '/' + SIUnits.SECOND, SIUnits.METER_PER_SECOND);
		assertEquals("Meter per second squared symbol is not correct.",
				SIUnits.METER + '/' + SIUnits.SECOND + "\u00B2", SIUnits.METER_PER_SECOND_SQUARED);
		assertEquals("Reciprocal meter symbol is not correct.",
				SIUnits.METER + "\u207B\u00B9", SIUnits.RECIPROCAL_METER);
		assertEquals("Kilogram per cubic meter symbol is not correct.",
				SIUnits.KILOGRAM + '/' + SIUnits.CUBIC_METER, SIUnits.KILOGRAM_PER_CUBIC_METER);
		assertEquals("Kilogram per square meter symbol is not correct.",
				SIUnits.KILOGRAM + '/' + SIUnits.SQUARE_METER, SIUnits.KILOGRAM_PER_SQUARE_METER);
		assertEquals("Cubic meter per kilogram symbol is not correct.",
				SIUnits.CUBIC_METER + '/' + SIUnits.KILOGRAM, SIUnits.CUBIC_METER_PER_KILOGRAM);
		assertEquals("Ampere per square meter symbol is not correct.",
				SIUnits.AMPERE + '/' + SIUnits.SQUARE_METER, SIUnits.AMPERE_PER_SQUARE_METER);
		assertEquals("Ampere per meter symbol is not correct.",
				SIUnits.AMPERE + '/' + SIUnits.METER, SIUnits.AMPERE_PER_METER);
		assertEquals("Mole per cubic meter symbol is not correct.",
				SIUnits.MOLE + '/' + SIUnits.CUBIC_METER, SIUnits.MOLE_PER_CUBIC_METER);
		assertEquals("Candela per square meter symbol is not correct.",
				SIUnits.CANDELA + '/' + SIUnits.SQUARE_METER, SIUnits.CANDELA_PER_SQUARE_METER);
	}

	/**
	 * Tests derived units in the SI with special names and symbols.
	 */
	public void testSISpecialDerivedUnits() {
		assertEquals("Radian symbol is not correct.", "rad", SIUnits.RADIAN);
		assertEquals("Steradian symbol is not correct.", "sr", SIUnits.STERADIAN);
		assertEquals("Hertz symbol is not correct.", "Hz", SIUnits.HERTZ);
		assertEquals("Newton symbol is not correct.", "N", SIUnits.NEWTON);
		assertEquals("Pascal symbol is not correct.", "Pa", SIUnits.PASCAL);
		assertEquals("Joule symbol is not correct.", "J", SIUnits.JOULE);
		assertEquals("Watt symbol is not correct.", "W", SIUnits.WATT);
		assertEquals("Coulomb symbol is not correct.", "C", SIUnits.COULOMB);
		assertEquals("Volt symbol is not correct.", "V", SIUnits.VOLT);
		assertEquals("Farad symbol is not correct.", "F", SIUnits.FARAD);
		assertEquals("Ohm symbol is not correct.", "\u2126", SIUnits.OHM);
		assertEquals("Siemens symbol is not correct.", "S", SIUnits.SIEMENS);
		assertEquals("Weber symbol is not correct.", "Wb", SIUnits.WEBER);
		assertEquals("Tesla symbol is not correct.", "T", SIUnits.TESLA);
		assertEquals("Henry symbol is not correct.", "H", SIUnits.HENRY);
		assertEquals("Degree Celsius symbol is not correct.", "\u2103", SIUnits.DEGREE_CELSIUS);
		assertEquals("Lumen symbol is not correct.", "lm", SIUnits.LUMEN);
		assertEquals("Lux symbol is not correct.", "lx", SIUnits.LUX);
		assertEquals("Becquerel symbol is not correct.", "Bq", SIUnits.BECQUEREL);
		assertEquals("Gray symbol is not correct.", "Gy", SIUnits.GRAY);
		assertEquals("Sievert symbol is not correct.", "Sv", SIUnits.SIEVERT);
		assertEquals("Katal symbol is not correct.", "kat", SIUnits.KATAL);
	}

	/**
	 * Tests SI coherent derived units whose names and symbols include SI
	 * coherent derived units with special names and symbols.
	 */
	public void testSICompositeSpecialDerivedUnits() {
		assertEquals("Pascal second symbol is not correct.",
				SIUnits.PASCAL + ' ' + SIUnits.SECOND, SIUnits.PASCAL_SECOND);
		assertEquals("Newton meter symbol is not correct.",
				SIUnits.NEWTON + ' ' + SIUnits.METER, SIUnits.NEWTON_METER);
		assertEquals("Newton per meter symbol is not correct.",
				SIUnits.NEWTON + '/' + SIUnits.METER, SIUnits.NEWTON_PER_METER);
		assertEquals("Radian per second symbol is not correct.",
				SIUnits.RADIAN + '/' + SIUnits.SECOND, SIUnits.RADIAN_PER_SECOND);
		assertEquals("Radian per second squared symbol is not correct.",
				SIUnits.RADIAN + '/' + SIUnits.SECOND + "\u00B2", SIUnits.RADIAN_PER_SECOND_SQUARED);
		assertEquals("Watt per square meter symbol is not correct.",
				SIUnits.WATT + '/' + SIUnits.METER + "\u00B2", SIUnits.WATT_PER_SQUARE_METER);
		assertEquals("Joule per kelvin symbol is not correct.",
				SIUnits.JOULE + '/' + SIUnits.KELVIN, SIUnits.JOULE_PER_KELVIN);
		assertEquals("Joule per kilogram kelvin symbol is not correct.",
				SIUnits.JOULE + "/(" + SIUnits.KILOGRAM + ' ' + SIUnits.KELVIN + ')',
				SIUnits.JOULE_PER_KILOGRAM_KELVIN);
		assertEquals("Joule per kilogram symbol is not correct.",
				SIUnits.JOULE + '/' + SIUnits.KILOGRAM, SIUnits.JOULE_PER_KILOGRAM);
		assertEquals("Watt per meter kelvin symbol is not correct.",
				SIUnits.WATT + "/(" + SIUnits.METER + ' ' + SIUnits.KELVIN + ')', SIUnits.WATT_PER_METER_KELVIN);
		assertEquals("Volt per meter symbol is not correct.",
				SIUnits.VOLT + '/' + SIUnits.METER, SIUnits.VOLT_PER_METER);
		assertEquals("Coulomb per cubic meter symbol is not correct.",
				SIUnits.COULOMB + '/' + SIUnits.METER + "\u00B3", SIUnits.COULOMB_PER_CUBIC_METER);
		assertEquals("Coulomb per square meter symbol is not correct.",
				SIUnits.COULOMB + '/' + SIUnits.METER + "\u00B2", SIUnits.COULOMB_PER_SQUARE_METER);
		assertEquals("Farad per meter symbol is not correct.",
				SIUnits.FARAD + '/' + SIUnits.METER, SIUnits.FARAD_PER_METER);
		assertEquals("Henry per meter symbol is not correct.",
				SIUnits.HENRY + '/' + SIUnits.METER, SIUnits.HENRY_PER_METER);
		assertEquals("Joule per mole symbol is not correct.",
				SIUnits.JOULE + '/' + SIUnits.MOLE, SIUnits.JOULE_PER_MOLE);
		assertEquals("Joule per mole kelvin symbol is not correct.",
				SIUnits.JOULE + "/(" + SIUnits.MOLE + ' ' + SIUnits.KELVIN + ')', SIUnits.JOULE_PER_MOLE_KELVIN);
		assertEquals("Coulomb per kilogram symbol is not correct.",
				SIUnits.COULOMB + '/' + SIUnits.KILOGRAM, SIUnits.COULOMB_PER_KILOGRAM);
		assertEquals("Gray per second symbol is not correct.",
				SIUnits.GRAY + '/' + SIUnits.SECOND, SIUnits.GRAY_PER_SECOND);
		assertEquals("Watt per steradian symbol is not correct.",
				SIUnits.WATT + '/' + SIUnits.STERADIAN, SIUnits.WATT_PER_STERADIAN);
		assertEquals("Watt per square meter steradian symbol is not correct.",
				SIUnits.WATT + "/(" + SIUnits.METER + "\u00B2 " + SIUnits.STERADIAN + ')',
				SIUnits.WATT_PER_SQUARE_METER_STERADIAN);
		assertEquals("Katal per cubic meter symbol is not correct.",
				SIUnits.KATAL + '/' + SIUnits.METER + "\u00B3", SIUnits.KATAL_PER_CUBIC_METER);
	}

	/**
	 * Tests non-SI units accepted for use with the International System of
	 * Units.
	 */
	public void testNonSIUnits() {
		assertEquals("Time minute symbol is not correct.", "min", SIUnits.TIME_MINUTE);
		assertEquals("Hour symbol is not correct.", "h", SIUnits.HOUR);
		assertEquals("Day symbol is not correct.", "d", SIUnits.DAY);
		assertEquals("Degree symbol is not correct.", "\u00B0", SIUnits.DEGREE);
		assertEquals("Plane angle minute symbol is not correct.", "\u2032", SIUnits.PLANE_ANGLE_MINUTE);
		assertEquals("Plane angle second symbol is not correct.", "\u2033", SIUnits.PLANE_ANGLE_SECOND);
		assertEquals("Hectare symbol is not correct.", "ha", SIUnits.HECTARE);
		assertEquals("Liter symbol is not correct.", "l", SIUnits.LITER);
		assertEquals("Tonne symbol is not correct.", "t", SIUnits.TONNE);
		assertEquals("Bar symbol is not correct.", "bar", SIUnits.BAR);
		assertEquals("Millimeter of mercury symbol is not correct.", "mmHg", SIUnits.MILLIMETER_OF_MERCURY);
		assertEquals("Angstrom symbol is not correct.", "\u212B", SIUnits.ANGSTROM);
		assertEquals("Nautical mile symbol is not correct.", "M", SIUnits.NAUTICAL_MILE);
		assertEquals("Barn symbol is not correct.", "b", SIUnits.BARN);
		assertEquals("Knot symbol is not correct.", "kn", SIUnits.KNOT);
		assertEquals("Neper symbol is not correct.", "Np", SIUnits.NEPER);
		assertEquals("Bel symbol is not correct.", "B", SIUnits.BEL);
		assertEquals("Decibel symbol is not correct.", "dB", SIUnits.DECIBEL);
	}

	/**
	 * Tests non-SI units associated with the CGS and the CGS-Gaussian system of
	 * units.
	 */
	public void testNonSIUnitsCGS() {
		assertEquals("Erg symbol is not correct.", "erg", SIUnits.ERG);
		assertEquals("Dyne symbol is not correct.", "dyn", SIUnits.DYNE);
		assertEquals("Poise symbol is not correct.", "P", SIUnits.POISE);
		assertEquals("Stokes symbol is not correct.", "St", SIUnits.STOKES);
		assertEquals("Stilb symbol is not correct.", "sb", SIUnits.STILB);
		assertEquals("Phot symbol is not correct.", "ph", SIUnits.PHOT);
		assertEquals("Gal symbol is not correct.", "Gal", SIUnits.GAL);
		assertEquals("Maxwell symbol is not correct.", "Mx", SIUnits.MAXWELL);
		assertEquals("Gauss symbol is not correct.", "G", SIUnits.GAUSS);
		assertEquals("Oersted symbol is not correct.", "Oe", SIUnits.OERSTED);
	}
}
