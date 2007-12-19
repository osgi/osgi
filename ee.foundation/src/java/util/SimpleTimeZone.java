/*
 * $Date$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2007). All Rights Reserved.
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
public class SimpleTimeZone extends java.util.TimeZone {
	public SimpleTimeZone(int var0, java.lang.String var1) { }
	public SimpleTimeZone(int var0, java.lang.String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) { }
	public SimpleTimeZone(int var0, java.lang.String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) { }
	public SimpleTimeZone(int var0, java.lang.String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) { }
	public int getOffset(int var0, int var1, int var2, int var3, int var4, int var5) { return 0; }
	public int getRawOffset() { return 0; }
	public boolean inDaylightTime(java.util.Date var0) { return false; }
	public void setDSTSavings(int var0) { }
	public void setEndRule(int var0, int var1, int var2) { }
	public void setEndRule(int var0, int var1, int var2, int var3) { }
	public void setEndRule(int var0, int var1, int var2, int var3, boolean var4) { }
	public void setRawOffset(int var0) { }
	public void setStartRule(int var0, int var1, int var2) { }
	public void setStartRule(int var0, int var1, int var2, int var3) { }
	public void setStartRule(int var0, int var1, int var2, int var3, boolean var4) { }
	public void setStartYear(int var0) { }
	public boolean useDaylightTime() { return false; }
	public final static int STANDARD_TIME = 1;
	public final static int UTC_TIME = 2;
	public final static int WALL_TIME = 0;
}

