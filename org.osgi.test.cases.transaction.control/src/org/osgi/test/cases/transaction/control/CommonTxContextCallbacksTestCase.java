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
package org.osgi.test.cases.transaction.control;

import static java.util.Arrays.asList;
import static org.osgi.service.transaction.control.TransactionStatus.ACTIVE;
import static org.osgi.service.transaction.control.TransactionStatus.COMMITTED;
import static org.osgi.service.transaction.control.TransactionStatus.COMMITTING;
import static org.osgi.service.transaction.control.TransactionStatus.MARKED_ROLLBACK;
import static org.osgi.service.transaction.control.TransactionStatus.NO_TRANSACTION;
import static org.osgi.service.transaction.control.TransactionStatus.ROLLED_BACK;
import static org.osgi.service.transaction.control.TransactionStatus.ROLLING_BACK;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionException;
import org.osgi.service.transaction.control.TransactionRolledBackException;
import org.osgi.service.transaction.control.TransactionStatus;
import org.osgi.test.cases.transaction.control.resources.EmptyLocalResource;
import org.osgi.test.cases.transaction.control.resources.EmptyXAResource;

/**
 * This test case runs against both local and XA Transaction Control services
 * testing the TransactionContext lifecycle callbacks as defined in 147.4.1
 * 
 * @author $Id$
 */
public class CommonTxContextCallbacksTestCase extends TxControlTestCase {
	
