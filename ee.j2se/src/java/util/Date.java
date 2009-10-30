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
public class Date implements java.io.Serializable, java.lang.Cloneable, java.lang.Comparable<java.util.Date> {
	public Date() { } 
	/** @deprecated */ public Date(int var0, int var1, int var2) { } 
	/** @deprecated */ public Date(int var0, int var1, int var2, int var3, int var4) { } 
	/** @deprecated */ public Date(int var0, int var1, int var2, int var3, int var4, int var5) { } 
	public Date(long var0) { } 
	/** @deprecated */ public Date(java.lang.String var0) { } 
	/** @deprecated */ public static long UTC(int var0, int var1, int var2, int var3, int var4, int var5) { return 0l; }
	public boolean after(java.util.Date var0) { return false; }
	public boolean before(java.util.Date var0) { return false; }
	public java.lang.Object clone() { return null; }
	public int compareTo(java.util.Date var0) { return 0; }
	/** @deprecated */ public int getDate() { return 0; }
	/** @deprecated */ public int getDay() { return 0; }
	/** @deprecated */ public int getHours() { return 0; }
	/** @deprecated */ public int getMinutes() { return 0; }
	/** @deprecated */ public int getMonth() { return 0; }
	/** @deprecated */ public int getSeconds() { return 0; }
	public long getTime() { return 0l; }
	/** @deprecated */ public int getTimezoneOffset() { return 0; }
	/** @deprecated */ public int getYear() { return 0; }
	public int hashCode() { return 0; }
	/** @deprecated */ public static long parse(java.lang.String var0) { return 0l; }
	/** @deprecated */ public void setDate(int var0) { }
	/** @deprecated */ public void setHours(int var0) { }
	/** @deprecated */ public void setMinutes(int var0) { }
	/** @deprecated */ public void setMonth(int var0) { }
	/** @deprecated */ public void setSeconds(int var0) { }
	public void setTime(long var0) { }
	/** @deprecated */ public void setYear(int var0) { }
	/** @deprecated */ public java.lang.String toGMTString() { return null; }
	/** @deprecated */ public java.lang.String toLocaleString() { return null; }
}

