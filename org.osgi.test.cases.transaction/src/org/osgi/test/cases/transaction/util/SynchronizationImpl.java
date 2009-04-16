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

import javax.transaction.Synchronization;

/**
 * @version $Rev$ $Date$
 */
public class SynchronizationImpl implements Synchronization
{
    private boolean _beforeCompletionCalled;
    private int _afterCompletionStatus;
    private RuntimeException _beforeCompletionException;
    private RuntimeException _afterCompletionException;

    public void afterCompletion(int status)
    {
        System.out.println("afterCompletion(" + status + ")");
        _afterCompletionStatus = status;

        if(_afterCompletionException != null)
        {
            throw _afterCompletionException;
        }
    }

    public void beforeCompletion()
    {
        System.out.println("beforeCompletion()");
        _beforeCompletionCalled = true;

        if(_beforeCompletionException != null)
        {
            throw _beforeCompletionException;
        }
    }
    
    public boolean beforeCompletionCalled()
    {
        return _beforeCompletionCalled;
    }

    public int afterCompletionStatus()
    {
        return _afterCompletionStatus;
    }

    public SynchronizationImpl setBeforeCompletionException(RuntimeException e)
    {
        _beforeCompletionException = e;
        return this;
    }

    public SynchronizationImpl setAfterCompletionException(RuntimeException e)
    {
        _afterCompletionException = e;
        return this;
    }
}