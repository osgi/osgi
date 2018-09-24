/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.cdi.junit;

import static org.junit.Assert.*;

import javax.enterprise.inject.spi.BeanManager;

import org.junit.Test;
import org.osgi.framework.Bundle;

public class CdiExtenderTests extends AbstractTestCase {

	@Test
	public void testStopExtender() throws Exception {
		Bundle cdiExtenderBundle = getCdiExtenderBundle();

		try (CloseableTracker<BeanManager, BeanManager> tracker = trackBM(cdiBundle);) {
			BeanManager beanManager = tracker.waitForService(timeout);

			assertNotNull(beanManager);

			int trackingCount = tracker.getTrackingCount();

			cdiExtenderBundle.stop();

			for (int i = 10; (i > 0) && (tracker.getTrackingCount() == trackingCount); i--) {
				Thread.sleep(20);
			}

			beanManager = tracker.getService();

			assertNull(beanManager);

			trackingCount = tracker.getTrackingCount();

			cdiExtenderBundle.start();

			for (int i = 10; (i > 0) && (tracker.getTrackingCount() == trackingCount); i--) {
				Thread.sleep(20);
			}

			beanManager = tracker.getService();

			assertNotNull(beanManager);
		}
	}

}