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
package org.osgi.test.cases.residentialmanagement.tb3;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.residentialmanagement.util.Service1;
import org.osgi.test.cases.residentialmanagement.util.Service2;
import org.osgi.test.cases.residentialmanagement.util.Service3;
/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class TestServiceImpl implements Service1 ,Service2,Service3{
	ServiceRegistration< ? > serviceReg;
	TestServiceImpl(){
	}
	@Override
	public void testMessage1(){
		System.out.println("test");
	}
	@Override
	public void testMessage2(){
		System.out.println("test");
	}
	@Override
	public void modifytServiceProperty(){
		Dictionary<String,Object> props = new Hashtable<>();
		props.put("testKey1", "testValue2");
		props.put("testKey2", "testValue4");
		props.put("testKey3", "testValue3");
		
		serviceReg.setProperties(props);
	}

	public void setServiceRegistration(ServiceRegistration< ? > sr) {
		this.serviceReg = sr;
	}
}
