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

package org.osgi.impl.bundle.jmx.framework.codec;

import static org.osgi.impl.bundle.jmx.codec.Util.LongArrayFrom;

import java.util.HashMap;
import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;

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
public class BundleBatchActionResult extends BundleBatchResult {

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
	@SuppressWarnings("boxing")
	public BundleBatchActionResult(CompositeData compositeData) {
		success = ((Boolean) compositeData.get(FrameworkMBean.SUCCESS))
				.booleanValue();
		errorMessage = (String) compositeData.get(FrameworkMBean.ERROR);
		Long[] c = (Long[]) compositeData.get(FrameworkMBean.COMPLETED);
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
		c = (Long[]) compositeData.get(FrameworkMBean.REMAINING);
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

	/**
	 * Answer the receiver encoded as CompositeData
	 * 
	 * @return the CompositeData encoding of the receiver.
	 */
	@SuppressWarnings("boxing")
	public CompositeData asCompositeData() {
		Map<String, Object> items = new HashMap<String, Object>();
		items.put(FrameworkMBean.SUCCESS, success);
		items.put(FrameworkMBean.ERROR, errorMessage);
		items.put(FrameworkMBean.COMPLETED, LongArrayFrom(completed));
		items.put(FrameworkMBean.BUNDLE_IN_ERROR, bundleInError);
		items.put(FrameworkMBean.REMAINING, LongArrayFrom(remaining));

		try {
			return new CompositeDataSupport(
					FrameworkMBean.BATCH_ACTION_RESULT_TYPE, items);
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

	private long bundleInError;

	private long[] remaining;
}
