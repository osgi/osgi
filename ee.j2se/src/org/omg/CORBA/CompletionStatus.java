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

package org.omg.CORBA;
public final class CompletionStatus implements org.omg.CORBA.portable.IDLEntity {
	public final static org.omg.CORBA.CompletionStatus COMPLETED_MAYBE; static { COMPLETED_MAYBE = null; }
	public final static org.omg.CORBA.CompletionStatus COMPLETED_NO; static { COMPLETED_NO = null; }
	public final static org.omg.CORBA.CompletionStatus COMPLETED_YES; static { COMPLETED_YES = null; }
	public final static int _COMPLETED_MAYBE = 2;
	public final static int _COMPLETED_NO = 1;
	public final static int _COMPLETED_YES = 0;
	public static org.omg.CORBA.CompletionStatus from_int(int var0) { return null; }
	public int value() { return 0; }
	private CompletionStatus() { } /* generated constructor to prevent compiler adding default public constructor */
}

