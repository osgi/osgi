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

package org.omg.CORBA;
public class ParameterMode implements org.omg.CORBA.portable.IDLEntity {
	protected ParameterMode(int var0) { }
	public static org.omg.CORBA.ParameterMode from_int(int var0) { return null; }
	public int value() { return 0; }
	public final static org.omg.CORBA.ParameterMode PARAM_IN; static { PARAM_IN = null; }
	public final static org.omg.CORBA.ParameterMode PARAM_INOUT; static { PARAM_INOUT = null; }
	public final static org.omg.CORBA.ParameterMode PARAM_OUT; static { PARAM_OUT = null; }
	public final static int _PARAM_IN = 0;
	public final static int _PARAM_INOUT = 2;
	public final static int _PARAM_OUT = 1;
}

