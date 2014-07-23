/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.test.cases.enoceansimulation;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is intended to provide in/out (i.e. read/write) methods to emulate
 * an EnOcean gateway during integration tests.
 */
public interface EnOceanInOut {

	/**
	 * Resets the data being exchanged on both input and output streams.
	 */
	public void resetBuffers();

	/**
	 * Returns a handle to the current input stream used by the driver's host.
	 * 
	 * @return
	 */
	public InputStream getInputStream();

	/**
	 * Returns a handle to the current output stream used by the driver's host.
	 * 
	 * @return
	 */
	public OutputStream getOutputStream();

}
