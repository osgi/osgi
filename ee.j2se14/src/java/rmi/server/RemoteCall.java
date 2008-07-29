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
/** @deprecated */ public abstract interface RemoteCall {
	/** @deprecated */ public abstract void done() throws java.io.IOException;
	/** @deprecated */ public abstract void executeCall() throws java.lang.Exception;
	/** @deprecated */ public abstract java.io.ObjectInput getInputStream() throws java.io.IOException;
	/** @deprecated */ public abstract java.io.ObjectOutput getOutputStream() throws java.io.IOException;
	/** @deprecated */ public abstract java.io.ObjectOutput getResultStream(boolean var0) throws java.io.IOException;
	/** @deprecated */ public abstract void releaseInputStream() throws java.io.IOException;
	/** @deprecated */ public abstract void releaseOutputStream() throws java.io.IOException;
}

