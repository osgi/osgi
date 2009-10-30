/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.math;
public class BigDecimal extends java.lang.Number implements java.lang.Comparable<java.math.BigDecimal> {
	public final static java.math.BigDecimal ONE; static { ONE = null; }
	public final static int ROUND_CEILING = 2;
	public final static int ROUND_DOWN = 1;
	public final static int ROUND_FLOOR = 3;
	public final static int ROUND_HALF_DOWN = 5;
	public final static int ROUND_HALF_EVEN = 6;
	public final static int ROUND_HALF_UP = 4;
	public final static int ROUND_UNNECESSARY = 7;
	public final static int ROUND_UP = 0;
	public final static java.math.BigDecimal TEN; static { TEN = null; }
	public final static java.math.BigDecimal ZERO; static { ZERO = null; }
	public BigDecimal(double var0) { } 
	public BigDecimal(double var0, java.math.MathContext var1) { } 
	public BigDecimal(int var0) { } 
	public BigDecimal(int var0, java.math.MathContext var1) { } 
	public BigDecimal(long var0) { } 
	public BigDecimal(long var0, java.math.MathContext var1) { } 
	public BigDecimal(java.lang.String var0) { } 
	public BigDecimal(java.lang.String var0, java.math.MathContext var1) { } 
	public BigDecimal(java.math.BigInteger var0) { } 
	public BigDecimal(java.math.BigInteger var0, int var1) { } 
	public BigDecimal(java.math.BigInteger var0, int var1, java.math.MathContext var2) { } 
	public BigDecimal(java.math.BigInteger var0, java.math.MathContext var1) { } 
	public BigDecimal(char[] var0) { } 
	public BigDecimal(char[] var0, int var1, int var2) { } 
	public BigDecimal(char[] var0, int var1, int var2, java.math.MathContext var3) { } 
	public BigDecimal(char[] var0, java.math.MathContext var1) { } 
	public java.math.BigDecimal abs() { return null; }
	public java.math.BigDecimal abs(java.math.MathContext var0) { return null; }
	public java.math.BigDecimal add(java.math.BigDecimal var0) { return null; }
	public java.math.BigDecimal add(java.math.BigDecimal var0, java.math.MathContext var1) { return null; }
	public byte byteValueExact() { return 0; }
	public int compareTo(java.math.BigDecimal var0) { return 0; }
	public java.math.BigDecimal divide(java.math.BigDecimal var0) { return null; }
	public java.math.BigDecimal divide(java.math.BigDecimal var0, int var1) { return null; }
	public java.math.BigDecimal divide(java.math.BigDecimal var0, int var1, int var2) { return null; }
	public java.math.BigDecimal divide(java.math.BigDecimal var0, int var1, java.math.RoundingMode var2) { return null; }
	public java.math.BigDecimal divide(java.math.BigDecimal var0, java.math.MathContext var1) { return null; }
	public java.math.BigDecimal divide(java.math.BigDecimal var0, java.math.RoundingMode var1) { return null; }
	public java.math.BigDecimal[] divideAndRemainder(java.math.BigDecimal var0) { return null; }
	public java.math.BigDecimal[] divideAndRemainder(java.math.BigDecimal var0, java.math.MathContext var1) { return null; }
	public java.math.BigDecimal divideToIntegralValue(java.math.BigDecimal var0) { return null; }
	public java.math.BigDecimal divideToIntegralValue(java.math.BigDecimal var0, java.math.MathContext var1) { return null; }
	public double doubleValue() { return 0.0d; }
	public float floatValue() { return 0.0f; }
	public int hashCode() { return 0; }
	public int intValue() { return 0; }
	public int intValueExact() { return 0; }
	public long longValue() { return 0l; }
	public long longValueExact() { return 0l; }
	public java.math.BigDecimal max(java.math.BigDecimal var0) { return null; }
	public java.math.BigDecimal min(java.math.BigDecimal var0) { return null; }
	public java.math.BigDecimal movePointLeft(int var0) { return null; }
	public java.math.BigDecimal movePointRight(int var0) { return null; }
	public java.math.BigDecimal multiply(java.math.BigDecimal var0) { return null; }
	public java.math.BigDecimal multiply(java.math.BigDecimal var0, java.math.MathContext var1) { return null; }
	public java.math.BigDecimal negate() { return null; }
	public java.math.BigDecimal negate(java.math.MathContext var0) { return null; }
	public java.math.BigDecimal plus() { return null; }
	public java.math.BigDecimal plus(java.math.MathContext var0) { return null; }
	public java.math.BigDecimal pow(int var0) { return null; }
	public java.math.BigDecimal pow(int var0, java.math.MathContext var1) { return null; }
	public int precision() { return 0; }
	public java.math.BigDecimal remainder(java.math.BigDecimal var0) { return null; }
	public java.math.BigDecimal remainder(java.math.BigDecimal var0, java.math.MathContext var1) { return null; }
	public java.math.BigDecimal round(java.math.MathContext var0) { return null; }
	public int scale() { return 0; }
	public java.math.BigDecimal scaleByPowerOfTen(int var0) { return null; }
	public java.math.BigDecimal setScale(int var0) { return null; }
	public java.math.BigDecimal setScale(int var0, int var1) { return null; }
	public java.math.BigDecimal setScale(int var0, java.math.RoundingMode var1) { return null; }
	public short shortValueExact() { return 0; }
	public int signum() { return 0; }
	public java.math.BigDecimal stripTrailingZeros() { return null; }
	public java.math.BigDecimal subtract(java.math.BigDecimal var0) { return null; }
	public java.math.BigDecimal subtract(java.math.BigDecimal var0, java.math.MathContext var1) { return null; }
	public java.math.BigInteger toBigInteger() { return null; }
	public java.math.BigInteger toBigIntegerExact() { return null; }
	public java.lang.String toEngineeringString() { return null; }
	public java.lang.String toPlainString() { return null; }
	public java.math.BigDecimal ulp() { return null; }
	public java.math.BigInteger unscaledValue() { return null; }
	public static java.math.BigDecimal valueOf(double var0) { return null; }
	public static java.math.BigDecimal valueOf(long var0) { return null; }
	public static java.math.BigDecimal valueOf(long var0, int var1) { return null; }
}

