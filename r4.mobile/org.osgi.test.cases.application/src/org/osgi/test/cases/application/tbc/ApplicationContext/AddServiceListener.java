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
package org.osgi.test.cases.application.tbc.ApplicationContext;

import java.util.Hashtable;

import org.osgi.application.ApplicationContext;
import org.osgi.application.ApplicationServiceEvent;
import org.osgi.application.ApplicationServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;
import org.osgi.test.cases.application.tbc.util.TestAppController;
import org.osgi.test.cases.application.tbc.util.TestingActivator;


/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>addServiceListener</code> method, according to MEG reference
 * documentation.
 */
public class AddServiceListener implements ApplicationServiceListener {
    private ApplicationTestControl tbc;
    private boolean serviceChanged;
    private ServiceReference serviceReference;
    private Object serviceObject;    

    public AddServiceListener(ApplicationTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testAddServiceListener001(); 
        testAddServiceListener002();
        testAddServiceListener003();
        testAddServiceListener004();
        testAddServiceListener005();
        testAddServiceListener006();
        testAddServiceListener007();
        testAddServiceListener008();
        testAddServiceListener009();
        testAddServiceListener010();
        testAddServiceListener011();
        testAddServiceListener012();
        testAddServiceListener013();
        testAddServiceListener014();
        testAddServiceListener015();
        testAddServiceListener016();
        testAddServiceListener017();
    }

