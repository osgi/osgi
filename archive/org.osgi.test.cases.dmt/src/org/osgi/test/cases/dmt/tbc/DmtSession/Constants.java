/* 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

/*
 * REVISION HISTORY:
 * 
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Feb 15, 2005  Alexandre Santos
 * 1             Implements TCK
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @generalDescription This class tests all of <code>constants<code> of DmtSession
 *                     
 * @author Alexandre Santos
 */

public class Constants {
    
    private DmtTestControl tbc;

    public Constants(DmtTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testConstants001();
    }
    
    /**
     * @testID testConstants001
     * @testDescription This method asserts the constants values
     */    
    public void testConstants001() {
        tbc.assertEquals("Asserting LOCK_TYPE_SHARED value", 0, DmtSession.LOCK_TYPE_SHARED);
        tbc.assertEquals("Asserting LOCK_TYPE_EXCLUSIVE value", 1, DmtSession.LOCK_TYPE_EXCLUSIVE);
        tbc.assertEquals("Asserting LOCK_TYPE_ATOMIC value", 2, DmtSession.LOCK_TYPE_ATOMIC);
    }    

}
