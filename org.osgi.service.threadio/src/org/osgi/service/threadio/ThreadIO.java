/*
 * Copyright (c) OSGi Alliance (2008, 2010). All Rights Reserved.
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
package org.osgi.service.threadio;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Enable multiplexing of the standard IO streams for input, output, and error.
 * 
 * This service provides access the System I/O streams on a per thread basis.
 * The standard streams are singletons, this service replaces the singletons
 * with a service that has a per-thread stack of streams. If no streams are
 * pushed output is directed to the original streams that were set before this
 * service was started.
 * 
 * Users can push a triplet of streams. After this push, all standard IO is
 * redirected through the given streams for that thread only. When the user is
 * ready, he can pop the streams and the previous situation is restored.
 * 
 * @ThreadSafe
 * @ProvideInterface
 * @version $Id$
 */
public interface ThreadIO {
	interface Entry {
		void close();
	}
	
	/**
	 * Associate these streams with the current thread.
	 * 
	 * Ensure that when output is performed on System.in, System.out, System.err
	 * it will happen on the given streams. If a {@code null} is given for any
	 * of the parameters the stream must not be replaced.
	 * 
	 * This method can be called multiple times on the same thread. A Thread IO
	 * implementation must stack these calls. It is paramount that users of this
	 * service ensure they follow the bracketing. The following code snippet
	 * provides such a model:
	 * 
	 * <pre>
	 *   threadio.pushStreams(in, out, err);
	 *   try {
	 *       ... do real work
	 *   } finally {
	 *      threadio.pop();
	 *   }
	 * </pre>
	 * 
	 * The streams will automatically be canceled when the bundle that has
	 * gotten this service is stopped or returns this service. If the ThreadIO
	 * service holds the only reference to a stream, it must remove this stream
	 * from the stack of streams.
	 * 
	 * @param in InputStream to use for the current thread when System.in is
	 *        used. If {@code null} then ignore.
	 * @param out PrintStream to use for the current thread when System.out is
	 *        used. If {@code null} then ignore.
	 * @param err PrintStream to use for the current thread when System.err is
	 *        used. If {@code null} then ignore.
	 * @return 
	 */
	Entry pushStreams(InputStream in, PrintStream out, PrintStream err);

}
