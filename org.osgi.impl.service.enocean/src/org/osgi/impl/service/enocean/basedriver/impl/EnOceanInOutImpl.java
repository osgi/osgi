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

package org.osgi.impl.service.enocean.basedriver.impl;

import java.io.InputStream;
import org.osgi.test.cases.enoceansimulation.EnOceanInOut;

/**
 * 
 */
public class EnOceanInOutImpl implements EnOceanInOut {

	/**
	 * EnOceanHostImpl's input stream.
	 */
	private CustomInputStream		inputStream;

	// /**
	// * EnOceanHostImpl's output stream.
	// */
	// private ByteArrayOutputStream outputStream;

	/**
	 * 
	 */
	public EnOceanInOutImpl() {
		inputStream = new CustomInputStream(new byte[] {});
		// outputStream = new ByteArrayOutputStream();
	}

	public void resetBuffers() {
		inputStream.reset();
		// outputStream.reset();
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	// public OutputStream getOutputStream() {
	// return outputStream;
	// }

}
