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
 * 26/08/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */

package org.osgi.test.cases.application.tbc.UseCases;

import org.osgi.service.application.ApplicationHandle;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.Event.EventHandlerImpl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Santos This test class validates lifecycle states changes
 *         of an application
 */
public class LifecycleStates {

    private ApplicationTestControl tbc;

    public LifecycleStates(ApplicationTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testLifecycleStates001();
        testLifecycleStates002();
        testLifecycleStates003();
        testLifecycleStates004();
    }

    /**
     * This method asserts if the REGISTERED,UNREGISTERED and MODIFIED events are fired
     * when we launch and destroy an application.
     * 
     * @spec 116.2.8 Application Events
     */
    private void testLifecycleStates001() {
        tbc.log("#testLifecycleStates001");
        ApplicationHandle handle = null;
        try {
    	    synchronized (tbc) {
    	    	tbc.wait(ApplicationConstants.SHORT_TIMEOUT);
    		}
        	
            tbc.resetEventProperties();
            EventHandlerImpl.waitNotify = false;
                                 
            synchronized (tbc) {
                handle = (ApplicationHandle) tbc.getAppDescriptor().launch(null);            	
                tbc.wait(ApplicationConstants.SHORT_TIMEOUT);
            }
            
            tbc.assertTrue("Asserting if the system has received a register event after the launch of the app.",
                    tbc.isRegistered() && !tbc.isModified()
                        && !tbc.isUnregistered());
            
            tbc.resetEventProperties();
            
            synchronized (tbc) {
                handle.destroy();            	
                tbc.wait(ApplicationConstants.SHORT_TIMEOUT);
            }
            
            tbc.assertTrue("Asserting if the system has received an unregistered event and a modified event after the destroy of the app.",
                !tbc.isRegistered() && tbc.isModified()
                    && tbc.isUnregistered());
            
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	EventHandlerImpl.waitNotify = true;
        	tbc.cleanUp(handle);
        }
    }

    /**
     * This method asserts if the UNREGISTERING event is fired
     * when we install and uninstall an application.
     * 
     * @spec 116.2.8 Application Events
     */
    private void testLifecycleStates002() {
        tbc.log("#testLifecycleStates002");
        try {            
            EventHandlerImpl.waitNotify = false;
            
            tbc.resetEventProperties();                       
            
            synchronized (tbc) {
            	tbc.unregisterDescriptor();
                tbc.wait(ApplicationConstants.SHORT_TIMEOUT);
            }
            
            tbc.assertTrue("Asserting if the system has received a unregistering event after the uninstall of the app.",
                !tbc.isRegistered() && tbc.isUnregistered());                                           
                        
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	EventHandlerImpl.waitNotify = true;
        	tbc.installDescriptor();
        }
    }
    
    
    /**
     * This method asserts if the REGISTERED events is fired
     * when we install an application.
     * 
     * @spec 116.2.8 Application Events
     */
    private void testLifecycleStates003() {
        tbc.log("#testLifecycleStates003");
        try {            
            EventHandlerImpl.waitNotify = false;                                  
            
            synchronized (tbc) {
            	tbc.unregisterDescriptor();
                tbc.wait(ApplicationConstants.SHORT_TIMEOUT);
            }
            
            tbc.resetEventProperties();                       
            
           	tbc.installDescriptor();
            
            tbc.assertTrue("Asserting if the system has received a registered event after the install of the app.",
                tbc.isRegistered() && !tbc.isUnregistered());
                        
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	EventHandlerImpl.waitNotify = true;
        }
    }    

    /**
     * This method asserts if the MODIFIED event is fired
     * when we have locked and unlocked the descriptor.
     * 
     * @spec 116.2.8 Application Events
     */
    private void testLifecycleStates004() {
        tbc.log("#testLifecycleStates004");
        try {           
            tbc.resetEventProperties();                       
            EventHandlerImpl.waitNotify = false;  
            
            synchronized (tbc) {
            	tbc.getAppDescriptor().lock();
                tbc.wait(ApplicationConstants.SHORT_TIMEOUT);
            }
            
            tbc.assertTrue("Asserting if the system has received a modified event after the lock of the descriptor.",
                !tbc.isRegistered() && tbc.isModified()
                    && !tbc.isUnregistered());
            
            tbc.resetEventProperties();                       

            synchronized (tbc) {
            	tbc.getAppDescriptor().unlock();
                tbc.wait(ApplicationConstants.SHORT_TIMEOUT);
            }            
            
            tbc.assertTrue("Asserting if the system has received a modified event after the unlock of the descriptor.",
                !tbc.isRegistered() && tbc.isModified()
                    && !tbc.isUnregistered());
                        
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	EventHandlerImpl.waitNotify = true;
        }
    }

}
