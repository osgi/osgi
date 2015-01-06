/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal;

import org.osgi.service.dal.SIUnits;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Validates the values of {@link SIUnits} constants.
 */
public final class SIUnitsTest extends DefaultTestBundleControl {

	/**
	 * Tests SI base unit symbols.
	 */
	public void testSIBaseUnits() {
		assertEquals("Metre symbol is not correct.", "m", SIUnits.METRE);
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
		assertEquals("Square metre symbol is not correct.", SIUnits.METRE + "\u00B2", SIUnits.SQUARE_METRE);
		assertEquals("Cubic metre symbol is not correct.", SIUnits.METRE + "\u00B3", SIUnits.CUBIC_METRE);
		assertEquals("Metre per second symbol is not correct.",
				SIUnits.METRE + '/' + SIUnits.SECOND, SIUnits.METRE_PER_SECOND);
		assertEquals("Metre per second squared symbol is not correct.",
				SIUnits.METRE + '/' + SIUnits.SECOND + "\u00B2", SIUnits.METRE_PER_SECOND_SQUARED);
		assertEquals("Reciprocal metre symbol is not correct.",
				SIUnits.METRE + "\u207B\u00B9", SIUnits.RECIPROCAL_METRE);
		assertEquals("Kilogram per cubic metre symbol is not correct.",
				SIUnits.KILOGRAM + '/' + SIUnits.CUBIC_METRE, SIUnits.KILOGRAM_PER_CUBIC_METRE);
		assertEquals("Kilogram per square metre symbol is not correct.",
				SIUnits.KILOGRAM + '/' + SIUnits.SQUARE_METRE, SIUnits.KILOGRAM_PER_SQUARE_METRE);
		assertEquals("Cubic metre per kilogram symbol is not correct.",
				SIUnits.CUBIC_METRE + '/' + SIUnits.KILOGRAM, SIUnits.CUBIC_METRE_PER_KILOGRAM);
		assertEquals("Ampere per square metre symbol is not correct.",
				SIUnits.AMPERE + '/' + SIUnits.SQUARE_METRE, SIUnits.AMPERE_PER_SQUARE_METRE);
		assertEquals("Ampere per metre symbol is not correct.",
				SIUnits.AMPERE + '/' + SIUnits.METRE, SIUnits.AMPERE_PER_METRE);
		assertEquals("Mole per cubic metre symbol is not correct.",
				SIUnits.MOLE + '/' + SIUnits.CUBIC_METRE, SIUnits.MOLE_PER_CUBIC_METRE);
		assertEquals("Candela per square metre symbol is not correct.",
				SIUnits.CANDELA + '/' + SIUnits.SQUARE_METRE, SIUnits.CANDELA_PER_SQUARE_METRE);
	}

	/**
	 * Tests derived units in the SI with special names and symbols
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
		assertEquals("Newton metre symbol is not correct.",
				SIUnits.NEWTON + ' ' + SIUnits.METRE, SIUnits.NEWTON_METRE);
		assertEquals("Newton per metre symbol is not correct.",
				SIUnits.NEWTON + '/' + SIUnits.METRE, SIUnits.NEWTON_PER_METRE);
		assertEquals("Radian per second symbol is not correct.",
				SIUnits.RADIAN + '/' + SIUnits.SECOND, SIUnits.RADIAN_PER_SECOND);
		assertEquals("Radian per second squared symbol is not correct.",
				SIUnits.RADIAN + '/' + SIUnits.SECOND + "\u00B2", SIUnits.RADIAN_PER_SECOND_SQUARED);
		assertEquals("Watt per square metre symbol is not correct.",
				SIUnits.WATT + '/' + SIUnits.METRE + "\u00B2", SIUnits.WATT_PER_SQUARE_METRE);
		assertEquals("Joule per kelvin symbol is not correct.",
				SIUnits.JOULE + '/' + SIUnits.KELVIN, SIUnits.JOULE_PER_KELVIN);
		assertEquals("Joule per kilogram kelvin symbol is not correct.",
				SIUnits.JOULE + "/(" + SIUnits.KILOGRAM + ' ' + SIUnits.KELVIN + ')',
				SIUnits.JOULE_PER_KILOGRAM_KELVIN);
		assertEquals("Joule per kilogram symbol is not correct.",
				SIUnits.JOULE + '/' + SIUnits.KILOGRAM, SIUnits.JOULE_PER_KILOGRAM);
		assertEquals("Watt per metre kelvin symbol is not correct.",
				SIUnits.WATT + "/(" + SIUnits.METRE + ' ' + SIUnits.KELVIN + ')', SIUnits.WATT_PER_METRE_KELVIN);
		assertEquals("Volt per metre symbol is not correct.",
				SIUnits.VOLT + '/' + SIUnits.METRE, SIUnits.VOLT_PER_METRE);
		assertEquals("Coulomb per cubic metre symbol is not correct.",
				SIUnits.COULOMB + '/' + SIUnits.METRE + "\u00B3", SIUnits.COULOMB_PER_CUBIC_METRE);
		assertEquals("Coulomb per square metre symbol is not correct.",
				SIUnits.COULOMB + '/' + SIUnits.METRE + "\u00B2", SIUnits.COULOMB_PER_SQUARE_METRE);
		assertEquals("Farad per metre symbol is not correct.",
				SIUnits.FARAD + '/' + SIUnits.METRE, SIUnits.FARAD_PER_METRE);
		assertEquals("Henry per metre symbol is not correct.",
				SIUnits.HENRY + '/' + SIUnits.METRE, SIUnits.HENRY_PER_METRE);
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
		assertEquals("Watt per square metre steradian symbol is not correct.",
				SIUnits.WATT + "/(" + SIUnits.METRE + "\u00B2 " + SIUnits.STERADIAN + ')',
				SIUnits.WATT_PER_SQUARE_METRE_STERADIAN);
		assertEquals("Katal per cubic metre symbol is not correct.",
				SIUnits.KATAL + '/' + SIUnits.METRE + "\u00B3", SIUnits.KATAL_PER_CUBIC_METRE);
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
		assertEquals("Litre symbol is not correct.", "l", SIUnits.LITRE);
		assertEquals("Tonne symbol is not correct.", "t", SIUnits.TONNE);
		assertEquals("Bar symbol is not correct.", "bar", SIUnits.BAR);
		assertEquals("Millimetre of mercury symbol is not correct.", "mmHg", SIUnits.MILLIMETRE_OF_MERCURY);
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
