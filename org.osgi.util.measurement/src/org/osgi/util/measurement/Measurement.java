/*
 * $Header$
 *
 * Copyright (c) The Open Services Gateway Initiative (2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.util.measurement;

import java.util.*;

/**
 * Represents a value with an error, a unit and a time-stamp.
 *
 * <p>A <tt>Measurement</tt> object is used for maintaining the tuple
 * of value, error, unit and time-stamp.
 * The value and error are represented as doubles
 * and the time is measured in milliseconds since midnight,
 * January 1, 1970 UTC.
 *
 * <p>Mathematic methods are provided that correctly calculate
 * taking the error into account. A runtime error
 * will occur when two measurements are
 * used in an incompatible way. E.g., when a speed
 * (m/s) is added to a distance (m). The measurement
 * class will correctly track changes in unit during
 * multiplication and division, always coercing the
 * result to the most simple form.	
 * See {@link Unit} for more information on the
 * supported units.
 *
 * <p>Errors in the measurement class are absolute errors.
 * Measurement errors should use the P95 rule. Actual values
 * must fall in the range value +/- error 95% or more of the time.
 *
 * <p>A <tt>Measurement</tt> object is immutable in order to be
 * easily shared.
 *
 * <p>Note: This class has a natural ordering that is
 * inconsistent with equals. See {@link #compareTo}.
 *
 * @version $Revision$
 */

public class Measurement implements Comparable
{
    /* package private so it can be accessed by Unit */
    final double value;
    final double error;
    final long time;
    final Unit unit;
    private String name;

	
	/**
     * Create a new <tt>Measurement</tt> object from a String.
     *
     * The format of the string must be:
	 * <value> ':' <unit> [ ':' error ]
     * whitespace is not allowed
	 *
     * @param encoded The encoded value of the <tt>Measurement</tt>.
     * @throws IllegalArgumentException If the encoded value cannot be 
     * properly parsed.
	 */
	public Measurement(String encoded) {
		try {
			StringTokenizer st = new StringTokenizer(encoded,":");
			value = Double.parseDouble(st.nextToken());
			unit = Unit.fromString(st.nextToken());
			if (st.hasMoreTokens()) {
				error = Double.parseDouble(st.nextToken());
			}
			else {
				error = 0.0d;
			}
			time = 0l;
		}
		catch (NoSuchElementException e) {
			throw new IllegalArgumentException("Invalid value");
		}
	}
			
		
    /**
     * Create a new <tt>Measurement</tt> object.
     *
     * @param value The value of the <tt>Measurement</tt>.
     * @param error The error of the <tt>Measurement</tt>.
     * @param unit The <tt>Unit</tt> object in which the value is measured.
     * If this argument is <tt>null</tt>, then the unit will be set to {@link Unit#unity}.
     * @param time The time measured in milliseconds since midnight, January 1, 1970 UTC.
     */
    public Measurement(double value, double error, Unit unit, long time)
    {
        this.value  = value;
        this.error  = Math.abs(error);
        this.unit   = (unit != null) ? unit : Unit.unity;
        this.time   = time;
    }

    /**
     * Create a new <tt>Measurement</tt> object with a time of zero.
     *
     * @param value The value of the <tt>Measurement</tt>.
     * @param error The error of the <tt>Measurement</tt>.
     * @param unit The <tt>Unit</tt> object in which the value is measured.
     * If this argument is <tt>null</tt>, then the unit will be set to {@link Unit#unity}.
     */
    public Measurement(double value, double error, Unit unit)
    {
        this(value, error, unit, 0l);
    }

    /**
     * Create a new <tt>Measurement</tt> object with an error of 0.0 and
     * a time of zero.
     *
     * @param value The value of the <tt>Measurement</tt>.
     * @param unit The <tt>Unit</tt> in which the value is measured.
     * If this argument is <tt>null</tt>, then the unit will be set to {@link Unit#unity}.
     */
    public Measurement(double value, Unit unit)
    {
        this(value, 0.0d, unit, 0l);
    }

