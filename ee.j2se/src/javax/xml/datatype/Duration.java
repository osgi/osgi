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
public abstract class Duration {
	public Duration() { } 
	public abstract javax.xml.datatype.Duration add(javax.xml.datatype.Duration var0);
	public abstract void addTo(java.util.Calendar var0);
	public void addTo(java.util.Date var0) { }
	public abstract int compare(javax.xml.datatype.Duration var0);
	public int getDays() { return 0; }
	public abstract java.lang.Number getField(javax.xml.datatype.DatatypeConstants.Field var0);
	public int getHours() { return 0; }
	public int getMinutes() { return 0; }
	public int getMonths() { return 0; }
	public int getSeconds() { return 0; }
	public abstract int getSign();
	public long getTimeInMillis(java.util.Calendar var0) { return 0l; }
	public long getTimeInMillis(java.util.Date var0) { return 0l; }
	public javax.xml.namespace.QName getXMLSchemaType() { return null; }
	public int getYears() { return 0; }
	public abstract int hashCode();
	public boolean isLongerThan(javax.xml.datatype.Duration var0) { return false; }
	public abstract boolean isSet(javax.xml.datatype.DatatypeConstants.Field var0);
	public boolean isShorterThan(javax.xml.datatype.Duration var0) { return false; }
	public javax.xml.datatype.Duration multiply(int var0) { return null; }
	public abstract javax.xml.datatype.Duration multiply(java.math.BigDecimal var0);
	public abstract javax.xml.datatype.Duration negate();
	public abstract javax.xml.datatype.Duration normalizeWith(java.util.Calendar var0);
	public javax.xml.datatype.Duration subtract(javax.xml.datatype.Duration var0) { return null; }
}

