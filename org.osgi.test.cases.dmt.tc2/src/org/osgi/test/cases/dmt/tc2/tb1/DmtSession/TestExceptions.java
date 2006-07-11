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
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Aug 25, 2005  Luiz Felipe Guimaraes
 * 173           [MEGTCK][DMT] Changes on interface names and plugins
 * ============  ==============================================================

 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import info.dmtree.*;
import info.dmtree.security.DmtPermission;
import info.dmtree.security.DmtPrincipalPermission;

import java.lang.reflect.*;
import java.util.Vector;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.*;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This class simulates the following Exceptions that can be thrown in a DmtSession:
 * 1) DmtException (INVALID_URI, URI_TOO_LONG, PERMISSION_DENIED, COMAND_FAILED-when the URI is not within the current session's subtree-).
 * 2) DmtIllegalStateException (in case of timeout or closed session).
 * 3) SecurityException (in case of local sessions, if the caller does not have DmtPermission for the node)
 */
public class TestExceptions implements TestInterface {
	private DmtTestControl tbc;
	
	//Default values, so the values are instaciated once.
	private Boolean defaultBoolean = new Boolean(false);
	private DmtData defaultDmtData = new DmtData(1);
	private String defaultString = "test"; 
	private Acl defaultAcl = new Acl("Add=*&Replace=*");
	
	private Method[] methods = DmtSession.class.getDeclaredMethods();
	
    private Vector methodsDontThrowAnyExceptions = new Vector(6);
    
    private Vector methodsDontThrowSecurityException = new Vector(6);
	
	public TestExceptions(DmtTestControl tbc) {
		this.tbc = tbc;
		
	    //Adds the methods that do not throw any exceptions tested here.
	    methodsDontThrowAnyExceptions.add("getLockType");
	    methodsDontThrowAnyExceptions.add("getPrincipal");
	    methodsDontThrowAnyExceptions.add("getSessionId");
	    methodsDontThrowAnyExceptions.add("getState");
	    methodsDontThrowAnyExceptions.add("getRootUri");
	    methodsDontThrowAnyExceptions.add("mangle");
        
        //Theses methods below do not throw SecurityException 
        methodsDontThrowSecurityException.add("commit");
        methodsDontThrowSecurityException.add("rollback");
        methodsDontThrowSecurityException.add("close");
            
	}

	public void run() {
		testExceptions001();
		testExceptions002();
		testExceptions003();
		testExceptions004();
		testExceptions005();
		testExceptions006();
		testExceptions007();
	

	}

	/**
	 * This method simulates DmtException.INVALID_URI in all DmtSession methods that throw this Exception
	 * (if URI or node name is an empty string, if the URI or node name ends with the '\' or '/' character, 
 	 * if the URI contains the segment "." at a position other than the beginning of the URI or if
 	 * the node name is ".." or the URI contains such a segment) 
 	 * 
	 * @spec DmtException.INVALID_URI
	 */
	private void testExceptions001() {

		try {
		    tbc.log("#testExceptions001");
		    openSessionAndSimulateDmtException(DmtException.INVALID_URI);
		} catch (Throwable e) {
			tbc.log(e.getMessage());

		}
	}
	
	/**
	 * This method simulates DmtException.COMMAND_FAILED in all DmtSession methods that throw this Exception
	 * (in case of if the URI is not within the current session's subtree)  
	 * 
	 * @spec DmtException.COMMAND_FAILED
	 */
	private void testExceptions002() {
		try {
		    tbc.log("#testExceptions002");
		    openSessionAndSimulateDmtException(DmtException.COMMAND_FAILED);
		} catch (Throwable e) {
		    tbc.log(e.getMessage());
		}
	}
	
	/**
	 * This method simulates DmtException.URI_TOO_LONG in all DmtSession methods that throw this Exception  
	 * (if the target URI contains too many segments or if its segments is too long)
	 * 
	 * @spec DmtException.URIS_TOO_LONG
	 */
	private void testExceptions003() {
		try {
		    tbc.log("#testExceptions003");
		    if (DmtTestControl.URIS_TOO_LONG.length>0) {
		        openSessionAndSimulateDmtException(DmtException.URI_TOO_LONG);
		    } else {
		        tbc.log("#There are no maximum node length and maximum node segments, " +
		        		"DmtException.URI_TOO_LONG will not be tested");
		    }
		    
		} catch (Throwable e) {
		    tbc.log(e.getMessage());
		}
	}
	
	/**
	 * This method simulates DmtException.PERMISSION_DENIED in all methods that throw this Exception  
	 * (if the principal associated with the session does not have adequate access control permissions (ACL) 
	 * on the target)
	 * 
	 * @spec DmtException.PERMISSION_DENIED
	 */
	private void testExceptions004() {
		try {
		    tbc.log("#testExceptions004");
		    openSessionAndSimulateDmtException(DmtException.PERMISSION_DENIED);
		} catch (Throwable e) {
		    tbc.log(e.getMessage());
		}
	}
	
