/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal;

import org.osgi.service.dal.Units;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Validates the values of {@link Units} constants.
 */
public final class UnitsTest extends DefaultTestBundleControl {

	/**
	 * Tests SI base unit symbols.
	 */
	public void testSIBaseUnits() {
		assertEquals("Metre symbol is not correct.", "m", Units.METRE);
		assertEquals("Kilogram symbol is not correct.", "kg", Units.KILOGRAM);
		assertEquals("Second symbol is not correct.", "s", Units.SECOND);
		assertEquals("Ampere symbol is not correct.", "A", Units.AMPERE);
		assertEquals("Kelvin symbol is not correct.", "\u212A", Units.KELVIN);
		assertEquals("Mole symbol is not correct.", "mol", Units.MOLE);
		assertEquals("Candela symbol is not correct.", "cd", Units.CANDELA);
	}

	/**
	 * Tests derived units in the SI expressed in terms of base units.
	 */
	public void testSIDerivedUnits() {
		assertEquals("Square metre symbol is not correct.", Units.METRE + "\u00B2", Units.SQUARE_METRE);
		assertEquals("Cubic metre symbol is not correct.", Units.METRE + "\u00B3", Units.CUBIC_METRE);
		assertEquals("Metre per second symbol is not correct.",
				Units.METRE + '/' + Units.SECOND, Units.METRE_PER_SECOND);
		assertEquals("Metre per second squared symbol is not correct.",
				Units.METRE + '/' + Units.SECOND + "\u00B2", Units.METRE_PER_SECOND_SQUARED);
		assertEquals("Reciprocal metre symbol is not correct.",
				Units.METRE + "\u207B\u00B9", Units.RECIPROCAL_METRE);
		assertEquals("Kilogram per cubic metre symbol is not correct.",
				Units.KILOGRAM + '/' + Units.CUBIC_METRE, Units.KILOGRAM_PER_CUBIC_METRE);
		assertEquals("Kilogram per square metre symbol is not correct.",
				Units.KILOGRAM + '/' + Units.SQUARE_METRE, Units.KILOGRAM_PER_SQUARE_METRE);
		assertEquals("Cubic metre per kilogram symbol is not correct.",
				Units.CUBIC_METRE + '/' + Units.KILOGRAM, Units.CUBIC_METRE_PER_KILOGRAM);
		assertEquals("Ampere per square metre symbol is not correct.",
				Units.AMPERE + '/' + Units.SQUARE_METRE, Units.AMPERE_PER_SQUARE_METRE);
		assertEquals("Ampere per metre symbol is not correct.",
				Units.AMPERE + '/' + Units.METRE, Units.AMPERE_PER_METRE);
		assertEquals("Mole per cubic metre symbol is not correct.",
				Units.MOLE + '/' + Units.CUBIC_METRE, Units.MOLE_PER_CUBIC_METRE);
		assertEquals("Candela per square metre symbol is not correct.",
				Units.CANDELA + '/' + Units.SQUARE_METRE, Units.CANDELA_PER_SQUARE_METRE);
	}

