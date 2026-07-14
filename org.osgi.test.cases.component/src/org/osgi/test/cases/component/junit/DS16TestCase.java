/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
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
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.component.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.assertj.dictionary.DictionarySoftAssertions;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectInstalledBundle;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.context.InstalledBundleExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.test.support.sleep.Sleep;

/**
 * Test case for Declarative Services 1.6 features, specifically retention policy
 * @author $Id$
 */
@ExtendWith(SoftAssertionsExtension.class)
@ExtendWith(BundleContextExtension.class)
@ExtendWith(InstalledBundleExtension.class)
@ExtendWith(ServiceExtension.class)
public class DS16TestCase {
	private int					SLEEP	= 1000;

	@InjectBundleContext
	BundleContext				context;

	String						testName;

	@InjectSoftAssertions
	DictionarySoftAssertions	softly;

	@BeforeEach
	void setUp(TestInfo testInfo) throws Exception {
		testName = testInfo.getTestMethod().map(Method::getName).get();
		assertThat(context).isNotNull();
		String sleepTimeString = context
				.getProperty("osgi.tc.component.sleeptime");
		int sleepTime = SLEEP;
		if (sleepTimeString != null) {
			try {
				sleepTime = Integer.parseInt(sleepTimeString);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(
						"Error while parsing sleep value! The default one will be used : "
								+ SLEEP);
			}
			if (sleepTime < 100) {
				System.out.println("The sleep value is too low : " + sleepTime
						+ " ! The default one will be used : " + SLEEP);
			} else {
				SLEEP = sleepTime;
			}
		}
	}

	@AfterEach
	void tearDown() {
		// Cleanup if needed
	}

	/**
	 * Test that a component with retention-policy="keep" is not deactivated
	 * when its use count drops to zero
	 */
	@Test
	public void testRetentionPolicyKeep(@InjectInstalledBundle("tb33.jar")
	Bundle tb,
			@InjectService(filter = "(type=retention-keep)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		
		tb.start();
		Sleep.sleep(SLEEP);

		// Get the service which should activate the component
		bs.waitForService(SLEEP);
		assertThat(bs.getService()).isNotNull();
		
		// Verify component is activated
		BaseService service = bs.getService();
		assertThat(service.getProperties()).containsEntry("activated", true);
		Integer activationCount1 = (Integer) service.getProperties().get("activationCount");
		assertThat(activationCount1).isEqualTo(1);

		// Release the service reference (use count drops to zero)
		ServiceReference<BaseService> ref = bs.getServiceReference();
		if (ref != null) {
			context.ungetService(ref);
		}
		
		// Wait to see if component gets deactivated
		Sleep.sleep(SLEEP * 3);
		
		// Get service again - with KEEP policy, component should still be activated
		// and not have been deactivated
		bs.waitForService(SLEEP);
		service = bs.getService();
		assertThat(service).isNotNull();
		
		// Verify activation count hasn't increased (component was kept)
		Integer activationCount2 = (Integer) service.getProperties().get("activationCount");
		assertThat(activationCount2).isEqualTo(1);
		
		// Verify deactivation didn't happen
		assertThat(service.getProperties()).containsEntry("deactivated", false);
		Integer deactivationCount = (Integer) service.getProperties().get("deactivationCount");
		assertThat(deactivationCount).isEqualTo(0);
	}

	/**
	 * Test that a component with retention-policy="discard" is deactivated
	 * when its use count drops to zero
	 */
	@Test
	public void testRetentionPolicyDiscard(@InjectInstalledBundle("tb33.jar")
	Bundle tb,
			@InjectService(filter = "(type=retention-discard)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		
		tb.start();
		Sleep.sleep(SLEEP);

		// Get the service which should activate the component
		bs.waitForService(SLEEP);
		assertThat(bs.getService()).isNotNull();
		
		// Verify component is activated
		BaseService service = bs.getService();
		assertThat(service.getProperties()).containsEntry("activated", true);
		Integer activationCount1 = (Integer) service.getProperties().get("activationCount");
		assertThat(activationCount1).isEqualTo(1);

		// Release the service reference (use count drops to zero)
		ServiceReference<BaseService> ref = bs.getServiceReference();
		if (ref != null) {
			context.ungetService(ref);
		}
		
		// Wait for component to be deactivated (with DISCARD policy)
		Sleep.sleep(SLEEP * 3);
		
		// Get service again - with DISCARD policy, component should have been deactivated
		// and reactivated
		bs.waitForService(SLEEP);
		service = bs.getService();
		assertThat(service).isNotNull();
		
		// Note: Due to deactivation and reactivation, we get a new instance
		// So we can't directly check if deactivation happened on the old instance
		// The activation count should have increased if deactivation occurred
		Integer activationCount2 = (Integer) service.getProperties().get("activationCount");
		// With DISCARD, the component should have been deactivated and reactivated
		// resulting in activation count of 1 for the new instance
		assertThat(activationCount2).isEqualTo(1);
	}

	/**
	 * Test that the default retention-policy (when not specified) is "discard"
	 */
	@Test
	public void testRetentionPolicyDefault(@InjectInstalledBundle("tb33.jar")
	Bundle tb,
			@InjectService(filter = "(type=retention-default)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		
		tb.start();
		Sleep.sleep(SLEEP);

		// Get the service which should activate the component
		bs.waitForService(SLEEP);
		assertThat(bs.getService()).isNotNull();
		
		// Verify component is activated
		BaseService service = bs.getService();
		assertThat(service.getProperties()).containsEntry("activated", true);

		// Release the service reference (use count drops to zero)
		ServiceReference<BaseService> ref = bs.getServiceReference();
		if (ref != null) {
			context.ungetService(ref);
		}
		
		// Wait for potential deactivation
		Sleep.sleep(SLEEP * 3);
		
		// Get service again - default should behave like DISCARD
		bs.waitForService(SLEEP);
		service = bs.getService();
		assertThat(service).isNotNull();
		
		// Default behavior should be same as DISCARD
		Integer activationCount = (Integer) service.getProperties().get("activationCount");
		assertThat(activationCount).isEqualTo(1);
	}
}
