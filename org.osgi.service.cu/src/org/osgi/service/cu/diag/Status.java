/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2005, 2006). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.cu.diag;


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
