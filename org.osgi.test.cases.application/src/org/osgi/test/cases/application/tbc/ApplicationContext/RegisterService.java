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

import java.util.Hashtable;

import org.osgi.application.ApplicationContext;
import org.osgi.application.ApplicationServiceEvent;
import org.osgi.application.ApplicationServiceListener;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;


/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>registerService</code> method, according to MEG reference
 * documentation.
 */
public class RegisterService implements ApplicationServiceListener {

    private ApplicationTestControl tbc;
	private Object serviceObject;
	private ServiceReference serviceReference;
	private boolean serviceChanged;
	private int serviceType;

    public RegisterService(ApplicationTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testRegisterService001();
        testRegisterService002();
        testRegisterService003();
        testRegisterService004();
        testRegisterService005();
        testRegisterService006();
        testRegisterService007();
        testRegisterService008();
        testRegisterService009();
        testRegisterService010();
        testRegisterService011();
        testRegisterService012();
        testRegisterService013();
        testRegisterService014();
        testRegisterService015();
        testRegisterService016();
    }
    
    /**
     * This method asserts that when the application instance was
     * stopped, all registered services is unregistered.
     * 
     * @spec ApplicationContext.registerService(String,Object,Dictionary)
     */
    private void testRegisterService001() {
        tbc.log("#testRegisterService001");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            
            appContext.registerService(this.getClass().getName(), this, null);
            
            handle.destroy();
                       
            tbc.assertTrue("Asserting if the registered services was unregistered when the application instanced is stopped.", !tbc.isTestClassRegistered(this.getClass().getName()));          
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.cleanUp(handle);
        }
    }    

    /**
     * This method asserts if registerService really register the class 
     * passed as parameter in service register.
     * 
     * @spec ApplicationContext.registerService(String,Object,Dictionary)
     */
    private void testRegisterService002() {
        tbc.log("#testRegisterService002");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            appContext.registerService(this.getClass().getName(), this, null);            
            
            int value = tbc.getServiceId(this.getClass().getName());
            
            tbc.assertTrue("Asserting if the returned object is an instance of LogService.", (value != -1));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {           
            tbc.cleanUp(handle);  
        }
    }   
    
    /**
     * This method asserts if NullPointerException is thrown when we
     * pass null as clazz parameter.
     * 
     * @spec ApplicationContext.registerService(String,Object,Dictionary)
     */
    private void testRegisterService003() {
    	//TODO what specified when null is passed as clazz parameter?
        tbc.log("#testRegisterService003");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            appContext.registerService((String) null, this, null);
            
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
     * This method asserts if IllegalArgumentException is thrown when we
     * pass null as service parameter.
     * 
     * @spec ApplicationContext.registerService(String,Object,Dictionary)
     */
    private void testRegisterService004() {
        tbc.log("#testRegisterService004");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            appContext.registerService(this.getClass().getName(), null, null);
            
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
     * This method asserts If any of the passed properties
     * have already been specified by the registering bundle,
     * their values will be overwritten by the Framework.
     *  
     * @spec ApplicationContext.registerService(String,Object,Dictionary)
     */
    private void testRegisterService005() {
        tbc.log("#testRegisterService005");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            Hashtable hash = new Hashtable();
            hash.put(Constants.SERVICE_ID, new Long(2355));
            hash.put(Constants.OBJECTCLASS, new String[] { "test2" });
            hash.put("test", "test");
            
            appContext.registerService(this.getClass().getName(), this, hash);            
            
            Long val = (Long) tbc.getServiceProperty(this.getClass().getName(), Constants.SERVICE_ID, null);
            
            tbc.assertTrue("Asserting if the Constants.SERVICE_ID passed as parameter was changed by the framework.", !(val.intValue()==2355));
            
            String[] values = (String[]) tbc.getServiceProperty(this.getClass().getName(), Constants.OBJECTCLASS, null);
            
            tbc.assertNotNull("Asserting if the OBJECTCLASS returned is not null.", values);
            
            tbc.assertTrue("Asserting if the Constants.OBJECTCLASS passed as parameter was changed by the framework.", !values[0].equals("test2"));
            
            String value = (String) tbc.getServiceProperty(this.getClass().getName(), "test", null);
            
            tbc.assertTrue("Asserting if the other parameter passed was added to the framework parameters.", value.equals("test"));            
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {           
            tbc.cleanUp(handle);  
        }
    }
    
    /**
     * This method asserts if A service event of type ServiceEvent.REGISTERED is fired and
     * if this event triggers the corresponding ApplicationServiceEvent to be delivered
     * to the applications that registered the appropriate listener.
     *  
     * @spec ApplicationContext.registerService(String,Object,Dictionary)
     */
    private void testRegisterService006() {
        tbc.log("#testRegisterService006");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
               
            appContext.addServiceListener(this, ApplicationConstants.XML_REG);
            
            serviceChanged = false;            	
            appContext.registerService(this.getClass().getName(), this, null);            	
            
            tbc
            .assertTrue(
                "Asserting if the addServiceListener add this test class as a listener for ApplicationServiceEvents.",
                serviceChanged);
        
            tbc.assertEquals("Asserting if the type received was the ServiceEvent.REGISTERED value.", ServiceEvent.REGISTERED, serviceType);
            tbc.assertTrue("Asserting if the serviceReference received by the event was org.osgi.test.cases.application.tbc.ApplicationContext.RegisterService", (serviceReference.toString().indexOf("org.osgi.test.cases.application.tbc.ApplicationContext.RegisterService") >= 0));
            tbc.assertNull("Asserting if null was received as serviceObject", serviceObject);                                             
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {           
            tbc.cleanUp(handle);  
        }
    }    
    
    /**
     * This method asserts that when the application instance was
     * stopped, all registered services is unregistered.
     * 
     * @spec ApplicationContext.registerService(String[],Object,Dictionary)
     */
    private void testRegisterService007() {
        tbc.log("#testRegisterService007");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            appContext.registerService(new String[] { this.getClass().getName(), Object.class.getName() }, this, null);            
            
            handle.destroy();
            
            tbc.assertTrue("Asserting if the registered services was unregistered when the application instanced is stopped(RegisterService).", !tbc.isTestClassRegistered(this.getClass().getName()));
            tbc.assertTrue("Asserting if the registered services was unregistered when the application instanced is stopped(Object).", !tbc.isTestClassRegistered(Object.class.getName()));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.cleanUp(handle);
        }
    }    
    
    /**
     * This method asserts if registerService really register the classes 
     * passed as parameter in service register.
     * 
     * @spec ApplicationContext.registerService(String[],Object,Dictionary)
     */
    private void testRegisterService008() {
        tbc.log("#testRegisterService008");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            appContext.registerService(new String[] { this.getClass().getName(), Object.class.getName() }, this, null);            
            
            int value = tbc.getServiceId(this.getClass().getName());
            
            tbc.assertTrue("Asserting if we can get the service using " + this.getClass().getName(), (value != -1));
            
            value = tbc.getServiceId(Object.class.getName());
            
            tbc.assertTrue("Asserting if we can get the service using " + Object.class.getName(), (value != -1));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.cleanUp(handle);
        }
    }    
    
    /**
     * This method asserts if IllegalArgumentException is thrown
     * when we try to register interfaces that the service
     * does not implement.
     * 
     * @spec ApplicationContext.registerService(String[],Object,Dictionary)
     */
    private void testRegisterService009() {
        tbc.log("#testRegisterService009");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            appContext.registerService(new String[] { this.getClass().getName(), ApplicationTestControl.class.getName() }, this, null);            
            
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
     * This method asserts if NullPointerException is thrown when we
     * pass null as classes parameter.
     * 
     * @spec ApplicationContext.registerService(String[],Object,Dictionary)
     */
    private void testRegisterService010() {
        tbc.log("#testRegisterService010");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            appContext.registerService((String[]) null, this, null);
            
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
     * This method asserts if IllegalArgumentException is thrown when we
     * pass null as service parameter.
     * 
     * @spec ApplicationContext.registerService(String,Object,Dictionary)
     */
    private void testRegisterService011() {
        tbc.log("#testRegisterService011");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            appContext.registerService(new String[] { this.getClass().getName(), ApplicationTestControl.class.getName() }, null, null);
            
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
     * This method asserts if IllegalStateException is thrown when the
     * application instance has been stopped .
     * 
     * @spec ApplicationContext.registerService(String,Object,Dictionary)
     */
    private void testRegisterService012() {
        tbc.log("#testRegisterService012");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            
            handle.destroy();
                       
            appContext.registerService(this.getClass().getName(), this, null);
            
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
     * This method asserts if IllegalStateException is thrown when the
     * application instance has been stopped .
     * 
     * @spec ApplicationContext.registerService(String,Object,Dictionary)
     */
    private void testRegisterService013() {
        tbc.log("#testRegisterService013");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            
            handle.destroy();
                       
            appContext.registerService(new String[] { this.getClass().getName(), Object.class.getName() }, this, null);
            
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
     * This method asserts if IllegalArgumentException is thrown
     * when we try to register interfaces that the service
     * does not implement.
     * 
     * @spec ApplicationContext.registerService(String,Object,Dictionary)
     */
    private void testRegisterService014() {
        tbc.log("#testRegisterService014");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            
            appContext.registerService(ApplicationTestControl.class.getName(), this, null);

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
     * This method asserts If any of the passed properties
     * have already been specified by the registering bundle,
     * their values will be overwritten by the Framework.
     *  
     * @spec ApplicationContext.registerService(String[],Object,Dictionary)
     */
    private void testRegisterService015() {
        tbc.log("#testRegisterService015");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            Hashtable hash = new Hashtable();
            hash.put(Constants.SERVICE_ID, new Long(2355));
            hash.put(Constants.OBJECTCLASS, new String[] { "test1", "test2" });
            hash.put("test", "test");                 
            
            appContext.registerService(new String[] { this.getClass().getName(), Object.class.getName() }, this, hash);            
            
            Long val = (Long) tbc.getServiceProperty(this.getClass().getName(), Constants.SERVICE_ID, null);
            
            tbc.assertTrue("Asserting if the Constants.SERVICE_ID passed as parameter was changed by the framework.", !(val.intValue()==2355));
            
            String[] values = (String[]) tbc.getServiceProperty(this.getClass().getName(), Constants.OBJECTCLASS, null);
            
            tbc.assertNotNull("Asserting if the OBJECTCLASS returned is not null.", values);
            
            tbc.assertTrue("Asserting if the Constants.OBJECTCLASS passed as parameter was changed by the framework.", !values[0].equals("test1") && !values[0].equals("test2"));
            
            String value = (String) tbc.getServiceProperty(this.getClass().getName(), "test", null);
            
            tbc.assertTrue("Asserting if the other parameter passed was added to the framework parameters.", value.equals("test"));            
            
            val = (Long) tbc.getServiceProperty(Object.class.getName(), Constants.SERVICE_ID, null);
            
            tbc.assertTrue("Asserting if the Constants.SERVICE_ID passed as parameter was changed by the framework.", !(val.intValue()==2355));
            
            values = (String[]) tbc.getServiceProperty(Object.class.getName(), Constants.OBJECTCLASS, null);
            
            tbc.assertNotNull("Asserting if the OBJECTCLASS returned is not null.", values);
            
            tbc.assertTrue("Asserting if the Constants.OBJECTCLASS passed as parameter was changed by the framework.", !values[0].equals("test1") && !values[0].equals("test2"));
            
            value = (String) tbc.getServiceProperty(Object.class.getName(), "test", null);
            
            tbc.assertTrue("Asserting if the other parameter passed was added to the framework parameters.", value.equals("test"));            
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {           
            tbc.cleanUp(handle);  
        }
    }
    
    /**
     * This method asserts if A service event of type ServiceEvent.REGISTERED is fired and
     * if this event triggers the corresponding ApplicationServiceEvent to be delivered
     * to the applications that registered the appropriate listener.
     *  
     * @spec ApplicationContext.registerService(String[],Object,Dictionary)
     */
    private void testRegisterService016() {
        tbc.log("#testRegisterService016");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
               
            appContext.addServiceListener(this, ApplicationConstants.XML_REG);
            
            serviceChanged = false;            	
            appContext.registerService(new String[] { this.getClass().getName(), Object.class.getName() }, this, null);            	
            
            tbc
            .assertTrue(
                "Asserting if the addServiceListener add this test class as a listener for ApplicationServiceEvents.",
                serviceChanged);
        
            tbc.assertEquals("Asserting if the type received was ServiceEvent.REGISTERED value.", ServiceEvent.REGISTERED, serviceType);
            tbc.assertTrue("Asserting if the serviceReference received by the event was org.osgi.test.cases.application.tbc.ApplicationContext.RegisterService", (serviceReference.toString().indexOf("org.osgi.test.cases.application.tbc.ApplicationContext.RegisterService") >= 0));
            tbc.assertNull("Asserting if null was received as serviceObject", serviceObject);                                             
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {           
            tbc.cleanUp(handle);  
        }
    }     

	public void serviceChanged(ApplicationServiceEvent event) {
		serviceType = event.getType();
		serviceObject = event.getServiceObject();
		serviceReference = event.getServiceReference();
		serviceChanged = true;
	}    
    
}