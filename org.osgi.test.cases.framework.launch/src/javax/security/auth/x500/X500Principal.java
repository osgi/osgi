/*
 * $Revision: 6107 $
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
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

package javax.security.auth.x500;
public final class X500Principal implements java.io.Serializable, java.security.Principal {
	public X500Principal(java.io.InputStream var0) { }
	public X500Principal(java.lang.String var0) { }
	public X500Principal(byte[] var0) { }
	public byte[] getEncoded() { return null; }
	public java.lang.String getName() { return null; }
	public java.lang.String getName(java.lang.String var0) { return null; }
	public final static java.lang.String CANONICAL = "CANONICAL";
	public final static java.lang.String RFC1779 = "RFC1779";
	public final static java.lang.String RFC2253 = "RFC2253";
}

