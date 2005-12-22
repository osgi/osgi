package org.osgi.test.cases.application.tbc.ApplicationException;

import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Santos
 * 
 * This test class validates the implementation of
 * <code>ApplicationException</code> constructor, according to MEG reference
 * documentation.
 */
public class ApplicationException {

    private ApplicationTestControl tbc;

    public ApplicationException(ApplicationTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testApplicationException001();
        testApplicationException002();
        testApplicationException003();
        testApplicationException004();
        testApplicationException005();
        testApplicationException006();
        testApplicationException007();
        testApplicationException008();
        testApplicationException009();
        testApplicationException010();
        testApplicationException011();
        testApplicationException012();
    }    
    
    /**
     * This method asserts if the passed errorcode
     * is returned correctly (APPLICAITON_NOT_LAUNCHABLE)
     * 
     * @spec ApplicationException.ApplicationException(int)
     */
    private void testApplicationException001() {
        tbc.log("#testApplicationException001");
        try {        	
        	org.osgi.service.application.ApplicationException exception = new org.osgi.service.application.ApplicationException(org.osgi.service.application.ApplicationException.APPLICAITON_NOT_LAUNCHABLE);
            tbc.assertEquals("Asserting if the errorcode returned was APPLICAITON_NOT_LAUNCHABLE.", org.osgi.service.application.ApplicationException.APPLICAITON_NOT_LAUNCHABLE, exception.getErrorCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    } 	
    
    /**
     * This method asserts if the passed errorcode
     * is returned correctly (APPLICATION_INTERNAL_ERROR).
     * 
     * @spec ApplicationException.ApplicationException(int)
     */
    private void testApplicationException002() {
        tbc.log("#testApplicationException002");
        try {        	
        	org.osgi.service.application.ApplicationException exception = new org.osgi.service.application.ApplicationException(org.osgi.service.application.ApplicationException.APPLICATION_INTERNAL_ERROR);
            tbc.assertEquals("Asserting if the errorcode returned was APPLICATION_INTERNAL_ERROR.", org.osgi.service.application.ApplicationException.APPLICATION_INTERNAL_ERROR, exception.getErrorCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    }
    
    /**
     * This method asserts if the passed errorcode
     * is returned correctly (APPLICATION_LOCKED).
     * 
     * @spec ApplicationException.ApplicationException(int)
     */
    private void testApplicationException003() {
        tbc.log("#testApplicationException003");
        try {        	
        	org.osgi.service.application.ApplicationException exception = new org.osgi.service.application.ApplicationException(org.osgi.service.application.ApplicationException.APPLICATION_LOCKED);
            tbc.assertEquals("Asserting if the errorcode returned was APPLICATION_LOCKED.", org.osgi.service.application.ApplicationException.APPLICATION_LOCKED, exception.getErrorCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    }   
    
    /**
     * This method asserts if the passed parameters
     * are returned correctly (APPLICATION_LOCKED).
     * 
     * @spec ApplicationException.ApplicationException(int,String)
     */
    private void testApplicationException004() {
        tbc.log("#testApplicationException004");
        try {        	
        	org.osgi.service.application.ApplicationException exception = new org.osgi.service.application.ApplicationException(org.osgi.service.application.ApplicationException.APPLICATION_LOCKED,"test");
            tbc.assertEquals("Asserting if the errorcode returned was APPLICATION_LOCKED.", org.osgi.service.application.ApplicationException.APPLICATION_LOCKED, exception.getErrorCode());
            tbc.assertEquals("Asserting if the message returned was test.", "test", exception.getMessage());            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    }
    
    /**
     * This method asserts if the errorCode and the message
     * are returned correctly (APPLICATION_INTERNAL_ERROR). 
     * 
     * @spec ApplicationException.ApplicationException(int,String)
     */
    private void testApplicationException005() {
        tbc.log("#testApplicationException005");
        try {        	
        	org.osgi.service.application.ApplicationException exception = new org.osgi.service.application.ApplicationException(org.osgi.service.application.ApplicationException.APPLICATION_INTERNAL_ERROR, (String)null);
            tbc.assertEquals("Asserting if the errorcode returned was APPLICATION_INTERNAL_ERROR.", org.osgi.service.application.ApplicationException.APPLICATION_INTERNAL_ERROR, exception.getErrorCode());
            tbc.assertNull("Asserting if the message returned was test.", exception.getMessage());            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    } 
    
    /**
     * This method asserts if the passed parameters
     * are returned correctly (APPLICAITON_NOT_LAUNCHABLE).
     * 
     * @spec ApplicationException.ApplicationException(int,String)
     */
    private void testApplicationException006() {
        tbc.log("#testApplicationException006");
        try {        	
        	org.osgi.service.application.ApplicationException exception = new org.osgi.service.application.ApplicationException(org.osgi.service.application.ApplicationException.APPLICAITON_NOT_LAUNCHABLE,"test");
            tbc.assertEquals("Asserting if the errorcode returned was APPLICAITON_NOT_LAUNCHABLE.", org.osgi.service.application.ApplicationException.APPLICAITON_NOT_LAUNCHABLE, exception.getErrorCode());
            tbc.assertEquals("Asserting if the message returned was test.", "test", exception.getMessage());            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    }   
    
    /**
     * This method asserts if the passed parameters
     * are returned correctly (APPLICAITON_NOT_LAUNCHABLE).
     * 
     * @spec ApplicationException.ApplicationException(int,String,Throwable)
     */
    private void testApplicationException007() {
        tbc.log("#testApplicationException007");
        try {        	
        	Throwable cause =  new IllegalArgumentException();
        	org.osgi.service.application.ApplicationException exception = new org.osgi.service.application.ApplicationException(org.osgi.service.application.ApplicationException.APPLICAITON_NOT_LAUNCHABLE,"test", cause);
            tbc.assertEquals("Asserting if the errorcode returned was APPLICAITON_NOT_LAUNCHABLE.", org.osgi.service.application.ApplicationException.APPLICAITON_NOT_LAUNCHABLE, exception.getErrorCode());
            tbc.assertEquals("Asserting if the message returned was test.", "test", exception.getMessage());   
            tbc.assertEquals("Asserting if the cause returned is IllegalArgumentException", cause, exception.getCause());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    } 
    
    /**
     * This method asserts if the passed parameters
     * are returned correctly (APPLICATION_INTERNAL_ERROR).
     * 
     * @spec ApplicationException.ApplicationException(int,String,Throwable)
     */
    private void testApplicationException008() {
        tbc.log("#testApplicationException008");
        try {        	
        	Throwable cause =  new IllegalArgumentException();
        	org.osgi.service.application.ApplicationException exception = new org.osgi.service.application.ApplicationException(org.osgi.service.application.ApplicationException.APPLICATION_INTERNAL_ERROR,null, cause);
            tbc.assertEquals("Asserting if the errorcode returned was APPLICATION_INTERNAL_ERROR.", org.osgi.service.application.ApplicationException.APPLICATION_INTERNAL_ERROR, exception.getErrorCode());
            tbc.assertNull("Asserting if the returned message was null.", exception.getMessage());   
            tbc.assertEquals("Asserting if the cause returned is IllegalArgumentException", cause, exception.getCause());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    }      
    
    /**
     * This method asserts if the passed parameters
     * are returned correctly (APPLICATION_LOCKED). 
     * 
     * @spec ApplicationException.ApplicationException(int,String,Throwable)
     */
    private void testApplicationException009() {
        tbc.log("#testApplicationException009");
        try {        	
        	org.osgi.service.application.ApplicationException exception = new org.osgi.service.application.ApplicationException(org.osgi.service.application.ApplicationException.APPLICATION_LOCKED,"test", null);
            tbc.assertEquals("Asserting if the errorcode returned was APPLICATION_LOCKED.", org.osgi.service.application.ApplicationException.APPLICATION_LOCKED, exception.getErrorCode());
            tbc.assertEquals("Asserting if the message returned was test.", "test", exception.getMessage());   
            tbc.assertNull("Asserting if the returned cause was null", exception.getCause());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    } 
    
    /**
     * This method asserts if the passed parameters
     * are returned correctly (APPLICATION_LOCKED). 
     * 
     * @spec ApplicationException.ApplicationException(int,Throwable)
     */
    private void testApplicationException010() {
        tbc.log("#testApplicationException010");
        try {        	
        	Throwable cause =  new IllegalArgumentException();
        	org.osgi.service.application.ApplicationException exception = new org.osgi.service.application.ApplicationException(org.osgi.service.application.ApplicationException.APPLICATION_LOCKED, cause);
            tbc.assertEquals("Asserting if the errorcode returned was APPLICATION_LOCKED.", org.osgi.service.application.ApplicationException.APPLICATION_LOCKED, exception.getErrorCode());  
            tbc.assertEquals("Asserting if the cause returned is IllegalArgumentException", cause, exception.getCause());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    } 
    
    /**
     * This method asserts if the passed parameters
     * are returned correctly (APPLICATION_INTERNAL_ERROR). 
     * 
     * @spec ApplicationException.ApplicationException(int,Throwable)
     */
    private void testApplicationException011() {
        tbc.log("#testApplicationException011");
        try {        	
        	Throwable cause =  new IllegalArgumentException();
        	org.osgi.service.application.ApplicationException exception = new org.osgi.service.application.ApplicationException(org.osgi.service.application.ApplicationException.APPLICATION_INTERNAL_ERROR, cause);
            tbc.assertEquals("Asserting if the errorcode returned was APPLICATION_INTERNAL_ERROR.", org.osgi.service.application.ApplicationException.APPLICATION_INTERNAL_ERROR, exception.getErrorCode());  
            tbc.assertEquals("Asserting if the cause returned is IllegalArgumentException", cause, exception.getCause());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    }    
    
    /**
     * This method asserts if the passed parameters
     * are returned correctly (APPLICATION_INTERNAL_ERROR). 
     * 
     * @spec ApplicationException.ApplicationException(int,Throwable)
     */
    private void testApplicationException012() {
        tbc.log("#testApplicationException012");
        try {        	
        	org.osgi.service.application.ApplicationException exception = new org.osgi.service.application.ApplicationException(org.osgi.service.application.ApplicationException.APPLICAITON_NOT_LAUNCHABLE, (Throwable) null);
            tbc.assertEquals("Asserting if the errorcode returned was APPLICATION_INTERNAL_ERROR.", org.osgi.service.application.ApplicationException.APPLICAITON_NOT_LAUNCHABLE, exception.getErrorCode());  
            tbc.assertNull("Asserting if the returned cause was null", exception.getCause());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
                + e.getClass().getName());
        }
    }       
	
}
