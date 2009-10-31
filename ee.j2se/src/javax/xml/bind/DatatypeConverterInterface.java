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

package javax.xml.bind;
public interface DatatypeConverterInterface {
	java.lang.String parseAnySimpleType(java.lang.String var0);
	byte[] parseBase64Binary(java.lang.String var0);
	boolean parseBoolean(java.lang.String var0);
	byte parseByte(java.lang.String var0);
	java.util.Calendar parseDate(java.lang.String var0);
	java.util.Calendar parseDateTime(java.lang.String var0);
	java.math.BigDecimal parseDecimal(java.lang.String var0);
	double parseDouble(java.lang.String var0);
	float parseFloat(java.lang.String var0);
	byte[] parseHexBinary(java.lang.String var0);
	int parseInt(java.lang.String var0);
	java.math.BigInteger parseInteger(java.lang.String var0);
	long parseLong(java.lang.String var0);
	javax.xml.namespace.QName parseQName(java.lang.String var0, javax.xml.namespace.NamespaceContext var1);
	short parseShort(java.lang.String var0);
	java.lang.String parseString(java.lang.String var0);
	java.util.Calendar parseTime(java.lang.String var0);
	long parseUnsignedInt(java.lang.String var0);
	int parseUnsignedShort(java.lang.String var0);
	java.lang.String printAnySimpleType(java.lang.String var0);
	java.lang.String printBase64Binary(byte[] var0);
	java.lang.String printBoolean(boolean var0);
	java.lang.String printByte(byte var0);
	java.lang.String printDate(java.util.Calendar var0);
	java.lang.String printDateTime(java.util.Calendar var0);
	java.lang.String printDecimal(java.math.BigDecimal var0);
	java.lang.String printDouble(double var0);
	java.lang.String printFloat(float var0);
	java.lang.String printHexBinary(byte[] var0);
	java.lang.String printInt(int var0);
	java.lang.String printInteger(java.math.BigInteger var0);
	java.lang.String printLong(long var0);
	java.lang.String printQName(javax.xml.namespace.QName var0, javax.xml.namespace.NamespaceContext var1);
	java.lang.String printShort(short var0);
	java.lang.String printString(java.lang.String var0);
	java.lang.String printTime(java.util.Calendar var0);
	java.lang.String printUnsignedInt(long var0);
	java.lang.String printUnsignedShort(int var0);
}

