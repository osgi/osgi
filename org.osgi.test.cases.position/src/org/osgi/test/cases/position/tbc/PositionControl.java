/*
 * $Header$
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
package org.osgi.test.cases.position.tbc;

import org.osgi.test.cases.util.*;
import org.osgi.util.measurement.Unit;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.position.Position;

public class PositionControl extends DefaultTestBundleControl {
	static double	degrees	= Math.PI / 180.0;

	/**
	 * Tests <tt>Position</tt> class constructors by displaying object
	 * contents after creation.
	 */
	public void testPositionConstructor() {
		// Construct some Measurements
		Measurement[] m = {new Measurement(1.0, 0.1, Unit.rad), // 0 rad with
																// error
				new Measurement(1.0, 0.0, Unit.rad), // 1 rad with error=0
				new Measurement(1.0, Double.NaN, Unit.rad), // 2 rad with error
															// NaN
				new Measurement(1.0, Unit.rad), // 3 rad without error
				new Measurement(1.0, 0.1, Unit.m), // 4 m with error
				new Measurement(1.0, 0.0, Unit.m), // 5 m with error=0
				new Measurement(1.0, Double.NaN, Unit.m), // 6 m with error NaN
				new Measurement(1.0, Unit.m), // 7 m without error
				new Measurement(1.0, 0.1, Unit.m_s), // 8 m_s with error
				new Measurement(1.0, 0.0, Unit.m_s), // 9 m_s with error=0
				new Measurement(1.0, Double.NaN, Unit.m_s), //10 m_s with error
															// NaN
				new Measurement(1.0, Unit.m_s), //11 m_s without error
				new Measurement(1.0) //12 without unit& error
		};
		// Construct some Positions;
		// test Mesurement object contents
		// Position with errors and correct units
		Position pos = new Position(m[0], m[0], m[4], m[8], m[0]);
		assertPosition(" Position with errors and correct units:", pos, m[0],
				m[0], m[4], m[8], m[0]);
		// Position with errors=0 and correct units
		pos = new Position(m[1], m[1], m[5], m[9], m[1]);
		assertPosition(" Position with errors=0 and correct units:", pos, m[1],
				m[1], m[5], m[9], m[1]);
		// Position with errors NaN and correct units
		pos = new Position(m[2], m[2], m[6], m[10], m[2]);
		assertPosition(" Position with errors NaN and correct units:", pos,
				m[2], m[2], m[6], m[10], m[2]);
		// Position without errors and correct units
		pos = new Position(m[3], m[3], m[7], m[11], m[3]);
		assertPosition(" Position without errors but correct units:", pos,
				m[3], m[3], m[7], m[11], m[3]);
		// Position without errors & units
		try {
			pos = new Position(m[12], m[12], m[12], m[12], m[12]);
			// This code should never be reached :-)
			log("Exeception for missing units is not thrown. :-(");
		}
		catch (java.lang.IllegalArgumentException e) {
			// IllegalArgumentException should be thrown. So just be quiet!
		}
		// Position with null parameters
		pos = new Position(null, null, null, null, null);
		assertPosition("Position with null-parameters:", pos, null, null, null,
				null, null);
		// Flip around the World test
		Position myOffice = new Position(new Measurement(52.1369 * degrees,
				0.0, Unit.rad), // Latitude
				new Measurement(9.8943 * degrees, 0.0, Unit.rad), // Longitude
				new Measurement(235.0, 0.0, Unit.m), // Altitude
				new Measurement(0.0, 0.0, Unit.m_s), // Speed
				new Measurement(0.0 * degrees, 0.0, Unit.rad)); // Track
		Position myOfficeToo = new Position(new Measurement((180.0 - 52.1369)
				* degrees, 0.0, Unit.rad), // Latitude
				new Measurement((180.0 + 9.8943) * degrees, 0.0, Unit.rad), // Longitude
				new Measurement(235.0, 0.0, Unit.m), // Altitude
				new Measurement(0.0, 0.0, Unit.m_s), // Speed
				new Measurement(0.0 * degrees, 0.0, Unit.rad)); // Track
		assertPosition("Flip around the World test:", myOffice, myOfficeToo);
	}

	void assertPosition(String text, Position pos, Measurement lat,
			Measurement lon, Measurement alt, Measurement speed,
			Measurement track) {
		assertEquals(text + " Latitude", lat, pos.getLatitude());
		assertEquals(text + " Longitude", lon, pos.getLongitude());
		assertEquals(text + " Altitude", alt, pos.getAltitude());
		assertEquals(text + " Speed", speed, pos.getSpeed());
		assertEquals(text + " Track", track, pos.getTrack());
	}

	void assertPosition(String text, Position pos, Position xpos) {
		assertEquals(text + " Latitude", xpos.getLatitude().getValue(), pos
				.getLatitude().getValue(), 0.00001);
		assertEquals(text + " Longitude", xpos.getLongitude().getValue(), pos
				.getLongitude().getValue(), 0.00001);
		assertEquals(text + " Altitude", xpos.getAltitude().getValue(), pos
				.getAltitude().getValue(), 0.00001);
		assertEquals(text + " Speed", xpos.getSpeed().getValue(), pos
				.getSpeed().getValue(), 0.00001);
		assertEquals(text + " Track", xpos.getTrack().getValue(), pos
				.getTrack().getValue(), 0.00001);
		assertEquals(text + " Latitude Error", xpos.getLatitude().getError(),
				pos.getLatitude().getError(), 0.00001);
		assertEquals(text + " Longitude Error", xpos.getLongitude().getError(),
				pos.getLongitude().getError(), 0.00001);
		assertEquals(text + " Altitude Error", xpos.getAltitude().getError(),
				pos.getAltitude().getError(), 0.00001);
		assertEquals(text + " Speed Error", xpos.getSpeed().getError(), pos
				.getSpeed().getError(), 0.00001);
		assertEquals(text + " Track Error", xpos.getTrack().getError(), pos
				.getTrack().getError(), 0.00001);
		assertEquals(text + " Latitude Unit", xpos.getLatitude().getUnit(), pos
				.getLatitude().getUnit());
		assertEquals(text + " Longitude Unit", xpos.getLongitude().getUnit(),
				pos.getLongitude().getUnit());
		assertEquals(text + " Altitude Unit", xpos.getAltitude().getUnit(), pos
				.getAltitude().getUnit());
		assertEquals(text + " Speed Unit", xpos.getSpeed().getUnit(), pos
				.getSpeed().getUnit());
		assertEquals(text + " Track Unit", xpos.getTrack().getUnit(), pos
				.getTrack().getUnit());
	}

	/**
	 * Test the boundary conditions of the lat/lon normalization. We do this in
	 * degrees so the input is easier to verify.
	 */
	public void testLatLon() {
		assertLatLon("Origin       ", 0, 0, 0, 0);
		assertLatLon("North pole   ", 0, 90, 0, 90);
		assertLatLon("Southpole	", 0, -90, 0, -90);
		assertLatLon("Wrap 270     ", 270, 0, -90, 0);
		assertLatLon("Wrap -270    ", -270, 0, 90, 0);
		assertLatLon("Wrap -200    ", -200, 0, 160, 0);
		assertLatLon("Wrap 200     ", 200, 0, -160, 0);
		assertLatLon("No Wrap 160  ", 160, 0, 160, 0);
		assertLatLon("Lat overfl + ", 180, 100, 0, 80);
		assertLatLon("Lat overfl - ", -180, 100, 0, 80);
		assertLatLon("Lat overfl +1", 181, 100, 1, 80);
		assertLatLon("Lat overfl -1", -181, 100, -1, 80);
		assertLatLon("+/- lon 180  ", 180, 0, -180, 0);
		assertLatLon("+/- lon -180 ", -180, 0, -180, 0);
		assertLatLon("+/- lon -181 ", -181, 0, 179, 0);
		assertLatLon("+/- lon -179 ", -179, 0, -179, 0);
		assertLatLon("+/- lon 181  ", 181, 0, -179, 0);
		assertLatLon("+/- lon 179  ", 179, 0, 179, 0);
		assertLatLon("+/- lat 90   ", 0, 90, 0, 90);
		assertLatLon("+/- lat 91   ", 0, 91, -180, 89); // Should flip to other
														// side of world
		assertLatLon("+/- lat 89   ", 0, 89, 0, 89);
		assertLatLon("+/- lat -91  ", 0, -91, -180, -89); // Should flip to
														  // other side of world
		assertLatLon("+/- lat -89  ", 0, -89, 0, -89);
		assertLatLon("+/- lat 89   ", 0, 89, 0, 89);
		assertLatLon("+/- lat/lon  ", 200, -99, 20, -81); // Should flip to
														  // other side of world
		assertLatLon("+/- lon 540  ", 540, 0, -180, 0);
		assertLatLon("+/- lon -540 ", -540, 0, -180, 0);
		assertLatLon("+/- lon -539 ", -539, 0, -179, 0);
		assertLatLon("+/- lon -541 ", -541, 0, 179, 0);
		assertLatLon("+/- lat 450  ", 0, 450, 0, 90);
		assertLatLon("+/- lat -450 ", 0, -450, 0, -90);
		assertLatLon("Wrap N. Pole1", -540, 450, -180, 90);
		assertLatLon("Wrap N. Pole2", 540, 450, -180, 90);
		assertLatLon("Wrap S. Pole1", 540, -450, -180, -90);
		assertLatLon("Wrap S. Pole2", -540, -450, -180, -90);
	}

	void assertLatLon(String test, int lon, int lat, int xlon, int xlat) {
		Measurement mlon = new Measurement(lon * degrees, Unit.rad);
		Measurement mlat = new Measurement(lat * degrees, Unit.rad);
		Position pos = new Position(mlat, mlon, null, null, null);
		assertEquals(test + " Longitude ", xlon, Math.round(pos.getLongitude()
				.getValue()
				/ degrees));
		assertEquals(test + " Latitude  ", xlat, Math.round(pos.getLatitude()
				.getValue()
				/ degrees));
	}

	public void testTrack() {
		assertTrack(0, 0);
		assertTrack(-1, 359);
		assertTrack(1, 1);
		assertTrack(359, 359);
		assertTrack(360, 0);
		assertTrack(361, 1);
		assertTrack(-359, 1);
		assertTrack(-719, 1);
		assertTrack(-720, 0);
		assertTrack(-721, 359);
	}

	void assertTrack(double in, double out) {
		Position p = new Position(null, null, null, null, new Measurement(in
				* degrees, 0, Unit.rad));
		assertEquals("Track", out, p.getTrack().getValue() / degrees, 0.00001);
	}
}
