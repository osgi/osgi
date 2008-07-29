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
public abstract class ServerRequest {
	public ServerRequest() { }
	public void arguments(org.omg.CORBA.NVList var0) { }
	public abstract org.omg.CORBA.Context ctx();
	/** @deprecated */ public void except(org.omg.CORBA.Any var0) { }
	/** @deprecated */ public java.lang.String op_name() { return null; }
	public java.lang.String operation() { return null; }
	/** @deprecated */ public void params(org.omg.CORBA.NVList var0) { }
	/** @deprecated */ public void result(org.omg.CORBA.Any var0) { }
	public void set_exception(org.omg.CORBA.Any var0) { }
	public void set_result(org.omg.CORBA.Any var0) { }
}

