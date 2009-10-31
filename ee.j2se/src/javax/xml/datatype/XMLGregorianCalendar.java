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
public abstract class XMLGregorianCalendar implements java.lang.Cloneable {
	public XMLGregorianCalendar() { } 
	public abstract void add(javax.xml.datatype.Duration var0);
	public abstract void clear();
	public abstract java.lang.Object clone();
	public abstract int compare(javax.xml.datatype.XMLGregorianCalendar var0);
	public abstract int getDay();
	public abstract java.math.BigInteger getEon();
	public abstract java.math.BigInteger getEonAndYear();
	public abstract java.math.BigDecimal getFractionalSecond();
	public abstract int getHour();
	public int getMillisecond() { return 0; }
	public abstract int getMinute();
	public abstract int getMonth();
	public abstract int getSecond();
	public abstract java.util.TimeZone getTimeZone(int var0);
	public abstract int getTimezone();
	public abstract javax.xml.namespace.QName getXMLSchemaType();
	public abstract int getYear();
	public abstract boolean isValid();
	public abstract javax.xml.datatype.XMLGregorianCalendar normalize();
	public abstract void reset();
	public abstract void setDay(int var0);
	public abstract void setFractionalSecond(java.math.BigDecimal var0);
	public abstract void setHour(int var0);
	public abstract void setMillisecond(int var0);
	public abstract void setMinute(int var0);
	public abstract void setMonth(int var0);
	public abstract void setSecond(int var0);
	public void setTime(int var0, int var1, int var2) { }
	public void setTime(int var0, int var1, int var2, int var3) { }
	public void setTime(int var0, int var1, int var2, java.math.BigDecimal var3) { }
	public abstract void setTimezone(int var0);
	public abstract void setYear(int var0);
	public abstract void setYear(java.math.BigInteger var0);
	public abstract java.util.GregorianCalendar toGregorianCalendar();
	public abstract java.util.GregorianCalendar toGregorianCalendar(java.util.TimeZone var0, java.util.Locale var1, javax.xml.datatype.XMLGregorianCalendar var2);
	public abstract java.lang.String toXMLFormat();
}