    /**
     * Create a new <tt>Measurement</tt> object with an error of 0.0,
     * a unit of {@link Unit#unity} and a time of zero.
     *
     * @param value The value of the <tt>Measurement</tt>.
     */
    public Measurement(double value)
    {
        this(value, 0.0d, null, 0l);
    }


    /**
     * Returns the value of this <tt>Measurement</tt> object.
     *
     * @return The value of this <tt>Measurement</tt> object as a double.
     */
    public final double getValue()
    {
        return value;
    }

    /**
     * Returns the error of this <tt>Measurement</tt> object.
     * The error is always a positive value.
     *
     * @return The error of this <tt>Measurement</tt> as a double.
     */
    public final double getError()
    {
        return error;
    }

    /**
     * Returns the <tt>Unit</tt> object of this <tt>Measurement</tt> object.
     *
     * @return The <tt>Unit</tt> object of this <tt>Measurement</tt> object.
     *
     * @see Unit
     */
    public final Unit getUnit()
    {
        return unit;
    }

    /**
     * Returns the time at which this <tt>Measurement</tt> object was taken.
     * The time is measured in milliseconds since midnight, January 1, 1970 UTC, or
     * zero when not defined.
     *
     * @return The time at which this <tt>Measurement</tt> object was taken or zero.
     */
    public final long getTime()
    {
        return time;
    }

    /**
     * Returns a new <tt>Measurement</tt> object that is the product
     * of this object multiplied by the specified object.
     *
     * @param m The <tt>Measurement</tt> object that will be multiplied with
     * this object.
     * @return A new <tt>Measurement</tt> that is the product
     * of this object multiplied by the specified object.
     * The error and unit of the new object are computed.
     * The time of the new object is set to the time of this object.
     * @throws ArithmeticException If the <tt>Unit</tt> objects
     * of this object and the specified object cannot be multiplied.
     * @see Unit
     */
    public Measurement mul(Measurement m)
    {
        double mvalue = m.value;

        return new Measurement(value * mvalue, Math.abs(value) * m.error + error * Math.abs(mvalue),  unit.mul(m.unit), time);
    }

    /**
     * Returns a new <tt>Measurement</tt> object that is the product
     * of this object multiplied by the specified value.
     *
     * @param d The value that will be multiplied with
     * this object.
     * @param u The <tt>Unit</tt> of the specified value.
     * @return A new <tt>Measurement</tt> object that is the product
     * of this object multiplied by the specified value.
     * The error and unit of the new object are computed.
     * The time of the new object is set to the time of this object.
     * @throws ArithmeticException If the units
     * of this object and the specified value cannot be multiplied.
     * @see Unit
     */
    public Measurement mul(double d, Unit u)
    {
        return new Measurement(value * d, error * Math.abs(d), unit.mul(u), time);
    }

    /**
     * Returns a new <tt>Measurement</tt> object that is the product
     * of this object multiplied by the specified value.
     *
     * @param d The value that will be multiplied with
     * this object.
     * @return A new <tt>Measurement</tt> object that is the product
     * of this object multiplied by the specified value.
     * The error of the new object is computed.
     * The unit and time of the new object is set to the
     * unit and time of this object.
     */
    public Measurement mul(double d)
    {
        return new Measurement(value * d, error * Math.abs(d), unit, time);
    }

    /**
     * Returns a new <tt>Measurement</tt> object that is the quotient
     * of this object divided by the specified object.
     *
     * @param m The <tt>Measurement</tt> object that will be the divisor of
     * this object.
     * @return A new <tt>Measurement</tt> object that is the quotient
     * of this object divided by the specified object.
     * The error and unit of the new object are computed.
     * The time of the new object is set to the time of this object.
     * @throws ArithmeticException If the <tt>Unit</tt> objects
     * of this object and the specified object cannot be divided.
     * @see Unit
     */
    public Measurement div(Measurement m)
    {
        double mvalue = m.value;

        return new Measurement(value / mvalue, (Math.abs(value) * m.error + error * Math.abs(mvalue)) / (mvalue * mvalue), unit.div(m.unit), time);
    }

