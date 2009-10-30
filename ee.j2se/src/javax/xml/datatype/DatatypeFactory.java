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

package javax.xml.datatype;
public abstract class DatatypeFactory {
	public final static java.lang.String DATATYPEFACTORY_IMPLEMENTATION_CLASS = "com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl";
	public final static java.lang.String DATATYPEFACTORY_PROPERTY = "javax.xml.datatype.DatatypeFactory";
	protected DatatypeFactory() { } 
	public abstract javax.xml.datatype.Duration newDuration(long var0);
	public abstract javax.xml.datatype.Duration newDuration(java.lang.String var0);
	public javax.xml.datatype.Duration newDuration(boolean var0, int var1, int var2, int var3, int var4, int var5, int var6) { return null; }
	public abstract javax.xml.datatype.Duration newDuration(boolean var0, java.math.BigInteger var1, java.math.BigInteger var2, java.math.BigInteger var3, java.math.BigInteger var4, java.math.BigInteger var5, java.math.BigDecimal var6);
	public javax.xml.datatype.Duration newDurationDayTime(long var0) { return null; }
	public javax.xml.datatype.Duration newDurationDayTime(java.lang.String var0) { return null; }
	public javax.xml.datatype.Duration newDurationDayTime(boolean var0, int var1, int var2, int var3, int var4) { return null; }
	public javax.xml.datatype.Duration newDurationDayTime(boolean var0, java.math.BigInteger var1, java.math.BigInteger var2, java.math.BigInteger var3, java.math.BigInteger var4) { return null; }
	public javax.xml.datatype.Duration newDurationYearMonth(long var0) { return null; }
	public javax.xml.datatype.Duration newDurationYearMonth(java.lang.String var0) { return null; }
	public javax.xml.datatype.Duration newDurationYearMonth(boolean var0, int var1, int var2) { return null; }
	public javax.xml.datatype.Duration newDurationYearMonth(boolean var0, java.math.BigInteger var1, java.math.BigInteger var2) { return null; }
	public static javax.xml.datatype.DatatypeFactory newInstance() throws javax.xml.datatype.DatatypeConfigurationException { return null; }
	public abstract javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendar();
	public javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendar(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) { return null; }
	public abstract javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendar(java.lang.String var0);
	public abstract javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendar(java.math.BigInteger var0, int var1, int var2, int var3, int var4, int var5, java.math.BigDecimal var6, int var7);
	public abstract javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendar(java.util.GregorianCalendar var0);
	public javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendarDate(int var0, int var1, int var2, int var3) { return null; }
	public javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendarTime(int var0, int var1, int var2, int var3) { return null; }
	public javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendarTime(int var0, int var1, int var2, int var3, int var4) { return null; }
	public javax.xml.datatype.XMLGregorianCalendar newXMLGregorianCalendarTime(int var0, int var1, int var2, java.math.BigDecimal var3, int var4) { return null; }
}

