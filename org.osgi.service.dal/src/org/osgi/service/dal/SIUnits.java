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

package org.osgi.service.dal;

/**
 * Contains most of the International System of Units unit symbols. The constant
 * name represents the unit name. The constant value represents the unit symbol
 * as it's defined in {@link PropertyMetadata#UNITS}.
 */
public final class SIUnits {
	private SIUnits() {
		// non-instantiable
	}

	// Table 1. SI base units
	/**
	 * Unit of length defined by the International System of Units (SI). It's
	 * one of be base units called meter.
	 */
	public static final String	METER							= "m";

	/**
	 * Unit of mass defined by the International System of Units (SI). It's one
	 * of be base units called kilogram.
	 */
	public static final String	KILOGRAM						= "kg";

	/**
	 * Unit of time defined by the International System of Units (SI). It's one
	 * of be base units called second.
	 */
	public static final String	SECOND							= "s";

	/**
	 * Unit of electric current defined by the International System of Units
	 * (SI). It's one of be base units called ampere.
	 */
	public static final String	AMPERE							= "A";

	/**
	 * Unit of thermodynamic temperature defined by the International System of
	 * Units (SI). It's one of be base units called kelvin.
	 */
	// Kelvin sign
	public static final String	KELVIN							= "\u212A";

	/**
	 * Unit of amount of substance defined by the International System of Units
	 * (SI). It's one of be base units called mole.
	 */
	public static final String	MOLE							= "mol";

	/**
	 * Unit of luminous intensity defined by the International System of Units
	 * (SI). It's one of be base units called candela.
	 */
	public static final String	CANDELA							= "cd";

	// Table 2. Examples of coherent derived units in the SI expressed in terms
	// of base units.
	/**
	 * Unit of area. It's one of coherent derived units in the SI expressed in
	 * terms of base units. The unit is called square meter.
	 */
	// m and superscript two
	public static final String	SQUARE_METER					= METER + "\u00B2";

	/**
	 * Unit of volume. It's one of coherent derived units in the SI expressed in
	 * terms of base units. The unit is called cubic meter.
	 */
	// m and superscript three
	public static final String	CUBIC_METER						= METER + "\u00B3";

	/**
	 * Unit of speed, velocity. It's one of coherent derived units in the SI
	 * expressed in terms of base units. The unit is called meter per second.
	 */
	public static final String	METER_PER_SECOND				= METER + '/' + SECOND;

	/**
	 * Unit of acceleration. It's one of coherent derived units in the SI
	 * expressed in terms of base units. The unit is called meter per second
	 * squared.
	 */
	// m per s and superscript two
	public static final String	METER_PER_SECOND_SQUARED		= METER + '/' + SECOND + "\u00B2";

	/**
	 * Unit of wavenumber. It's one of coherent derived units in the SI
	 * expressed in terms of base units. The unit is called reciprocal meter.
	 */
	// m and superscript minus and superscript one
	public static final String	RECIPROCAL_METER				= METER + "\u207B\u00B9";

	/**
	 * Unit of density, mass density, mass concentration. It's one of coherent
	 * derived units in the SI expressed in terms of base units. The unit is
	 * called kilogram per cubic meter.
	 */
	// kg per m and superscript three
	public static final String	KILOGRAM_PER_CUBIC_METER		= KILOGRAM + '/' + METER + "\u00B3";

	/**
	 * Unit of surface density. It's one of coherent derived units in the SI
	 * expressed in terms of base units. The unit is called kilogram per square
	 * meter.
	 */
	// kg per m and superscript two
	public static final String	KILOGRAM_PER_SQUARE_METER		= KILOGRAM + '/' + METER + "\u00B2";

	/**
	 * Unit of specific volume. It's one of coherent derived units in the SI
	 * expressed in terms of base units. The unit is called cubic meter per
	 * kilogram.
	 */
	// m and superscript three per kg
	public static final String	CUBIC_METER_PER_KILOGRAM		= METER + "\u00B3" + '/' + KILOGRAM;

	/**
	 * Unit of current density. It's one of coherent derived units in the SI
	 * expressed in terms of base units. The unit is called ampere per square
	 * meter.
	 */
	// A per m and superscript two
	public static final String	AMPERE_PER_SQUARE_METER			= AMPERE + '/' + METER + "\u00B2";

