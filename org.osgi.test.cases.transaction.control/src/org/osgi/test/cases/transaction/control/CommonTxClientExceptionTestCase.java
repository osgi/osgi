/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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
package org.osgi.test.cases.transaction.control;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.transaction.xa.XAException;

import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionException;
import org.osgi.service.transaction.control.TransactionRolledBackException;
import org.osgi.test.cases.transaction.control.resources.PoisonLocalResource;
import org.osgi.test.cases.transaction.control.resources.PoisonXAResource;

/**
 * This test case runs against both local and XA Transaction Control services
 * testing the Exception Management as defined in 147.3.2
 * 
 * @author $Id$
 */
public class CommonTxClientExceptionTestCase extends TxControlTestCase {
	
	/**
	 * 147.3.2.1 Client throws a {@link RuntimeException}
	 */
	public void testClientRuntimeException() {

		final IllegalArgumentException iae = new IllegalArgumentException(
				"Testing");

		try {
			txControl.required(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					throw iae;
				}
			});
			fail("Should fail with a ScopedWorkException");
		} catch (ScopedWorkException swe) {
			assertSame(iae, swe.getCause());
		}
	}

	/**
	 * 147.3.2.1 Client throws a checked {@link Exception}
	 */
	public void testClientCheckedException() {
		
		final IOException ioe = new IOException("Testing");
		
		try {
			txControl.required(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					throw ioe;
				}
			});
			fail("Should fail with a ScopedWorkException");
		} catch (ScopedWorkException swe) {
			assertSame(ioe, swe.getCause());
		}
	}

	/**
	 * 147.3.2.1 Client throws an exception, then the second scope throws a
	 * {@link ScopedWorkException}
	 */
	public void testScopedWorkExceptionNesting() {

		final IOException ioe = new IOException("Testing");

		try {
			txControl.required(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					return txControl.requiresNew(new Callable<Object>() {
						@Override
						public Object call() throws Exception {
							throw ioe;
						}
					});
				}
			});
			fail("Should fail with a ScopedWorkException");
		} catch (ScopedWorkException swe) {
			assertSame(ioe, swe.getCause());
			Throwable[] suppressed = swe.getSuppressed();
			assertNotNull(suppressed);
			assertEquals(1, suppressed.length);
			assertTrue(suppressed[0] instanceof ScopedWorkException);
			assertSame(ioe, suppressed[0].getCause());
		}
	}

	/**
	 * 147.3.2.2 Client rethrows a {@link RuntimeException}
	 */
	public void testRethrowRuntimeException() {

		final IllegalArgumentException iae = new IllegalArgumentException(
				"Testing");

		try {
			try {
				txControl.required(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						throw iae;
					}
				});
				fail("Should fail with a ScopedWorkException");
			} catch (ScopedWorkException swe) {
				throw swe.asRuntimeException();
			}
			fail("Should fail with the original exception");
		} catch (IllegalArgumentException e) {
			assertSame(iae, e);
		}
	}

	/**
	 * 147.3.2.2 Client rethrows a {@link RuntimeException} but saying it might
	 * be something else.
	 */
	public void testRethrowRuntimeExceptionWhenUsingAs() {

		final IllegalArgumentException iae = new IllegalArgumentException(
				"Testing");

		try {
			try {
				txControl.required(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						throw iae;
					}
				});
				fail("Should fail with a ScopedWorkException");
			} catch (ScopedWorkException swe) {
				swe.as(IOException.class);
			}
			fail("Should fail with the original exception");
		} catch (Exception e) {
			assertSame(iae, e);
		}
	}

	/**
	 * 147.3.2.2 Client rethrows a {@link RuntimeException} but saying it might
	 * be something else.
	 */
	public void testRethrowRuntimeExceptionWhenUsingAsOneOF() {

		final IllegalArgumentException iae = new IllegalArgumentException(
				"Testing");

		try {
			try {
				txControl.required(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						throw iae;
					}
				});
				fail("Should fail with a ScopedWorkException");
			} catch (ScopedWorkException swe) {
				swe.asOneOf(IOException.class, ClassNotFoundException.class);
			}
			fail("Should fail with the original exception");
		} catch (Exception e) {
			assertSame(iae, e);
		}
	}

	/**
	 * 147.3.2.2 Client rethrows a {@link RuntimeException}
	 */
	public void testRethrowCheckedException() {

		final IOException ioe = new IOException("Testing");

		try {
			try {
				txControl.required(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						throw ioe;
					}
				});
				fail("Should fail with a ScopedWorkException");
			} catch (ScopedWorkException swe) {
				swe.as(IOException.class);
			}
			fail("Should fail with the original exception");
		} catch (IOException e) {
			assertSame(ioe, e);
		}
	}

	/**
	 * 147.3.2.2 Client rethrows a checked {@link Exception} but saying it might
	 * be something else.
	 */
	public void testRethrowCheckedExceptionWhenUsingAsWrongType() {

		final IOException ioe = new IOException("Testing");

		try {
			try {
				txControl.required(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						throw ioe;
					}
				});
				fail("Should fail with a ScopedWorkException");
			} catch (ScopedWorkException swe) {
				throw swe.as(ClassNotFoundException.class);
			}
			fail("Should fail with the original exception");
		} catch (Exception e) {
			assertSame(ioe, e);
		}
	}

	/**
	 * 147.3.2.3 and table 147.3 - An error in local committing must trigger a
	 * TransactionRolledBackException
	 */
	public void testTransactionExceptionNotWrappedLocal() {

		try {
			boolean testSupported = txControl.required(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {

					if (txControl.getCurrentContext().supportsLocal()) {
						txControl.getCurrentContext()
								.registerLocalResource(new PoisonLocalResource(
										new TransactionException("Kaboom")));
						return true;
					}
					return false;
				}
			});

			if (!testSupported) {
				return;
			}

			fail("Should fail with a TransactionRolledBackException");
		} catch (Exception e) {
			assertTrue(e instanceof TransactionRolledBackException);
		}
	}

	/**
	 * 147.3.2.3 and 147.4.6.1 - An error in XA committing must trigger a
	 * TransactionRolledBackException
	 */
	public void testTransactionExceptionNotWrappedXA() {

		try {
			boolean testSupported = txControl.required(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {

					if (txControl.getCurrentContext().supportsXA()) {
						txControl.getCurrentContext().registerXAResource(
								new PoisonXAResource(new XAException("Kaboom")),
								null);
						return true;
					}
					return false;
				}
			});

			if (!testSupported) {
				return;
			}

			fail("Should fail with a TransactionRolledBackException");
		} catch (Exception e) {
			assertTrue(e instanceof TransactionRolledBackException);
		}
	}

	/**
	 * 147.3.2.3 and table 147.3 - An error in local committing must trigger a
	 * TransactionRolledBackException
	 */
	public void testTransactionExceptionSuppressedLocal() {

		try {
			boolean supportsTest = txControl.required(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {

					if (txControl.getCurrentContext().supportsLocal()) {

						txControl.getCurrentContext()
								.registerLocalResource(new PoisonLocalResource(
										new TransactionException("Kaboom")));
						throw new Exception("Bang");
					} else {
						return false;
					}
				}
			});

			if (!supportsTest) {
				return;
			}

			fail("Should fail with a ScopedWorkException");
		} catch (Exception e) {
			assertTrue(e instanceof ScopedWorkException);
			Throwable[] suppressed = e.getSuppressed();
			assertNotNull(suppressed);
			assertEquals(1, suppressed.length);
			assertTrue(suppressed[0] instanceof TransactionException);
		}
	}

	/**
	 * 147.3.2.3 and 147.4.6.1 - An error in XA committing must trigger a
	 * TransactionRolledBackException
	 */
	public void testTransactionExceptionSuppressedXA() {

		try {
			boolean testSupported = txControl.required(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {

					if (txControl.getCurrentContext().supportsXA()) {
						txControl.getCurrentContext().registerXAResource(
								new PoisonXAResource(new XAException("Kaboom")),
								null);
						throw new Exception("Bang");
					} else {
						return false;
					}
				}
			});

			if (!testSupported) {
				return;
			}

			fail("Should fail with a ScopedWorkException");
		} catch (Exception e) {
			assertTrue(e instanceof ScopedWorkException);
			Throwable[] suppressed = e.getSuppressed();
			assertNotNull(suppressed);
			assertEquals(1, suppressed.length);
			assertTrue(suppressed[0].getMessage(),
					suppressed[0] instanceof TransactionException);
		}
	}
}
