/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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

package org.osgi.test.cases.transaction.util;

import javax.transaction.UserTransaction;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @version $Rev$ $Date$
 */
public class UserTransactionFactory {

	private static ServiceReference _userTxServiceRef;
	private static UserTransaction _userTx;
	private static BundleContext _context;

	public static void setBundleContext(BundleContext context) {
		_context = context;

		_userTxServiceRef = _context.getServiceReference(UserTransaction.class
				.getName());
		_userTx = (UserTransaction) _context.getService(_userTxServiceRef);
	}
	
    public static UserTransaction getUserTransaction() {
        return _userTx;	
    }
    
}