	/**
	 * Unit of magnetic field strength. It's one of coherent derived units in
	 * the SI expressed in terms of base units. The unit is called ampere per
	 * meter.
	 */
	public static final String	AMPERE_PER_METER				= AMPERE + '/' + METER;

	/**
	 * Unit of amount concentration, concentration. It's one of coherent derived
	 * units in the SI expressed in terms of base units. The unit is called mole
	 * per cubic meter.
	 */
	// mol per m and superscript three
	public static final String	MOLE_PER_CUBIC_METER			= MOLE + '/' + METER + "\u00B3";

	/**
	 * Unit of luminance. It's one of coherent derived units in the SI expressed
	 * in terms of base units. The unit is called candela per square meter.
	 */
	// cd per m and superscript two
	public static final String	CANDELA_PER_SQUARE_METER		= CANDELA + '/' + METER + "\u00B2";

	// Table 3. Coherent derived units in the SI with special names and symbols
	/**
	 * Unit of plane angle. It's one of the coherent derived units in the SI
	 * with special names and symbols. The unit is called radian.
	 */
	public static final String	RADIAN							= "rad";

	/**
	 * Unit of solid angle. It's one of the coherent derived units in the SI
	 * with special names and symbols. The unit is called steradian.
	 */
	public static final String	STERADIAN						= "sr";

	/**
	 * Unit of frequency. It's one of the coherent derived units in the SI with
	 * special names and symbols. The unit is called hertz.
	 */
	public static final String	HERTZ							= "Hz";

	/**
	 * Unit of force. It's one of the coherent derived units in the SI with
	 * special names and symbols. The unit is called newton.
	 */
	public static final String	NEWTON							= "N";

	/**
	 * Unit of pressure, stress. It's one of the coherent derived units in the
	 * SI with special names and symbols. The unit is called pascal.
	 */
	public static final String	PASCAL							= "Pa";

	/**
	 * Unit of energy, work, amount of electricity. It's one of the coherent
	 * derived units in the SI with special names and symbols. The unit is
	 * called joule.
	 */
	public static final String	JOULE							= "J";

	/**
	 * Unit of power, radiant flux. It's one of the coherent derived units in
	 * the SI with special names and symbols. The unit is called watt.
	 */
	public static final String	WATT							= "W";

	/**
	 * Unit of electronic charge, amount of electricity. It's one of the
	 * coherent derived units in the SI with special names and symbols. The unit
	 * is called coulomb.
	 */
	public static final String	COULOMB							= "C";

	/**
	 * Unit of electric potential difference, electromotive force. It's one of
	 * the coherent derived units in the SI with special names and symbols. The
	 * unit is called volt.
	 */
	public static final String	VOLT							= "V";

	/**
	 * Unit of capacitance. It's one of the coherent derived units in the SI
	 * with special names and symbols. The unit is called farad.
	 */
	public static final String	FARAD							= "F";

	/**
	 * Unit of electric resistance. It's one of the coherent derived units in
	 * the SI with special names and symbols. The unit is called ohm.
	 */
	// Ohm sign
	public static final String	OHM								= "\u2126";

	/**
	 * Unit of electric conductance. It's one of the coherent derived units in
	 * the SI with special names and symbols. The unit is called siemens.
	 */
	public static final String	SIEMENS							= "S";

	/**
	 * Unit of magnetic flux. It's one of the coherent derived units in the SI
	 * with special names and symbols. The unit is called weber.
	 */
	public static final String	WEBER							= "Wb";

	/**
	 * Unit of magnetic flux density. It's one of the coherent derived units in
	 * the SI with special names and symbols. The unit is called tesla.
	 */
	public static final String	TESLA							= "T";

	/**
	 * Unit of inductance. It's one of the coherent derived units in the SI with
	 * special names and symbols. The unit is called henry.
	 */
	public static final String	HENRY							= "H";

	/**
	 * Unit of Celsius temperature. It's one of the coherent derived units in
	 * the SI with special names and symbols. The unit is called degree Celsius.
	 */
	// degree celsius
	public static final String	DEGREE_CELSIUS					= "\u2103";

	/**
	 * Unit of luminous flux. It's one of the coherent derived units in the SI
	 * with special names and symbols. The unit is called lumen.
	 */
	public static final String	LUMEN							= "lm";

