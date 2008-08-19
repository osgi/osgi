/*
 * $Date$
 *
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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

package java.rmi.server;
/** @deprecated */ public class LogStream extends java.io.PrintStream {
	/** @deprecated */ public static java.io.PrintStream getDefaultStream() { return null; }
	/** @deprecated */ public java.io.OutputStream getOutputStream() { return null; }
	/** @deprecated */ public static java.rmi.server.LogStream log(java.lang.String var0) { return null; }
	/** @deprecated */ public static int parseLevel(java.lang.String var0) { return 0; }
	/** @deprecated */ public static void setDefaultStream(java.io.PrintStream var0) { }
	/** @deprecated */ public void setOutputStream(java.io.OutputStream var0) { }
	public final static int BRIEF = 10;
	public final static int SILENT = 0;
	public final static int VERBOSE = 20;
	private LogStream() { super((java.io.OutputStream) null, false); } /* generated constructor to prevent compiler adding default public constructor */
}

