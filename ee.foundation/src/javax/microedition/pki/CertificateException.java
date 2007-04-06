/*
 * $Id$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2006). All Rights Reserved.
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

package javax.microedition.pki;
public class CertificateException extends java.io.IOException {
	public CertificateException(java.lang.String var0, javax.microedition.pki.Certificate var1, byte var2) { }
	public CertificateException(javax.microedition.pki.Certificate var0, byte var1) { }
	public javax.microedition.pki.Certificate getCertificate() { return null; }
	public byte getReason() { return 0; }
	public final static byte BAD_EXTENSIONS = 1;
	public final static byte BROKEN_CHAIN = 11;
	public final static byte CERTIFICATE_CHAIN_TOO_LONG = 2;
	public final static byte EXPIRED = 3;
	public final static byte INAPPROPRIATE_KEY_USAGE = 10;
	public final static byte MISSING_SIGNATURE = 5;
	public final static byte NOT_YET_VALID = 6;
	public final static byte ROOT_CA_EXPIRED = 12;
	public final static byte SITENAME_MISMATCH = 7;
	public final static byte UNAUTHORIZED_INTERMEDIATE_CA = 4;
	public final static byte UNRECOGNIZED_ISSUER = 8;
	public final static byte UNSUPPORTED_PUBLIC_KEY_TYPE = 13;
	public final static byte UNSUPPORTED_SIGALG = 9;
	public final static byte VERIFICATION_FAILED = 14;
}