	/**
	 * Unit of illuminance. It's one of the coherent derived units in the SI
	 * with special names and symbols. The unit is called lux.
	 */
	public static final String	LUX								= "lx";

	/**
	 * Unit of activity referred to a radionuclide. It's one of the coherent
	 * derived units in the SI with special names and symbols. The unit is
	 * called becquerel.
	 */
	public static final String	BECQUEREL						= "Bq";

	/**
	 * Unit of absorbed dose, specific energy (imparted), kerma. It's one of the
	 * coherent derived units in the SI with special names and symbols. The unit
	 * is called gray.
	 */
	public static final String	GRAY							= "Gy";

	/**
	 * Unit of dose equivalent, ambient dose equivalent, directional dose
	 * equivalent, personal dose equivalent. It's one of the coherent derived
	 * units in the SI with special names and symbols. The unit is called
	 * sievert.
	 */
	public static final String	SIEVERT							= "Sv";

	/**
	 * Unit of catalytic activity. It's one of the coherent derived units in the
	 * SI with special names and symbols. The unit is called katal.
	 */
	public static final String	KATAL							= "kat";

	// Table 4. Examples of SI coherent derived units whose names and symbols
	// include SI coherent derived units with special names and symbols
	/**
	 * Unit of dynamic viscosity. It's one of coherent derived units whose names
	 * and symbols include SI coherent derived units with special names and
	 * symbols. The unit is called pascal second.
	 */
	public static final String	PASCAL_SECOND					= PASCAL + ' ' + SECOND;

	/**
	 * Unit of moment of force. It's one of coherent derived units whose names
	 * and symbols include SI coherent derived units with special names and
	 * symbols. The unit is called newton meter.
	 */
	public static final String	NEWTON_METER					= NEWTON + ' ' + METER;

	/**
	 * Unit of surface tension. It's one of coherent derived units whose names
	 * and symbols include SI coherent derived units with special names and
	 * symbols. The unit is called newton per meter.
	 */
	public static final String	NEWTON_PER_METER				= NEWTON + '/' + METER;

	/**
	 * Unit of angular velocity. It's one of coherent derived units whose names
	 * and symbols include SI coherent derived units with special names and
	 * symbols. The unit is called radian per second.
	 */
	public static final String	RADIAN_PER_SECOND				= RADIAN + '/' + SECOND;

	/**
	 * Unit of angular acceleration. It's one of coherent derived units whose
	 * names and symbols include SI coherent derived units with special names
	 * and symbols. The unit is called radian per second squared.
	 */
	// rad per s and superscript two
	public static final String	RADIAN_PER_SECOND_SQUARED		= RADIAN + '/' + SECOND + "\u00B2";

	/**
	 * Unit of heat flux density, irradiance. It's one of coherent derived units
	 * whose names and symbols include SI coherent derived units with special
	 * names and symbols. The unit is called watt per square meter.
	 */
	// W per m and superscript two
	public static final String	WATT_PER_SQUARE_METER			= WATT + '/' + METER + "\u00B2";

	/**
	 * Unit of heat capacity, entropy. It's one of coherent derived units whose
	 * names and symbols include SI coherent derived units with special names
	 * and symbols. The unit is called joule per kelvin.
	 */
	public static final String	JOULE_PER_KELVIN				= JOULE + '/' + KELVIN;

	/**
	 * Unit of specific heat capacity, specific entropy. It's one of coherent
	 * derived units whose names and symbols include SI coherent derived units
	 * with special names and symbols. The unit is called joule per kilogram
	 * kelvin.
	 */
	public static final String	JOULE_PER_KILOGRAM_KELVIN		= JOULE + "/(" + KILOGRAM + ' ' + KELVIN + ')';

	/**
	 * Unit of specific energy. It's one of coherent derived units whose names
	 * and symbols include SI coherent derived units with special names and
	 * symbols. The unit is called joule per kilogram.
	 */
	public static final String	JOULE_PER_KILOGRAM				= JOULE + '/' + KILOGRAM;

