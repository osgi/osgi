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

import javax.transaction.Status;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 * @version $Rev$ $Date$
 */
public class TransactionUtil {  
	
	// start with a clean transaction manager by roll back previous active transaction
	// and clear out XAResourceImpl left from previous test
	public static void startWithCleanTM(TransactionManager tm) throws Exception {
        // let's always start with no transaction when starting each test.
        if (tm.getStatus() != Status.STATUS_NO_TRANSACTION)
        {
            tm.rollback();
        }
            
        //clean out XAResourceImpl left out from the previous test
        XAResourceImpl.clear();
	}
	
    public static void startWithClean(TransactionManager tm, UserTransaction ut) throws Exception {
        if (ut.getStatus() != Status.STATUS_NO_TRANSACTION) {
            ut.rollback();
        }
            
        // let's always start with no transaction when starting each test.
        if (tm.getStatus() != Status.STATUS_NO_TRANSACTION)
        {
            tm.rollback();
        }
            
        //clean out XAResourceImpl left out from the previous test
        XAResourceImpl.clear();
    }
    
    public static void startWithCleanUT(UserTransaction ut) throws Exception{
        if (ut.getStatus() != Status.STATUS_NO_TRANSACTION) {
            ut.rollback();
        }

        //clean out XAResourceImpl left out from the previous test
        XAResourceImpl.clear();
    }

	private TransactionUtil() {	
	}

}
