/*
 * Copyright 2008 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.jmx.codec;

import static org.osgi.jmx.codec.Util.LONG_ARRAY_TYPE;
import static org.osgi.jmx.codec.Util.LongArrayFrom;

import javax.management.openmbean.*;

import org.osgi.jmx.framework.FrameworkMBean;

/**
 * This class represents the CODEC for the resulting composite data from the
 * batch operations on the bundles in the <link>FrameworkMBean</link>. It serves
 * as both the documentation of the type structure and as the codification of
 * the mechanism to convert to/from the CompositeData.
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
 * <td>long</td>
 * </tr>
 * <tr>
 * <td>Remaining</td>
 * <td>Array of long</td>
 * </tr>
 * </table>
 */
public class BundleBatchActionResult {

	/**
	 * Construct a result signifying the successful completion of the batch
	 * operation.
	 */
	public BundleBatchActionResult() {
		success = true;
	}

	/**
	 * Construct a result representing the contents of the supplied
	 * CompositeData returned from a batch operation.
	 * 
	 * @param compositeData
	 *            - the CompositeData representing the result of a batch
	 *            operation.
	 */
	public BundleBatchActionResult(CompositeData compositeData) {
		errorMessage = (String) compositeData
				.get(FrameworkMBean.BUNDLE_ERROR_MESSAGE);
		Long[] c = (Long[]) compositeData.get(FrameworkMBean.BUNDLE_COMPLETED);
		bundleInError = (Long) compositeData
				.get(FrameworkMBean.BUNDLE_IN_ERROR);
		if (c != null) {
			completed = new long[c.length];
			for (int i = 0; i < c.length; i++) {
				completed[i] = c[i];
			}
		} else {
			completed = new long[0];
		}
		c = (Long[]) compositeData.get(FrameworkMBean.BUNDLE_REMAINING);
		if (c != null) {
			remaining = new long[c.length];
			for (int i = 0; i < c.length; i++) {
				remaining[i] = c[i];
			}
		} else {
			remaining = new long[0];
		}
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
	public BundleBatchActionResult(String errorMessage, long[] completed,
			long bundleInError, long[] remaining) {
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
		itemTypes[3] = SimpleType.LONG;
		itemTypes[4] = LONG_ARRAY_TYPE;

		itemDescriptions[0] = "Whether the operation was successful";
		itemDescriptions[1] = "The error message if unsuccessful";
		itemDescriptions[2] = "The bundle ids of the successfully completed installs";
		itemDescriptions[3] = "The id of the bundle causing the error";
		itemDescriptions[4] = "The ids of the remaining bundles";

		try {
			return new CompositeType(FrameworkMBean.BUNDLE_BATCH_ACTION_RESULT,
					description, itemNames, itemDescriptions, itemTypes);
		} catch (OpenDataException e) {
			throw new IllegalStateException(
					"Unable to build bundle batch action result type", e);
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
		itemValues[4] = LongArrayFrom(remaining);

		try {
			return new CompositeDataSupport(RESULT, itemNames, itemValues);
		} catch (OpenDataException e) {
			throw new IllegalStateException(
					"Cannot form batch result open data", e);
		}
	}

	/**
	 * Answer the bundle identifier which indicates the bundle that produced an
	 * error during the batch operation.
	 * 
	 * @return the bundle identifier of the bundle in error, or -1L if no error
	 *         occurred
	 */
	public long getBundleInError() {
		return bundleInError;
	}

	/**
	 * If the operation failed, answer the list of bundle identifiers that
	 * successfully completed the batch operation. If the operation was
	 * successful, then the list is null;
	 * 
	 * @return the list of bundle identifiers or null if the operation was
	 *         successful
	 */
	public long[] getCompleted() {
		return completed;
	}

	/**
	 * Answer the error message indicating the error that occurred during the
	 * batch operation or null, if the operation was a success.
	 * 
	 * @return the String error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * If the operation was unsuccessful, answer the list of bundle identifiers
	 * of the bundles that were not processed during the batch operation. If the
	 * operation was a success, then answer null
	 * 
	 * @return the remaining bundle identifiers or null if the operation was a
	 *         success
	 */
	public long[] getRemaining() {
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
	 * The CompositeType which represents the result of batch operations on the
	 * <link>FrameworkMBean</link>
	 */
	public final static CompositeType RESULT = createResultType();

	private long bundleInError;

	private long[] completed;

	private String errorMessage;

	private long[] remaining;

	private boolean success = true;
}