	/**
	 * Unit of thermal conductivity. It's one of coherent derived units whose
	 * names and symbols include SI coherent derived units with special names
	 * and symbols. The unit is called watt per meter kelvin.
	 */
	public static final String	WATT_PER_METER_KELVIN			= WATT + "/(" + METER + ' ' + KELVIN + ')';

	/**
	 * Unit of energy density. It's one of coherent derived units whose names
	 * and symbols include SI coherent derived units with special names and
	 * symbols. The unit is called joule per cubic meter.
	 */
	// J per m and superscript three
	public static final String	JOULE_PER_CUBIC_METER			= JOULE + '/' + METER + "\u00B3";

	/**
	 * Unit of electric field strength. It's one of coherent derived units whose
	 * names and symbols include SI coherent derived units with special names
	 * and symbols. The unit is called volt per meter.
	 */
	public static final String	VOLT_PER_METER					= VOLT + '/' + METER;

	/**
	 * Unit of electric charge density. It's one of coherent derived units whose
	 * names and symbols include SI coherent derived units with special names
	 * and symbols. The unit is called coulomb per cubic meter.
	 */
	// C per m and superscript three
	public static final String	COULOMB_PER_CUBIC_METER			= COULOMB + '/' + METER + "\u00B3";

	/**
	 * Unit of surface charge density, electric flux density, electric
	 * displacement. It's one of coherent derived units whose names and symbols
	 * include SI coherent derived units with special names and symbols. The
	 * unit is called coulomb per square meter.
	 */
	// C per m and superscript two
	public static final String	COULOMB_PER_SQUARE_METER		= COULOMB + '/' + METER + "\u00B2";

	/**
	 * Unit of permittivity. It's one of coherent derived units whose names and
	 * symbols include SI coherent derived units with special names and symbols.
	 * The unit is called farad per meter.
	 */
	public static final String	FARAD_PER_METER					= FARAD + '/' + METER;

	/**
	 * Unit of permeability. It's one of coherent derived units whose names and
	 * symbols include SI coherent derived units with special names and symbols.
	 * The unit is called henry per meter.
	 */
	public static final String	HENRY_PER_METER					= HENRY + '/' + METER;

	/**
	 * Unit of molar energy. It's one of coherent derived units whose names and
	 * symbols include SI coherent derived units with special names and symbols.
	 * The unit is called joule per mole.
	 */
	public static final String	JOULE_PER_MOLE					= JOULE + '/' + MOLE;

	/**
	 * Unit of molar entropy, molar heat capacity. It's one of coherent derived
	 * units whose names and symbols include SI coherent derived units with
	 * special names and symbols. The unit is called joule per mole kelvin.
	 */
	public static final String	JOULE_PER_MOLE_KELVIN			= JOULE + "/(" + MOLE + ' ' + KELVIN + ')';

	/**
	 * Unit of exposure (x- and gamma-rays). It's one of coherent derived units
	 * whose names and symbols include SI coherent derived units with special
	 * names and symbols. The unit is called coulomb per kilogram.
	 */
	public static final String	COULOMB_PER_KILOGRAM			= COULOMB + '/' + KILOGRAM;

	/**
	 * Unit of absorbed dose rate. It's one of coherent derived units whose
	 * names and symbols include SI coherent derived units with special names
	 * and symbols. The unit is called gray per second.
	 */
	public static final String	GRAY_PER_SECOND					= GRAY + '/' + SECOND;

	/**
	 * Unit of radiant intensity. It's one of coherent derived units whose names
	 * and symbols include SI coherent derived units with special names and
	 * symbols. The unit is called watt per steradian.
	 */
	public static final String	WATT_PER_STERADIAN				= WATT + '/' + STERADIAN;

	/**
	 * Unit of radiance. It's one of coherent derived units whose names and
	 * symbols include SI coherent derived units with special names and symbols.
	 * The unit is called watt per square meter steradian.
	 */
	public static final String	WATT_PER_SQUARE_METER_STERADIAN	= WATT + "/(" + METER + "\u00B2" + ' ' + STERADIAN + ')';

	/**
	 * Unit of catalytic activity concentration. It's one of coherent derived
	 * units whose names and symbols include SI coherent derived units with
	 * special names and symbols. The unit is called katal per cubic meter.
	 */
	public static final String	KATAL_PER_CUBIC_METER			= KATAL + '/' + METER + "\u00B3";

