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

package javax.net.ssl;
public class SSLEngineResult {
	public enum HandshakeStatus {
		FINISHED,
		NEED_TASK,
		NEED_UNWRAP,
		NEED_WRAP,
		NOT_HANDSHAKING;
	}
	public enum Status {
		BUFFER_OVERFLOW,
		BUFFER_UNDERFLOW,
		CLOSED,
		OK;
	}
	public SSLEngineResult(javax.net.ssl.SSLEngineResult.Status var0, javax.net.ssl.SSLEngineResult.HandshakeStatus var1, int var2, int var3) { } 
	public final int bytesConsumed() { return 0; }
	public final int bytesProduced() { return 0; }
	public final javax.net.ssl.SSLEngineResult.HandshakeStatus getHandshakeStatus() { return null; }
	public final javax.net.ssl.SSLEngineResult.Status getStatus() { return null; }
}

