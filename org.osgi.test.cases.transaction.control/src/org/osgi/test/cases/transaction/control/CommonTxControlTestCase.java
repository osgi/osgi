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

import java.util.concurrent.Callable;

public class CommonTxControlTestCase extends TxControlTestCase {
	
	/**
	 * A basic test that ensures that a Transaction Control reports the correct
	 * state
	 * 
	 * @throws Exception
	 */
	public void testTxControlScopeReporting() throws Exception {

		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());

		txControl.supports(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				assertTrue(txControl.activeScope());
				assertFalse(txControl.activeTransaction());
				return null;
			}
		});

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				assertTrue(txControl.activeScope());
				assertTrue(txControl.activeTransaction());
				return null;
			}
		});

	}
}
