/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
package org.osgi.test.cases.composite.tb3client;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.composite.tb3.SomeAPI;
import org.osgi.test.cases.composite.tb3.params.Type1;
import org.osgi.test.cases.composite.tb3.params.Type2;

public class Client implements BundleActivator{
	void testing(SomeAPI api, Type1 type1, Type2 type2) {
		api.doWork(type1);
		api.doWork(type2);
	}

	public void start(BundleContext context) throws Exception {
		testing(
				new SomeAPI() {
					
					public void doWork(Type2 type) {
						System.out.println("Did some work on: " + type.toString());
					}
					
					public void doWork(Type1 type) {
						System.out.println("Did some work on: " + type.toString());
					}
				},
				new Type1(){
					public String toString() {
						return "type1";
					}
				},
				new Type2(){
					public String toString() {
						return "type2";
					}	
				}
		);
	}

	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
