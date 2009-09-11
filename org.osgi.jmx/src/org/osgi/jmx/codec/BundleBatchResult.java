/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.jmx.codec;

import static org.osgi.jmx.service.framework.FrameworkMBean.BUNDLE_COMPLETED;
import static org.osgi.jmx.service.framework.FrameworkMBean.BUNDLE_ERROR_MESSAGE;
import static org.osgi.jmx.service.framework.FrameworkMBean.BUNDLE_IN_ERROR;
import static org.osgi.jmx.service.framework.FrameworkMBean.BUNDLE_REMAINING;
import static org.osgi.jmx.service.framework.FrameworkMBean.BUNDLE_SUCCESS;

/**
 */
abstract public class BundleBatchResult {

	/**
	 * The item names in the CompositeData representing the result of a batch
	 * operation
	 */
	protected static final String[] BUNDLE_ACTION_RESULT = { BUNDLE_SUCCESS,
			BUNDLE_ERROR_MESSAGE, BUNDLE_COMPLETED, BUNDLE_IN_ERROR,
			BUNDLE_REMAINING };

	/**
	 * Answer the list of bundle identifiers that successfully completed the
	 * batch operation. If the operation was unsuccessful, this will be a
	 * partial list. If this operation was successful, this will be the full
	 * list of bundle ids. This list corresponds one to one with the supplied
	 * list of bundle locations provided to the batch install operations.
	 * 
	 * @return the list of identifiers of the bundles that successfully
	 *         installed
	 */
	public long[] getCompleted() {
		return completed;
	}

	/**
	 * Answer the error message indicating the error that occurred during the
	 * batch operation or null if the operation was successful
	 * 
	 * @return the String error message if the operation was unsuccessful, or
	 *         null if the operation was successful
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Answer true if the batch operation was successful, false otherwise.
	 * 
	 * @return the success of the batch operation
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * The list of bundles successfully completed
	 */
	protected long[] completed;
	/**
	 * The error message of a failed result
	 */
	protected String errorMessage;
	/**
	 * True if the action completed without error
	 */
	protected boolean success = true;

}
