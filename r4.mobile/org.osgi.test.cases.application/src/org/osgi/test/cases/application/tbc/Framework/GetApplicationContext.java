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
 * 30/08/2005   Alexandre Santos
 * 153          [MEGTCK][APP] Implement OAT test cases
 * ===========  ==============================================================
 */

package org.osgi.test.cases.application.tbc.Framework;

import org.osgi.application.ApplicationContext;
import org.osgi.application.Framework;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves 
 * 
 * This Test Class Validates the implementation of
 * <code>getApplicationContext</code> method, according to MEG
 * reference documentation.
 */
public class GetApplicationContext {

    private ApplicationTestControl tbc;

    public GetApplicationContext(ApplicationTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testGetApplicationContext001();
        testGetApplicationContext002();
        testGetApplicationContext003();
        testGetApplicationContext004();
    }

    /**
     * This method asserts that when we pass an object that is not the
     * representative object of an application, IllegalArgumentException is
     * thrown.
     * 
     * @spec Framework.getApplicationContext(Object)
     */
    private void testGetApplicationContext001() {
        tbc.log("#testGetApplicationContext001");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            Framework.getApplicationContext(new Object());
            
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
        } finally {
        	tbc.cleanUp(handle);
        }
    }
    
    /**
     * This method asserts if NullPointerException is thrown
     * when we pass null as parameter.
     * 
     * @spec Framework.getApplicationContext(Object)
     */
    private void testGetApplicationContext002() {
        tbc.log("#testGetApplicationContext002");
        ApplicationHandle handle = null;
        try {           
            Framework.getApplicationContext(null);
            tbc.failException("#", NullPointerException.class);
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
     * This method asserts that ApplicationContext object is singleton
     * for the corresponding application’s activator. Subsequent calls
     * to the getMyApplicationContext method with the same application’s
     * activator must return the same ApplicationContext object
     * representative object of an application.
     * 
     * @spec Framework.getApplicationContext(Object)
     */
    private void testGetApplicationContext003() {
        tbc.log("#testGetApplicationContext003");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext context = Framework.getApplicationContext(tbc.getAppInstance());
            
            tbc.assertNotNull("Asserting that an ApplicationCOntext was returned.", context);
            
            ApplicationContext context2 = Framework.getApplicationContext(tbc.getAppInstance());
            
            tbc.assertEquals("Asserting if subsequent calls to the getMyApplicationContext method with the same application’s activator returns the same ApplicationContext.", context, context2);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.cleanUp(handle);
        }
    }    
    
    /**
     * This method asserts if is an error try to get the ApplicationContext
     * object for an application that is already stopped. Existing
     * ApplicationContext objects must be invalidated once the
     * application’s activator is stopped.
     * 
     * @spec Framework.getApplicationContext(Object)
     */
    private void testGetApplicationContext004() {
        tbc.log("#testGetApplicationContext004");
        try {           
            Framework.getApplicationContext(tbc.getAppInstance());            
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
}
