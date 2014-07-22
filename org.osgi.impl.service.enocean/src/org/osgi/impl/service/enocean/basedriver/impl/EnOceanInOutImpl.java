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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.osgi.test.cases.enoceansimulation.EnOceanInOut;

/**
 * 
 */
public class EnOceanInOutImpl implements EnOceanInOut {

	/**
	 * EnOceanHostImpl's input stream.
	 */
	private CustomInputStream	serialInputStream;

	/**
	 * EnOceanHostImpl's output stream.
	 */
	private ByteArrayOutputStream	serialOutputStream;

	/**
	 * 
	 */
	public EnOceanInOutImpl() {
		serialInputStream = new CustomInputStream(new byte[] {});
		serialOutputStream = new ByteArrayOutputStream();
	}

	public void resetBuffers() {
		serialInputStream.reset();
		serialOutputStream.reset();
	}

	public InputStream getSerialInputStream() {
		return serialInputStream;
	}

	public OutputStream getSerialOutputStream() {
		return serialOutputStream;
	}

}
