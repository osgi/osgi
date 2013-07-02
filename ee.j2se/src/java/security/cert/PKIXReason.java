/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package java.security.cert;
public enum PKIXReason implements java.security.cert.CertPathValidatorException.Reason {
	INVALID_KEY_USAGE,
	INVALID_NAME,
	INVALID_POLICY,
	NAME_CHAINING,
	NOT_CA_CERT,
	NO_TRUST_ANCHOR,
	PATH_TOO_LONG,
	UNRECOGNIZED_CRIT_EXT;
}