	/**
	 * This method simulates DmtIllegalStateException in all methods that throw this Exception
	 * (if the session is already closed) 
	 * 
	 * @spec 117.12.7 DmtSession
	 */
	private void testExceptions005() {
		try {
		    tbc.log("#testExceptions005");
		    openSessionAndSimulateDmtIllegalStateException(false);
		} catch (Throwable e) {
		    tbc.log(e.getMessage());
		}
	}
	
	/**
	 * This method simulates DmtIllegalStateException in all methods that throw this Exception
	 * (if the session is invalidated because of timeout)  
	 * 
	 * @spec 117.12.7 DmtSession
	 */
	private void testExceptions006() {
		try {
		    tbc.log("#testExceptions006");

		    openSessionAndSimulateDmtIllegalStateException(true);
		} catch (Throwable e) {
		    tbc.log(e.getMessage());
		}
	}
	
	/**
	 * This method simulates SecurityException in all DmtSession methods that throw this Exception
	 * (if the caller does not have the necessary DmtPermission)
	 * 
	 * @spec 117.12.7 DmtSession
	 */
	private void testExceptions007() {
		try {
		    tbc.log("#testExceptions007");
		    openSessionAndSimulateSecurityException();
		} catch (Throwable e) {
		    tbc.log(e.getMessage());
		}
	}
	/**
	 * It opens only one session and call all methods of DmtSession with this session, 
	 * expecting that a DmtException is thrown. After all methods, it closes the session 
	 * so the subtree is not blocked.  
	 * @param code The DmtException being tested
	 */
	private void openSessionAndSimulateDmtException(int code) {
		DmtSession session = null;
	    try {
	        String exceptionName = DmtConstants.getDmtExceptionCodeText(code);
	        tbc.log("#Asserting DmtException." + exceptionName + " in all DmtSession methods that throw this DmtException.");
			switch (code) {
			    case DmtException.INVALID_URI:
			    case DmtException.URI_TOO_LONG:
			        session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,DmtSession.LOCK_TYPE_ATOMIC);
			    	break;
			    case DmtException.COMMAND_FAILED:
			        //The session is opened in a subtree different source
			        session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_ATOMIC);
			    	tbc.log("#The root URI for all methods that throws DmtException." + exceptionName + " is \"" + session.getRootUri()+"\"");
			    	break;
			    case DmtException.PERMISSION_DENIED:
			        //Sets the Acl of TestExexPlugin.ROOT not to have any permissions for DmtContstants.PRINCIPAL
			        //because if an ACL is not set then the effective ACL of that node must be the ACL of its first 
			        //ancestor that has a non-empty ACL (and the root "." has "Add=*&Get=*&Replace=*")
			        session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			    	session.setNodeAcl(TestExecPluginActivator.ROOT,new Acl("Get=" + DmtConstants.PRINCIPAL_2));
			    	session.close();
			    	
			        tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(), DmtConstants.PRINCIPAL, "*"));
					session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,".",DmtSession.LOCK_TYPE_EXCLUSIVE);
					tbc.log("#The Acl is not set for the URI's used, so DmtException." + exceptionName + " must be thrown");
			    	break;
			}
			
		    for (int i=0;i<methods.length;i++) {
				Method currentMethod = methods[i];
				
				//Only the public fields must be gotten.
				if (Modifier.isPublic(currentMethod.getModifiers())) {
				    //Only methods with more than 0 parameter throws DmtException, except isNodeUri and mangle
					if (currentMethod.getParameterTypes().length>0) {
					    if (!currentMethod.getName().equals("isNodeUri") && !currentMethod.getName().equals("mangle") ) {
					        assertDmtException(session,currentMethod,code);
					        
					    }
					}
					
				}
				
			}
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
		    tbc.cleanUp(session,TestExecPluginActivator.ROOT);
		}
	}
	/**
	 * Returns default valued to the specified method. Notice that the nodeUri is always the first parameter
	 * (when there is at least one parameter)
	 * @param method The method to get the parameters
	 * @return The array of objects with the default values
	 */
	private Object[] getDefaultValuesToParameters(Method method) {
		Class[] parameters = method.getParameterTypes();
		int numberOfParameters= parameters.length;
		if (numberOfParameters==0) {
		    return new Object[0];
		} else {
			Object[] values = new Object[numberOfParameters];
			
			//The first parameter of DmtSession is always nodeUri
			values[0] = getValidUriForThisMethod(method.getName());

			for (int i=1;i<numberOfParameters;i++) {
			    if (parameters[i].getName().equals(Acl.class.getName())) {
				    values[i]= defaultAcl;
				} else if (parameters[i].getName().equals(String.class.getName())) {
				    values[i]= defaultString;
				} else if (parameters[i].getName().equals(DmtData.class.getName())) {
				    values[i]= defaultDmtData;
				} else if (parameters[i].getName().equals("boolean")) {
				    values[i]= defaultBoolean;
				}
			}
			
			//copy and renameNode needs different values for the second parameter, so it is overwritten
			if (method.getName().equals("copy")) {
			    //second parameter must be an inexistent node
			    values[1] = TestExecPluginActivator.INEXISTENT_NODE;
			} else if (method.getName().equals("renameNode")) {
			    //second parameter must be an inexistent value
			    values[1] = TestExecPluginActivator.INEXISTENT_NODE_NAME;
			}		
			
			return values;
		}
	}
	/**
	 * Asserts that DmtIllegalStateException is thrown if the session is closed or timed out.
	 * @param session The session (timed out or closed)
 	 * @param method The method's name.
	 * @param timeOut True if the exception being tested is because of timeout. False if it is because of a closed session.
	 */
	private void assertDmtIllegalStateException(DmtSession session,Method method,boolean timeOut) {
	    String methodName = method.getName();
	    Object[] parameterValues = getDefaultValuesToParameters(method);
	    String problemName=(timeOut)?"timed out":"closed";
	    try {
		    method.invoke(session,parameterValues);
			tbc.failException("DmtIllegalStateException was not thrown when the session is "+ problemName + " and DmtSession." + 
			    methodName+ " is called.",DmtIllegalStateException.class);
		
		} catch (InvocationTargetException e) {
			
			Throwable exceptionReturned = e.getTargetException();
			
			if (exceptionReturned instanceof DmtIllegalStateException) {
				tbc.pass("DmtIllegalStateException correctly thrown when the session is "+ problemName + " and DmtSession." + methodName +" is called.");
			} else {
			    tbc.failExpectedOtherException(DmtException.class, exceptionReturned);
			}
				
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.closeSession(session);
		}
	}
	/**
	 * It opens a session and, after that, closes (if timeOut is set to false) or wait for timeout 
	 * (if timeout is set to true and there is a timeout period in this implementation)
	 * @param timeOut True if the exception being tested is because of timeout. False if it is because of a closed session.
	 */
	private synchronized void openSessionAndSimulateDmtIllegalStateException(boolean timeOut) {
	    DmtSession session= null;
	    boolean timeoutPeriodDefined = true;
	    try {
		    if (timeOut) {
		        if (DmtConstants.TIMEOUT>0) {
		            session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,DmtSession.LOCK_TYPE_ATOMIC);
		            //Session should be invalidated after this period.
		            wait(DmtConstants.TIMEOUT);
		            tbc.assertEquals("Asserts that DmtSession.getState returns STATE_INVALID in case of timeout",DmtSession.STATE_INVALID,session.getState());
		            
		        } else {
		            tbc.log("#Timeout period was not defined, tests of DmtIllegalStateException in case of timeout will not be tested");
		            timeoutPeriodDefined = false;
		        }
		    } else {
		        session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,DmtSession.LOCK_TYPE_ATOMIC);
		        session.close();	        
		    }
		        
		    //If the test timeout is not defined, the methods will not be called. (The tests of a closed session always are called)  
		    if (timeoutPeriodDefined) {
			    for (int i=0;i<methods.length;i++) {
					Method currentMethod = methods[i];
					//Only the public fields must be gotten.
					if (Modifier.isPublic(currentMethod.getModifiers())) {
					    //There are some methods that do not throw DmtIllegalStateException
					    if (!methodsDontThrowAnyExceptions.contains(currentMethod.getName())) {
					        assertDmtIllegalStateException(session,currentMethod,timeOut);
					    }
					}
					
				}
		    }
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.closeSession(session);
		}
	
	}
	/**
	 * Asserts that SecurityException is thrown if the caller does not have DmtPermission for the node 
	 *
	 */
	private void openSessionAndSimulateSecurityException() {
	    DmtSession session= null;
	    try {
            // DmtPermission.GET is required for session opening
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), TestExecPluginActivator.ROOT, DmtPermission.GET));
	        session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,DmtSession.LOCK_TYPE_ATOMIC);
	        //DmtPermission is not present, so SecurityException must be thrown 
            tbc.setPermissions(new PermissionInfo[0]);
			    for (int i=0;i<methods.length;i++) {
					Method currentMethod = methods[i];
					
					//Only the public fields must be gotten.
					if (Modifier.isPublic(currentMethod.getModifiers())) {
					    String currentMethodName = currentMethod.getName();
					    //There are some methods that do not throw SecurityException. 
					    if (!methodsDontThrowAnyExceptions.contains(currentMethodName) && !methodsDontThrowSecurityException.contains(currentMethodName)) { 
					        Object[] parameterValues = getDefaultValuesToParameters(currentMethod);
						    try {
						        currentMethod.invoke(session,parameterValues);
								tbc.failException("SecurityException was not thrown when the caller does not have DmtPermission for the specified node. Method: DmtSession." + currentMethodName,SecurityException.class);
							
							} catch (InvocationTargetException e) {
								
								Throwable exceptionReturned = e.getTargetException();
								
								if (exceptionReturned instanceof SecurityException) {
									tbc.pass("SecurityException correctly thrown when the caller does not have DmtPermission for the specified node. Method: DmtSession." + currentMethodName);
								} else {
									tbc.failExpectedOtherException(DmtException.class, exceptionReturned);
								}
							}
					}
					
				}
		    }
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.closeSession(session);
		}
	
	}
	/**
	 * This method asserts the specific DmtException. A single DmtException code can be thrown by
	 * different causes, so we use a single session (the DmtException is not fatal, so the session
	 * is not invalidated), get the default values for the parameters (it depends on how many and
	 * the types) and change (if necessary) the nodeUri (always the first one) to enclose different
	 * uris.  
	 * @param session The session to be used.
	 * @param method The method that must throw the DmtException
	 * @param code The code of DmtException being tested
	 */
	private void assertDmtException(DmtSession session,Method method,int code) {
        Object[] parameterValues = getDefaultValuesToParameters(method);

        String methodName = method.getName(); 
        
        switch (code) {
            case DmtException.INVALID_URI:
                tbc.log("#DmtSession." + methodName +" - " + DmtConstants.getDmtExceptionCodeText(code));
                for (int i=0;i<DmtTestControl.INVALID_URIS.length;i++) {
    			    //The first parameter is always the nodeUri. 
    			    parameterValues[0] = DmtTestControl.INVALID_URIS[i];
    				invokeMethodThrowsDmtException(session,method,parameterValues,DmtException.INVALID_URI);
    			}
                break;
    			
    			
            case DmtException.URI_TOO_LONG:
                for (int i=0;i<DmtTestControl.URIS_TOO_LONG.length;i++) {
    			    //The first parameter is always the nodeUri
    			    parameterValues[0] = DmtTestControl.URIS_TOO_LONG[i];
    				invokeMethodThrowsDmtException(session,method,parameterValues,DmtException.URI_TOO_LONG);
    			}
                break;
                
            case DmtException.PERMISSION_DENIED:
                parameterValues[0] = getValidUriForThisMethod(method.getName());
            case DmtException.COMMAND_FAILED:
                invokeMethodThrowsDmtException(session,method,parameterValues,code);
            	break;
        }
	    
	}
	
	
	/**
	 * Invokes a method by reflection. A specific DmtException must be thrown
	 * 
	 * @param session The session where the method will be invoked
	 * @param method The name of the DmtSession method 
	 * @param parameters Parameters 
	 * @param code The code of DmtException to be tested.
	 */
	private void invokeMethodThrowsDmtException(DmtSession session,Method method,Object[] parameters,int code) {
		String currentMethodName = method.getName();
		String dmtExceptionCodeName = DmtConstants.getDmtExceptionCodeText(code);
		try {
			method.invoke(session,parameters);
			tbc.failException("DmtException."+ dmtExceptionCodeName +" was not thrown in DmtSession." +
					currentMethodName ,DmtException.class);
			
		} catch (InvocationTargetException e) {
			
			Throwable exceptionReturned = e.getTargetException();
			
			if (exceptionReturned instanceof DmtException) {
				DmtException exception = (DmtException)exceptionReturned;
				tbc.assertEquals("Asserts that DmtSession." + currentMethodName + " throws DmtException."+ dmtExceptionCodeName +
				    " correctly. Node URI: \"" + parameters[0] + "\"",code,exception.getCode());
				
			} else {
			    tbc.failExpectedOtherException(DmtException.class, exceptionReturned);
			}
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
		
	}	
	
	/**
	 * Some methods need a nodeUri existent, others need a leaf node, etc. So, the nodeUri must be 
	 * different, according to the method.
	 *  
	 * @param methodName The method name
	 * @return A nodeUri valid for this method. 
	 */
	private String getValidUriForThisMethod(String methodName) {
	    if (methodName.equals("createInteriorNode")) {
	        return TestExecPluginActivator.INEXISTENT_NODE;
	        
	    } else if (methodName.equals("createLeafNode")) {
	        return TestExecPluginActivator.INEXISTENT_LEAF_NODE;
	        
	    } else if (methodName.equals("setDefaultNodeValue") 
	            || methodName.equals("getNodeValue") 
	            || methodName.equals("setNodeValue")
	            || methodName.equals("getNodeSize")) {
	        return TestExecPluginActivator.LEAF_NODE;
	        
	    } else {
	        return TestExecPluginActivator.INTERIOR_NODE;
	    }
	}
	
}

