/*
 * Copyright (c) OSGi Alliance (2012, 2016). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.resolver.junit;

import java.util.Collections;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReference;

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

			bg = new Thread(() -> {
				resolveError.set(shouldNotResolve(context));
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
				Collections.singleton(rootResource), null,
				Collections.singleton(providerResource));
	}
}
