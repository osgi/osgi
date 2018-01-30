/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
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

import static org.junit.Assert.assertNotEquals;
import static org.osgi.service.transaction.control.TransactionStatus.*;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionContext;
import org.osgi.service.transaction.control.TransactionException;

/**
 * This test case runs against both local and XA Transaction Control services
 * testing the Scope lifecycle as defined in 147.3.1 and 147.3.3
 * 
 * @author $Id$
 */
public class CommonTxLifecycleTestCase extends TxControlTestCase {
	
	/**
	 * 147.3.1 Required starts a Transaction scope from unscoped
	 */
	public void testRequiredFromUnscoped() {

		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());
		assertNull(txControl.getCurrentContext());

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				assertTrue(txControl.activeScope());
				assertTrue(txControl.activeTransaction());

				TransactionContext current = txControl.getCurrentContext();
				assertNotNull(current);
				assertEquals(ACTIVE, current.getTransactionStatus());
				return null;
			}
		});
	}

	/**
	 * 147.3.1 Required starts a Transaction scope from no transaction
	 */
	public void testRequiredFromNoTransaction() {
		
		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());
		assertNull(txControl.getCurrentContext());
		
		txControl.notSupported(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				assertTrue(txControl.activeScope());
				assertFalse(txControl.activeTransaction());
				final TransactionContext outer = txControl.getCurrentContext();
				assertNotNull(outer);
				assertEquals(NO_TRANSACTION, outer.getTransactionStatus());
				assertNull(outer.getTransactionKey());

				TransactionContext nested = txControl
						.required(new Callable<TransactionContext>() {
							@Override
							public TransactionContext call() throws Exception {
								assertTrue(txControl.activeScope());
								assertTrue(txControl.activeTransaction());

								TransactionContext current = txControl
										.getCurrentContext();
								assertNotNull(current);
								assertEquals(ACTIVE,
										current.getTransactionStatus());

								assertNotEquals(outer.getTransactionKey(),
										current.getTransactionKey());

								return current;
							}
				});

				assertEquals(COMMITTED, nested.getTransactionStatus());

				assertSame(outer, txControl.getCurrentContext());
				assertEquals(NO_TRANSACTION, outer.getTransactionStatus());

				return null;
			}
		});
	}

	/**
	 * 147.3.1 Required inherits a Transaction scope from transaction scope
	 */
	public void testRequiredFromTransaction() {

		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());
		assertNull(txControl.getCurrentContext());

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				assertTrue(txControl.activeScope());
				assertTrue(txControl.activeTransaction());
				final TransactionContext outer = txControl.getCurrentContext();
				assertNotNull(outer);
				assertEquals(ACTIVE, outer.getTransactionStatus());

				txControl.required(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						assertTrue(txControl.activeScope());
						assertTrue(txControl.activeTransaction());

						TransactionContext current = txControl
								.getCurrentContext();
						assertNotNull(current);
						assertEquals(ACTIVE, current.getTransactionStatus());

						assertEquals(outer.getTransactionKey(),
								current.getTransactionKey());

						return null;
					}
				});

				assertSame(outer, txControl.getCurrentContext());
				assertEquals(ACTIVE, outer.getTransactionStatus());

				return null;
			}
		});
	}

	/**
	 * 147.3.1 RequiresNew starts a Transaction scope from unscoped
	 */
	public void testRequiresNewFromUnscoped() {

		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());
		assertNull(txControl.getCurrentContext());

		txControl.requiresNew(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				assertTrue(txControl.activeScope());
				assertTrue(txControl.activeTransaction());

				TransactionContext current = txControl.getCurrentContext();
				assertNotNull(current);
				assertEquals(ACTIVE, current.getTransactionStatus());
				return null;
			}
		});
	}

	/**
	 * 147.3.1 RequiresNew starts a Transaction scope from no transaction
	 */
	public void testRequiresNewFromNoTransaction() {

		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());
		assertNull(txControl.getCurrentContext());

		txControl.notSupported(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				assertTrue(txControl.activeScope());
				assertFalse(txControl.activeTransaction());
				final TransactionContext outer = txControl.getCurrentContext();
				assertNotNull(outer);
				assertEquals(NO_TRANSACTION, outer.getTransactionStatus());
				assertNull(outer.getTransactionKey());

				TransactionContext nested = txControl
						.requiresNew(new Callable<TransactionContext>() {
					@Override
							public TransactionContext call() throws Exception {
						assertTrue(txControl.activeScope());
						assertTrue(txControl.activeTransaction());

						TransactionContext current = txControl
								.getCurrentContext();
						assertNotNull(current);
						assertEquals(ACTIVE, current.getTransactionStatus());

						assertNotEquals(outer.getTransactionKey(),
								current.getTransactionKey());

								return current;
					}
				});

				assertEquals(COMMITTED, nested.getTransactionStatus());

				assertSame(outer, txControl.getCurrentContext());
				assertEquals(NO_TRANSACTION, outer.getTransactionStatus());

				return null;
			}
		});
	}

	/**
	 * 147.3.1 RequiresNew inherits a Transaction scope from transaction scope
	 */
	public void testRequiresNewFromTransaction() {

		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());
		assertNull(txControl.getCurrentContext());

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				assertTrue(txControl.activeScope());
				assertTrue(txControl.activeTransaction());
				final TransactionContext outer = txControl.getCurrentContext();
				assertNotNull(outer);
				assertEquals(ACTIVE, outer.getTransactionStatus());

				TransactionContext nested = txControl
						.requiresNew(new Callable<TransactionContext>() {
					@Override
							public TransactionContext call() throws Exception {
						assertTrue(txControl.activeScope());
						assertTrue(txControl.activeTransaction());

						TransactionContext current = txControl
								.getCurrentContext();
						assertNotNull(current);
						assertEquals(ACTIVE, current.getTransactionStatus());

						assertNotEquals(outer.getTransactionKey(),
								current.getTransactionKey());

								return current;
					}
				});

				assertEquals(COMMITTED, nested.getTransactionStatus());

				assertSame(outer, txControl.getCurrentContext());
				assertEquals(ACTIVE, outer.getTransactionStatus());

				return null;
			}
		});
	}

	/**
	 * 147.3.1 Supports starts a new no transaction scope from unscoped
	 */
	public void testSupportsFromUnscoped() {

		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());
		assertNull(txControl.getCurrentContext());

		txControl.supports(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				assertTrue(txControl.activeScope());
				assertFalse(txControl.activeTransaction());

				TransactionContext current = txControl.getCurrentContext();
				assertNotNull(current);
				assertEquals(NO_TRANSACTION, current.getTransactionStatus());
				return null;
			}
		});
	}

	/**
	 * 147.3.1 Supports starts a Transaction scope from no transaction
	 */
	public void testSupportsFromNoTransaction() {

		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());
		assertNull(txControl.getCurrentContext());

		txControl.notSupported(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				assertTrue(txControl.activeScope());
				assertFalse(txControl.activeTransaction());
				final TransactionContext outer = txControl.getCurrentContext();
				assertNotNull(outer);
				assertEquals(NO_TRANSACTION, outer.getTransactionStatus());
				assertNull(outer.getTransactionKey());

				return txControl.supports(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						assertTrue(txControl.activeScope());
						assertFalse(txControl.activeTransaction());

						TransactionContext current = txControl
								.getCurrentContext();
						assertNotNull(current);
						assertEquals(NO_TRANSACTION,
								current.getTransactionStatus());

						assertEquals(outer.getTransactionKey(),
								current.getTransactionKey());

						return null;
					}
				});
			}
		});
	}

	/**
	 * 147.3.1 Supports inherits a Transaction scope from transaction scope
	 */
	public void testSupportsFromTransaction() {

		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());
		assertNull(txControl.getCurrentContext());

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				assertTrue(txControl.activeScope());
				assertTrue(txControl.activeTransaction());
				final TransactionContext outer = txControl.getCurrentContext();
				assertNotNull(outer);
				assertEquals(ACTIVE, outer.getTransactionStatus());

				txControl.supports(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						assertTrue(txControl.activeScope());
						assertTrue(txControl.activeTransaction());

						TransactionContext current = txControl
								.getCurrentContext();
						assertNotNull(current);
						assertEquals(ACTIVE, current.getTransactionStatus());

						assertEquals(outer.getTransactionKey(),
								current.getTransactionKey());

						return null;
					}
				});

				assertSame(outer, txControl.getCurrentContext());
				assertEquals(ACTIVE, outer.getTransactionStatus());

				return null;
			}
		});
	}

	/**
	 * 147.3.1 notSupported starts a new no transaction scope from unscoped
	 */
	public void testNotSupportedFromUnscoped() {

		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());
		assertNull(txControl.getCurrentContext());

		txControl.notSupported(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				assertTrue(txControl.activeScope());
				assertFalse(txControl.activeTransaction());

				TransactionContext current = txControl.getCurrentContext();
				assertNotNull(current);
				assertEquals(NO_TRANSACTION, current.getTransactionStatus());
				return null;
			}
		});
	}

	/**
	 * 147.3.1 notSupported inherits from no transaction
	 */
	public void testNotSupportedFromNoTransaction() {

		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());
		assertNull(txControl.getCurrentContext());

		txControl.notSupported(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				assertTrue(txControl.activeScope());
				assertFalse(txControl.activeTransaction());
				final TransactionContext outer = txControl.getCurrentContext();
				assertNotNull(outer);
				assertEquals(NO_TRANSACTION, outer.getTransactionStatus());
				assertNull(outer.getTransactionKey());

				return txControl.notSupported(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						assertTrue(txControl.activeScope());
						assertFalse(txControl.activeTransaction());

						TransactionContext current = txControl
								.getCurrentContext();
						assertNotNull(current);
						assertEquals(NO_TRANSACTION,
								current.getTransactionStatus());

						assertEquals(outer.getTransactionKey(),
								current.getTransactionKey());

						return null;
					}
				});
			}
		});
	}

	/**
	 * 147.3.1 NotSupported starts a no transaction scope from transaction scope
	 */
	public void testNotSupportedFromTransaction() {

		assertFalse(txControl.activeScope());
		assertFalse(txControl.activeTransaction());
		assertNull(txControl.getCurrentContext());

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				assertTrue(txControl.activeScope());
				assertTrue(txControl.activeTransaction());
				final TransactionContext outer = txControl.getCurrentContext();
				assertNotNull(outer);
				assertEquals(ACTIVE, outer.getTransactionStatus());

				txControl.notSupported(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						assertTrue(txControl.activeScope());
						assertFalse(txControl.activeTransaction());

						TransactionContext current = txControl
								.getCurrentContext();
						assertNotNull(current);
						assertEquals(NO_TRANSACTION,
								current.getTransactionStatus());

						assertNotEquals(outer.getTransactionKey(),
								current.getTransactionKey());

						return null;
					}
				});

				assertSame(outer, txControl.getCurrentContext());
				assertEquals(ACTIVE, outer.getTransactionStatus());

				return null;
			}
		});
	}

	/**
	 * 147.3.3.1 setRollbackOnly test
	 */
	public void testMarkRollback() {

		TransactionContext ctx = txControl
				.required(new Callable<TransactionContext>() {
					@Override
					public TransactionContext call() throws Exception {
						TransactionContext current = txControl
								.getCurrentContext();
						assertNotNull(current);

						assertEquals(ACTIVE, current.getTransactionStatus());
						assertFalse(current.getRollbackOnly());

						current.setRollbackOnly();

						assertEquals(MARKED_ROLLBACK,
								current.getTransactionStatus());
						assertTrue(current.getRollbackOnly());

						return current;
					}
				});

		assertEquals(ROLLED_BACK, ctx.getTransactionStatus());
	}

	/**
	 * 147.3.3.1 runtime exception triggers rollback
	 */
	public void testUncheckedExceptionCausesRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		try {
			txControl.required(new Callable<TransactionContext>() {
				@Override
				public TransactionContext call() throws Exception {
					TransactionContext current = txControl.getCurrentContext();
					assertNotNull(current);

					assertEquals(ACTIVE, current.getTransactionStatus());
					assertFalse(current.getRollbackOnly());

					ctx.set(current);

					throw new RuntimeException("Boom");
				}
			});
			fail("Expected a ScopedWorkException");
		} catch (ScopedWorkException e) {}

		assertEquals(ROLLED_BACK, ctx.get().getTransactionStatus());
	}

	/**
	 * 147.3.3.1 checked exception triggers rollback
	 */
	public void testCheckedExceptionCausesRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		try {
			txControl.required(new Callable<TransactionContext>() {
				@Override
				public TransactionContext call() throws Exception {
					TransactionContext current = txControl.getCurrentContext();
					assertNotNull(current);

					assertEquals(ACTIVE, current.getTransactionStatus());
					assertFalse(current.getRollbackOnly());

					ctx.set(current);

					throw new Exception("Boom");
				}
			});
			fail("Expected a ScopedWorkException");
		} catch (ScopedWorkException e) {}

		assertEquals(ROLLED_BACK, ctx.get().getTransactionStatus());
	}

	/**
	 * 147.3.3.2 ignored runtime exception does not trigger rollback
	 */
	public void testIgnoredUncheckedExceptionNoRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		try {
			txControl.build().noRollbackFor(RuntimeException.class).required(
					new Callable<TransactionContext>() {
						@Override
						public TransactionContext call() throws Exception {
							TransactionContext current = txControl
									.getCurrentContext();
							assertNotNull(current);

							assertEquals(ACTIVE,
									current.getTransactionStatus());
							assertFalse(current.getRollbackOnly());

							ctx.set(current);

							throw new RuntimeException("Boom");
						}
					});
			fail("Expected a ScopedWorkException");
		} catch (ScopedWorkException e) {}

		assertEquals(COMMITTED, ctx.get().getTransactionStatus());
	}

	/**
	 * 147.3.3.2 ignored checked exception does not trigger rollback
	 */
	public void testIgnoredCheckedExceptionNoRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		try {
			txControl.build().noRollbackFor(Exception.class).required(
					new Callable<TransactionContext>() {
						@Override
						public TransactionContext call() throws Exception {
							TransactionContext current = txControl
									.getCurrentContext();
							assertNotNull(current);

							assertEquals(ACTIVE,
									current.getTransactionStatus());
							assertFalse(current.getRollbackOnly());

							ctx.set(current);

							throw new Exception("Boom");
						}
					});
			fail("Expected a ScopedWorkException");
		} catch (ScopedWorkException e) {}

		assertEquals(COMMITTED, ctx.get().getTransactionStatus());
	}

	/**
	 * 147.3.3.2 ignored runtime exception does not trigger rollback
	 */
	public void testIgnoredUncheckedExceptionSubclassNoRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		try {
			txControl.build().noRollbackFor(RuntimeException.class).required(
					new Callable<TransactionContext>() {
						@Override
						public TransactionContext call() throws Exception {
							TransactionContext current = txControl
									.getCurrentContext();
							assertNotNull(current);

							assertEquals(ACTIVE,
									current.getTransactionStatus());
							assertFalse(current.getRollbackOnly());

							ctx.set(current);

							throw new IllegalArgumentException("Boom");
						}
					});
			fail("Expected a ScopedWorkException");
		} catch (ScopedWorkException e) {}

		assertEquals(COMMITTED, ctx.get().getTransactionStatus());
	}

	/**
	 * 147.3.3.2 ignored checked exception does not trigger rollback
	 */
	public void testIgnoredCheckedExceptionSubclassNoRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		try {
			txControl.build().noRollbackFor(Exception.class).required(
					new Callable<TransactionContext>() {
						@Override
						public TransactionContext call() throws Exception {
							TransactionContext current = txControl
									.getCurrentContext();
							assertNotNull(current);

							assertEquals(ACTIVE,
									current.getTransactionStatus());
							assertFalse(current.getRollbackOnly());

							ctx.set(current);

							throw new ClassNotFoundException("Boom");
						}
					});
			fail("Expected a ScopedWorkException");
		} catch (ScopedWorkException e) {}

		assertEquals(COMMITTED, ctx.get().getTransactionStatus());
	}

	/**
	 * 147.3.3.2 ignored different runtime exception triggers rollback
	 */
	public void testIgnoredDifferentUncheckedExceptionRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		try {
			txControl.build()
					.noRollbackFor(IllegalArgumentException.class)
					.required(new Callable<TransactionContext>() {
						@Override
						public TransactionContext call() throws Exception {
							TransactionContext current = txControl
									.getCurrentContext();
							assertNotNull(current);

							assertEquals(ACTIVE,
									current.getTransactionStatus());
							assertFalse(current.getRollbackOnly());

							ctx.set(current);

							throw new RuntimeException("Boom");
						}
					});
			fail("Expected a ScopedWorkException");
		} catch (ScopedWorkException e) {}

		assertEquals(ROLLED_BACK, ctx.get().getTransactionStatus());
	}

	/**
	 * 147.3.3.2 ignored different checked exception triggers rollback
	 */
	public void testIgnoredDifferentCheckedExceptionRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		try {
			txControl.build()
					.noRollbackFor(ClassNotFoundException.class)
					.required(new Callable<TransactionContext>() {
						@Override
						public TransactionContext call() throws Exception {
							TransactionContext current = txControl
									.getCurrentContext();
							assertNotNull(current);

							assertEquals(ACTIVE,
									current.getTransactionStatus());
							assertFalse(current.getRollbackOnly());

							ctx.set(current);

							throw new Exception("Boom");
						}
					});
			fail("Expected a ScopedWorkException");
		} catch (ScopedWorkException e) {}

		assertEquals(ROLLED_BACK, ctx.get().getTransactionStatus());
	}

	/**
	 * 147.3.3.2 ignored runtime exception does not trigger rollback
	 */
	public void testIgnoredExceptionInstanceNoRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		try {
			txControl.required(new Callable<TransactionContext>() {
				@Override
				public TransactionContext call() throws Exception {
					TransactionContext current = txControl.getCurrentContext();
					assertNotNull(current);

					assertEquals(ACTIVE, current.getTransactionStatus());
					assertFalse(current.getRollbackOnly());

					ctx.set(current);

					RuntimeException runtimeException = new RuntimeException(
							"Boom");

					txControl.ignoreException(runtimeException);

					throw runtimeException;
				}
			});
			fail("Expected a ScopedWorkException");
		} catch (ScopedWorkException e) {}

		assertEquals(COMMITTED, ctx.get().getTransactionStatus());
	}

	/**
	 * 147.3.3.3 runtime exception triggers rollback in inherited transaction
	 */
	public void testUncheckedExceptionMarksRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		txControl.required(new Callable<TransactionContext>() {
			@Override
			public TransactionContext call() throws Exception {
				try {
					txControl.required(new Callable<TransactionContext>() {
						@Override
						public TransactionContext call() throws Exception {
							TransactionContext current = txControl
									.getCurrentContext();
							assertNotNull(current);

							assertEquals(ACTIVE,
									current.getTransactionStatus());
							assertFalse(current.getRollbackOnly());

							ctx.set(current);

							throw new RuntimeException("Boom");
						}
					});
					fail("Expected a ScopedWorkException");
				} catch (ScopedWorkException e) {}

				assertEquals(MARKED_ROLLBACK, ctx.get().getTransactionStatus());
				return null;
			}
		});

		assertEquals(ROLLED_BACK, ctx.get().getTransactionStatus());
	}

	/**
	 * 147.3.3.3 checked exception triggers rollback in inherited transaction
	 */
	public void testCheckedExceptionMarksRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		txControl.required(new Callable<TransactionContext>() {
			@Override
			public TransactionContext call() throws Exception {
				try {
					txControl.required(new Callable<TransactionContext>() {
						@Override
						public TransactionContext call() throws Exception {
							TransactionContext current = txControl
									.getCurrentContext();
							assertNotNull(current);

							assertEquals(ACTIVE,
									current.getTransactionStatus());
							assertFalse(current.getRollbackOnly());

							ctx.set(current);

							throw new Exception("Boom");
						}
					});
					fail("Expected a ScopedWorkException");
				} catch (ScopedWorkException e) {}

				assertEquals(MARKED_ROLLBACK, ctx.get().getTransactionStatus());
				return null;
			}
		});

		assertEquals(ROLLED_BACK, ctx.get().getTransactionStatus());
	}

	/**
	 * 147.3.3.3 runtime exception triggers rollback in inherited transaction
	 */
	public void testIgnoredNestedUncheckedExceptionNoRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		txControl.required(new Callable<TransactionContext>() {
			@Override
			public TransactionContext call() throws Exception {
				try {
					txControl.build()
							.noRollbackFor(RuntimeException.class)
							.required(new Callable<TransactionContext>() {
								@Override
								public TransactionContext call()
										throws Exception {
									TransactionContext current = txControl
											.getCurrentContext();
									assertNotNull(current);

									assertEquals(ACTIVE,
											current.getTransactionStatus());
									assertFalse(current.getRollbackOnly());

									ctx.set(current);

									throw new RuntimeException("Boom");
								}
							});
					fail("Expected a ScopedWorkException");
				} catch (ScopedWorkException e) {}

				assertEquals(ACTIVE, ctx.get().getTransactionStatus());
				return null;
			}
		});

		assertEquals(COMMITTED, ctx.get().getTransactionStatus());
	}

	/**
	 * 147.3.3.3 checked exception triggers rollback in inherited transaction
	 */
	public void testIgnoredNestedCheckedExceptionNoRollback() {

		final AtomicReference<TransactionContext> ctx = new AtomicReference<>();

		txControl.required(new Callable<TransactionContext>() {
			@Override
			public TransactionContext call() throws Exception {
				try {
					txControl.build().noRollbackFor(Exception.class).required(
							new Callable<TransactionContext>() {
								@Override
								public TransactionContext call()
										throws Exception {
									TransactionContext current = txControl
											.getCurrentContext();
									assertNotNull(current);

									assertEquals(ACTIVE,
											current.getTransactionStatus());
									assertFalse(current.getRollbackOnly());

									ctx.set(current);

									throw new Exception("Boom");
								}
							});
					fail("Expected a ScopedWorkException");
				} catch (ScopedWorkException e) {}

				assertEquals(ACTIVE, ctx.get().getTransactionStatus());
				return null;
			}
		});

		assertEquals(COMMITTED, ctx.get().getTransactionStatus());
	}

	/**
	 * Note that read only support is not required so this test must have a bail
	 * out! 147.3.3.4.3 Read Only status cannot be changed in inherited
	 * transaction
	 */
	public void testReadOnlyChange() {

		txControl.build().readOnly().required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				if (txControl.getCurrentContext().isReadOnly()) {
					// Supported, so run the test

					try {
						txControl.required(new Callable<Object>() {
							@Override
							public Object call() throws Exception {
								fail("Should not start the nested transaction");
								return null;
							}
						});
						fail("Should not be able to expand the read only scope");
					} catch (TransactionException te) {}
				}
				return null;
			}
		});

	}

	/**
	 * Note that read only support is not required so this test must have a bail
	 * out! 147.3.3.4.3 Read Only status cannot be changed in inherited
	 * transaction
	 */
	public void testReadOnlySame() {

		txControl.build().readOnly().required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				if (txControl.getCurrentContext().isReadOnly()) {
					// Supported, so run the test
					return txControl.build().readOnly().required(
							new Callable<Object>() {
								@Override
								public Object call() throws Exception {
									return null;
								}
							});
				}
				return null;
			}
		});

	}

	/**
	 * Note that read only support is not required so this test must have a bail
	 * out! 147.3.3.4.3 Read Only status cannot be changed in inherited
	 * transaction
	 */
	public void testReadOnlyChangeNewTran() {

		txControl.build().readOnly().required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				if (txControl.getCurrentContext().isReadOnly()) {
					// Supported, so run the test
					txControl.requiresNew(new Callable<Object>() {
						@Override
						public Object call() throws Exception {
							return null;
						}
					});
				}
				return null;
			}
		});

	}
}
