/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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
 */

package org.osgi.service.cu;


/**
 * Indicates the return status of a selt-test performed over a Device by calling method checkStatus(). 
 * The status value can be: STATUS_OK or STATUS_FAILED.
 * In case of STATUS_FAILED, the getError() method returns the error that occured.
 *  
 * @version $Revision$
  */
public interface Status 
{
   /**
 	* Indicates that the method was successfully performed. 
 	*/
   	public static int STATUS_OK = 1;
   	
   /**
 	* Indicates that the method was not successfully performed. 
 	*/
   	public static int STATUS_FAILED = 2;
   
   /**
    * Returns the method status after invocation. The status can be one of the
    * following values: STATUS_OK or STATUS_FAILED.
    * 
    * @return The method status after invocation
   	*/
   	public byte getStatus();
   
   /**
    * Returns the error code if the status is STATUS_FAILED.
    * 
    * @return The error code if the status is STATUS_FAILED, -1 if there is no error code.
   	*/
   	public int getError();
   
   /**
    * Returns the additional information on the status. The error message
    * can contain the actionID that was not successful.
    * 
    * @return The error message or null if there is no message.
   	*/
   	public String getMessage();
}
