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
 * 31/08/2005   Alexandre Santos
 * 153          [MEGTCK][APP] Implement OAT test cases
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc.ApplicationContext;

import org.osgi.application.ApplicationContext;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;


/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>locateServices</code> method, according to MEG reference
 * documentation.
 */
public class LocateServices {
    private ApplicationTestControl tbc;

    public LocateServices(ApplicationTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testLocateServices001();
        testLocateServices002();
        testLocateServices003();
        testLocateServices004();
    }    
    
    /**
     * This method asserts if locateServices returns the service object for the
     * specified referenceName.
     * 
     * @spec ApplicationContext.locateServices(String)
     */
    private void testLocateServices001() {
        tbc.log("#testLocateServices001");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            Object[] log = appContext.locateServices(ApplicationConstants.XML_LOG);
            
            tbc.assertEquals("Asserting if the returned array has only one element.", 1, log.length);
            tbc.assertTrue("Asserting if the first element is an instance of LogService.", (log[0] instanceof org.osgi.service.log.LogService));                       
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.cleanUp(handle);
        }
    }
    
    /**
     * This method asserts if locateServices returns null
     * when no bound service is available.
     * 
     * @spec ApplicationContext.locateServices(String)
     */
    private void testLocateServices002() {
        tbc.log("#testLocateServices002");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            Object[] activator = appContext.locateServices(ApplicationConstants.XML_ACTIVATOR);
            tbc.assertNull("Asserting if null is returned when no bound service is available.", activator);                       
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.cleanUp(handle);
        }
    }    
    
    /**
     * This method asserts if NullPointerException is thrown when we
     * pass null as parameter.
     * 
     * @spec ApplicationContext.locateServices(String)
     */
    private void testLocateServices003() {
        tbc.log("#testLocateServices003");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                       
            appContext.locateServices(null);
            
            tbc.failException("", NullPointerException.class);            
        } catch (NullPointerException e) {
            tbc.pass(MessagesConstants.getMessage(
                MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
                new String[]{NullPointerException.class.getName()}));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.EXCEPTION_THROWN, new String[]{
                		NullPointerException.class.getName(),
                    e.getClass().getName()}));
        } finally {
        	tbc.cleanUp(handle);
        }
    }    
    
    /**
     * This method asserts if IllegalStateException is thrown when the
     * application instance has been stopped.
     * 
     * @spec ApplicationContext.locateServices(String)
     */
    private void testLocateServices004() {
        tbc.log("#testLocateServices004");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            
            handle.destroy();
                       
            appContext.locateServices(ApplicationConstants.XML_ACTIVATOR);
            
            tbc.failException("", IllegalStateException.class);            
        } catch (IllegalStateException e) {
            tbc.pass(MessagesConstants.getMessage(
                MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
                new String[]{IllegalStateException.class.getName()}));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.EXCEPTION_THROWN, new String[]{
                    IllegalStateException.class.getName(),
                    e.getClass().getName()}));            
        } finally {
        	tbc.cleanUp(handle);
        }
    }    
    
}