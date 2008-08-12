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
 * 
 */
/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 17/10/2005   Alexandre Santos
 * 206          [MEGTCK][MONITOR]Remove NO access permission in Plugis tests
 * ===========  ==============================================================
 */

package org.osgi.test.cases.application.tbc.Others;

import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves 
 * 		
 * This Test Class Validates the implementation of
 * <code>ApplicationServiceEvent</code> constructor, according to MEG
 * reference documentation.
 */
public class ApplicationServiceEvent {
    private ApplicationTestControl tbc;

    public ApplicationServiceEvent(ApplicationTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testApplicationServiceEvent001();
        testApplicationServiceEvent002();
    }    
    
    /**
     * This method asserts that IllegalArgumentException is thrown when
     * null is passed as reference parameter.
     * 
     * @spec ApplicationServiceEvent.ApplicationServiceEvent(int,ServiceReference,Object)
     */
    private void testApplicationServiceEvent001() {
        tbc.log("#testApplicationServiceEvent001");
        try {
        	Object obj = new Object();
        	new org.osgi.application.ApplicationServiceEvent(0, null, obj);
            tbc.failException("#", IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            tbc.pass(MessagesConstants.getMessage(
                MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
                new String[]{IllegalArgumentException.class.getName()}));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.EXCEPTION_THROWN, new String[]{
                		IllegalArgumentException.class.getName(),
                    e.getClass().getName()}));
        }
    }  
    
    /**
     * This method asserts that no exception is thrown when
     * null is passed as serviceObject parameter.
     * 
     * @spec ApplicationServiceEvent.ApplicationServiceEvent(int,ServiceReference,Object)
     */
    private void testApplicationServiceEvent002() {
        tbc.log("#testApplicationServiceEvent002");
        try {
        	ServiceReference srv = tbc.getServiceReference();
        	org.osgi.application.ApplicationServiceEvent evt = new org.osgi.application.ApplicationServiceEvent(0, srv, null);
            tbc.assertNull("Asserting that null is returned by getServiceObject()", evt.getServiceObject());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    }    
    
    
    
}