    /**
     * Returns a new <tt>Measurement</tt> object that is the quotient
     * of this object divided by the specified value.
     *
     * @param d The value that will be the divisor of
     * this object.
     * @param u The <tt>Unit</tt> object of the specified value.
     * @return A new <tt>Measurement</tt> that is the quotient
     * of this object divided by the specified value.
     * The error and unit of the new object are computed.
     * The time of the new object is set to the time of this object.
     * @throws ArithmeticException If the <tt>Unit</tt> objects
     * of this object and the specified object cannot be divided.
     * @see Unit
     */
    public Measurement div(double d, Unit u)
    {
        return new Measurement(value / d, error / Math.abs(d), unit.div(u), time);
    }

    /**
     * Returns a new <tt>Measurement</tt> object that is the quotient
     * of this object divided by the specified value.
     *
     * @param d The value that will be the divisor of
     * this object.
     * @return A new <tt>Measurement</tt> object that is the quotient
     * of this object divided by the specified value.
     * The error of the new object is computed.
     * The unit and time of the new object is set to the
     * <tt>Unit</tt> and time of this object.
     */
    public Measurement div(double d)
    {
        return new Measurement(value / d, error / Math.abs(d), unit, time);
    }

    /**
     * Returns a new <tt>Measurement</tt> object that is the sum
     * of this object added to the specified object.
     *
     * The error and unit of the new object are computed.
     * The time of the new object is set to the time of this object.
     *
     * @param m The <tt>Measurement</tt> object that will be added with
     * this object.
     * @return A new <tt>Measurement</tt> object that is the sum of this and m.
     * @see Unit
     * @throws ArithmeticException If the <tt>Unit</tt> objects
     * of this object and the specified object cannot be added.
     */
    public Measurement add(Measurement m)
    {
        return new Measurement(value + m.value, error + m.error, unit.add(m.unit), time);
    }

    /**
     * Returns a new <tt>Measurement</tt> object that is the sum
     * of this object added to the specified value.
     *
     * @param d The value that will be added with
     * this object.
     * @param u The <tt>Unit</tt> object of the specified value.
     * @return A new <tt>Measurement</tt> object that is the sum
     * of this object added to the specified value.
     * The unit of the new object is computed.
     * The error and time of the new object is set to the
     * error and time of this object.
     * @throws ArithmeticException If the <tt>Unit</tt> objects
     * of this object and the specified value cannot be added.
     * @see Unit
     */
    public Measurement add(double d, Unit u)
    {
        return new Measurement(value + d, error, unit.add(u), time);
    }

    /**
     * Returns a new <tt>Measurement</tt> object that is the sum
     * of this object added to the specified value.
     *
     * @param d The value that will be added with
     * this object.
     * @return A new <tt>Measurement</tt> object that is the sum
     * of this object added to the specified value.
     * The error, unit, and time of the new object is set to the
     * error, <tt>Unit</tt> and time of this object.
     */
    public Measurement add(double d)
    {
        return new Measurement(value + d, error, unit, time);
    }

    /**
     * Returns a new <tt>Measurement</tt> object that is the subtraction
     * of the specified object from this object.
     *
     * @param m The <tt>Measurement</tt> object that will be subtracted from
     * this object.
     * @return A new <tt>Measurement</tt> object that is the subtraction
     * of the specified object from this object.
     * The error and unit of the new object are computed.
     * The time of the new object is set to the time of this object.
     * @throws ArithmeticException If the <tt>Unit</tt> objects
     * of this object and the specified object cannot be subtracted.
     * @see Unit
     */
    public Measurement sub(Measurement m)
    {
        return new Measurement(value - m.value, error + m.error, unit.sub(m.unit), time);
    }

