/*
 *  Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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
 * 13/10/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */

package org.osgi.test.cases.application.tbc.ApplicationContext;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.application.ApplicationContext;
import org.osgi.framework.Constants;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;
import org.osgi.test.cases.application.tbc.util.TestAppController;

/**
 * @author Alexandre Santos
 * 
 * This test class validates the implementation of
 * <code>getServiceProperties</code> method, according to MEG reference
 * documentation.
 */
public class GetServiceProperties {
	private ApplicationTestControl tbc;

	/**
	 * @param tbc
	 */
	public GetServiceProperties(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetServiceProperties001();
		testGetServiceProperties002();
		testGetServiceProperties003();
		testGetServiceProperties004();
		testGetServiceProperties005();
	}

	/**
	 * This method asserts that NullPointerException is thrown when we
	 * pass null as serviceObject.
	 * 
	 * @spec ApplicationContext.getServiceProperties(Object)
	 */
	private void testGetServiceProperties001() {
		tbc.log("#testGetServiceProperties001");
		ApplicationHandle handle = null;
		try {
			handle = tbc.getAppDescriptor().launch(null);
			ApplicationContext appContext = org.osgi.application.Framework
					.getApplicationContext(tbc.getAppInstance());
			appContext.getServiceProperties(null);
			tbc.failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { NullPointerException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(handle);
		}
	}
	
	/**
	 * This method asserts that getServiceProperties can
	 * be called passing a valid object and no exception is
	 * thrown.
	 * 
	 * @spec ApplicationContext.getServiceProperties(Object)
	 */
	private void testGetServiceProperties002() {
		tbc.log("#testGetServiceProperties002");
		ApplicationHandle handle = null;
		try {
			handle = tbc.getAppDescriptor().launch(null);
			ApplicationContext appContext = org.osgi.application.Framework
					.getApplicationContext(tbc.getAppInstance());
			TestAppController appController = (TestAppController) appContext.locateService(ApplicationConstants.XML_APP);	
			
			tbc.assertNotNull("Asserting that a non-null object was returned.", appController);
			
			appContext.getServiceProperties(appController);
			
			tbc.pass("No Exception was thrown.");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.cleanUp(handle);
        }
    }
	
    /**
     * This method asserts if IllegalStateException is thrown when the
     * application instance has been stopped.
     * 
     * @spec ApplicationContext.getServiceProperties(Object)
     */
	private void testGetServiceProperties003() {
        tbc.log("#testGetServiceProperties003");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            
			TestAppController appController = (TestAppController) appContext.locateService(ApplicationConstants.XML_APP);	
			
			tbc.assertNotNull("Asserting that a non-null object was returned.", appController);
			
			handle.destroy();
			
			appContext.getServiceProperties(appController);            
            
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
    
	/**
	 * This method asserts that IllegalArgumentException is thrown 
	 * if the application is not a service object at all
	 * 
	 * @spec ApplicationContext.getServiceProperties(Object)
	 */
	private void testGetServiceProperties004() {
		tbc.log("#testGetServiceProperties004");
		ApplicationHandle handle = null;
		try {
			handle = tbc.getAppDescriptor().launch(null);
			ApplicationContext appContext = org.osgi.application.Framework
					.getApplicationContext(tbc.getAppInstance());
			appContext.getServiceProperties(new String());
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(handle);
		}
	}
	
	/**
	 * This method asserts if it returns the properties of the
	 * highest object.
	 * 
	 * @spec ApplicationContext.getServiceProperties(Object)
	 */
	private void testGetServiceProperties005() {
		tbc.log("#testGetServiceProperties005");
		ApplicationHandle handle = null;
		try {
        	tbc.startActivator(true);
        	tbc.startActivator2(true);
        	
			handle = tbc.getAppDescriptor().launch(null);
        	
			ApplicationContext appContext = org.osgi.application.Framework
					.getApplicationContext(tbc.getAppInstance());
			        	
        	Hashtable hash = new Hashtable();
        	hash.put("Test", "Test2");
        	hash.put(Constants.SERVICE_RANKING, new Integer(9) );
        	
        	tbc.getTestingActivator2().setProperties(hash);
        	       	
        	Object service = appContext.locateService(ApplicationConstants.XML_ACTIVATOR);
        	
			Map map = appContext.getServiceProperties(service); 
						
			tbc.assertTrue("Assering if the returned map contains the key Test", map.containsKey("Test"));
			tbc.assertEquals("Assering if the returned map contains the value Test2 for the key Test", "Test2", map.get("Test"));

        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.stopActivator();
        	tbc.stopActivator2();
        	tbc.cleanUp(handle);
        }
    }
	
}