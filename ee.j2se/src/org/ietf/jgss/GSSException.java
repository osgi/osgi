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

package org.ietf.jgss;
public class GSSException extends java.lang.Exception {
	public final static int BAD_BINDINGS = 1;
	public final static int BAD_MECH = 2;
	public final static int BAD_MIC = 6;
	public final static int BAD_NAME = 3;
	public final static int BAD_NAMETYPE = 4;
	public final static int BAD_QOP = 14;
	public final static int BAD_STATUS = 5;
	public final static int CONTEXT_EXPIRED = 7;
	public final static int CREDENTIALS_EXPIRED = 8;
	public final static int DEFECTIVE_CREDENTIAL = 9;
	public final static int DEFECTIVE_TOKEN = 10;
	public final static int DUPLICATE_ELEMENT = 17;
	public final static int DUPLICATE_TOKEN = 19;
	public final static int FAILURE = 11;
	public final static int GAP_TOKEN = 22;
	public final static int NAME_NOT_MN = 18;
	public final static int NO_CONTEXT = 12;
	public final static int NO_CRED = 13;
	public final static int OLD_TOKEN = 20;
	public final static int UNAUTHORIZED = 15;
	public final static int UNAVAILABLE = 16;
	public final static int UNSEQ_TOKEN = 21;
	public GSSException(int var0) { } 
	public GSSException(int var0, int var1, java.lang.String var2) { } 
	public int getMajor() { return 0; }
	public java.lang.String getMajorString() { return null; }
	public int getMinor() { return 0; }
	public java.lang.String getMinorString() { return null; }
	public void setMinor(int var0, java.lang.String var1) { }
}