    /**
     * This method asserts that after an addServiceListener call, when a service
     * has a lifecycle change this application instance is notified. And, after
     * a removeServiceListener call, this instance must not be advise anymore
     * about changes of lifecycles.
     * 
     * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String)
     */
    private void testAddServiceListener001() {
        tbc.log("#testAddServiceListener001");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            
            appContext.addServiceListener(this, ApplicationConstants.XML_APP);
            
            Hashtable ht = new Hashtable();
            ht.put("MyName", "MyValue");
            
            serviceChanged = false;            	
            tbc.getAppController().setProperties(ht);            	
            
            tbc
                .assertTrue(
                    "Asserting if the addServiceListener add this test class as a listener for ApplicationServiceEvents.",
                    serviceChanged);
            
            tbc.assertTrue("Asserting if the serviceReference received by the event was org.osgi.test.cases.application.tbc.util.TestAppController", (serviceReference.toString().indexOf("org.osgi.test.cases.application.tbc.util.TestAppController") >= 0));
            tbc.assertEquals("Asserting if the TestAppController was received by the event as serviceObject after a call for locateService.", tbc.getAppController(), serviceObject);
                        
            appContext.removeServiceListener(this);
            
           	serviceChanged = false;
           	tbc.getAppController().setProperties(ht);

            tbc
                .assertTrue(
                    "Asserting if the removeServiceListener remove this test class from the list of listeners for ServiceEvents.",
                    !serviceChanged);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.getAppController().resetProperties();
        	tbc.cleanUp(handle);
        }
    }
    
    /**
     * This method asserts if IllegalStateException is thrown when the
     * application instance has been stopped.
     * 
     * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String)
     */
    private void testAddServiceListener002() {
        tbc.log("#testAddServiceListener002");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            
            handle.destroy();
                       
            appContext.addServiceListener(this, ApplicationConstants.XML_APP);
            
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
     * application instance has been stopped.
     * 
     * @spec ApplicationContext.removeServiceListener(ApplicationServiceListener)
     */
    private void testAddServiceListener003() {
        tbc.log("#testAddServiceListener003");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            
            handle.destroy();
                       
            appContext.removeServiceListener(this);
            
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
     * This method asserts that when we call addServiceListener two times with
     * the same parameters, no exception is thrown.
     * 
     * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String)
     */
    private void testAddServiceListener004() {
        tbc.log("#testAddServiceListener004");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            appContext.addServiceListener(this, ApplicationConstants.XML_APP);
            appContext.addServiceListener(this, ApplicationConstants.XML_APP);
            tbc.pass("No exception was thrown.");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.cleanUp(handle);
        }
    }    
    
    /**
     * This method asserts that when we call removeServiceListener without a
     * prior call to addServiceListener, no exception is thrown.
     * 
     * @spec ApplicationContext.removeServiceListener(ApplicationServiceListener)
     */
    private void testAddServiceListener005() {
        tbc.log("#testAddServiceListener005");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            appContext.removeServiceListener(this);
            tbc.pass("No exception was thrown.");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.cleanUp(handle);
        }
    }        
    
    /**
     * This method asserts that the registered listener will only receive the 
     * ApplicationServiceEvents realted to the passed referred service. 
     * 
     * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String)
     */
    private void testAddServiceListener006() {
        tbc.log("#testAddServiceListener006");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            
            appContext.addServiceListener(this, ApplicationConstants.XML_APP);       
                        
            Hashtable ht = new Hashtable();
            ht.put("MyName", "MyValue");
            TestAppController app = null;
            
            serviceChanged = false;            	
            app = (TestAppController) appContext.locateService(ApplicationConstants.XML_APP);                
            tbc.getAppController().setProperties(ht);               
            
            tbc
            .assertTrue(
                "Asserting if the addServiceListener add this test class as a listener for ServiceEvents.",
                serviceChanged);
            tbc.assertTrue("Asserting if the serviceReference received by the event was org.osgi.test.cases.application.tbc.util.TestAppController", (serviceReference.toString().indexOf("org.osgi.test.cases.application.tbc.util.TestAppController") >= 0));           
            tbc.assertEquals("Asserting if the TestAppController was received by the event as serviceObject after a call for locateService.", app, serviceObject);            
                        
           	serviceChanged = false;
           	tbc.getEventBundle().setProperties(ht);            	
            
            tbc
            .assertTrue(
                "Asserting if the registered listener will only receive the ApplicationServiceEvents realted to the referred service.",
                !serviceChanged);

        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.getEventBundle().resetProperties();
        	tbc.getAppController().resetProperties();   		
        	tbc.cleanUp(handle);
        }
    }    
    
    /**
     * This method asserts if IllegalArgumentExceptions is thrown when there is
     * no service in the application descriptor with the specified referenceName.
     * 
     * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String)
     */
    private void testAddServiceListener007() {
        tbc.log("#testAddServiceListener007");
        ApplicationHandle handle = null;
        try {
        	handle = tbc.getAppDescriptor().launch(null);
                
            ApplicationContext appContext = org.osgi.application.Framework
                    .getApplicationContext(tbc.getAppInstance());
                
            appContext.addServiceListener(this, ApplicationConstants.XML_INEXISTENT);
                
            tbc.failException("", IllegalArgumentException.class);
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
     * This method asserts if NullPointerException is thrown when null
     * is passed as listener.
     * 
     * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String)
     */
    private void testAddServiceListener008() {
        tbc.log("#testAddServiceListener008");
        ApplicationHandle handle = null;
        try {
        	handle = tbc.getAppDescriptor().launch(null);
                
            ApplicationContext appContext = org.osgi.application.Framework
                    .getApplicationContext(tbc.getAppInstance());
                
            appContext.addServiceListener(null, ApplicationConstants.XML_INEXISTENT);
                
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
    * This method asserts if NullPointerException is thrown when null
    * is passed as referenceName.
    * 
    * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String)
    */
    private void testAddServiceListener009() {
       tbc.log("#testAddServiceListener009");
       ApplicationHandle handle = null;
       try {
       	handle = tbc.getAppDescriptor().launch(null);
               
           ApplicationContext appContext = org.osgi.application.Framework
                   .getApplicationContext(tbc.getAppInstance());
               
           appContext.addServiceListener(this, (String) null);
               
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
    * This method asserts that the registered listener will only receive the 
    * ApplicationServiceEvents realted to the passed referred service. 
    * 
    * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String[])
    */
    private void testAddServiceListener010() {
       tbc.log("#testAddServiceListener010");
       ApplicationHandle handle = null;
       ApplicationContext appContext = null;
       try {
           handle = tbc.getAppDescriptor().launch(null);
           
           appContext = org.osgi.application.Framework
               .getApplicationContext(tbc.getAppInstance());
           
           appContext.addServiceListener(this, new String[] { ApplicationConstants.XML_APP, ApplicationConstants.XML_ACTIVATOR });       
                       
           Hashtable ht = new Hashtable();
           ht.put("MyName", "MyValue");
           TestAppController app = null;
           
           serviceChanged = false;            	
           app = (TestAppController) appContext.locateService(ApplicationConstants.XML_APP);                
           tbc.getAppController().setProperties(ht);               
           
           tbc
           .assertTrue(
               "Asserting if the addServiceListener add this test class as a listener for ServiceEvents.",
               serviceChanged);
           tbc.assertTrue("Asserting if the serviceReference received by the event was org.osgi.test.cases.application.tbc.util.TestAppController", (serviceReference.toString().indexOf("org.osgi.test.cases.application.tbc.util.TestAppController") >= 0));
           tbc.assertEquals("Asserting if the TestAppController was received by the event as serviceObject after a call for locateService.", app, serviceObject);            
                       
           tbc.startActivator(false);
           
           serviceChanged = false;
           TestingActivator activator = (TestingActivator) appContext.locateService(ApplicationConstants.XML_ACTIVATOR);
           activator.setProperties(ht);           
           
           tbc
           .assertTrue(
               "Asserting if the addServiceListener add this test class as a listener for ServiceEvents.",
               serviceChanged);
           tbc.assertTrue("Asserting if the serviceReference received by the event was org.osgi.test.cases.application.tbc.util.TestingActivator", (serviceReference.toString().indexOf("org.osgi.test.cases.application.tbc.util.TestingActivator") >= 0));
           tbc.assertEquals("Asserting if the TestAppController was received by the event as serviceObject after a call for locateService.", activator, serviceObject);                      
                      
           serviceChanged = false;
           tbc.getEventBundle().setProperties(ht);            	
           
           tbc
           .assertTrue(
               "Asserting if the registered listener will only receive the ApplicationServiceEvents realted to the referred service.",
               !serviceChanged);

       } catch (Exception e) {
           tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
               + e.getClass().getName());
       } finally {
			appContext.removeServiceListener(this);
			tbc.stopActivator();
			tbc.getEventBundle().resetProperties();
			tbc.getAppController().resetProperties();
			tbc.cleanUp(handle);
	   }
   } 
    
    /**
     * This method asserts if IllegalStateException is thrown when the
     * application instance has been stopped.
     * 
     * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String[])
     */
    private void testAddServiceListener011() {
        tbc.log("#testAddServiceListener011");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            
            handle.destroy();
                       
            appContext.addServiceListener(this,new String[] { ApplicationConstants.XML_APP });
            
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
     * This method asserts that when we call addServiceListener two times with
     * the same parameters, no exception is thrown.
     * 
     * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String[])
     */
    private void testAddServiceListener012() {
        tbc.log("#testAddServiceListener012");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            appContext.addServiceListener(this,new String[] { ApplicationConstants.XML_APP });
            appContext.addServiceListener(this,new String[] { ApplicationConstants.XML_APP });
            tbc.pass("No exception was thrown.");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
        	tbc.cleanUp(handle);
        }
    }          
    
    /**
     * This method asserts if NullPointerException is thrown when null
     * is passed as listener.
     * 
     * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String[])
     */
    private void testAddServiceListener013() {
        tbc.log("#testAddServiceListener013");
        ApplicationHandle handle = null;
        try {
        	handle = tbc.getAppDescriptor().launch(null);
                
            ApplicationContext appContext = org.osgi.application.Framework
                    .getApplicationContext(tbc.getAppInstance());
                
            appContext.addServiceListener(null,new String[] { ApplicationConstants.XML_APP });
                
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
    * This method asserts if NullPointerException is thrown when null
    * is passed as referenceNames
    * 
    * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String[])
    */
    private void testAddServiceListener014() {
       tbc.log("#testAddServiceListener014");
       ApplicationHandle handle = null;
       try {
       	handle = tbc.getAppDescriptor().launch(null);
               
           ApplicationContext appContext = org.osgi.application.Framework
                   .getApplicationContext(tbc.getAppInstance());
               
           appContext.addServiceListener(this,(String[]) null);
               
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
    * This method asserts if IllegalArgumentExceptions is thrown if
    * the referenceNames array is empty. 
    * 
    * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String[])
    */
    private void testAddServiceListener015() {
       tbc.log("#testAddServiceListener015");
       ApplicationHandle handle = null;
       try {
       	handle = tbc.getAppDescriptor().launch(null);
               
           ApplicationContext appContext = org.osgi.application.Framework
                   .getApplicationContext(tbc.getAppInstance());
               
           appContext.addServiceListener(this, new String[] { });
               
           tbc.failException("", IllegalArgumentException.class);
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
    * This method asserts if IllegalArgumentExceptions is thrown when
    * the referenceNames contains unknown references.
    * 
    * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String[])
    */
    private void testAddServiceListener016() {
       tbc.log("#testAddServiceListener016");
       ApplicationHandle handle = null;
       try {
       	handle = tbc.getAppDescriptor().launch(null);
               
           ApplicationContext appContext = org.osgi.application.Framework
                   .getApplicationContext(tbc.getAppInstance());
               
           appContext.addServiceListener(this, new String[] { ApplicationConstants.XML_APP , ApplicationConstants.XML_INEXISTENT });
               
           tbc.failException("", IllegalArgumentException.class);
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
    * This method asserts if IllegalArgumentExceptions is thrown when the
    * referenceNames contains null references.
    * 
    * @spec ApplicationContext.addServiceListener(ApplicationServiceListener,String[])
    */
    private void testAddServiceListener017() {
       tbc.log("#testAddServiceListener017");
       ApplicationHandle handle = null;
       try {
       	handle = tbc.getAppDescriptor().launch(null);
               
           ApplicationContext appContext = org.osgi.application.Framework
                   .getApplicationContext(tbc.getAppInstance());
               
           appContext.addServiceListener(this, new String[] { ApplicationConstants.XML_APP , null });
               
           tbc.failException("", IllegalArgumentException.class);
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

    public void serviceChanged(ApplicationServiceEvent event) {
		serviceObject = event.getServiceObject();
		serviceReference = event.getServiceReference();
		serviceChanged = true;
	}    
    
}