	/**
	 * Tests derived units in the SI with special names and symbols
	 */
	public void testSISpecialDerivedUnits() {
		assertEquals("Radian symbol is not correct.", "rad", Units.RADIAN);
		assertEquals("Steradian symbol is not correct.", "sr", Units.STERADIAN);
		assertEquals("Hertz symbol is not correct.", "Hz", Units.HERTZ);
		assertEquals("Newton symbol is not correct.", "N", Units.NEWTON);
		assertEquals("Pascal symbol is not correct.", "Pa", Units.PASCAL);
		assertEquals("Joule symbol is not correct.", "J", Units.JOULE);
		assertEquals("Watt symbol is not correct.", "W", Units.WATT);
		assertEquals("Coulomb symbol is not correct.", "C", Units.COULOMB);
		assertEquals("Volt symbol is not correct.", "V", Units.VOLT);
		assertEquals("Farad symbol is not correct.", "F", Units.FARAD);
		assertEquals("Ohm symbol is not correct.", "\u2126", Units.OHM);
		assertEquals("Siemens symbol is not correct.", "S", Units.SIEMENS);
		assertEquals("Weber symbol is not correct.", "Wb", Units.WEBER);
		assertEquals("Tesla symbol is not correct.", "T", Units.TESLA);
		assertEquals("Henry symbol is not correct.", "H", Units.HENRY);
		assertEquals("Degree Celsius symbol is not correct.", "\u2103", Units.DEGREE_CELSIUS);
		assertEquals("Lumen symbol is not correct.", "lm", Units.LUMEN);
		assertEquals("Lux symbol is not correct.", "lx", Units.LUX);
		assertEquals("Becquerel symbol is not correct.", "Bq", Units.BECQUEREL);
		assertEquals("Gray symbol is not correct.", "Gy", Units.GRAY);
		assertEquals("Sievert symbol is not correct.", "Sv", Units.SIEVERT);
		assertEquals("Katal symbol is not correct.", "kat", Units.KATAL);
	}

	/**
	 * Tests SI coherent derived units whose names and symbols include SI
	 * coherent derived units with special names and symbols.
	 */
	public void testSICompositeSpecialDerivedUnits() {
		assertEquals("Pascal second symbol is not correct.",
				Units.PASCAL + ' ' + Units.SECOND, Units.PASCAL_SECOND);
		assertEquals("Newton metre symbol is not correct.",
				Units.NEWTON + ' ' + Units.METRE, Units.NEWTON_METRE);
		assertEquals("Newton per metre symbol is not correct.",
				Units.NEWTON + '/' + Units.METRE, Units.NEWTON_PER_METRE);
		assertEquals("Radian per second symbol is not correct.",
				Units.RADIAN + '/' + Units.SECOND, Units.RADIAN_PER_SECOND);
		assertEquals("Radian per second squared symbol is not correct.",
				Units.RADIAN + '/' + Units.SECOND + "\u00B2", Units.RADIAN_PER_SECOND_SQUARED);
		assertEquals("Watt per square metre symbol is not correct.",
				Units.WATT + '/' + Units.METRE + "\u00B2", Units.WATT_PER_SQUARE_METRE);
		assertEquals("Joule per kelvin symbol is not correct.",
				Units.JOULE + '/' + Units.KELVIN, Units.JOULE_PER_KELVIN);
		assertEquals("Joule per kilogram kelvin symbol is not correct.",
				Units.JOULE + "/(" + Units.KILOGRAM + ' ' + Units.KELVIN + ')',
				Units.JOULE_PER_KILOGRAM_KELVIN);
		assertEquals("Joule per kilogram symbol is not correct.",
				Units.JOULE + '/' + Units.KILOGRAM, Units.JOULE_PER_KILOGRAM);
		assertEquals("Watt per metre kelvin symbol is not correct.",
				Units.WATT + "/(" + Units.METRE + ' ' + Units.KELVIN + ')', Units.WATT_PER_METRE_KELVIN);
		assertEquals("Volt per metre symbol is not correct.",
				Units.VOLT + '/' + Units.METRE, Units.VOLT_PER_METRE);
		assertEquals("Coulomb per cubic metre symbol is not correct.",
				Units.COULOMB + '/' + Units.METRE + "\u00B3", Units.COULOMB_PER_CUBIC_METRE);
		assertEquals("Coulomb per square metre symbol is not correct.",
				Units.COULOMB + '/' + Units.METRE + "\u00B2", Units.COULOMB_PER_SQUARE_METRE);
		assertEquals("Farad per metre symbol is not correct.",
				Units.FARAD + '/' + Units.METRE, Units.FARAD_PER_METRE);
		assertEquals("Henry per metre symbol is not correct.",
				Units.HENRY + '/' + Units.METRE, Units.HENRY_PER_METRE);
		assertEquals("Joule per mole symbol is not correct.",
				Units.JOULE + '/' + Units.MOLE, Units.JOULE_PER_MOLE);
		assertEquals("Joule per mole kelvin symbol is not correct.",
				Units.JOULE + "/(" + Units.MOLE + ' ' + Units.KELVIN + ')', Units.JOULE_PER_MOLE_KELVIN);
		assertEquals("Coulomb per kilogram symbol is not correct.",
				Units.COULOMB + '/' + Units.KILOGRAM, Units.COULOMB_PER_KILOGRAM);
		assertEquals("Gray per second symbol is not correct.",
				Units.GRAY + '/' + Units.SECOND, Units.GRAY_PER_SECOND);
		assertEquals("Watt per steradian symbol is not correct.",
				Units.WATT + '/' + Units.STERADIAN, Units.WATT_PER_STERADIAN);
		assertEquals("Watt per square metre steradian symbol is not correct.",
				Units.WATT + "/(" + Units.METRE + "\u00B2 " + Units.STERADIAN + ')',
				Units.WATT_PER_SQUARE_METRE_STERADIAN);
		assertEquals("Katal per cubic metre symbol is not correct.",
				Units.KATAL + '/' + Units.METRE + "\u00B3", Units.KATAL_PER_CUBIC_METRE);
	}

