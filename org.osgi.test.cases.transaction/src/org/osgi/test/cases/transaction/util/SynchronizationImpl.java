/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.transaction.util;

import javax.transaction.Synchronization;

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