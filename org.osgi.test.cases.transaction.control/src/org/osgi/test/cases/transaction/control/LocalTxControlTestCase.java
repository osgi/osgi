/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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
package org.osgi.test.cases.transaction.control;

import static org.junit.Assume.assumeTrue;

import java.util.concurrent.Callable;

public class LocalTxControlTestCase extends TxControlTestCase {
	
	/**
	 * A basic test that ensures that a Local Transaction Control reports that
	 * it supports local resources
	 * 
	 * @throws Exception
	 */
	public void testTxControlSupportsLocal() throws Exception {
		assumeTrue(localEnabled);

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				assertTrue(txControl.getCurrentContext().supportsLocal());
				return null;
			}
		});

	}
}
