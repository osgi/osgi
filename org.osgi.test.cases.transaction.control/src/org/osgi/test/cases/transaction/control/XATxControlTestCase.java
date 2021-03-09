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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.transaction.control.resources.EmptyXAResource;

/**
 * This test case runs against XA Transaction Control services testing the
 * behaviour defined in 147.4.6 Note that this test does not test the XA
 * algorithm itself, but it does test recovery This test case also tests
 * recovery as defined in 147.6
 * 
 * @author $Id$
 */
public class XATxControlTestCase extends TxControlTestCase {
	
	/**
	 * A basic test that ensures that an XA Transaction Control reports that it
	 * supports XA resources
	 * 
	 * @throws Exception
	 */
	public void testTxControlSupportsXA() throws Exception {
		if (!xaEnabled) {
			return;
		}

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				assertTrue(txControl.getCurrentContext().supportsXA());
				return null;
			}
		});

	}

	/**
	 * A basic test that checks the recovery behaviour of
	 * 
	 * @throws Exception
	 */
	public void testTxControlSupportsRecovery() throws Exception {
		if (!recoveryEnabled) {
			return;
		}

		Class< ? > recoveryIface = txControlBundle.loadClass(
				"org.osgi.service.transaction.control.recovery.RecoverableXAResource");

		final AtomicBoolean firstCommit = new AtomicBoolean(true);

		final Map<String,Xid> stored = new HashMap<>();

		final XAResource res1 = new EmptyXAResource() {
			@Override
			public void commit(Xid xid, boolean onePhase) throws XAException {
				if (!firstCommit.getAndSet(false)) {
					stored.put("res1", xid);
					throw new XAException(XAException.XAER_RMFAIL);
				}
			}
		};

		final XAResource res2 = new EmptyXAResource() {
			@Override
			public void commit(Xid xid, boolean onePhase) throws XAException {
				if (!firstCommit.getAndSet(false)) {
					stored.put("res2", xid);
					throw new XAException(XAException.XAER_RMFAIL);
				}
			}
		};

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				txControl.getCurrentContext().registerXAResource(res1, "res1");
				txControl.getCurrentContext().registerXAResource(res2, "res2");

				return null;
			}
		});

		assertFalse(stored.isEmpty());
		
		final String name = stored.keySet().iterator().next();
		final Semaphore sem = new Semaphore(0);
		
		Object recovery = Proxy.newProxyInstance(recoveryIface.getClassLoader(),
				new Class[] {
						recoveryIface
				}, new InvocationHandler() {

					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						if ("releaseXAResource".equals(method.getName())) {
							return null;
						} else if ("getXAResource"
								.equals(method.getName())) {
							return new EmptyXAResource() {

								@Override
								public void commit(Xid xid, boolean onePhase)
										throws XAException {
									Xid removed = stored.remove(name);
									if (xid.equals(removed)) {
										sem.release();
									} else {
										System.out.println("Expected to remove "
												+ xid + " but found "
												+ removed);
									}
								}

								@Override
								public Xid[] recover(int flag)
										throws XAException {
									Xid id = stored.get(name);
									return id == null ? new Xid[0] : new Xid[] {
											id
									};
								}
							};
						} else if ("getId".equals(method.getName())) {
							return name;
						}
						throw new IllegalArgumentException(
								"Unrecognised method "
										+ method.toGenericString());
					}
				});

		ServiceRegistration< ? > reg = getContext()
				.registerService(recoveryIface.getName(), recovery, null);

		try {
			assertTrue(sem.tryAcquire(10, TimeUnit.SECONDS));
			assertTrue(stored.isEmpty());
		} finally {
			reg.unregister();
		}
	}
}
