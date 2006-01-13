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
 * Dez 06, 2005  Andre Assad
 * 267           Implement test cases for new classes
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc2.tbc.ResourceProcessorException;

import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.MessagesConstants;

/**
 * This Test Class Validates the implementation of
 * <code>ResourceProcessorException</code>, and the get methods:
 * <code>getCode, getMessage, getCause</code>. According to MEG reference
 * documentation.
 */
public class ResourceProcessorException {
    
    private DeploymentTestControl tbc;
    
    public ResourceProcessorException(DeploymentTestControl tbc) {
        this.tbc = tbc;
    }
    
    public void run() {
        testResourceProcessorException001();
        testResourceProcessorException002();
    }
    
    /**
     * Tests ResourceProcessorException constructor that receives two parameters
     * 
     * @spec ResourceProcessorException.ResourceProcessorException(int, String) 
     */
    private void testResourceProcessorException001() {
        tbc.log("#testResourceProcessorException001");
        try {
            org.osgi.service.deploymentadmin.spi.ResourceProcessorException e = new org.osgi.service.deploymentadmin.spi.ResourceProcessorException(
                org.osgi.service.deploymentadmin.spi.ResourceProcessorException.CODE_PREPARE, DeploymentConstants.EXCEPTION_MESSAGE);
            
            tbc.assertEquals("The code of the exception was correctly set",
                org.osgi.service.deploymentadmin.spi.ResourceProcessorException.CODE_PREPARE, e.getCode());
            tbc.assertEquals("The message of the exception is correctly set", DeploymentConstants.EXCEPTION_MESSAGE, e.getMessage());
            tbc.assertNull("The cause of the exception is correctly set to null", e.getCause());
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        }
    }
    
    /**
     * Tests ResourceProcessorException constructor that receives three parameters
     * 
     * @spec ResourceProcessorException.ResourceProcessorException(int, String, Throwable) 
     */
    private void testResourceProcessorException002() {
        tbc.log("#testResourceProcessorException002");
        try {
            Exception ex = new Exception("test");
            org.osgi.service.deploymentadmin.spi.ResourceProcessorException e = new org.osgi.service.deploymentadmin.spi.ResourceProcessorException(
                org.osgi.service.deploymentadmin.spi.ResourceProcessorException.CODE_PREPARE, DeploymentConstants.EXCEPTION_MESSAGE, ex);
            
            tbc.assertEquals("The code of the exception was correctly set",
                org.osgi.service.deploymentadmin.spi.ResourceProcessorException.CODE_PREPARE, e.getCode());
            tbc.assertEquals("The message of the exception is correctly set", DeploymentConstants.EXCEPTION_MESSAGE, e.getMessage());
            tbc.assertEquals("The cause of the exception is correctly set", ex, e.getCause());
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        }
    }
}
