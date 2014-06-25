/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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
		Object service = async.mediate(normalReg.getReference());
		assertIsAssignable(service, MyService.class);
		assertNotAssignable(service, AnotherService.class, MyServiceImpl.class);
	}

	public void testReferenceToMultiObjectClassOnly() throws Exception {
		Object service = async.mediate(multiObjectClassReg.getReference());
		assertIsAssignable(service, MyService.class, AnotherService.class);
		assertNotAssignable(service, MyServiceImpl.class);
	}

	public void testReferenceToImplClass() throws Exception {
		Object service = async.mediate(implOnlyObjectClassReg.getReference());
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
	}

	public void testDirectToImplClass() throws Exception {
		Object service = async.mediate(myService);
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
	}

	public void testReferenceMediateToFinalClass() {
		Object service = async.mediate(finalClassReg.getReference());
		// Should fall back to MyServiceImpl type
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
		assertNotAssignable(service, FinalMyServiceImpl.class);
	}

	public void testDirectMediateToFinalClass() {
		Object service = async.mediate(new MyServiceImpl.FinalMyServiceImpl());
		// Should fall back to MyServiceImpl type
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
		assertNotAssignable(service, FinalMyServiceImpl.class);
	}

	public void testReferenceMediateToFinalMethod() {
		Object service = async.mediate(finalMethodReg.getReference());
		// Should fall back to MyServiceImpl type
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
		assertNotAssignable(service, FinalMethodMyServiceImpl.class);
	}

	public void testDirectMediateToFinalMethod() {
		Object service = async.mediate(new MyServiceImpl.FinalMethodMyServiceImpl());
		// Should fall back to MyServiceImpl type
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
		assertNotAssignable(service, FinalMethodMyServiceImpl.class);
	}

	public void testReferenceMediateToExtendFinalClass() {
		Object service = async.mediate(extendFinalMethodReg.getReference());
		// Should fall back to MyServiceImpl type
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
		assertNotAssignable(service, ExtendedFinalMethodMyServiceImpl.class, FinalMethodMyServiceImpl.class);
	}

	public void testDirectMediateToExtendFinalMethod() {
		Object service = async.mediate(new MyServiceImpl.ExtendedFinalMethodMyServiceImpl());
		// Should fall back to MyServiceImpl type
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
		assertNotAssignable(service, ExtendedFinalMethodMyServiceImpl.class, FinalMethodMyServiceImpl.class);
	}

	public void testReferenceMediateToNoZeroArgConstructor() {
		Object service = async.mediate(noZeroArgConstructorReg.getReference());
		// Should fall back to MyServiceImpl type
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
		assertNotAssignable(service, NoZeroArgConstructorMyServiceImpl.class);
	}

	public void testDirectMediateToNoZeroArgConstructor() {
		Object service = async.mediate(new MyServiceImpl.NoZeroArgConstructorMyServiceImpl(1));
		// Should fall back to MyServiceImpl type
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
		assertNotAssignable(service, NoZeroArgConstructorMyServiceImpl.class);
	}

	public void testReferenceMediateToTypesNotVisibleToClient() {
		Object service = getClientAsync().mediate(normalReg.getReference());
		assertIsAssignable(service, MyService.class);
		assertNotAssignable(service, AnotherService.class, MyServiceImpl.class);
	}

	public void testDirectMediateToTypesNotVisibleToClient() {
		Object service = getClientAsync().mediate(myService);
		assertIsAssignable(service, MyService.class, AnotherService.class, MyServiceImpl.class);
	}

	public void testReferenceMediateToVMTypes() {
		Object service = async.mediate(listReg.getReference());
		assertIsAssignable(service, List.class);
	}

	public void testDirectMediateToVMTypes() {
		Object service = async.mediate(new ArrayList<String>());
		assertIsAssignable(service, List.class, ArrayList.class);
	}
}
