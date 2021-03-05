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
package org.osgi.test.cases.async.junit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.async.Async;
import org.osgi.test.cases.async.junit.impl.MyServiceImpl;
import org.osgi.test.cases.async.junit.impl.MyServiceImpl.ExtendedFinalMethodMyServiceImpl;
import org.osgi.test.cases.async.junit.impl.MyServiceImpl.FinalMethodMyServiceImpl;
import org.osgi.test.cases.async.junit.impl.MyServiceImpl.FinalMyServiceImpl;
import org.osgi.test.cases.async.junit.impl.MyServiceImpl.NoZeroArgConstructorMyServiceImpl;
import org.osgi.test.cases.async.services.AnotherService;
import org.osgi.test.cases.async.services.MyService;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tracker.ServiceTracker;

public class BuildingMediatorTestCase extends OSGiTestCase {
	
	private ServiceTracker<Async, Async>	asyncTracker;
	private Async	async;

	private MyServiceImpl myService;
	private ServiceRegistration<MyService> normalReg;
	private ServiceRegistration<?> multiObjectClassReg;
	private ServiceRegistration<MyServiceImpl> implOnlyObjectClassReg;
	private ServiceRegistration<MyServiceImpl.FinalMyServiceImpl> finalClassReg;
	private ServiceRegistration<MyServiceImpl.FinalMethodMyServiceImpl> finalMethodReg;
	private ServiceRegistration<MyServiceImpl.ExtendedFinalMethodMyServiceImpl> extendFinalMethodReg;
	private ServiceRegistration<MyServiceImpl.NoZeroArgConstructorMyServiceImpl> noZeroArgConstructorReg;
	@SuppressWarnings("rawtypes")
	private ServiceRegistration<List> listReg;

	private Bundle clientBundle;
	
	protected void setUp() throws InterruptedException, BundleException, IOException {
		myService = new MyServiceImpl();

		normalReg = getContext().registerService(MyService.class, myService, null);
		multiObjectClassReg = getContext().registerService(new String[] {MyService.class.getName(), AnotherService.class.getName()}, myService, null);
		implOnlyObjectClassReg = getContext().registerService(MyServiceImpl.class, myService, null);
		finalClassReg = getContext().registerService(MyServiceImpl.FinalMyServiceImpl.class, new MyServiceImpl.FinalMyServiceImpl(), null);
		finalMethodReg = getContext().registerService(MyServiceImpl.FinalMethodMyServiceImpl.class, new MyServiceImpl.FinalMethodMyServiceImpl(), null);
		extendFinalMethodReg = getContext().registerService(MyServiceImpl.ExtendedFinalMethodMyServiceImpl.class, new MyServiceImpl.ExtendedFinalMethodMyServiceImpl(), null);
		noZeroArgConstructorReg = getContext().registerService(MyServiceImpl.NoZeroArgConstructorMyServiceImpl.class, new MyServiceImpl.NoZeroArgConstructorMyServiceImpl(1), null);
		
		listReg =  getContext().registerService(List.class, new ArrayList<String>(), null);

		asyncTracker = new ServiceTracker<Async, Async>(getContext(), Async.class, null);
		asyncTracker.open();
		async = asyncTracker.waitForService(5000);

		assertNotNull("No Async service available within 5 seconds", async);

		clientBundle = install("tb1.jar");
		clientBundle.start();
	}
	
	protected void tearDown() {
		normalReg.unregister();
		multiObjectClassReg.unregister();
		implOnlyObjectClassReg.unregister();
		finalClassReg.unregister();
		finalMethodReg.unregister();
		extendFinalMethodReg.unregister();
		noZeroArgConstructorReg.unregister();
		listReg.unregister();

		asyncTracker.close();

		try {
			clientBundle.stop();
		} catch (BundleException e) {
			// did out best
		}
		// Note that we don't uninstall clientBundle each time
	}

	private Async getClientAsync() {
		BundleContext context = clientBundle.getBundleContext();
		ServiceReference<Async> asyncRef = context.getServiceReference(Async.class);
		assertNotNull("No Async service for client.", asyncRef);
		Async clientAsync = context.getService(asyncRef);
		assertNotNull("Client Async is null.", clientAsync);
		return clientAsync;
	}

	private void assertIsAssignable(Object service, Class<?>... classes) {
		for (Class<?> clazz : classes) {
			assertTrue("The service is not assignable to: " + clazz.getName(), clazz.isAssignableFrom(service.getClass()));
		}
	}

	private void assertNotAssignable(Object service, Class<?>... classes) {
		for (Class<?> clazz : classes) {
			assertFalse("The service is assignable to: " + clazz.getName(), clazz.isAssignableFrom(service.getClass()));
		}
	}

	public void testReferenceToSingleObjectClassOnly() throws Exception {
		Object service = async.mediate(normalReg.getReference(), MyService.class);
		assertIsAssignable(service, MyService.class);
		assertNotAssignable(service, AnotherService.class, MyServiceImpl.class);
	}

	/* Added by tim.ward@paremus.com to cover new mediator construction rules */
	
