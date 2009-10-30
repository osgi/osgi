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

package java.util;
public final class Scanner implements java.util.Iterator<java.lang.String> {
	public Scanner(java.io.File var0) throws java.io.FileNotFoundException { } 
	public Scanner(java.io.File var0, java.lang.String var1) throws java.io.FileNotFoundException { } 
	public Scanner(java.io.InputStream var0) { } 
	public Scanner(java.io.InputStream var0, java.lang.String var1) { } 
	public Scanner(java.lang.Readable var0) { } 
	public Scanner(java.lang.String var0) { } 
	public Scanner(java.nio.channels.ReadableByteChannel var0) { } 
	public Scanner(java.nio.channels.ReadableByteChannel var0, java.lang.String var1) { } 
	public void close() { }
	public java.util.regex.Pattern delimiter() { return null; }
	public java.lang.String findInLine(java.lang.String var0) { return null; }
	public java.lang.String findInLine(java.util.regex.Pattern var0) { return null; }
	public java.lang.String findWithinHorizon(java.lang.String var0, int var1) { return null; }
	public java.lang.String findWithinHorizon(java.util.regex.Pattern var0, int var1) { return null; }
	public boolean hasNext() { return false; }
	public boolean hasNext(java.lang.String var0) { return false; }
	public boolean hasNext(java.util.regex.Pattern var0) { return false; }
	public boolean hasNextBigDecimal() { return false; }
	public boolean hasNextBigInteger() { return false; }
	public boolean hasNextBigInteger(int var0) { return false; }
	public boolean hasNextBoolean() { return false; }
	public boolean hasNextByte() { return false; }
	public boolean hasNextByte(int var0) { return false; }
	public boolean hasNextDouble() { return false; }
	public boolean hasNextFloat() { return false; }
	public boolean hasNextInt() { return false; }
	public boolean hasNextInt(int var0) { return false; }
	public boolean hasNextLine() { return false; }
	public boolean hasNextLong() { return false; }
	public boolean hasNextLong(int var0) { return false; }
	public boolean hasNextShort() { return false; }
	public boolean hasNextShort(int var0) { return false; }
	public java.io.IOException ioException() { return null; }
	public java.util.Locale locale() { return null; }
	public java.util.regex.MatchResult match() { return null; }
	public java.lang.String next() { return null; }
	public java.lang.String next(java.lang.String var0) { return null; }
	public java.lang.String next(java.util.regex.Pattern var0) { return null; }
	public java.math.BigDecimal nextBigDecimal() { return null; }
	public java.math.BigInteger nextBigInteger() { return null; }
	public java.math.BigInteger nextBigInteger(int var0) { return null; }
	public boolean nextBoolean() { return false; }
	public byte nextByte() { return 0; }
	public byte nextByte(int var0) { return 0; }
	public double nextDouble() { return 0.0d; }
	public float nextFloat() { return 0.0f; }
	public int nextInt() { return 0; }
	public int nextInt(int var0) { return 0; }
	public java.lang.String nextLine() { return null; }
	public long nextLong() { return 0l; }
	public long nextLong(int var0) { return 0l; }
	public short nextShort() { return 0; }
	public short nextShort(int var0) { return 0; }
	public int radix() { return 0; }
	public void remove() { }
	public java.util.Scanner skip(java.lang.String var0) { return null; }
	public java.util.Scanner skip(java.util.regex.Pattern var0) { return null; }
	public java.util.Scanner useDelimiter(java.lang.String var0) { return null; }
	public java.util.Scanner useDelimiter(java.util.regex.Pattern var0) { return null; }
	public java.util.Scanner useLocale(java.util.Locale var0) { return null; }
	public java.util.Scanner useRadix(int var0) { return null; }
}