	// Table 6. Non-SI units accepted for use with the International System of
	// Units
	/**
	 * Unit of time. It's one of non-SI units accepted for use with the
	 * International System of Units. The unit is called minute.
	 */
	public static final String	TIME_MINUTE						= "min";

	/**
	 * Unit of time. It's one of non-SI units accepted for use with the
	 * International System of Units. The unit is called hour.
	 */
	public static final String	HOUR							= "h";

	/**
	 * Unit of time. It's one of non-SI units accepted for use with the
	 * International System of Units. The unit is called day.
	 */
	public static final String	DAY								= "d";

	/**
	 * Unit of plane angle. It's one of non-SI units accepted for use with the
	 * International System of Units. The unit is called degree.
	 */
	// degree sign
	public static final String	DEGREE							= "\u00B0";

	/**
	 * Unit of plane angle. It's one of non-SI units accepted for use with the
	 * International System of Units. The unit is called minute.
	 */
	// prime
	public static final String	PLANE_ANGLE_MINUTE				= "\u2032";

	/**
	 * Unit of plane angle. It's one of non-SI units accepted for use with the
	 * International System of Units. The unit is called second.
	 */
	// double prime
	public static final String	PLANE_ANGLE_SECOND				= "\u2033";

	/**
	 * Unit of area. It's one of non-SI units accepted for use with the
	 * International System of Units. The unit is called hectare.
	 */
	public static final String	HECTARE							= "ha";

	/**
	 * Unit of volume. It's one of non-SI units accepted for use with the
	 * International System of Units. The unit is called liter. International
	 * System of Units accepts two symbols: lower-case l and capital L. That
	 * constant value is using the lower-case l.
	 */
	public static final String	LITER							= "l";

	/**
	 * Unit of mass. It's one of non-SI units accepted for use with the
	 * International System of Units. The unit is called tonne.
	 */
	public static final String	TONNE							= "t";

	// Table 8. Other non-SI units
	/**
	 * Unit of pressure. It's one of other non-SI units. The unit is called bar.
	 */
	public static final String	BAR								= "bar";

	/**
	 * Unit of pressure. It's one of other non-SI units. The unit is called
	 * millimeter of mercury.
	 */
	public static final String	MILLIMETER_OF_MERCURY			= "mmHg";

	/**
	 * Unit of length. It's one of other non-SI units. The unit is called
	 * angstrom.
	 */
	// Angstrom sign
	public static final String	ANGSTROM						= "\u212B";

	/**
	 * Unit of distance. It's one of other non-SI units. The unit is called
	 * nautical mile.
	 */
	public static final String	NAUTICAL_MILE					= "M";

	/**
	 * Unit of area. It's one of other non-SI units. The unit is called barn.
	 */
	public static final String	BARN							= "b";

	/**
	 * Unit of speed. It's one of other non-SI units. The unit is called knot.
	 */
	public static final String	KNOT							= "kn";

	/**
	 * Unit of logarithmic ratio quantities. It's one of other non-SI units. The
	 * unit is called neper.
	 */
	public static final String	NEPER							= "Np";

	/**
	 * Unit of logarithmic ratio quantities. It's one of other non-SI units. The
	 * unit is called bel.
	 */
	public static final String	BEL								= "B";

	/**
	 * Unit of logarithmic ratio quantities. It's one of other non-SI units. The
	 * unit is called decibel.
	 */
	public static final String	DECIBEL							= "dB";

	// Table 9. Non-SI units associated with the CGS and the CGS-Gaussian system
	// of units
	/**
	 * Unit of energy. It's one of non-SI units associated with the CGS and the
	 * CGS-Gaussian system of units. The unit is called erg.
	 */
	public static final String	ERG								= "erg";

	/**
	 * Unit of force. It's one of non-SI units associated with the CGS and the
	 * CGS-Gaussian system of units. The unit is called dyne.
	 */
	public static final String	DYNE							= "dyn";

	/**
	 * Unit of dynamic viscosity. It's one of non-SI units associated with the
	 * CGS and the CGS-Gaussian system of units. The unit is called poise.
	 */
	public static final String	POISE							= "P";

	/**
	 * Unit of kinematic viscosity. It's one of non-SI units associated with the
	 * CGS and the CGS-Gaussian system of units. The unit is called stokes.
	 */
	public static final String	STOKES							= "St";