	@SuppressWarnings("unchecked")
	public void testReferenceToSingleObjectClassMediatedToWrongType() throws Exception {
		@SuppressWarnings("rawtypes")
		Object service = async.mediate((ServiceReference) normalReg.getReference(), AnotherService.class);
		assertIsAssignable(service, AnotherService.class);
		assertNotAssignable(service, MyService.class, MyServiceImpl.class);
	}
	
	@SuppressWarnings("unchecked")
	public void testReferenceToMultiObjectClassOnly() throws Exception {
		Object service = async.mediate((ServiceReference<MyService>)multiObjectClassReg.getReference(), MyService.class);
		assertIsAssignable(service, MyService.class);
		assertNotAssignable(service, AnotherService.class, MyServiceImpl.class);
		service = async.mediate((ServiceReference<AnotherService>)multiObjectClassReg.getReference(), AnotherService.class);
		assertIsAssignable(service, AnotherService.class);
		assertNotAssignable(service, MyService.class, MyServiceImpl.class);
	}
	/* End additions */

	public void testReferenceToImplClass() throws Exception {
		Object service = async.mediate(implOnlyObjectClassReg.getReference(), MyServiceImpl.class);
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
	}

	public void testDirectToImplClass() throws Exception {
		Object service = async.mediate(myService, MyServiceImpl.class);
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
		
		/* Added by tim.ward@paremus.com to cover new mediator construction rules */
		
		service = async.mediate(myService, AnotherService.class);
		assertIsAssignable(service, AnotherService.class);
		assertNotAssignable(service, MyService.class, MyServiceImpl.class);

		service = async.mediate(myService, MyService.class);
		assertIsAssignable(service, MyService.class);
		assertNotAssignable(service, AnotherService.class, MyServiceImpl.class);
		
		/* End additions */
	}

	public void testReferenceMediateToFinalClass() {
		try {
			async.mediate(finalClassReg.getReference(), FinalMyServiceImpl.class);
			fail("Should not be able to mediate a final class");
		} catch (IllegalArgumentException iee) {
			//Expected
		}
	}

	public void testDirectMediateToFinalClass() {
		try {
			async.mediate(new MyServiceImpl.FinalMyServiceImpl(), FinalMyServiceImpl.class);
			fail("Should not be able to mediate a final class");
		} catch (IllegalArgumentException iee) {
			//Expected
		}
	}

	public void testReferenceMediateToFinalMethod() {
		try {
			async.mediate(finalMethodReg.getReference(), FinalMethodMyServiceImpl.class);
			fail("Should not be able to mediate a class with a final method");
		} catch (IllegalArgumentException iee) {
			//Expected
		}
	}

	public void testDirectMediateToFinalMethod() {
		try {
			async.mediate(new MyServiceImpl.FinalMethodMyServiceImpl(), FinalMethodMyServiceImpl.class);
			fail("Should not be able to mediate a class with a final method");
		} catch (IllegalArgumentException iee) {
			//Expected
		}
	}

	public void testReferenceMediateToExtendFinalClass() {
		try {
			async.mediate(extendFinalMethodReg.getReference(), ExtendedFinalMethodMyServiceImpl.class);
			fail("Should not be able to mediate a class with a final method");
		} catch (IllegalArgumentException iee) {
			//Expected
		}
	}

	public void testDirectMediateToExtendFinalMethod() {
		try {
			async.mediate(new MyServiceImpl.ExtendedFinalMethodMyServiceImpl(), 
				ExtendedFinalMethodMyServiceImpl.class);
			fail("Should not be able to mediate a class with a final method");
		} catch (IllegalArgumentException iee) {
			//Expected
		}
	}

	public void testReferenceMediateToNoZeroArgConstructor() {
		try {
			async.mediate(noZeroArgConstructorReg.getReference(), NoZeroArgConstructorMyServiceImpl.class);
			fail("Should not be able to mediate a class no zero args constructor");
		} catch (IllegalArgumentException iee) {
			//Expected
		}
	}

	public void testDirectMediateToNoZeroArgConstructor() {
		try {
			async.mediate(new MyServiceImpl.NoZeroArgConstructorMyServiceImpl(1), NoZeroArgConstructorMyServiceImpl.class);
			fail("Should not be able to mediate a class no zero args constructor");
		} catch (IllegalArgumentException iee) {
			//Expected
		}
	}

	public void testReferenceMediateToTypesNotVisibleToClient() {
		Object service = getClientAsync().mediate(normalReg.getReference(), MyService.class);
		assertIsAssignable(service, MyService.class);
		assertNotAssignable(service, AnotherService.class, MyServiceImpl.class);
	}

	public void testDirectMediateToTypesNotVisibleToClient() {
		Object service = getClientAsync().mediate(myService, MyServiceImpl.class);
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
	}

	public void testReferenceMediateToVMTypes() {
		Object service = async.mediate(listReg.getReference(), List.class);
		assertIsAssignable(service, List.class);
	}

	public void testDirectMediateToVMTypes() {
		Object service = async.mediate(new ArrayList<String>(), ArrayList.class);
		assertIsAssignable(service, List.class, ArrayList.class);
	}
}