    /**
     * Returns a new <tt>Measurement</tt> object that is the subtraction
     * of the specified value from this object.
     *
     * @param d The value that will be subtracted from
     * this object.
     * @param u The <tt>Unit</tt> object of the specified value.
     * @return A new <tt>Measurement</tt> object that is the subtraction
     * of the specified value from this object.
     * The unit of the new object is computed.
     * The error and time of the new object is set to the
     * error and time of this object.
     * @throws ArithmeticException If the <tt>Unit</tt> objects
     * of this object and the specified object cannot be subtracted.
     * @see Unit
     */
    public Measurement sub(double d, Unit u)
    {
        return new Measurement(value - d, error, unit.sub(u), time);
    }

    /**
     * Returns a new <tt>Measurement</tt> object that is the subtraction
     * of the specified value from this object.
     *
     * @param d The value that will be subtracted from
     * this object.
     * @return A new <tt>Measurement</tt> object that is the subtraction
     * of the specified value from this object.
     * The error, unit and time of the new object is set to the
     * error, <tt>Unit</tt> object and time of this object.
     */
    public Measurement sub(double d)
    {
        return new Measurement(value - d, error, unit, time);
    }

    /**
     * Returns a <tt>String</tt> object representing this <tt>Measurement</tt> object.
     *
     * @return a <tt>String</tt> object representing this <tt>Measurement</tt> object.
     */
    public String toString()
    {
        if (name == null)
        {
            StringBuffer sb = new StringBuffer();

            sb.append(value);

            if (error != 0.0d)
            {
                sb.append(" +/- ");
                sb.append(error);
            }

            String u = unit.toString();
            if (u.length() > 0)
            {
                sb.append(" ");
                sb.append(u);
            }

            name = sb.toString();
        }

        return name;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer if this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>Note: This class has a natural ordering that is
     * inconsistent with equals. For this method, another <tt>Measurement</tt>
     * object is considered equal if there is some <tt>x</tt> such that
     * <pre>
     * 	getValue()-getError() <= x <= getValue()+getError()
     * </pre>
     * for both <tt>Measurement</tt> objects being compared.
     *
     * @param   obj The object to be compared.
     * @return  A negative integer, zero, or a positive integer if this object
     *		is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException If the specified object is not of type <tt>Measurement</tt>.
     * @throws ArithmeticException If the unit of the specified <tt>Measurement</tt> object is
     * not equal to the <tt>Unit</tt> object of this object.
     */
    public int compareTo(Object obj)
    {
        if (this == obj)
        {
            return 0;
        }

        Measurement that = (Measurement) obj;

        if (!unit.equals(that.unit))
        {
            throw new ArithmeticException( "Cannot compare " + this + " and " + that);
        }

        if (value == that.value)
        {
            return 0;
        }

        if (value < that.value)
        {
            if ((value + error) >= (that.value - that.error))
            {
                return 0;
            }
            else
            {
                return -1;
            }
        }
        else
        {
            if ((value - error) <= (that.value + that.error))
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
    }

    /**
     * Returns a hash code value for this object.
     *
     * @return A hash code value for this object.
     */
    public int hashCode()
    {
        long bits = Double.doubleToLongBits(value + error);

        return ((int)(bits ^ (bits >>> 32))) ^ unit.hashCode();
    }

    /**
     * Returns whether the specified object is equal to this object.
     * Two <tt>Measurement</tt> objects are equal if they have same value,
     * error and <tt>Unit</tt>.
     *
     * <p>Note: This class has a natural ordering that is
     * inconsistent with equals. See {@link #compareTo}.
     *
     * @param obj The object to compare with this object.
     * @return <tt>true</tt> if this object is equal to the specified
     * object; <tt>false</tt> otherwise.
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Measurement))
        {
            return false;
        }

        Measurement that = (Measurement)obj;

        return (value == that.value) && (error == that.error) && unit.equals(that.unit);
    }
}


