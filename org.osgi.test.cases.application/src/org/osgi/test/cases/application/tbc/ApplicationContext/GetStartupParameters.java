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

import java.security.GuardedObject;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.application.ApplicationContext;
import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.service.event.Event;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;


/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>getStartupParameters</code> method, according to MEG reference
 * documentation.
 */
public class GetStartupParameters {
    private ApplicationTestControl tbc;

    public GetStartupParameters(ApplicationTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testGetStartupParameters001();
        testGetStartupParameters002();
        testGetStartupParameters003();
        testGetStartupParameters004();
        testGetStartupParameters005();
    }    
    
    /**
     * This method asserts if null is returned, when we pass
     * null as parameter in launch method.
     * 
     * @spec ApplicationContext.getStartupParameters()
     */
    private void testGetStartupParameters001() {
        tbc.log("#testGetStartupParameters001");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                                  
            tbc.assertNull("Asserting if null is returned when we pass null as parameter in launch method.", appContext.getStartupParameters());
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        } finally {
            tbc.cleanUp(handle);
        }
    }
    
    /**
     * This method asserts if the same object passed as parameter
     * in launch method is returned by getStartupParameters.
     * 
     * @spec ApplicationContext.getStartupParameters()
     */
    private void testGetStartupParameters002() {
        tbc.log("#testGetStartupParameters002");
        ApplicationHandle handle = null;
        try {
            Hashtable hash = new Hashtable();
            hash.put("dummy", "value");
            
            handle = tbc.getAppDescriptor().launch(hash);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
                        
            Hashtable result = (Hashtable) appContext.getStartupParameters();
            
            tbc.assertEquals("Asserting if the object passed as parameter in launch method is equal to the returned by getStartupParameters.", hash, result);
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
     * @spec ApplicationContext.getStartupParameters()
     */
    private void testGetStartupParameters003() {
        tbc.log("#testGetStartupParameters003");
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            ApplicationContext appContext = org.osgi.application.Framework
                .getApplicationContext(tbc.getAppInstance());
            
            handle.destroy();
                       
            appContext.getStartupParameters();
            
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
	 * This method asserts if a "org.osgi.triggeringevent" property is passed as arguments
	 * to the schedule, it is replaced by the RI with the event fired.
	 * 
	 * @spec ApplicationContext.getStartupParameters()
	 */	
	private void testGetStartupParameters004() {
		tbc.log("#testGetStartupParameters004");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);
			
			Hashtable hash = new Hashtable();
			hash.put(ScheduledApplication.TRIGGERING_EVENT, "test");
			
			sa = tbc.getAppDescriptor().schedule(null, hash, ApplicationConstants.TOPIC_EVENT, null, true);
			
			tbc.assertNotNull("Asserting that a ScheduledApplication was returned according to the used filter.", sa);
			
			tbc.sendEvent(ApplicationConstants.TOPIC_EVENT);
			
			ApplicationContext appContext = org.osgi.application.Framework
            .getApplicationContext(tbc.getAppInstance());
			
			Map hash2 = appContext.getStartupParameters();
			
			tbc.assertTrue("Asserting if the returned map has a different value for the org.osgi.triggeringevent.", hash2.get(ScheduledApplication.TRIGGERING_EVENT) instanceof GuardedObject);
						
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}
	
	/**
	 * This method asserts if when null was passed as parameter for the arguments,
	 * the Scheduler creates a Map and put a value for org.osgi.triggeringevent.
	 * 
	 * @spec ApplicationContext.getStartupParameters()
	 */	
	private void testGetStartupParameters005() {
		tbc.log("#testGetStartupParameters005");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);
					
			sa = tbc.getAppDescriptor().schedule(null, null, ApplicationConstants.TOPIC_EVENT, null, true);
			
			tbc.assertNotNull("Asserting that a ScheduledApplication was returned according to the used filter.", sa);
			
			tbc.sendEvent(ApplicationConstants.TOPIC_EVENT);
			
			ApplicationContext appContext = org.osgi.application.Framework
            .getApplicationContext(tbc.getAppInstance());
			
			Map hash = appContext.getStartupParameters();
			
			tbc.assertTrue("Asserting if the returned map has a different value for the org.osgi.triggeringevent.", hash.get(ScheduledApplication.TRIGGERING_EVENT) instanceof GuardedObject);
			
			GuardedObject obj = (GuardedObject) hash.get(ScheduledApplication.TRIGGERING_EVENT);

			Event evt = (Event) obj.getObject();
			
			tbc.assertEquals("Asserting if the event inside the GuardedObject has org/cesar/topic as topic", ApplicationConstants.TOPIC_EVENT, evt.getTopic());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}	
    
    
}