	/**
	 * 147.4.1 PreCompletion and PostCompletion callbacks
	 */
	public void testSuccessfulCallbackOrdering() {

		final List<TransactionStatus> list = new ArrayList<>();

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				txControl.getCurrentContext().preCompletion(new Runnable() {
					@Override
					public void run() {
						list.add(txControl.getCurrentContext()
								.getTransactionStatus());
					}
				});

				txControl.getCurrentContext()
						.postCompletion(new Consumer<TransactionStatus>() {
							@Override
							public void accept(TransactionStatus t) {
								list.add(t);
							}
						});

				return null;
			}
		});

		assertEquals(asList(ACTIVE, COMMITTED), list);
	}

	/**
	 * 147.4.1 PreCompletion and PostCompletion callbacks
	 */
	public void testSuccessfulCallbackOrderingNoTransaction() {

		final List<Integer> list = new ArrayList<>();

		txControl.supports(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				txControl.getCurrentContext().preCompletion(new Runnable() {
					@Override
					public void run() {
						list.add(txControl.getCurrentContext()
								.getTransactionStatus() == NO_TRANSACTION ? 1
										: 0);
					}
				});

				txControl.getCurrentContext()
						.postCompletion(new Consumer<TransactionStatus>() {
							@Override
							public void accept(TransactionStatus t) {
								list.add(t == NO_TRANSACTION ? 3 : 2);
							}
						});

				return null;
			}
		});

		assertEquals(asList(1, 3), list);
	}

	/**
	 * 147.4.1 PreCompletion registers a PreCompletion callback
	 */
	public void testLateRegisterOfPreCompletion() {

		final List<TransactionStatus> list = new ArrayList<>();

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				txControl.getCurrentContext().preCompletion(new Runnable() {
					@Override
					public void run() {
						try {
							txControl.getCurrentContext()
									.preCompletion(new Runnable() {
										@Override
										public void run() {
											// This should never
											// happen so
											// Add something
											// invalid to force
											// a failure
											list.add(NO_TRANSACTION);
										}
									});
							fail("This should never be reached");
						} catch (IllegalStateException ise) {}
						list.add(txControl.getCurrentContext()
								.getTransactionStatus());
					}
				});

				return null;
			}
		});

		assertEquals(asList(ACTIVE), list);
	}

	/**
	 * 147.4.1 PreCompletion registers a PostCompletion callback
	 */
	public void testLateRegisterOfPostCompletion() {

		final List<TransactionStatus> list = new ArrayList<>();

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				txControl.getCurrentContext().preCompletion(new Runnable() {
					@Override
					public void run() {
						list.add(txControl.getCurrentContext()
								.getTransactionStatus());
						txControl.getCurrentContext().postCompletion(
								new Consumer<TransactionStatus>() {
									@Override
									public void accept(TransactionStatus t) {
										list.add(t);
									}
								});
					}
				});

				return null;
			}
		});

		assertEquals(asList(ACTIVE, COMMITTED), list);
	}

	/**
	 * 147.4.1 PreCompletion registers a PostCompletion callbacks
	 */
	public void testLateRegisterOfPostCompletionNoTransaction() {

		final List<Integer> list = new ArrayList<>();

		txControl.supports(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				txControl.getCurrentContext().preCompletion(new Runnable() {
					@Override
					public void run() {
						list.add(txControl.getCurrentContext()
								.getTransactionStatus() == NO_TRANSACTION ? 1
										: 0);
						txControl.getCurrentContext().postCompletion(
								new Consumer<TransactionStatus>() {
									@Override
									public void accept(TransactionStatus t) {
										list.add(t == NO_TRANSACTION ? 3 : 2);
									}
								});
					}
				});

				return null;
			}
		});

		assertEquals(asList(1, 3), list);
	}
	
	/**
	 * 147.4.1 Resource registers a PostCompletion callback
	 */
	public void testLateRegisterOfPostCompletionFromCommittingLocalResource() {

		final List<TransactionStatus> list = new ArrayList<>();

		boolean testSupported = txControl.required(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {

				if (txControl.getCurrentContext().supportsLocal()) {
					txControl.getCurrentContext()
							.registerLocalResource(new EmptyLocalResource() {

								@Override
								public void commit()
										throws TransactionException {
									list.add(txControl.getCurrentContext()
											.getTransactionStatus());

									txControl.getCurrentContext()
											.postCompletion(
													new Consumer<TransactionStatus>() {
														@Override
														public void accept(
																TransactionStatus t) {
															list.add(t);
														}
													});
								}
							});

					return true;
				}
				return false;

			}
		});

		if (testSupported) {
			assertEquals(asList(COMMITTING, COMMITTED), list);
		}
	}

	/**
	 * 147.4.1 Resource registers a PostCompletion callback
	 */
	public void testLateRegisterOfPostCompletionFromRollingBackLocalResource() {
		
		final List<TransactionStatus> list = new ArrayList<>();
		
		boolean testSupported = txControl.required(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				
				if (txControl.getCurrentContext().supportsLocal()) {
					
					txControl.getCurrentContext()
							.registerLocalResource(new EmptyLocalResource() {
						
								@Override
								public void rollback()
										throws TransactionException {
									list.add(txControl.getCurrentContext()
											.getTransactionStatus());

									txControl.getCurrentContext()
											.postCompletion(
													new Consumer<TransactionStatus>() {
														@Override
														public void accept(
																TransactionStatus t) {
															list.add(t);
														}
													});
								}
							});

					txControl.setRollbackOnly();
					return true;
				} else {
					return false;
				}

			}
		});

		if (testSupported) {
			assertEquals(asList(ROLLING_BACK, ROLLED_BACK), list);
		}
	}

	/**
	 * 147.4.1 Resource registers a PostCompletion callback
	 */
	public void testLateRegisterOfPostCompletionFromCommittingXAResource() {

		final List<TransactionStatus> list = new ArrayList<>();

		boolean testSupported = txControl.required(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {

				if (txControl.getCurrentContext().supportsXA()) {

					txControl.getCurrentContext()
							.registerXAResource(new EmptyXAResource() {

								@Override
								public void commit(Xid xid, boolean onePhase)
										throws XAException {
									list.add(txControl.getCurrentContext()
											.getTransactionStatus());

									txControl.getCurrentContext()
											.postCompletion(
													new Consumer<TransactionStatus>() {
														@Override
														public void accept(
																TransactionStatus t) {
															list.add(t);
														}
													});
								}
							}, null);

					return true;
				} else {
					return false;
				}

			}
		});

		if (testSupported) {
			assertEquals(asList(COMMITTING, COMMITTED), list);
		}
	}

	/**
	 * 147.4.1 Resource registers a PostCompletion callback
	 */
	public void testLateRegisterOfPostCompletionFromRollingBackXAResource() {

		final List<TransactionStatus> list = new ArrayList<>();

		boolean testSupported = txControl.required(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {

				if (txControl.getCurrentContext().supportsXA()) {
					txControl.getCurrentContext()
							.registerXAResource(new EmptyXAResource() {

								@Override
								public void rollback(Xid id)
										throws XAException {
									list.add(txControl.getCurrentContext()
											.getTransactionStatus());

									txControl.getCurrentContext()
											.postCompletion(
													new Consumer<TransactionStatus>() {
														@Override
														public void accept(
																TransactionStatus t) {
															list.add(t);
														}
													});
								}
							}, null);
					
					txControl.setRollbackOnly();
					return true;
				}
				return false;
			}
		});

		if (testSupported) {
			assertEquals(asList(ROLLING_BACK, ROLLED_BACK), list);
		}
	}

	/**
	 * 147.4.1 Resource registers a PostCompletion callback
	 */
	public void testLateRegisterOfPostCompletionFromPostCompletion() {

		final List<TransactionStatus> list = new ArrayList<>();

		boolean testSupported = txControl.required(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {

				if (txControl.getCurrentContext().supportsLocal()) {

					txControl.getCurrentContext()
							.postCompletion(new Consumer<TransactionStatus>() {
								@Override
								public void accept(TransactionStatus t) {
									try {
										txControl.getCurrentContext()
												.postCompletion(
														new Consumer<TransactionStatus>() {

															@Override
															public void accept(
																	TransactionStatus t) {
																// This should
																// never
																// happen so
																// Add something
																// invalid to
																// force
																// a failure
																list.add(
																		NO_TRANSACTION);
															}

														});
										fail("Should not be able to add a post callback from a post callback");
									} catch (IllegalStateException ise) {}

									list.add(t);
								}
							});

					return true;
				}
				return false;
			}
		});

		if (testSupported) {
			assertEquals(asList(COMMITTED), list);
		}
	}

	/**
	 * 147.4.1.1 PreCompletion setRollbackOnly
	 */
	public void testSetRollbackOnlyFromPreCompletion() {

		final List<TransactionStatus> list = new ArrayList<>();

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				txControl.getCurrentContext().preCompletion(new Runnable() {
					@Override
					public void run() {
						txControl.setRollbackOnly();
						list.add(txControl.getCurrentContext()
								.getTransactionStatus());
					}
				});

				txControl.getCurrentContext()
						.postCompletion(new Consumer<TransactionStatus>() {
							@Override
							public void accept(TransactionStatus t) {
								list.add(t);
							}
						});

				return null;
			}
		});

		assertEquals(asList(MARKED_ROLLBACK, ROLLED_BACK), list);
	}

	/**
	 * 147.4.1.1 PreCompletion throw RuntimeException
	 */
	public void testPreCompletionThrowsException() {

		final List<TransactionStatus> list = new ArrayList<>();
		final RuntimeException re = new RuntimeException("Bang");

		try {
			txControl.required(new Callable<Object>() {
				@Override
				public Object call() throws Exception {

					txControl.getCurrentContext().preCompletion(new Runnable() {
						@Override
						public void run() {
							list.add(txControl.getCurrentContext()
									.getTransactionStatus());
							throw re;
						}
					});

					txControl.getCurrentContext()
							.postCompletion(new Consumer<TransactionStatus>() {
								@Override
								public void accept(TransactionStatus t) {
									list.add(t);
								}
							});

					return null;
				}
			});
		} catch (TransactionRolledBackException swe) {
			assertSame(re, swe.getCause());
		}

		assertEquals(asList(ACTIVE, ROLLED_BACK), list);
	}

	/**
	 * 147.4.1.1 precompletion should not hide a ScopedWorkException
	 */
	public void testPreCompletionThrowsExceptionSuppressed() {

		final RuntimeException re = new RuntimeException("Bang");
		final RuntimeException workEx = new RuntimeException("I win!");

		try {
			txControl.required(new Callable<Object>() {
				@Override
				public Object call() throws Exception {

					txControl.getCurrentContext().preCompletion(new Runnable() {
						@Override
						public void run() {
							throw re;
						}
					});

					throw workEx;
				}
			});
			fail("Should not exit normally");
		} catch (ScopedWorkException swe) {
			assertSame(workEx, swe.getCause());
			Throwable[] suppressed = swe.getSuppressed();
			assertNotNull(suppressed);
			assertEquals(1, suppressed.length);
			assertSame(re, suppressed[0]);
		}
	}

	/**
	 * 147.4.1.1 precompletion should cause a TransactionException for
	 * NO_TRANSACTION scope
	 */
	public void testPreCompletionThrowsExceptionNoTransaction() {

		final RuntimeException re = new RuntimeException("Bang");

		try {
			txControl.supports(new Callable<Object>() {
				@Override
				public Object call() throws Exception {

					txControl.getCurrentContext().preCompletion(new Runnable() {
						@Override
						public void run() {
							throw re;
						}
					});

					return null;
				}
			});
			fail("Should not exit normally");
		} catch (TransactionException te) {
			assertSame(re, te.getCause());
		}
	}

	/**
	 * 147.4.1.1 precompletion should not hide a ScopedWorkException
	 */
	public void testPreCompletionThrowsExceptionSuppressedNoTransaction() {

		final RuntimeException re = new RuntimeException("Bang");
		final RuntimeException workEx = new RuntimeException("I win!");

		try {
			txControl.supports(new Callable<Object>() {
				@Override
				public Object call() throws Exception {

					txControl.getCurrentContext().preCompletion(new Runnable() {
						@Override
						public void run() {
							throw re;
						}
					});

					throw workEx;
				}
			});
			fail("Should not exit normally");
		} catch (ScopedWorkException swe) {
			assertSame(workEx, swe.getCause());
			Throwable[] suppressed = swe.getSuppressed();
			assertNotNull(suppressed);
			assertEquals(1, suppressed.length);
			assertSame(re, suppressed[0]);
		}
	}

	/**
	 * 147.4.1.2 postcompletion exception should not result in an error
	 */
	public void testPostCompletionFailure() {

		final RuntimeException re = new RuntimeException("Bang");

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				txControl.getCurrentContext()
						.postCompletion(new Consumer<TransactionStatus>() {
							@Override
							public void accept(TransactionStatus t) {
								throw re;
							}
						});

				return null;
			}
		});
	}

	/**
	 * 147.4.1.2 postcompletion should have access to scoped variables
	 */
	public void testPostCompletionCanAccessScopedVariable() {

		final AtomicInteger marker = new AtomicInteger();

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				txControl.getCurrentContext().putScopedValue(Object.class, 5);

				txControl.getCurrentContext()
						.postCompletion(new Consumer<TransactionStatus>() {
							@Override
							public void accept(TransactionStatus t) {
								marker.set(
										(Integer) txControl.getCurrentContext()
												.getScopedValue(Object.class));
							}
						});

				return null;
			}
		});

		assertEquals(5, marker.get());
	}
}
