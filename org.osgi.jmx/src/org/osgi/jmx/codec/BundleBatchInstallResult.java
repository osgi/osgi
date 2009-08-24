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

import static org.osgi.jmx.codec.Util.LONG_ARRAY_TYPE;
import static org.osgi.jmx.codec.Util.LongArrayFrom;
import static org.osgi.jmx.codec.Util.STRING_ARRAY_TYPE;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.osgi.jmx.framework.FrameworkMBean;

/**
 * <p>
 * This class represents the CODEC for the resulting composite data from the
 * batch install operations on the bundles in the <link>FrameworkMBean</link>.
 * It serves as both the documentation of the type structure and as the
 * codification of the mechanism to convert to/from the CompositeData.
 * <p>
 * The structure of the composite data is:
 * <table border="1">
 * <tr>
 * <td>Success</td>
 * <td>Boolean</td>
 * </tr>
 * <tr>
 * <td>Error</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>Completed</td>
 * <td>Array of long</td>
 * </tr>
 * <tr>
 * <td>BundleInError</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>Remaining</td>
 * <td>Array of String</td>
 * </tr>
 * </table>
 */
public class BundleBatchInstallResult {
	/**
	 * Construct a result representing the contents of the supplied
	 * CompositeData returned from a batch operation.
	 * 
	 * @param compositeData
	 *            - the CompositeData representing the result of a batch
	 *            operation.
	 */
	public BundleBatchInstallResult(CompositeData compositeData) {
	    success = ((Boolean) compositeData.get(FrameworkMBean.BUNDLE_SUCCESS)).booleanValue();
		errorMessage = (String) compositeData
				.get(FrameworkMBean.BUNDLE_ERROR_MESSAGE);
		Long[] c = (Long[]) compositeData.get(FrameworkMBean.BUNDLE_COMPLETED);
		if (c != null) {
			completed = new long[c.length];
			for (int i = 0; i < c.length; i++) {
				completed[i] = c[i];
			}
		} else {
			completed = new long[0];
		}
		bundleInError = (String) compositeData
				.get(FrameworkMBean.BUNDLE_IN_ERROR);
		remaining = (String[]) compositeData
				.get(FrameworkMBean.BUNDLE_REMAINING);
	}

	/**
	 * Construct a result signifying the successful completion of the batch
	 * operation.
	 * 
	 * @param completed
	 *            - the resulting bundle identifiers of the installed bundles
	 */
	public BundleBatchInstallResult(long[] completed) {
		success = true;
		this.completed = completed;
	}

	/**
	 * Construct a result indictating the failure of a batch operation.
	 * 
	 * @param errorMessage
	 *            - the message indicating the error
	 * @param completed
	 *            - the list of bundle identifiers indicating bundles that have
	 *            successfully completed the batch operation
	 * @param bundleInError
	 *            - the identifier of the bundle which produced the error
	 * @param remaining
	 *            - the list of bundle identifiers which remain unprocessed
	 */
	public BundleBatchInstallResult(String errorMessage, long[] completed,
			String bundleInError, String[] remaining) {
		success = false;
		this.errorMessage = errorMessage;
		this.completed = completed;
		this.bundleInError = bundleInError;
		this.remaining = remaining;
	}

	private static CompositeType createResultType() {
		String description = "This type encapsulates a bundle batch install action result";
		String[] itemNames = FrameworkMBean.BUNDLE_ACTION_RESULT;
		OpenType[] itemTypes = new OpenType[itemNames.length];
		String[] itemDescriptions = new String[itemNames.length];
		itemTypes[0] = SimpleType.BOOLEAN;
		itemTypes[1] = SimpleType.STRING;
		itemTypes[2] = LONG_ARRAY_TYPE;
		itemTypes[3] = SimpleType.STRING;
		itemTypes[4] = STRING_ARRAY_TYPE;

		itemDescriptions[0] = "Whether the operation was successful";
		itemDescriptions[1] = "The error message if unsuccessful";
		itemDescriptions[2] = "The bundle ids of the successfully completed installs";
		itemDescriptions[3] = "The location of the bundle causing the error";
		itemDescriptions[4] = "The locations of the remaining bundles";

		try {
			return new CompositeType(
					FrameworkMBean.BUNDLE_BATCH_INSTALL_RESULT, description,
					itemNames, itemDescriptions, itemTypes);
		} catch (OpenDataException e) {
			throw new IllegalStateException(
					"Unable to build bundle action result type", e);
		}

	}

	/**
	 * Answer the receiver encoded as CompositeData
	 * 
	 * @return the CompositeData encoding of the receiver.
	 */
	public CompositeData asCompositeData() {
		String[] itemNames = FrameworkMBean.BUNDLE_ACTION_RESULT;
		Object[] itemValues = new Object[itemNames.length];
		itemValues[0] = success;
		itemValues[1] = errorMessage;
		itemValues[2] = LongArrayFrom(completed);
		itemValues[3] = bundleInError;
		itemValues[4] = remaining;

		try {
			return new CompositeDataSupport(BATCH_RESULT, itemNames, itemValues);
		} catch (OpenDataException e) {
			throw new IllegalStateException(
					"Cannot form batch result open data", e);
		}
	}

	/**
	 * Answer the bundle location which indicates the bundle that produced an
	 * error during the batch operation.
	 * 
	 * @return the bundle location of the bundle in error, or null if no error
	 *         occurred
	 */
	public String getBundleInError() {
		return bundleInError;
	}

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
	 * Answer the list of locations of the bundles that were not processed
	 * during the batch operation, or null if the operation was successsful
	 * 
	 * @return the remaining bundle locations if the operation was successful,
	 *         or null if the operation was unsuccsesful.
	 */
	public String[] getRemaining() {
		return remaining;
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
	 * The CompositeType which represents the result of batch install operations
	 * on the <link>FrameworkMBean</link>
	 */
	public final static CompositeType BATCH_RESULT = createResultType();

	private String bundleInError;

	private long[] completed;

	private String errorMessage;

	private String[] remaining;

	private boolean success = true;
}