	/**
	 * Tests non-SI units accepted for use with the International System of
	 * Units.
	 */
	public void testNonSIUnits() {
		assertEquals("Time minute symbol is not correct.", "min", Units.TIME_MINUTE);
		assertEquals("Hour symbol is not correct.", "h", Units.HOUR);
		assertEquals("Day symbol is not correct.", "d", Units.DAY);
		assertEquals("Degree symbol is not correct.", "\u00B0", Units.DEGREE);
		assertEquals("Plane angle minute symbol is not correct.", "\u2032", Units.PLANE_ANGLE_MINUTE);
		assertEquals("Plane angle second symbol is not correct.", "\u2033", Units.PLANE_ANGLE_SECOND);
		assertEquals("Hectare symbol is not correct.", "ha", Units.HECTARE);
		assertEquals("Litre symbol is not correct.", "l", Units.LITRE);
		assertEquals("Tonne symbol is not correct.", "t", Units.TONNE);
		assertEquals("Bar symbol is not correct.", "bar", Units.BAR);
		assertEquals("Millimetre of mercury symbol is not correct.", "mmHg", Units.MILLIMETRE_OF_MERCURY);
		assertEquals("Angstrom symbol is not correct.", "\u212B", Units.ANGSTROM);
		assertEquals("Nautical mile symbol is not correct.", "M", Units.NAUTICAL_MILE);
		assertEquals("Barn symbol is not correct.", "b", Units.BARN);
		assertEquals("Knot symbol is not correct.", "kn", Units.KNOT);
		assertEquals("Neper symbol is not correct.", "Np", Units.NEPER);
		assertEquals("Bel symbol is not correct.", "B", Units.BEL);
		assertEquals("Decibel symbol is not correct.", "dB", Units.DECIBEL);
	}

	/**
	 * Tests non-SI units associated with the CGS and the CGS-Gaussian system of
	 * units.
	 */
	public void testNonSIUnitsCGS() {
		assertEquals("Erg symbol is not correct.", "erg", Units.ERG);
		assertEquals("Dyne symbol is not correct.", "dyn", Units.DYNE);
		assertEquals("Poise symbol is not correct.", "P", Units.POISE);
		assertEquals("Stokes symbol is not correct.", "St", Units.STOKES);
		assertEquals("Stilb symbol is not correct.", "sb", Units.STILB);
		assertEquals("Phot symbol is not correct.", "ph", Units.PHOT);
		assertEquals("Gal symbol is not correct.", "Gal", Units.GAL);
		assertEquals("Maxwell symbol is not correct.", "Mx", Units.MAXWELL);
		assertEquals("Gauss symbol is not correct.", "G", Units.GAUSS);
		assertEquals("Oersted symbol is not correct.", "Oe", Units.OERSTED);
	}

}
