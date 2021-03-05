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
package org.osgi.test.cases.resolver.junit;

import java.util.Collections;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.resource.Resource;
import org.osgi.service.resolver.ResolutionException;

/**
 * Testing the onCancel method on ResolveContext
 */
public class ResolveOnCancelTestCase extends AbstractResolverTestCase {

	/*
	 * Test that onCancel is called first and only once.
	 */
	public void testOnCancelCalledFirstAndOnce() {
		TestResolveContext context = getTestContext();
		shouldResolve(context);
		context.checkOnCancelCalled();
	}

	/*
	 * Test that invoking the runnable from onCancel after the resolve operation
	 * completes does not throw an exception
	 */
	public void testOnCancelCallAfterResolve() {
		TestResolveContext context = getTestContext();
		shouldResolve(context);
		context.cancel();
	}

	/*
	 * Test that invoking onCancel callback will terminate resolve process and
	 * produce the expected exception
	 */
	public void testOnCancelTerminatesResolution() throws InterruptedException {
		final TestResolveContext context = getTestContext();
		final AtomicReference<ResolutionException> resolveError = new AtomicReference<>();
		context.startWaiting();
		Thread bg;
		try {

			bg = new Thread(new Runnable() {
				@Override
				public void run() {
					resolveError.set(shouldNotResolve(context));
				}
			});
			bg.start();
			context.cancel();
		} finally {
			context.stopWaiting();
		}
		bg.join();
		assertNotNull("No resolution exception.", resolveError.get());
		assertTrue("Wrong cause type: " + resolveError.get(),
				resolveError.get().getCause() instanceof CancellationException);
	}

	private TestResolveContext getTestContext() {
		TestResource rootResource = new TestResource();
		rootResource.addIdentity("root", null, null);
		rootResource.addRequirement("foo", "(foo=true)");
		TestResource providerResource = new TestResource();
		providerResource.addCapability("foo", "foo=true");

		return new TestResolveContext(
				Collections.<Resource> singleton(rootResource), null,
				Collections.<Resource> singleton(providerResource));
	}
}