	/**
	 * Unit of luminance. It's one of non-SI units associated with the CGS and
	 * the CGS-Gaussian system of units. The unit is called stilb.
	 */
	public static final String	STILB							= "sb";

	/**
	 * Unit of illuminance. It's one of non-SI units associated with the CGS and
	 * the CGS-Gaussian system of units. The unit is called phot.
	 */
	public static final String	PHOT							= "ph";

	/**
	 * Unit of acceleration. It's one of non-SI units associated with the CGS
	 * and the CGS-Gaussian system of units. The unit is called gal.
	 */
	public static final String	GAL								= "Gal";

	/**
	 * Unit of magnetic flux. It's one of non-SI units associated with the CGS
	 * and the CGS-Gaussian system of units. The unit is called maxwell.
	 */
	public static final String	MAXWELL							= "Mx";

	/**
	 * Unit of magnetic flux density. It's one of non-SI units associated with
	 * the CGS and the CGS-Gaussian system of units. The unit is called gauss.
	 */
	public static final String	GAUSS							= "G";

	/**
	 * Unit of magnetic field. It's one of non-SI units associated with the CGS
	 * and the CGS-Gaussian system of units. The unit is called oersted.
	 */
	public static final String	OERSTED							= "Oe";

	// Table 5. SI prefixes
	/**
	 * Adopted prefix symbol to form the symbols of the decimal multiples of SI
	 * units. It's called deca and represents the 1st power of ten.
	 */
	public static final String	PREFIX_DECA						= "da";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal multiples of SI
	 * units. It's called hecto and represents the 2nd power of ten.
	 */
	public static final String	PREFIX_HECTO					= "h";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal multiples of SI
	 * units. It's called kilo and represents the 3rd power of ten.
	 */
	public static final String	PREFIX_KILO						= "k";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal multiples of SI
	 * units. It's called mega and represents the 6th power of ten.
	 */
	public static final String	PREFIX_MEGA						= "M";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal multiples of SI
	 * units. It's called giga and represents the 9th power of ten.
	 */
	public static final String	PREFIX_GIGA						= "G";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal multiples of SI
	 * units. It's called exa and represents the 18th power of ten.
	 */
	public static final String	PREFIX_EXA						= "E";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal multiples of SI
	 * units. It's called zetta and represents the 21th power of ten.
	 */
	public static final String	PREFIX_ZETTA					= "Z";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal multiples of SI
	 * units. It's called yotta and represents the 24th power of ten.
	 */
	public static final String	PREFIX_YOTTA					= "Y";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal submultiples of
	 * SI units. It's called deci and represents the 1st negative power of ten.
	 */
	public static final String	PREFIX_DECI						= "d";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal submultiples of
	 * SI units. It's called centi and represents the 2nd negative power of ten.
	 */
	public static final String	PREFIX_CENTI					= "c";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal submultiples of
	 * SI units. It's called milli and represents the 3rd negative power of ten.
	 */
	public static final String	PREFIX_MILLI					= "m";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal submultiples of
	 * SI units. It's called micro and represents the 6th negative power of ten.
	 */
	// micro sign
	public static final String	PREFIX_MICRO					= "\u00B5";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal submultiples of
	 * SI units. It's called nano and represents the 9th negative power of ten.
	 */
	public static final String	PREFIX_NANO						= "n";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal submultiples of
	 * SI units. It's called pico and represents the 12th negative power of ten.
	 */
	public static final String	PREFIX_PICO						= "p";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal submultiples of
	 * SI units. It's called femto and represents the 15th negative power of
	 * ten.
	 */
	public static final String	PREFIX_FEMTO					= "f";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal submultiples of
	 * SI units. It's called atto and represents the 18th negative power of ten.
	 */
	public static final String	PREFIX_ATTO						= "a";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal submultiples of
	 * SI units. It's called zepto and represents the 21th negative power of
	 * ten.
	 */
	public static final String	PREFIX_ZEPTO					= "z";

	/**
	 * Adopted prefix symbol to form the symbols of the decimal submultiples of
	 * SI units. It's called yocto and represents the 24th negative power of
	 * ten.
	 */
	public static final String	PREFIX_YOCTO					= "y";
}
